package com.worldpay.service.payment.request.impl;

import com.google.common.base.Preconditions;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;
import java.util.Optional;

import static com.worldpay.service.model.payment.PaymentType.IDEAL;

public class DefaultWorldpayRequestService implements WorldpayRequestService {

    private static final String WORLDPAY_MERCHANT_TOKEN_ENABLED = "worldpay.merchant.token.enabled";

    protected final WorldpayUrlService bankWorldpayUrlService;
    protected final SiteConfigService siteConfigService;
    protected final Converter<AddressModel, Address> worldpayAddressConverter;
    protected final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;
    protected final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService;
    protected final CustomerEmailResolutionService customerEmailResolutionService;

    public DefaultWorldpayRequestService(final WorldpayUrlService bankWorldpayUrlService,
                                         final SiteConfigService siteConfigService,
                                         final Converter<AddressModel, Address> worldpayAddressConverter,
                                         final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy,
                                         final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService,
                                         final CustomerEmailResolutionService customerEmailResolutionService) {
        this.bankWorldpayUrlService = bankWorldpayUrlService;
        this.siteConfigService = siteConfigService;
        this.worldpayAddressConverter = worldpayAddressConverter;
        this.worldpayDeliveryAddressStrategy = worldpayDeliveryAddressStrategy;
        this.worldpayDynamicInteractionResolverService = worldpayDynamicInteractionResolverService;
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        return new Session(worldpayAdditionalInfo.getCustomerIPAddress(), worldpayAdditionalInfo.getSessionId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Browser createBrowser(final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        return new Browser(worldpayAdditionalInfo.getAcceptHeader(), worldpayAdditionalInfo.getUserAgentHeader(), worldpayAdditionalInfo.getDeviceType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shopper createShopper(final String customerEmail, final Session session, final Browser browser) {
        return new Shopper(customerEmail, null, browser, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shopper createAuthenticatedShopper(final String customerEmail, final String authenticatedShopperID, final Session session, final Browser browser) {
        if (isMerchantTokenEnabled()) {
            return new Shopper(customerEmail, null, browser, session);
        }
        return new Shopper(customerEmail, authenticatedShopperID, browser, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason) {
        if (isMerchantTokenEnabled()) {
            return createMerchantTokenRequest(tokenEventReference, tokenReason);
        }
        return createShopperTokenRequest(tokenEventReference, tokenReason);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequestForDeletion(final String tokenEventReference, final String tokenReason, final String authenticatedShopperId) {
        if (StringUtils.isBlank(authenticatedShopperId)) {
            return createMerchantTokenRequest(tokenEventReference, tokenReason);
        }
        return createShopperTokenRequest(tokenEventReference, tokenReason);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateTokenServiceRequest createTokenServiceRequest(final MerchantInfo merchantInfo, final String authenticatedShopperId,
                                                               final Payment csePayment, final TokenRequest tokenRequest) {
        if (isMerchantTokenEnabled()) {
            return CreateTokenServiceRequest.createTokenRequestForMerchantToken(merchantInfo, csePayment, tokenRequest);
        }

        return CreateTokenServiceRequest.createTokenRequestForShopperToken(merchantInfo, authenticatedShopperId, csePayment, tokenRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createBankPayment(final String paymentMethod, final String shopperBankCode) throws WorldpayConfigurationException {
        if (IDEAL.getMethodCode().equals(paymentMethod)) {
            return PaymentBuilder.createIDEALSSL(shopperBankCode, bankWorldpayUrlService.getFullSuccessURL(), bankWorldpayUrlService.getFullFailureURL(), bankWorldpayUrlService.getFullCancelURL());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token createToken(final String subscriptionId, final String securityCode) {
        return PaymentBuilder.createToken(subscriptionId, securityCode, isMerchantTokenEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateTokenServiceRequest createUpdateTokenServiceRequest(final MerchantInfo merchantInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                     final TokenRequest tokenRequest, final String paymentTokenID,
                                                                     final CardDetails cardDetails) {
        if (isMerchantTokenEnabled()) {
            return UpdateTokenServiceRequest.updateTokenRequestWithMerchantScope(merchantInfo, paymentTokenID, tokenRequest, cardDetails);
        }
        return UpdateTokenServiceRequest.updateTokenRequestWithShopperScope(merchantInfo, worldpayAdditionalInfoData.getAuthenticatedShopperId(), paymentTokenID, tokenRequest, cardDetails);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Additional3DSData createAdditional3DSData(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return Optional.ofNullable(worldpayAdditionalInfoData.getAdditional3DS2()).map(additional3DS2Info -> {
            final Additional3DSData additional3DSData = new Additional3DSData();
            additional3DSData.setDfReferenceId(additional3DS2Info.getDfReferenceId());

            final ChallengeWindowSizeEnum challengeWindowSizeEnum = ChallengeWindowSizeEnum.getEnum(additional3DS2Info.getChallengeWindowSize());
            additional3DSData.setChallengeWindowSize(challengeWindowSizeEnum);

            if (additional3DS2Info.getChallengePreference() != null) {
                final ChallengePreferenceEnum challengePreferenceEnum = ChallengePreferenceEnum.getEnum(additional3DS2Info.getChallengePreference());
                additional3DSData.setChallengePreference(challengePreferenceEnum);
            }

            return additional3DSData;

        }).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredCredentials createStoredCredentials(final Usage usage, final MerchantInitiatedReason merchantInitiatedReason, final String transactionIdentifier) {
        Preconditions.checkArgument(Objects.nonNull(usage), "Usage must be specified when creating a storedCredentials");
        return new StoredCredentials(merchantInitiatedReason, transactionIdentifier, usage);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CardDetails createCardDetails(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final AddressModel paymentAddress) {
        final CardDetails cardDetails = new CardDetails();
        final Date expiryDate = new Date(cseAdditionalAuthInfo.getExpiryMonth(), cseAdditionalAuthInfo.getExpiryYear());
        cardDetails.setExpiryDate(expiryDate);
        cardDetails.setCardHolderName(cseAdditionalAuthInfo.getCardHolderName());
        Optional.ofNullable(paymentAddress)
            .map(worldpayAddressConverter::convert)
            .ifPresent(cardDetails::setCardAddress);
        return cardDetails;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Address getAddressFromCart(final AbstractOrderModel abstractOrder, final boolean isDeliveryAddress) {
        final AddressModel address = isDeliveryAddress ? worldpayDeliveryAddressStrategy.getDeliveryAddress(abstractOrder) : abstractOrder.getPaymentAddress();
        return worldpayAddressConverter.convert(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Address getBillingAddress(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) {
        final AddressModel deliveryAddressModel = cartModel.getDeliveryAddress();
        if (deliveryAddressModel != null && Boolean.TRUE.equals(additionalAuthInfo.getUsingShippingAsBilling())) {
            return worldpayAddressConverter.convert(deliveryAddressModel);
        } else {
            if (cartModel.getPaymentAddress() != null) {
                return worldpayAddressConverter.convert(cartModel.getPaymentAddress());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DynamicInteractionType getDynamicInteractionType(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmailForCustomer(final CustomerModel customerModel) {
        return customerEmailResolutionService.getEmailForCustomer(customerModel);
    }

    private boolean isMerchantTokenEnabled() {
        return siteConfigService.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false);
    }

    private TokenRequest createMerchantTokenRequest(final String tokenEventReference, final String tokenReason) {
        return new TokenRequest(tokenEventReference, tokenReason, true);
    }

    private TokenRequest createShopperTokenRequest(final String tokenEventReference, final String tokenReason) {
        return new TokenRequest(tokenEventReference, tokenReason, false);
    }
}
