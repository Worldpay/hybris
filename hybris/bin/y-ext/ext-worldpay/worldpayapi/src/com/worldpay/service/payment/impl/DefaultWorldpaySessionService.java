package com.worldpay.service.payment.impl;

import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpaySessionService implements WorldpaySessionService {
    private static final String THREED_SECURE_ECHO_DATA_KEY = "3DSecureEchoData";
    private static final String THREED_SECURE_COOKIE_KEY = "3DSecureCookie";
    private static final String THREED_SECURE_WINDOW_KEY = "challengeWindowSize";
    private static final String WORLDPAY_ADDITIONAL_DATA_SESSION_ID = "worldpay_additional_data_session_id";

    private final SessionService sessionService;

    public DefaultWorldpaySessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSessionAttributesFor3DSecure(final DirectAuthoriseServiceResponse authoriseServiceResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        authoriseServiceResponse.get3DSecureFlow().filter(version -> ThreeDSecureFlowEnum.LEGACY_FLOW.equals(version)
                || ThreeDSecureFlowEnum.THREEDSFLEX_FLOW.equals(version))
                .ifPresent(flow -> {
                     /*
                        In case the transaction requires 3d secure, two strings need to be placed in the users session: echoData and a cookie.
                        These are needed to successfully reference the initial transaction in Worldpay when the user comes back from the 3d secure page.
                        Example values:
                            echoData=148556494881709
                            cookie=machine=0ab20014;path=/
                      */
                    sessionService.setAttribute(THREED_SECURE_ECHO_DATA_KEY, authoriseServiceResponse.getEchoData());
                    sessionService.setAttribute(THREED_SECURE_COOKIE_KEY, authoriseServiceResponse.getCookie());
                    if (ThreeDSecureFlowEnum.THREEDSFLEX_FLOW.toString().equals(flow.name())) {
                        sessionService.setAttribute(THREED_SECURE_WINDOW_KEY, worldpayAdditionalInfoData.getAdditional3DS2().getChallengeWindowSize());
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAndRemoveThreeDSecureCookie() {
        final String attribute = sessionService.getAttribute(THREED_SECURE_COOKIE_KEY);
        sessionService.removeAttribute(THREED_SECURE_COOKIE_KEY);
        return attribute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAndRemoveAdditionalDataSessionId() {
        final String attribute = sessionService.getAttribute(WORLDPAY_ADDITIONAL_DATA_SESSION_ID);
        sessionService.removeAttribute(WORLDPAY_ADDITIONAL_DATA_SESSION_ID);
        return attribute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChallengeWindowSizeEnum getWindowSizeChallengeFromSession() {
        return ChallengeWindowSizeEnum.getEnum(sessionService.getAttribute(THREED_SECURE_WINDOW_KEY));
    }
}
