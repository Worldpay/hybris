package com.worldpay.facades.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayDirectResponseService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayDirectResponseFacade implements WorldpayDirectResponseFacade {
    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayDirectResponseFacade.class);

    private static final String TERM_URL_PARAM_NAME = "termURL";
    private static final String PA_REQUEST_PARAM_NAME = "paRequest";
    private static final String ISSUER_URL_PARAM_NAME = "issuerURL";
    private static final String MERCHANT_DATA_PARAM_NAME = "merchantData";
    private static final String WIDTH_KEY = "width";
    private static final String HEIGHT_KEY = "height";
    private static final String JWT_KEY = "jwt";
    private static final String CHALLENGE_URL_KEY = "challengeUrl";
    private static final String V1 = "1";
    private static final String V2 = "2";
    private static final String WINDOW_SIZE = "challengeWindowSize";
    private static final String AUTO_SUBMIT_THREE_D_SECURE_FLEX_URL_KEY = "autoSubmitThreeDSecureFlexUrl";

    private final AcceleratorCheckoutFacade checkoutFacade;
    private final WorldpayUrlService worldpayUrlService;
    private final WorldpayDirectResponseService worldpayDirectResponseService;
    private final WorldpayJsonWebTokenService worldpayJsonWebTokenService;
    private final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    private final WorldpaySessionService worldpaySessionService;

    /**
     * Default constructor
     *
     * @param worldpayDirectResponseService    -
     * @param checkoutFacade                   -
     * @param worldpayUrlService               -
     * @param worldpayJsonWebTokenService      -
     * @param worldpayMerchantConfigDataFacade -
     * @param worldpaySessionService           -
     */
    public DefaultWorldpayDirectResponseFacade(final WorldpayDirectResponseService worldpayDirectResponseService,
                                               final AcceleratorCheckoutFacade checkoutFacade,
                                               final WorldpayUrlService worldpayUrlService, final WorldpayJsonWebTokenService worldpayJsonWebTokenService, final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade, final WorldpaySessionService worldpaySessionService) {
        this.worldpayDirectResponseService = worldpayDirectResponseService;
        this.checkoutFacade = checkoutFacade;
        this.worldpayUrlService = worldpayUrlService;
        this.worldpayJsonWebTokenService = worldpayJsonWebTokenService;
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
        this.worldpaySessionService = worldpaySessionService;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> retrieveAttributesForLegacy3dSecure(final DirectResponseData directResponseData) throws WorldpayConfigurationException {
        final Map<String, String> attributes = new HashMap<>();
        if (worldpayDirectResponseService.is3DSecureLegacyFlow(directResponseData)) {
            attributes.put(ISSUER_URL_PARAM_NAME, directResponseData.getIssuerURL());
            attributes.put(PA_REQUEST_PARAM_NAME, directResponseData.getPaRequest());
            attributes.put(TERM_URL_PARAM_NAME, worldpayUrlService.getFullThreeDSecureTermURL());
            attributes.put(MERCHANT_DATA_PARAM_NAME, checkoutFacade.getCheckoutCart().getWorldpayOrderCode());
        }

        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> retrieveAttributesForFlex3dSecure(final DirectResponseData directResponseData) throws WorldpayConfigurationException {
        final Map<String, String> attributes = new HashMap<>();
        if (worldpayDirectResponseService.is3DSecureFlexFlow(directResponseData)) {
            final WorldpayMerchantConfigData configData = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();
            attributes.put(JWT_KEY, worldpayJsonWebTokenService.createJsonWebTokenFor3DSecureFlexChallengeIframe(configData, directResponseData));
            attributes.put(CHALLENGE_URL_KEY, worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getThreeDSFlexJsonWebTokenSettings().getChallengeUrl());
            attributes.put(AUTO_SUBMIT_THREE_D_SECURE_FLEX_URL_KEY, worldpayUrlService.getFullThreeDSecureFlexAutosubmitUrl());
            attributes.putAll(getWindowChallengePreferenceAttributes(directResponseData));
        }

        return attributes;
    }

    protected Map<String, String> getWindowChallengePreferenceAttributes(final DirectResponseData directResponseData) {
        final Map<String, String> attributes = new HashMap<>();
        if (directResponseData.getMajor3DSVersion().equals(V1)) {
            attributes.put(HEIGHT_KEY, ChallengeWindowSizeEnum.R_390_400.getHeight());
            attributes.put(WIDTH_KEY, ChallengeWindowSizeEnum.R_390_400.getWidth());
        }

        if (directResponseData.getMajor3DSVersion().equals(V2)) {
            final ChallengeWindowSizeEnum windowSize = worldpaySessionService.getWindowSizeChallengeFromSession();

            if (windowSize != null) {
                attributes.put(HEIGHT_KEY, windowSize.getHeight());
                attributes.put(WIDTH_KEY, windowSize.getWidth());
            } else {
                LOG.warn("There is no {} variable stored in session, hence setting the default one {}", WINDOW_SIZE, ChallengeWindowSizeEnum.R_390_400);
                attributes.put(HEIGHT_KEY, ChallengeWindowSizeEnum.R_390_400.getHeight());
                attributes.put(WIDTH_KEY, ChallengeWindowSizeEnum.R_390_400.getWidth());
            }
        }

        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isCancelled(final DirectResponseData directResponseData) {
        return worldpayDirectResponseService.isCancelled(directResponseData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAuthorised(final DirectResponseData directResponseData) {
        return worldpayDirectResponseService.isAuthorised(directResponseData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean is3DSecureLegacyFlow(final DirectResponseData directResponseData) {
        return worldpayDirectResponseService.is3DSecureLegacyFlow(directResponseData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean is3DSecureFlexFlow(final DirectResponseData directResponseData) {
        return worldpayDirectResponseService.is3DSecureFlexFlow(directResponseData);
    }

}
