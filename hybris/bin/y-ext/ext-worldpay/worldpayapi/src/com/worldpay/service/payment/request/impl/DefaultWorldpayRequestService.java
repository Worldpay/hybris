package com.worldpay.service.payment.request.impl;

import com.google.common.base.Preconditions;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.hop.WorldpayOrderCodeVerificationService;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;

import static com.worldpay.service.model.payment.PaymentType.IDEAL;

public class DefaultWorldpayRequestService implements WorldpayRequestService {

    private static final String WORLDPAY_MERCHANT_TOKEN_ENABLED = "worldpay.merchant.token.enabled";

    protected final WorldpayUrlService bankWorldpayUrlService;
    protected final SiteConfigService siteConfigService;
    protected final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService;
    protected final WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationService;
    protected final WorldpayCartService worldpayCartService;

    public DefaultWorldpayRequestService(final WorldpayUrlService bankWorldpayUrlService,
                                         final SiteConfigService siteConfigService,
                                         final WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService,
                                         final WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationService,
                                         final WorldpayCartService worldpayCartService) {
        this.bankWorldpayUrlService = bankWorldpayUrlService;
        this.siteConfigService = siteConfigService;
        this.worldpayDynamicInteractionResolverService = worldpayDynamicInteractionResolverService;
        this.worldpayOrderCodeVerificationService = worldpayOrderCodeVerificationService;
        this.worldpayCartService = worldpayCartService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        final Session session = new Session();
        session.setId(worldpayAdditionalInfo.getSessionId());
        session.setShopperIPAddress(worldpayAdditionalInfo.getCustomerIPAddress());
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Browser createBrowser(final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        final Browser browser = new Browser();
        browser.setAcceptHeader(worldpayAdditionalInfo.getAcceptHeader());
        browser.setUserAgentHeader(worldpayAdditionalInfo.getUserAgentHeader());
        browser.setDeviceType(worldpayAdditionalInfo.getDeviceType());
        browser.setJavaEnabled(worldpayAdditionalInfo.getJavaEnabled());
        browser.setJavascriptEnabled(worldpayAdditionalInfo.getJavascriptEnabled());
        browser.setLanguage(worldpayAdditionalInfo.getLanguage());
        browser.setTimeZone(worldpayAdditionalInfo.getTimeZone());
        browser.setColorDepth(worldpayAdditionalInfo.getColorDepth());
        browser.setScreenHeight(worldpayAdditionalInfo.getScreenHeight());
        browser.setScreenWidth(worldpayAdditionalInfo.getScreenWidth());
        return browser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shopper createShopper(final String customerEmail, final Session session, final Browser browser) {
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(customerEmail);
        shopper.setBrowser(browser);
        shopper.setSession(session);

        return shopper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shopper createAuthenticatedShopper(final String customerEmail, final String authenticatedShopperID, final Session session, final Browser browser) {
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(customerEmail);
        shopper.setBrowser(browser);
        shopper.setSession(session);
        shopper.setAuthenticatedShopperID(isMerchantTokenEnabled() ? null : authenticatedShopperID);

        return shopper;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason) {
        return createTokenRequest(tokenEventReference, tokenReason, isMerchantTokenEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequestForDeletion(final String tokenEventReference, final String tokenReason, final String authenticatedShopperId) {
        return createTokenRequest(tokenEventReference, tokenReason, StringUtils.isBlank(authenticatedShopperId));
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
    public Payment createBankPayment(final String worldpayOrderCode, final String paymentMethod, final String shopperBankCode) throws WorldpayConfigurationException {
        if (IDEAL.getMethodCode().equals(paymentMethod)) {

            String encryptedOrderCode;
            try {
                encryptedOrderCode = worldpayOrderCodeVerificationService.getEncryptedOrderCode(worldpayOrderCode);
            } catch (final GeneralSecurityException e) {
                throw new ConversionException(e.getMessage(), e);
            }

            final String successURL = bankWorldpayUrlService.getFullSuccessURL() + "?orderId=" + UriUtils.encode(encryptedOrderCode, StandardCharsets.UTF_8.toString());

            return WorldpayInternalModelTransformerUtil.createAlternativeShopperBankCodePayment(PaymentType.IDEAL, shopperBankCode, successURL, bankWorldpayUrlService.getFullFailureURL(), bankWorldpayUrlService.getFullCancelURL(), null, null);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token createToken(final String subscriptionId, final String securityCode) {
        final Token token = new Token();
        token.setMerchantToken(isMerchantTokenEnabled());
        token.setPaymentTokenID(subscriptionId);
        token.setPaymentType(PaymentType.TOKENSSL.getMethodCode());
        if (securityCode != null) {
            final CardDetails cardDetails = new CardDetails();
            cardDetails.setCvcNumber(securityCode);
            token.setPaymentInstrument(cardDetails);
        }

        return token;
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
            additional3DSData.setChallengeWindowSize(ChallengeWindowSizeEnum.getEnum(additional3DS2Info.getChallengeWindowSize()).toString());
            Optional.ofNullable(additional3DS2Info.getChallengePreference())
                .map(ChallengePreferenceEnum::getEnum)
                .map(ChallengePreferenceEnum::toString).
                ifPresent(additional3DSData::setChallengePreference);

            return additional3DSData;

        }).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredCredentials createStoredCredentials(final Usage usage, final MerchantInitiatedReason merchantInitiatedReason, final String transactionIdentifier) {
        Preconditions.checkArgument(Objects.nonNull(usage), "Usage must be specified when creating a storedCredentials");
        final StoredCredentials storedCredentials = new StoredCredentials();
        storedCredentials.setMerchantInitiatedReason(merchantInitiatedReason);
        storedCredentials.setSchemeTransactionIdentifier(transactionIdentifier);
        storedCredentials.setUsage(usage);
        return storedCredentials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardDetails createCardDetails(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final AddressModel paymentAddress) {
        final CardDetails cardDetails = new CardDetails();
        final Date expiryDate = new Date();
        expiryDate.setMonth(cseAdditionalAuthInfo.getExpiryMonth());
        expiryDate.setYear(cseAdditionalAuthInfo.getExpiryYear());
        cardDetails.setExpiryDate(expiryDate);
        cardDetails.setCardHolderName(cseAdditionalAuthInfo.getCardHolderName());
        Optional.ofNullable(paymentAddress)
            .map(worldpayCartService::convertAddressModelToAddress)
            .ifPresent(cardDetails::setCardAddress);
        return cardDetails;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DynamicInteractionType getDynamicInteractionType(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);
    }

    @Override
    public boolean isMerchantTokenEnabled() {
        return siteConfigService.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false);
    }

    @Override
    public TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason, final boolean merchant) {
        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenEventReference(tokenEventReference);
        tokenRequest.setTokenReason(tokenReason);
        tokenRequest.setMerchantToken(merchant);
        return tokenRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlternativeShippingAddress createAlternativeShippingAddress() {
        //add here your own implementation
        return null;
    }
}
