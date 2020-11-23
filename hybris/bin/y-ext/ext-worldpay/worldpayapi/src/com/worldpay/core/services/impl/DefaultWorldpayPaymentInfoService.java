package com.worldpay.core.services.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.model.ApplePayPaymentInfoModel;
import com.worldpay.model.GooglePayPaymentInfoModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.SchemeResponse;
import com.worldpay.service.model.payment.Card;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenDetails;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static de.hybris.platform.core.enums.CreditCardType.CARD;
import static de.hybris.platform.core.enums.CreditCardType.SIMPLE_CLASSNAME;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation fo the {@link WorldpayPaymentInfoService}.
 * <p>
 * The implementation is responsible for creating the {@link WorldpayAPMPaymentInfoModel} or the {@link CreditCardPaymentInfoModel}
 * and for calculating the timeout date for APMs.
 * </p>
 */
public class DefaultWorldpayPaymentInfoService implements WorldpayPaymentInfoService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayPaymentInfoService.class);
    private static final String WORLDPAY_CREDIT_CARD_MAPPINGS = "worldpay.creditCard.mappings.";
    private static final String CART_MODEL_CANNOT_BE_NULL = "CartModel cannot be null";

    protected final ModelService modelService;
    protected final EnumerationService enumerationService;
    protected final APMConfigurationLookupService apmConfigurationLookupService;
    protected final ConfigurationService configurationService;
    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final CommerceCheckoutService commerceCheckoutService;
    protected final AddressService addressService;

    public DefaultWorldpayPaymentInfoService(final ModelService modelService,
                                             final EnumerationService enumerationService,
                                             final APMConfigurationLookupService apmConfigurationLookupService,
                                             final ConfigurationService configurationService,
                                             final WorldpayMerchantInfoService worldpayMerchantInfoService,
                                             final CommerceCheckoutService commerceCheckoutService,
                                             final AddressService addressService) {
        this.modelService = modelService;
        this.enumerationService = enumerationService;
        this.apmConfigurationLookupService = apmConfigurationLookupService;
        this.configurationService = configurationService;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.commerceCheckoutService = commerceCheckoutService;
        this.addressService = addressService;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#savePaymentType(PaymentTransactionModel, String)
     */
    @Override
    public void savePaymentType(final PaymentTransactionModel transactionModel, final String methodCode) {
        final PaymentInfoModel transactionPaymentInfo = transactionModel.getInfo();
        final PaymentInfoModel orderPaymentInfo = transactionModel.getOrder().getPaymentInfo();
        transactionPaymentInfo.setPaymentType(methodCode);
        orderPaymentInfo.setPaymentType(methodCode);
        modelService.saveAll(orderPaymentInfo, transactionPaymentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final OrderNotificationMessage orderNotificationMessage) throws WorldpayConfigurationException {
        final PaymentInfoModel paymentInfo = getPaymentInfoModel(paymentTransactionModel, orderNotificationMessage);
        attachPaymentInfoModel(paymentTransactionModel, orderModel, paymentInfo);
        updateStoredCredentialsOnPaymentInfo(paymentInfo, orderNotificationMessage);
        removePaymentInfoWhenCreatingNewOneFromNotification(orderModel);
        modelService.save(paymentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAndAttachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel) {
        updatePaymentInfo(orderModel, paymentInfoModel);
        attachPaymentInfoModel(paymentTransactionModel, orderModel, paymentInfoModel);
        modelService.save(paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createWorldpayApmPaymentInfo(PaymentTransactionModel)
     */
    @Override
    public WorldpayAPMPaymentInfoModel createWorldpayApmPaymentInfo(final PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException {
        final WorldpayAPMPaymentInfoModel apmPaymentInfoModel = modelService.clone(paymentTransactionModel.getInfo(), WorldpayAPMPaymentInfoModel.class);
        final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = apmConfigurationLookupService.getAPMConfigurationForCode(apmPaymentInfoModel.getPaymentType());
        apmPaymentInfoModel.setApmConfiguration(worldpayAPMConfigurationModel);
        apmPaymentInfoModel.setSaved(false);
        apmPaymentInfoModel.setMerchantId(worldpayMerchantInfoService.getMerchantInfoFromTransaction(paymentTransactionModel).getMerchantCode());
        apmPaymentInfoModel.setTimeoutDate(calculateAPMTimeoutDate(paymentTransactionModel.getCreationtime(), apmPaymentInfoModel.getApmConfiguration()));
        return apmPaymentInfoModel;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createPaymentInfo(CartModel)
     */
    @Override
    public PaymentInfoModel createPaymentInfo(final CartModel cartModel) {
        validateParameterNotNull(cartModel, CART_MODEL_CANNOT_BE_NULL);
        final PaymentInfoModel paymentInfoModel = modelService.create(PaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        return updatePaymentInfo(cartModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentInfoModel createPaymentInfoGooglePay(final CartModel cartModel, final GooglePayAdditionalAuthInfo googleAuthInfo,
                                                       final String paymentTokenId, final String obfuscatedCardNumber) {
        validateParameterNotNull(cartModel, CART_MODEL_CANNOT_BE_NULL);
        validateParameterNotNull(googleAuthInfo, "GooglePayAdditionalAuthInfo cannot be null");

        if (StringUtils.isNotEmpty(paymentTokenId)) {
            final WorldpayAPMPaymentInfoModel foundWorldpayAPMPaymentInfoModel = findMatchingTokenisedAPM(cartModel.getUser(), paymentTokenId);
            if (foundWorldpayAPMPaymentInfoModel != null) {
                cartModel.setPaymentInfo(foundWorldpayAPMPaymentInfoModel);
                modelService.save(cartModel);
                return foundWorldpayAPMPaymentInfoModel;
            }
        }

        final GooglePayPaymentInfoModel paymentInfoModel = createGooglePaymentInfoModel(cartModel, googleAuthInfo, paymentTokenId, obfuscatedCardNumber);

        return updatePaymentInfo(cartModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentInfoModel createPaymentInfoApplePay(final CartModel cartModel, final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) {
        validateParameterNotNull(cartModel, CART_MODEL_CANNOT_BE_NULL);
        validateParameterNotNull(applePayAdditionalAuthInfo, "ApplePayAdditionalAuthInfo cannot be null");
        final ApplePayPaymentInfoModel paymentInfoModel = modelService.create(ApplePayPaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfoModel.setVersion(applePayAdditionalAuthInfo.getVersion());
        paymentInfoModel.setTransactionId(applePayAdditionalAuthInfo.getHeader().getTransactionId());
        cartModel.setPaymentInfo(paymentInfoModel);
        updatePaymentInfo(cartModel, paymentInfoModel);
        modelService.saveAll(cartModel,paymentInfoModel);
        return paymentInfoModel;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createCreditCardPaymentInfo(AbstractOrderModel, TokenReply, boolean, String)
     */
    @Override
    public CreditCardPaymentInfoModel createCreditCardPaymentInfo(final AbstractOrderModel abstractOrderModel, final TokenReply tokenReply, final boolean saveCard, final String merchantId) {
        validateParameterNotNull(abstractOrderModel, CART_MODEL_CANNOT_BE_NULL);

        final Optional<CreditCardPaymentInfoModel> customerSavedCard = Optional.ofNullable(tokenReply)
            .map(existentTokenReply -> getCreditCardPaymentInfoModelBasedOnTokenReply(abstractOrderModel, existentTokenReply, saveCard));

        if (customerSavedCard.isPresent()) {
            return customerSavedCard.get();
        }

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = modelService.create(CreditCardPaymentInfoModel.class);
        creditCardPaymentInfoModel.setCode(generateCcPaymentInfoCode(abstractOrderModel));
        creditCardPaymentInfoModel.setWorldpayOrderCode(abstractOrderModel.getWorldpayOrderCode());
        creditCardPaymentInfoModel.setUser(abstractOrderModel.getUser());
        creditCardPaymentInfoModel.setMerchantId(merchantId);
        Optional.ofNullable(tokenReply)
            .map(TokenReply::getPaymentInstrument)
            .map(Card::getBin)
            .ifPresent(creditCardPaymentInfoModel::setBin);
        setPaymentTypeAndCreditCardType(creditCardPaymentInfoModel, tokenReply.getPaymentInstrument());
        updateCreditCardModel(creditCardPaymentInfoModel, tokenReply, saveCard);
        return creditCardPaymentInfoModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final PaymentReply paymentReply) {
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);
        modelService.save(creditCardPaymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CreditCardPaymentInfoModel> updateCreditCardPaymentInfo(final CartModel cartModel, final UpdateTokenServiceRequest updateTokenServiceRequest, final Boolean saveCard) {
        final String paymentTokenId = updateTokenServiceRequest.getUpdateTokenRequest().getPaymentTokenId();

        final CreditCardPaymentInfoModel matchingTokenisedCard = findMatchingTokenizedCard(cartModel.getUser(), paymentTokenId);
        if (matchingTokenisedCard != null) {
            final CardDetails cardDetails = updateTokenServiceRequest.getUpdateTokenRequest().getCardDetails();
            matchingTokenisedCard.setCcOwner(cardDetails.getCardHolderName());
            matchingTokenisedCard.setValidToMonth(cardDetails.getExpiryDate().getMonth());
            matchingTokenisedCard.setValidToYear(cardDetails.getExpiryDate().getYear());
            if (!matchingTokenisedCard.isSaved()) {
                matchingTokenisedCard.setSaved(saveCard);
            }
            modelService.save(matchingTokenisedCard);
            return Optional.of(matchingTokenisedCard);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionIdentifierOnPaymentInfo(final PaymentInfoModel paymentInfoModel, final String transactionIdentifier) {
        paymentInfoModel.setTransactionIdentifier(transactionIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPaymentInfoModelOnCart(final CartModel cartModel, final boolean isSaved) {
        final PaymentInfoModel paymentInfoModel = createPaymentInfo(cartModel);
        paymentInfoModel.setSaved(isSaved);
        cartModel.setPaymentInfo(paymentInfoModel);
        getOptionalPaymentTransactionFromCart(cartModel)
            .ifPresent(paymentTransactionModel -> savePaymentInfoOnPaymentTransaction(paymentInfoModel, paymentTransactionModel));
        modelService.saveAll(paymentInfoModel, cartModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentInfoOnCart(final CartModel cartModel, final CreditCardPaymentInfoModel creditCardPaymentInfo) {
        Optional.ofNullable(creditCardPaymentInfo).ifPresent(creditCardPaymentInfoModel -> {
            final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
            commerceCheckoutParameter.setCart(cartModel);
            commerceCheckoutParameter.setPaymentInfo(creditCardPaymentInfoModel);
            commerceCheckoutService.setPaymentInfo(commerceCheckoutParameter);
        });
    }

    /**
     * Workaround: Extra address created when an order is placed
     * Potential bug in class: DefaultCommercePlaceOrderStrategy
     * Method: public CommerceOrderResult placeOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {...}
     * Logic: if(cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null) {...}
     *
     * @param cartModel        holding the source address
     * @param paymentInfoModel holding the address owner
     * @return the cloned address model
     */
    public AddressModel cloneAndSetBillingAddressFromCart(final CartModel cartModel, final PaymentInfoModel paymentInfoModel) {
        final AddressModel paymentAddress = cartModel.getPaymentAddress();
        validateParameterNotNull(paymentAddress, "Payment Address cannot be null.");
        final AddressModel clonedAddress = addressService.cloneAddressForOwner(paymentAddress, paymentInfoModel);
        clonedAddress.setBillingAddress(true);
        clonedAddress.setShippingAddress(false);
        clonedAddress.setOwner(paymentInfoModel);
        paymentInfoModel.setBillingAddress(clonedAddress);
        return clonedAddress;
    }
    private void savePaymentInfoOnPaymentTransaction(final PaymentInfoModel paymentInfoModel, final PaymentTransactionModel paymentTransactionModel) {
        paymentTransactionModel.setInfo(paymentInfoModel);
        modelService.save(paymentTransactionModel);
    }

    private Optional<PaymentTransactionModel> getOptionalPaymentTransactionFromCart(final CartModel cartModel) {
        final String worldpayOrderCode = cartModel.getWorldpayOrderCode();
        return cartModel.getPaymentTransactions().stream()
            .filter(paymentTransactionModel ->
                paymentTransactionModel.getCode().equals(worldpayOrderCode))
            .findAny();
    }

    protected CreditCardPaymentInfoModel getCreditCardPaymentInfoModelBasedOnTokenReply(final AbstractOrderModel abstractOrderModel, final TokenReply tokenReply, final boolean saveCard) {
        final TokenEvent tokenEvent = TokenEvent.valueOf(tokenReply.getTokenDetails().getTokenEvent().toUpperCase());
        if (tokenEvent.equals(TokenEvent.MATCH)) {
            final CreditCardPaymentInfoModel customerSavedCard = findMatchingTokenizedCard(abstractOrderModel.getUser(), tokenReply.getTokenDetails().getPaymentTokenID());
            if (customerSavedCard != null) {
                if (!customerSavedCard.isSaved()) {
                    customerSavedCard.setSaved(saveCard);
                    modelService.save(customerSavedCard);
                }
                abstractOrderModel.setPaymentInfo(customerSavedCard);
                modelService.save(abstractOrderModel);
                return customerSavedCard;
            }
        }
        return null;
    }

    protected void updateStoredCredentialsOnPaymentInfo(final PaymentInfoModel paymentInfoModel, final OrderNotificationMessage orderNotificationMessage) {
        Optional.ofNullable(orderNotificationMessage.getPaymentReply().getSchemeResponse())
            .ifPresent(schemeResponse -> setTransactionIdentifier(paymentInfoModel, schemeResponse));
    }

    private void setTransactionIdentifier(final PaymentInfoModel paymentInfoModel, final SchemeResponse schemeResponse) {
        paymentInfoModel.setTransactionIdentifier(schemeResponse.getTransactionIdentifier());
    }

    protected void removePaymentInfoWhenCreatingNewOneFromNotification(final AbstractOrderModel orderModel) {
        orderModel.getUser().getPaymentInfos().stream()
            .filter(paymentInfoModel -> paymentInfoModel.getWorldpayOrderCode().equals(orderModel.getWorldpayOrderCode()))
            .filter(paymentInfoModel -> paymentInfoModel.getClass().equals(PaymentInfoModel.class))
            .findAny().ifPresent(modelService::remove);
    }

    protected void setPaymentTypeAndCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final Card paymentInstrument) {
        if (paymentInstrument != null && paymentInstrument.getPaymentType() != null) {
            doSetCreditCardTypeAndPaymentType(creditCardPaymentInfoModel, paymentInstrument.getPaymentType().getMethodCode());
        } else {
            doSetCreditCardTypeAndPaymentType(creditCardPaymentInfoModel, PaymentType.CARD_SSL.getMethodCode());
        }
    }

    protected PaymentInfoModel getPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel,
                                                   final OrderNotificationMessage orderNotificationMessage)
        throws WorldpayConfigurationException {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();
        savePaymentType(paymentTransactionModel, paymentReply.getPaymentMethodCode());
        final PaymentInfoModel paymentTransactionModelInfo = paymentTransactionModel.getInfo();
        if (paymentTransactionModelInfo.getIsApm()) {
            final TokenReply tokenReply = orderNotificationMessage.getTokenReply();
            if (tokenReply != null && orderNotificationMessage.getPaymentReply().getPaymentMethodCode().equalsIgnoreCase(PaymentType.PAYPAL.getMethodCode())) {
                return createPaypalTokenisedPaymentInfo(paymentTransactionModel, orderNotificationMessage, tokenReply);
            }
            return createWorldpayApmPaymentInfo(paymentTransactionModel);
        }
        return createCreditCardPaymentInfo(paymentTransactionModel, orderNotificationMessage);
    }

    protected WorldpayAPMPaymentInfoModel createPaypalTokenisedPaymentInfo(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage, final TokenReply tokenReply) throws WorldpayConfigurationException {
        final String paymentTokenID = orderNotificationMessage.getTokenReply().getTokenDetails().getPaymentTokenID();
        final WorldpayAPMPaymentInfoModel foundWorldpayAPMPaymentInfoModel = findMatchingTokenisedAPM(paymentTransactionModel.getOrder().getUser(), paymentTokenID);
        if (foundWorldpayAPMPaymentInfoModel == null) {
            final WorldpayAPMPaymentInfoModel newWorldpayAPMPaymentInfo = createWorldpayApmPaymentInfo(paymentTransactionModel);
            newWorldpayAPMPaymentInfo.setAuthenticatedShopperID(tokenReply.getAuthenticatedShopperID());
            newWorldpayAPMPaymentInfo.setEventReference(tokenReply.getTokenDetails().getTokenEventReference());
            final TokenDetails tokenDetails = orderNotificationMessage.getTokenReply().getTokenDetails();
            newWorldpayAPMPaymentInfo.setSubscriptionId(tokenDetails.getPaymentTokenID());
            newWorldpayAPMPaymentInfo.setSaved(true);
            newWorldpayAPMPaymentInfo.setBillingAddress(paymentTransactionModel.getOrder().getPaymentAddress());
            final LocalDate dt = getDateTime(tokenDetails.getPaymentTokenExpiry());
            newWorldpayAPMPaymentInfo.setExpiryDate(java.util.Date.from(dt.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            return newWorldpayAPMPaymentInfo;
        }

        return foundWorldpayAPMPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel findMatchingTokenizedCard(final UserModel userModel, final String paymentTokenId) {
        return userModel.getPaymentInfos().stream()
            .filter(CreditCardPaymentInfoModel.class::isInstance)
            .map(CreditCardPaymentInfoModel.class::cast)
            .filter(card -> paymentTokenId.equals(card.getSubscriptionId()))
            .findAny()
            .orElse(null);
    }

    protected WorldpayAPMPaymentInfoModel findMatchingTokenisedAPM(final UserModel userModel, final String paymentTokenId) {
        return userModel.getPaymentInfos().stream()
            .filter(WorldpayAPMPaymentInfoModel.class::isInstance)
            .map(WorldpayAPMPaymentInfoModel.class::cast)
            .filter(apmPaymentInfoModel -> Boolean.TRUE.equals(apmPaymentInfoModel.isSaved()))
            .filter(apmPaymentInfoModel -> paymentTokenId.equals(apmPaymentInfoModel.getSubscriptionId()))
            .findAny()
            .orElse(null);
    }

    /**
     * This methods creates a CreditCardPaymentInfo with the information received from Worldpay.
     * If the response contains a token there are two cases to consider:
     * - The customer used the card details of an already tokenised card but not from the saved cards functionality:
     * -- The tokenEvent will contain the value "MATCH" or "CONFLICT" and the method searches for the CreditCardPaymentInfo that has that subscriptionId
     * -- If the card is not found in the customer's profile it will be created and attached to the customer.
     * - The customer used a non tokenised card:
     * -- The tokenEvent will contain the value "NEW" and a new CreditCardPaymentInfo will be created and associated to the customer's profile
     *
     * @param paymentTransactionModel  the {@PaymentTransactionModel} that belongs to the order
     * @param orderNotificationMessage the {@OrderNotificationMessage} received from Worldpay in the Redirect/HOP flow
     * @return
     */
    protected CreditCardPaymentInfoModel createCreditCardPaymentInfo(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();

        final TokenReply tokenReply = orderNotificationMessage.getTokenReply();
        if (tokenReply != null) {
            return getCreditCardWithTokenInformation(paymentTransactionModel, paymentReply, tokenReply);
        } else {
            final PaymentInfoModel paymentInfoModel = paymentTransactionModel.getInfo();
            if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
                final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
                if (StringUtils.isNotBlank(creditCardPaymentInfoModel.getSubscriptionId())) {
                    return creditCardPaymentInfoModel;
                }
            }
            return cloneCreditCardWithPaymentInformation(paymentTransactionModel, paymentReply);
        }
    }

    protected CreditCardPaymentInfoModel getCreditCardWithTokenInformation(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply, final TokenReply tokenReply) {
        final TokenEvent tokenEvent = TokenEvent.valueOf(tokenReply.getTokenDetails().getTokenEvent().toUpperCase());
        if (shouldSearchForExistingToken(tokenEvent)) {
            final String paymentTokenID = tokenReply.getTokenDetails().getPaymentTokenID();
            final CreditCardPaymentInfoModel customerSavedCard = findMatchingTokenizedCard(paymentTransactionModel.getOrder().getUser(), paymentTokenID);
            if (customerSavedCard != null) {
                return customerSavedCard;
            }
        }
        return cloneAndSaveCreditCardWithTokenInformation(paymentTransactionModel, tokenReply, paymentReply);
    }

    protected boolean shouldSearchForExistingToken(final TokenEvent tokenEvent) {
        return tokenEvent.equals(TokenEvent.MATCH) || tokenEvent.equals(TokenEvent.CONFLICT);
    }

    protected CreditCardPaymentInfoModel cloneCreditCardWithPaymentInformation(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = modelService.clone(paymentTransactionModel.getInfo(), CreditCardPaymentInfoModel.class);
        creditCardPaymentInfoModel.setOriginal(null);
        creditCardPaymentInfoModel.setDuplicate(false);
        final Card card = paymentReply.getCardDetails();
        final Date expiryDate = card.getExpiryDate();

        setCardInformation(creditCardPaymentInfoModel, card.getCardNumber(), expiryDate.getMonth(), expiryDate.getYear(), card.getCardHolderName());
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);

        return creditCardPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel cloneAndSaveCreditCardWithTokenInformation(final PaymentTransactionModel paymentTransactionModel,
                                                                                    final TokenReply tokenReply,
                                                                                    final PaymentReply paymentReply) {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = modelService.clone(paymentTransactionModel.getInfo(), CreditCardPaymentInfoModel.class);
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);
        return updateCreditCardModel(creditCardPaymentInfoModel, tokenReply, true);
    }

    protected String generateCcPaymentInfoCode(final AbstractOrderModel cartModel) {
        return cartModel.getCode() + "_" + UUID.randomUUID();
    }

    protected LocalDate getDateTime(final Date paymentTokenExpiryDate) {
        return LocalDate.of(Integer.parseInt(paymentTokenExpiryDate.getYear()), Integer.parseInt(paymentTokenExpiryDate.getMonth()), Integer.parseInt(paymentTokenExpiryDate.getDayOfMonth()));
    }

    protected java.util.Date calculateAPMTimeoutDate(final java.util.Date creationTime, final WorldpayAPMConfigurationModel apmConfiguration) {
        final Integer autoCancelPendingTimeoutInMinutes = apmConfiguration.getAutoCancelPendingTimeoutInMinutes();
        if (autoCancelPendingTimeoutInMinutes == null) {
            LOG.warn(MessageFormat.format("No auto cancel pending timeout found for APM configuration with code [{0}]", apmConfiguration.getCode()));
            return null;
        }
        return addMinutesToDate(creationTime, autoCancelPendingTimeoutInMinutes);
    }

    protected java.util.Date addMinutesToDate(final java.util.Date creationTime, final Integer autoCancelPendingTimeoutInMinutes) {
        return DateUtils.addMinutes(creationTime, autoCancelPendingTimeoutInMinutes);
    }

    /**
     * Fetch the Hybris credit card type value for the corresponding Worldpay credit card type value.
     *
     * @param methodCode Worldpay Credit Card Type.
     * @return Corresponding Hybris credit card type.
     */
    protected String getHybrisCCTypeForWPCCType(final String methodCode) {
        return configurationService.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + methodCode);
    }

    private void doSetCreditCardTypeAndPaymentType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final String methodCode) {
        final String creditCardTypeValue = getHybrisCCTypeForWPCCType(methodCode);
        final CreditCardType cardType = StringUtils.isNotBlank(creditCardTypeValue) ? enumerationService.getEnumerationValue(SIMPLE_CLASSNAME, creditCardTypeValue) : CARD;
        creditCardPaymentInfoModel.setPaymentType(methodCode);
        creditCardPaymentInfoModel.setType(cardType);
    }

    private void updateCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final PaymentReply paymentReply) {
        doSetCreditCardTypeAndPaymentType(creditCardPaymentInfoModel, paymentReply.getPaymentMethodCode());
    }

    private void attachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel) {
        orderModel.setPaymentInfo(paymentInfoModel);
        paymentTransactionModel.setInfo(paymentInfoModel);
        modelService.saveAll(orderModel, paymentTransactionModel);
    }

    private CreditCardPaymentInfoModel updateCreditCardModel(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final TokenReply tokenReply, final boolean saveCard) {
        creditCardPaymentInfoModel.setSubscriptionId(tokenReply.getTokenDetails().getPaymentTokenID());

        final Card card = tokenReply.getPaymentInstrument();
        final Date date = card.getExpiryDate();
        setCardInformation(creditCardPaymentInfoModel, card.getCardNumber(), date.getMonth(), date.getYear(), card.getCardHolderName());

        creditCardPaymentInfoModel.setSaved(saveCard);
        creditCardPaymentInfoModel.setAuthenticatedShopperID(tokenReply.getAuthenticatedShopperID());
        creditCardPaymentInfoModel.setEventReference(tokenReply.getTokenDetails().getTokenEventReference());
        final LocalDate dt = getDateTime(tokenReply.getTokenDetails().getPaymentTokenExpiry());
        creditCardPaymentInfoModel.setExpiryDate(java.util.Date.from(dt.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return creditCardPaymentInfoModel;
    }

    private void setCardInformation(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final String cardNumber, final String month, final String year, final String cardHolderName) {
        creditCardPaymentInfoModel.setNumber(cardNumber);
        creditCardPaymentInfoModel.setValidToMonth(month);
        creditCardPaymentInfoModel.setValidToYear(year);
        creditCardPaymentInfoModel.setCcOwner(cardHolderName);
    }

    private PaymentInfoModel updatePaymentInfo(final AbstractOrderModel cartModel, final PaymentInfoModel paymentInfoModel) {
        paymentInfoModel.setWorldpayOrderCode(cartModel.getWorldpayOrderCode());
        return paymentInfoModel;
    }

    protected GooglePayPaymentInfoModel createGooglePaymentInfoModel(final CartModel cartModel, final GooglePayAdditionalAuthInfo googleAuthInfo, final String paymentTokenId, final String obfuscatedCardNumber) {
        final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = apmConfigurationLookupService.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode());
        final GooglePayPaymentInfoModel paymentInfoModel = modelService.create(GooglePayPaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfoModel.setProtocolVersion(googleAuthInfo.getProtocolVersion());
        paymentInfoModel.setSignature(googleAuthInfo.getSignature());
        paymentInfoModel.setSignedMessage(googleAuthInfo.getSignedMessage());
        paymentInfoModel.setApmConfiguration(worldpayAPMConfigurationModel);
        paymentInfoModel.setSubscriptionId(paymentTokenId);
        paymentInfoModel.setObfuscatedCardNumber(obfuscatedCardNumber);
        paymentInfoModel.setSaved(googleAuthInfo.getSaveCard());
        cartModel.setPaymentInfo(paymentInfoModel);
        modelService.save(cartModel);
        return paymentInfoModel;
    }

}
