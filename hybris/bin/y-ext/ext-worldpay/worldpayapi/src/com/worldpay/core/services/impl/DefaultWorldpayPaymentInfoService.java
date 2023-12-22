package com.worldpay.core.services.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.data.Date;
import com.worldpay.data.payment.Card;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.TokenDetails;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.model.ApplePayPaymentInfoModel;
import com.worldpay.model.GooglePayPaymentInfoModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.payment.PaymentType;
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
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.user.AddressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayPaymentInfoService.class);
    private static final String WORLDPAY_CREDIT_CARD_MAPPINGS = "worldpay.creditCard.mappings.";
    private static final String CART_MODEL_CANNOT_BE_NULL = "CartModel cannot be null";
    private static final String PAYMENT_METHOD_PARAM = "paymentMethod";

    protected final APMConfigurationLookupService apmConfigurationLookupService;
    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final CommerceCheckoutService commerceCheckoutService;
    protected final AddressService addressService;
    protected final WorldpayServicesWrapper wrapper;

    public DefaultWorldpayPaymentInfoService(final APMConfigurationLookupService apmConfigurationLookupService,
                                             final WorldpayMerchantInfoService worldpayMerchantInfoService,
                                             final CommerceCheckoutService commerceCheckoutService,
                                             final AddressService addressService,
                                             final WorldpayServicesWrapper wrapper) {

        this.apmConfigurationLookupService = apmConfigurationLookupService;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.commerceCheckoutService = commerceCheckoutService;
        this.addressService = addressService;
        this.wrapper = wrapper;
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
        wrapper.modelService.saveAll(orderPaymentInfo, transactionPaymentInfo);
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAndAttachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel) {
        updatePaymentInfo(orderModel, paymentInfoModel);
        attachPaymentInfoModel(paymentTransactionModel, orderModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createWorldpayApmPaymentInfo(PaymentTransactionModel)
     */
    @Override
    public WorldpayAPMPaymentInfoModel createWorldpayApmPaymentInfo(final PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException {
        final WorldpayAPMPaymentInfoModel apmPaymentInfoModel = wrapper.modelService.clone(paymentTransactionModel.getInfo(), WorldpayAPMPaymentInfoModel.class);
        final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = apmConfigurationLookupService.getAPMConfigurationForCode(apmPaymentInfoModel.getPaymentType());
        apmPaymentInfoModel.setApmConfiguration(worldpayAPMConfigurationModel);
        apmPaymentInfoModel.setSaved(false);
        apmPaymentInfoModel.setMerchantId(worldpayMerchantInfoService.getMerchantInfoFromTransaction(paymentTransactionModel).getMerchantCode());
        apmPaymentInfoModel.setTimeoutDate(calculateAPMTimeoutDate(paymentTransactionModel.getCreationtime(), apmPaymentInfoModel.getApmConfiguration()));
        wrapper.modelService.save(apmPaymentInfoModel);
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
        final PaymentInfoModel paymentInfoModel = wrapper.modelService.create(PaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfoModel.setPaymentType(getPaymentMethodFromSession());
        return updatePaymentInfo(cartModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentInfoModel createPaymentInfoGooglePay(final CartModel cartModel, final GooglePayAdditionalAuthInfo googleAuthInfo,
                                                       final String paymentTokenId, final Card cardResponse) {
        validateParameterNotNull(cartModel, CART_MODEL_CANNOT_BE_NULL);
        validateParameterNotNull(googleAuthInfo, "GooglePayAdditionalAuthInfo cannot be null");

        if (StringUtils.isNotEmpty(paymentTokenId)) {
            final WorldpayAPMPaymentInfoModel foundWorldpayAPMPaymentInfoModel = findMatchingTokenisedAPM(cartModel.getUser(), paymentTokenId);
            if (foundWorldpayAPMPaymentInfoModel != null) {
                cartModel.setPaymentInfo(foundWorldpayAPMPaymentInfoModel);
                wrapper.modelService.save(cartModel);
                return foundWorldpayAPMPaymentInfoModel;
            }
        }

        final GooglePayPaymentInfoModel paymentInfoModel = createGooglePaymentInfoModel(cartModel, googleAuthInfo, paymentTokenId, cardResponse);

        return updatePaymentInfo(cartModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentInfoModel createPaymentInfoApplePay(final CartModel cartModel, final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) {
        validateParameterNotNull(cartModel, CART_MODEL_CANNOT_BE_NULL);
        validateParameterNotNull(applePayAdditionalAuthInfo, "ApplePayAdditionalAuthInfo cannot be null");
        final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = apmConfigurationLookupService.getAPMConfigurationForCode(PaymentType.APPLEPAYSSL.getMethodCode());
        final ApplePayPaymentInfoModel paymentInfoModel = wrapper.modelService.create(ApplePayPaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfoModel.setVersion(applePayAdditionalAuthInfo.getVersion());
        paymentInfoModel.setTransactionId(applePayAdditionalAuthInfo.getHeader().getTransactionId());
        paymentInfoModel.setApmConfiguration(worldpayAPMConfigurationModel);
        cartModel.setPaymentInfo(paymentInfoModel);
        wrapper.modelService.save(cartModel);
        return updatePaymentInfo(cartModel, paymentInfoModel);
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

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = wrapper.modelService.create(CreditCardPaymentInfoModel.class);
        creditCardPaymentInfoModel.setCode(generateCcPaymentInfoCode(abstractOrderModel));
        creditCardPaymentInfoModel.setWorldpayOrderCode(abstractOrderModel.getWorldpayOrderCode());
        creditCardPaymentInfoModel.setUser(abstractOrderModel.getUser());
        creditCardPaymentInfoModel.setMerchantId(merchantId);

        cloneAndSetBillingAddressFromCart((CartModel) abstractOrderModel, creditCardPaymentInfoModel);

        if (Objects.nonNull(tokenReply) && Objects.nonNull(tokenReply.getPaymentInstrument())) {
            creditCardPaymentInfoModel.setBin(Objects.nonNull(tokenReply.getPaymentInstrument().getBin()) ?
                    tokenReply.getPaymentInstrument().getBin() : StringUtils.EMPTY);
            setPaymentTypeAndCreditCardType(creditCardPaymentInfoModel, tokenReply.getPaymentInstrument());
            updateCreditCardModel(creditCardPaymentInfoModel, tokenReply, saveCard);
        }

        return creditCardPaymentInfoModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final PaymentReply paymentReply) {
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);
        wrapper.modelService.save(creditCardPaymentInfoModel);
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
            wrapper.modelService.save(matchingTokenisedCard);
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
        wrapper.modelService.save(paymentInfoModel);
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
        wrapper.modelService.saveAll(paymentInfoModel, cartModel);
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

    @Override
    public PaymentInfoModel createAPMPaymentInfo(final CartModel cartModel, final String apmCode, final String apmName) {
        final WorldpayAPMPaymentInfoModel paymentInfoModel = wrapper.modelService.create(WorldpayAPMPaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfoModel.setBillingAddress(cartModel.getPaymentAddress());
        paymentInfoModel.setPaymentType(apmCode);
        paymentInfoModel.setApmConfiguration(apmConfigurationLookupService.getAPMConfigurationForCode(apmCode));

        if (PaymentType.IDEAL.getMethodCode().equals(apmCode)) {
            cartModel.setShopperBankCode(apmName);
        } else {
            cartModel.setShopperBankCode(null);
        }

        cartModel.setPaymentInfo(paymentInfoModel);
        cartModel.setApmCode(apmCode);
        cartModel.setApmName(apmName);
        wrapper.modelService.save(cartModel);

        return updatePaymentInfo(cartModel, paymentInfoModel);
    }

    private void savePaymentInfoOnPaymentTransaction(final PaymentInfoModel paymentInfoModel, final PaymentTransactionModel paymentTransactionModel) {
        paymentTransactionModel.setInfo(paymentInfoModel);
        wrapper.modelService.save(paymentTransactionModel);
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
                    wrapper.modelService.save(customerSavedCard);
                }
                abstractOrderModel.setPaymentInfo(customerSavedCard);
                wrapper.modelService.save(abstractOrderModel);
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
                .findAny().ifPresent(wrapper.modelService::remove);
    }

    protected void setPaymentTypeAndCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final Card paymentInstrument) {
        if (paymentInstrument != null && paymentInstrument.getPaymentType() != null) {
            doSetCreditCardTypeAndPaymentType(creditCardPaymentInfoModel, paymentInstrument.getPaymentType());
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
        if (Boolean.TRUE.equals(paymentTransactionModelInfo.getIsApm())) {
            final TokenReply tokenReply = orderNotificationMessage.getTokenReply();
            if (tokenReply != null && orderNotificationMessage.getPaymentReply().getPaymentMethodCode().equalsIgnoreCase(PaymentType.PAYPAL.getMethodCode())) {
                return createPaypalTokenisedPaymentInfo(paymentTransactionModel, orderNotificationMessage, tokenReply);
            }
            return createWorldpayApmPaymentInfo(paymentTransactionModel);
        }
        return createAndSaveCreditCard(paymentTransactionModel, orderNotificationMessage);
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
            wrapper.modelService.save(newWorldpayAPMPaymentInfo);
            return newWorldpayAPMPaymentInfo;
        }

        return foundWorldpayAPMPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel findMatchingTokenizedCard(final UserModel userModel, final String paymentTokenId) {

        final List<CreditCardPaymentInfoModel> creditCardList = userModel.getPaymentInfos().stream()
                .filter(CreditCardPaymentInfoModel.class::isInstance)
                .map(CreditCardPaymentInfoModel.class::cast)
                .filter(card -> paymentTokenId.equals(card.getSubscriptionId()))
                .toList();

        return creditCardList.stream()
                .filter(card -> Boolean.TRUE.equals(card.isSaved()))
                .findAny()
                .orElse(getCreditCardPaymentInfoModel(creditCardList));
    }

    private CreditCardPaymentInfoModel getCreditCardPaymentInfoModel(final List<CreditCardPaymentInfoModel> creditCardList) {
         if (CollectionUtils.isNotEmpty(creditCardList)) {
            return creditCardList.stream().findFirst().get();
        } else {
            return null;
        }
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
    protected CreditCardPaymentInfoModel createAndSaveCreditCard(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
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
            return cloneAndSaveCreditCardWithPaymentInformation(paymentTransactionModel, paymentReply);
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

    protected CreditCardPaymentInfoModel cloneAndSaveCreditCardWithPaymentInformation(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = wrapper.modelService.clone(paymentTransactionModel.getInfo(), CreditCardPaymentInfoModel.class);
        creditCardPaymentInfoModel.setOriginal(null);
        creditCardPaymentInfoModel.setDuplicate(false);
        final Card card = paymentReply.getCardDetails();
        final Date expiryDate = card.getExpiryDate();

        setCardInformation(creditCardPaymentInfoModel, card.getCardNumber(), expiryDate.getMonth(), expiryDate.getYear(), card.getCardHolderName());
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);

        wrapper.modelService.save(creditCardPaymentInfoModel);
        return creditCardPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel cloneAndSaveCreditCardWithTokenInformation(final PaymentTransactionModel paymentTransactionModel,
                                                                                    final TokenReply tokenReply,
                                                                                    final PaymentReply paymentReply) {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = wrapper.modelService.clone(paymentTransactionModel.getInfo(), CreditCardPaymentInfoModel.class);
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
            LOG.warn("No auto cancel pending timeout found for APM configuration with code [{}]", apmConfiguration.getCode());
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
        return wrapper.configurationService.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + methodCode);
    }

    private void doSetCreditCardTypeAndPaymentType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final String methodCode) {
        final String creditCardTypeValue = getHybrisCCTypeForWPCCType(methodCode);
        final CreditCardType cardType = StringUtils.isNotBlank(creditCardTypeValue) ?
                wrapper.enumerationService.getEnumerationValue(SIMPLE_CLASSNAME, creditCardTypeValue) :
                CARD;
        creditCardPaymentInfoModel.setPaymentType(methodCode);
        creditCardPaymentInfoModel.setType(cardType);
    }

    private void updateCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final PaymentReply paymentReply) {
        doSetCreditCardTypeAndPaymentType(creditCardPaymentInfoModel, paymentReply.getPaymentMethodCode());
    }

    private void attachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel) {
        orderModel.setPaymentInfo(paymentInfoModel);
        paymentTransactionModel.setInfo(paymentInfoModel);
        wrapper.modelService.saveAll(orderModel, paymentTransactionModel);
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
        wrapper.modelService.save(creditCardPaymentInfoModel);
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
        wrapper.modelService.save(paymentInfoModel);
        return paymentInfoModel;
    }

    protected GooglePayPaymentInfoModel createGooglePaymentInfoModel(final CartModel cartModel, final GooglePayAdditionalAuthInfo googleAuthInfo, final String paymentTokenId, final Card cardResponse) {
        final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = apmConfigurationLookupService.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode());
        final GooglePayPaymentInfoModel paymentInfoModel = wrapper.modelService.create(GooglePayPaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfoModel.setProtocolVersion(googleAuthInfo.getProtocolVersion());
        paymentInfoModel.setSignature(googleAuthInfo.getSignature());
        paymentInfoModel.setSignedMessage(googleAuthInfo.getSignedMessage());
        paymentInfoModel.setApmConfiguration(worldpayAPMConfigurationModel);
        paymentInfoModel.setSubscriptionId(paymentTokenId);
        setGooglePayCardInformation(cardResponse, paymentInfoModel);
        paymentInfoModel.setSaved(googleAuthInfo.getSaveCard());
        cartModel.setPaymentInfo(paymentInfoModel);
        wrapper.modelService.save(cartModel);
        return paymentInfoModel;
    }

    private void setGooglePayCardInformation(final Card cardResponse, final GooglePayPaymentInfoModel paymentInfoModel) {
        Optional.ofNullable(cardResponse).ifPresent(cardResponseDetails -> {
            paymentInfoModel.setObfuscatedCardNumber(cardResponseDetails.getCardNumber());
            final Date expiryDate = cardResponseDetails.getExpiryDate();
            paymentInfoModel.setExpiryMonth(expiryDate.getMonth());
            paymentInfoModel.setExpiryYear(expiryDate.getYear());
        });
    }

    /**
     * Get the payment method from session cause of APM HPP payment.
     *
     * @return the payment method from session.
     */
    private String getPaymentMethodFromSession() {
        final String paymentMethod = wrapper.sessionService.getAttribute(PAYMENT_METHOD_PARAM);
        wrapper.sessionService.removeAttribute(PAYMENT_METHOD_PARAM);

        return paymentMethod != null ? paymentMethod : StringUtils.EMPTY;
    }
}
