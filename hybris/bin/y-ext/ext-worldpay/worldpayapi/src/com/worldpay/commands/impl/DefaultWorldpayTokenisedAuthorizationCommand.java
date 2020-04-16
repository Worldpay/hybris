package com.worldpay.commands.impl;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.util.WorldpayUtil;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Optional;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static java.text.MessageFormat.format;

/**
 * Default Worldpay Subscription Authorize Command - Used for orders whose payment method is a Token for an existing card.
 * <p>
 * Communicates through the worldpayServiceGateway to make the authorise call to Worldpay. Deals with the response
 * and interprets the result. At this time the tokenised orders will not use 3D security.
 * </p>
 */
public class DefaultWorldpayTokenisedAuthorizationCommand extends WorldpayCommand implements SubscriptionAuthorizationCommand {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayTokenisedAuthorizationCommand.class);
    private static final String UNKNOWN_MERCHANT_CODE = "unknownMerchantCode";

    private final Converter<BillingInfo, Address> worldpayBillingInfoAddressConverter;
    private final Converter<DirectAuthoriseServiceResponse, AuthorizationResult> worldpayAuthorizationResultConverter;
    private final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService;

    public DefaultWorldpayTokenisedAuthorizationCommand(final Converter<BillingInfo, Address> worldpayBillingInfoAddressConverter, final Converter<DirectAuthoriseServiceResponse, AuthorizationResult> worldpayAuthorizationResultConverter, final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService, final WorldpayMerchantInfoService worldpayMerchantInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final WorldpayServiceGateway worldpayServiceGateway) {
        super(worldpayMerchantInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway);
        this.worldpayBillingInfoAddressConverter = worldpayBillingInfoAddressConverter;
        this.worldpayAuthorizationResultConverter = worldpayAuthorizationResultConverter;
        this.worldpayDynamicInteractionResolverService = worldpayDynamicInteractionResolverService;
    }

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public AuthorizationResult perform(final SubscriptionAuthorizationRequest subscriptionAuthorizationRequest) {
        final WorldpayAdditionalInfoData additionalInfo = getWorldpayAdditionalInfoData(subscriptionAuthorizationRequest.getCv2());
        final MerchantInfo merchantInfo = getMerchant();
        if (merchantInfo != null) {
            final String merchantCode = merchantInfo.getMerchantCode();
            try {
                final DirectAuthoriseServiceRequest request = buildWorldpayRequest(subscriptionAuthorizationRequest, additionalInfo, merchantInfo);
                final DirectAuthoriseServiceResponse response = getWorldpayServiceGateway().directAuthorise(request);
                return getAuthorizationResult(merchantCode, response, subscriptionAuthorizationRequest);
            } catch (final WorldpayException e) {
                LOG.error("Worldpay Exception for transaction: " + subscriptionAuthorizationRequest.getMerchantTransactionCode(), e);
                return createErrorAuthorizeResult(merchantCode, subscriptionAuthorizationRequest);
            }
        }
        return createErrorAuthorizeResult(UNKNOWN_MERCHANT_CODE, subscriptionAuthorizationRequest);
    }

    private AuthorizationResult getAuthorizationResult(final String merchantCode,
                                                       final DirectAuthoriseServiceResponse response,
                                                       final SubscriptionAuthorizationRequest subscriptionAuthorizationRequest) throws WorldpayException {
        return Optional.ofNullable(response).map(directAuthoriseServiceResponse -> {
            final AuthorizationResult result = worldpayAuthorizationResultConverter.convert(directAuthoriseServiceResponse);
            addAuthorizationResultInfo(subscriptionAuthorizationRequest, merchantCode, result);
            setTotalAmountWhenTransactionIsAccepted(response, result);
            return result;
        }).orElseThrow(() -> new WorldpayException("Response from worldpay is empty"));
    }

    private void setTotalAmountWhenTransactionIsAccepted(final DirectAuthoriseServiceResponse response, final AuthorizationResult result) {
        if (ACCEPTED.equals(result.getTransactionStatus())) {
            result.setTotalAmount(getWorldpayOrderService().convertAmount(response.getPaymentReply().getAmount()));
        }
    }

    private AuthorizationResult createErrorAuthorizeResult(final String merchantCode, final SubscriptionAuthorizationRequest subscriptionAuthorizationRequest) {
        final AuthorizationResult result = new AuthorizationResult();
        result.setTransactionStatus(ERROR);
        result.setTransactionStatusDetails(GENERAL_SYSTEM_ERROR);
        addAuthorizationResultInfo(subscriptionAuthorizationRequest, merchantCode, result);
        return result;
    }

    protected void addAuthorizationResultInfo(final SubscriptionAuthorizationRequest subscriptionAuthorizationRequest,
                                              final String merchantCode, final AuthorizationResult result) {
        final String merchantTransactionCode = subscriptionAuthorizationRequest.getMerchantTransactionCode();
        result.setMerchantTransactionCode(merchantTransactionCode);
        result.setRequestId(merchantTransactionCode);
        result.setRequestToken(merchantCode);
        result.setAuthorizationTime(new Date());
        result.setCurrency(subscriptionAuthorizationRequest.getCurrency());
        result.setTotalAmount(subscriptionAuthorizationRequest.getTotalAmount());
    }

    protected WorldpayAdditionalInfoData getWorldpayAdditionalInfoData(final String cv2) {
        return WorldpayUtil.deserializeWorldpayAdditionalInfo(cv2);
    }

    private DirectAuthoriseServiceRequest buildWorldpayRequest(final SubscriptionAuthorizationRequest subscriptionAuthorizationRequest,
                                                               final WorldpayAdditionalInfoData additionalInfo,
                                                               final MerchantInfo merchantInfo) {
        final String worldpayOrderCode = subscriptionAuthorizationRequest.getMerchantTransactionCode();
        final Amount amount = getWorldpayOrderService().createAmount(subscriptionAuthorizationRequest.getCurrency(), subscriptionAuthorizationRequest.getTotalAmount().doubleValue());
        final BasicOrderInfo orderInfo = getWorldpayOrderService().createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, amount);
        final Session session = getWorldpayOrderService().createSession(additionalInfo);
        final Browser browser = getWorldpayOrderService().createBrowser(additionalInfo);
        final Additional3DSData additional3DSData = new Additional3DSData(additionalInfo.getAdditional3DS2().getDfReferenceId());
        final String customerEmail = additionalInfo.getCustomerEmail();
        final String authenticatedShopperId = additionalInfo.getAuthenticatedShopperId();
        final Shopper shopper = getWorldpayOrderService().createAuthenticatedShopper(customerEmail, authenticatedShopperId, session, browser);
        final Token token = getWorldpayOrderService().createToken(subscriptionAuthorizationRequest.getSubscriptionID(), additionalInfo.getSecurityCode());
        final Address shippingAddress = worldpayBillingInfoAddressConverter.convert(subscriptionAuthorizationRequest.getShippingInfo());
        final DynamicInteractionType dynamicInteractionType = worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(additionalInfo);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(orderInfo)
                .withPayment(token)
                .withShopper(shopper)
                .withShippingAddress(shippingAddress)
                .withBillingAddress(null)
                .withStatementNarrative(null)
                .withDynamicInteractionType(dynamicInteractionType)
                .withAdditional3DSData(additional3DSData)
                .build();

        return createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    protected DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        return DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters);
    }

    protected MerchantInfo getMerchant() {
        try {
            return getWorldpayMerchantInfoService().getCurrentSiteMerchant();
        } catch (final WorldpayConfigurationException e) {
            LOG.error(format("There is an error with the current merchants configuration. Exception: [{0}]", e.getMessage()), e);
        }
        return null;
    }
}
