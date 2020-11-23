package com.worldpay.service.hop.impl;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.hop.WorldpayHOPPService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.internal.i18n.I18NConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @{inheritDoc}
 */
public class DefaultWorldpayHOPService implements WorldpayHOPPService {
    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayHOPService.class);

    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_ERROR_URL = "errorURL";
    private static final String KEY_CANCEL_URL = "cancelURL";
    private static final String KEY_SUCCESS_URL = "successURL";
    private static final String KEY_PENDING_URL = "pendingURL";
    private static final String KEY_FAILURE_URL = "failureURL";

    protected final SessionService sessionService;
    protected final WorldpayURIService worldpayURIService;
    protected final WorldpayUrlService worldpayUrlService;
    protected final WorldpayRequestFactory worldpayRequestFactory;
    protected final WorldpayServiceGateway worldpayServiceGateway;

    public DefaultWorldpayHOPService(final SessionService sessionService, final WorldpayURIService worldpayURIService, final WorldpayUrlService worldpayUrlService, final WorldpayRequestFactory worldpayRequestFactory, final WorldpayServiceGateway worldpayServiceGateway) {
        this.sessionService = sessionService;
        this.worldpayURIService = worldpayURIService;
        this.worldpayUrlService = worldpayUrlService;
        this.worldpayRequestFactory = worldpayRequestFactory;
        this.worldpayServiceGateway = worldpayServiceGateway;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public PaymentData buildHOPPageData(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final MerchantInfo merchantInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final RedirectAuthoriseServiceRequest request = worldpayRequestFactory.buildRedirectAuthoriseRequest(merchantInfo, cartModel, additionalAuthInfo, worldpayAdditionalInfoData);
        final RedirectAuthoriseServiceResponse redirectAuthorise = worldpayServiceGateway.redirectAuthorise(request);

        if (redirectAuthorise == null) {
            throw new WorldpayException("Response from Worldpay is empty");
        }

        final RedirectReference redirectReference = redirectAuthorise.getRedirectReference();

        if (redirectReference == null) {
            final String errorMessage = redirectAuthorise.getErrorDetail().getMessage();
            LOG.error("Error detail: {}", errorMessage);
            throw new WorldpayException(MessageFormat.format("Reference returned from Worldpay is empty - message was: {0}", errorMessage));
        }

        sessionService.setAttribute(WORLDPAY_MERCHANT_CODE, merchantInfo.getMerchantCode());
        return internalbuildHOPPageData(redirectReference, request.getOrder().getBillingAddress().getCountryCode());
    }


    private PaymentData internalbuildHOPPageData(final RedirectReference redirectReference, final String countryCode) throws WorldpayException {
        final String redirectReferenceUrl = redirectReference.getValue();
        if (StringUtils.isEmpty(redirectReferenceUrl)) {
            throw new WorldpayException("RedirectReferenceUrl is required but is empty");
        }
        final Map<String, String> params = generateParams(countryCode, redirectReferenceUrl);

        return buildPaymentData(redirectReferenceUrl, params);
    }

    private PaymentData buildPaymentData(final String redirectReferenceUrl, final Map<String, String> params) {
        final PaymentData data = new PaymentData();
        data.setPostUrl(redirectReferenceUrl);
        data.setParameters(params);

        return data;
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
}
