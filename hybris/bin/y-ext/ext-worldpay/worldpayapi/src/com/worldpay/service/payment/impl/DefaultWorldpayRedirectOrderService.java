package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.mac.MacValidator;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.internal.i18n.I18NConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation of the {@link WorldpayRedirectOrderService}
 */
public class DefaultWorldpayRedirectOrderService extends AbstractWorldpayOrderService implements WorldpayRedirectOrderService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderService.class);

    private static final String KEY_MAC = "mac";
    private static final String KEY_MAC2 = "mac2";
    private static final String ORDER_KEY = "orderKey";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_ERROR_URL = "errorURL";
    private static final String KEY_CANCEL_URL = "cancelURL";
    private static final String KEY_SUCCESS_URL = "successURL";
    private static final String KEY_PENDING_URL = "pendingURL";
    private static final String KEY_FAILURE_URL = "failureURL";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String KEY_PAYMENT_AMOUNT = "paymentAmount";
    private static final String KEY_PAYMENT_CURRENCY = "paymentCurrency";
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    private final SessionService sessionService;
    private final WorldpayURIService worldpayURIService;
    private final WorldpayUrlService worldpayUrlService;
    private final MacValidator macValidator;
    private final WorldpayRequestFactory worldpayRequestFactory;

    public DefaultWorldpayRedirectOrderService(final SessionService sessionService, final WorldpayURIService worldpayURIService, final WorldpayUrlService worldpayUrlService, final MacValidator macValidator, final WorldpayRequestFactory worldpayRequestFactory, final CommerceCheckoutService commerceCheckoutService, final WorldpayPaymentInfoService worldpayPaymentInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final WorldpayServiceGateway worldpayServiceGateway, final AddressService addressService) {
        super(commerceCheckoutService, worldpayPaymentInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway, addressService);
        this.sessionService = sessionService;
        this.worldpayURIService = worldpayURIService;
        this.worldpayUrlService = worldpayUrlService;
        this.macValidator = macValidator;
        this.worldpayRequestFactory = worldpayRequestFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentData redirectAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final RedirectAuthoriseServiceRequest request = worldpayRequestFactory.buildRedirectAuthoriseRequest(merchantInfo, cartModel, additionalAuthInfo);
        final RedirectAuthoriseServiceResponse redirectAuthorise = getWorldpayServiceGateway().redirectAuthorise(request);

        if (redirectAuthorise == null) {
            throw new WorldpayException("Response from Worldpay is empty");
        }
        final RedirectReference redirectReference = redirectAuthorise.getRedirectReference();

        if (redirectReference == null) {
            final String errorMessage = redirectAuthorise.getErrorDetail().getMessage();
            LOG.error(MessageFormat.format("Error detail: {0}", errorMessage));
            throw new WorldpayException(MessageFormat.format("Reference returned from Worldpay is empty - message was: {0}", errorMessage));
        }

        sessionService.setAttribute(WORLDPAY_MERCHANT_CODE, merchantInfo.getMerchantCode());
        return buildHOPPageData(redirectReference, request.getOrder().getBillingAddress().getCountryCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completePendingRedirectAuthorise(final RedirectAuthoriseResult result, final String merchantCode, final CartModel cartModel) {
        validateParameterNotNull(result, "RedirectAuthoriseResult cannot be null");

        final PaymentInfoModel paymentInfoModel = getWorldpayPaymentInfoService().createPaymentInfo(cartModel);
        cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, result.getPaymentAmount());
        getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
        final PaymentTransactionModel paymentTransaction = getWorldpayPaymentTransactionService().createPaymentTransaction(result.getPending(), merchantCode, commerceCheckoutParameter);
        getWorldpayPaymentTransactionService().createPendingAuthorisePaymentTransactionEntry(paymentTransaction, merchantCode, cartModel, result.getPaymentAmount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeConfirmedRedirectAuthorise(final BigDecimal paymentAmount, final String merchantCode, final CartModel cartModel) {
        validateParameterNotNull(paymentAmount, "Payment amount cannot be null");

        final PaymentInfoModel paymentInfoModel = getWorldpayPaymentInfoService().createPaymentInfo(cartModel);
        cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, paymentAmount);
        getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
        final PaymentTransactionModel paymentTransaction = getWorldpayPaymentTransactionService().createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
        getWorldpayPaymentTransactionService().createNonPendingAuthorisePaymentTransactionEntry(paymentTransaction, merchantCode, cartModel, paymentAmount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateRedirectResponse(final MerchantInfo merchantInfo, final Map<String, String> resultMap) {
        final String orderKey = resultMap.get(ORDER_KEY);
        if (StringUtils.isBlank(orderKey)) {
            return false;
        }

        final String paymentStatus = resultMap.get(PAYMENT_STATUS);
        if (shouldValidateMac(merchantInfo, paymentStatus)) {
            final String mac2 = resultMap.getOrDefault(KEY_MAC2, "");
            final String mac = resultMap.getOrDefault(KEY_MAC, mac2);
            final String paymentAmount = resultMap.get(KEY_PAYMENT_AMOUNT);
            final String paymentCurrency = resultMap.get(KEY_PAYMENT_CURRENCY);
            return validateResponse(merchantInfo, orderKey, mac, paymentAmount, paymentCurrency, AuthorisedStatus.valueOf(paymentStatus));
        }
        return true;
    }

    protected boolean shouldValidateMac(final MerchantInfo merchantInfo, final String paymentStatus) {
        return merchantInfo.isUsingMacValidation() && AUTHORISED.name().equalsIgnoreCase(paymentStatus);
    }

    protected boolean validateResponse(final MerchantInfo merchantInfo, final String orderKey, final String mac, final String paymentAmount, final String paymentCurrency, final AuthorisedStatus paymentStatus) {
        try {
            return getMacValidator().validateResponse(orderKey, mac, paymentAmount, paymentCurrency, paymentStatus, merchantInfo.getMacSecret());
        } catch (WorldpayMacValidationException e) {
            LOG.error("Mac validation failed - see log for more details", e);
            return false;
        }
    }

    private PaymentData buildPaymentData(final String redirectReferenceUrl, final Map<String, String> params) {
        final PaymentData data = new PaymentData();
        data.setPostUrl(redirectReferenceUrl);
        data.setParameters(params);
        return data;
    }

    /**
     * Build HOP page data to be sent via POST to worldpay
     *
     * @param redirectReference The {@link RedirectReference} that contains the URL to redirect the user to
     * @param countryCode       The {@link CartModel} that contains the information about the order and the addresses
     * @return {@link PaymentData} object
     */
    private PaymentData buildHOPPageData(final RedirectReference redirectReference, final String countryCode) throws WorldpayException {
        final String redirectReferenceUrl = redirectReference.getValue();
        if (StringUtils.isEmpty(redirectReferenceUrl)) {
            throw new WorldpayException("RedirectReferenceUrl is required but is empty");
        }

        final Map<String, String> params = generateParams(countryCode, redirectReferenceUrl);

        return buildPaymentData(redirectReferenceUrl, params);
    }

    private Map<String, String> generateParams(final String countryCode, final String redirectReferenceUrl) throws WorldpayException {
        final Map<String, String> params = new HashMap<>();
        worldpayURIService.extractUrlParamsToMap(redirectReferenceUrl, params);
        params.put(KEY_COUNTRY, countryCode);
        params.put(KEY_LANGUAGE, ((LanguageModel) sessionService.getAttribute(I18NConstants.LANGUAGE_SESSION_ATTR_KEY)).getIsocode());
        params.put(KEY_SUCCESS_URL, worldpayUrlService.getFullSuccessURL());
        params.put(KEY_PENDING_URL, worldpayUrlService.getFullPendingURL());
        params.put(KEY_FAILURE_URL, worldpayUrlService.getFullFailureURL());
        params.put(KEY_CANCEL_URL, worldpayUrlService.getFullCancelURL());
        params.put(KEY_ERROR_URL, worldpayUrlService.getFullErrorURL());
        return params;
    }

    public MacValidator getMacValidator() {
        return macValidator;
    }

}
