package com.worldpay.service.payment.impl;

import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.model.WorldpayCartThreeDSChallengeSessionModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Objects;

/**
 * WorldpaySessionService for OCC - Spartacus. Stores the 3ds challenge session on the cart
 * instead of the JaloSession
 */
public class OccWorldpaySessionService implements WorldpaySessionService {
    private final CartService cartService;
    private final ModelService modelService;

    public OccWorldpaySessionService(final CartService cartService, final ModelService modelService) {
        this.cartService = cartService;
        this.modelService = modelService;
    }

    /**
     * Set the challenge info on the cart 3ds challenge session. Assigns WorldpayCartThreeDSChallengeSessionModel to cart if not present.
     * Saves the session cart
     *
     * @param authoriseServiceResponse
     * @param worldpayAdditionalInfoData
     */
    @Override
    public void setSessionAttributesFor3DSecure(final DirectAuthoriseServiceResponse authoriseServiceResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        authoriseServiceResponse.get3DSecureFlow().filter(version -> ThreeDSecureFlowEnum.LEGACY_FLOW.equals(version)
            || ThreeDSecureFlowEnum.THREEDSFLEX_FLOW.equals(version))
            .ifPresent(flow -> {
                    final WorldpayCartThreeDSChallengeSessionModel cartThreeDsChallengeSession = modelService.create(WorldpayCartThreeDSChallengeSessionModel.class);
                    cartThreeDsChallengeSession.setEchoData(authoriseServiceResponse.getEchoData());
                    cartThreeDsChallengeSession.setCookie(authoriseServiceResponse.getCookie());
                    cartThreeDsChallengeSession.setSessionId(worldpayAdditionalInfoData.getSessionId());
                    if (ThreeDSecureFlowEnum.THREEDSFLEX_FLOW.toString().equals(flow.name())) {
                        cartThreeDsChallengeSession.setChallengeWindowSize(worldpayAdditionalInfoData.getAdditional3DS2().getChallengeWindowSize());
                    }
                    final CartModel sessionCart = cartService.getSessionCart();
                    sessionCart.setThreeDSChallengeSession(cartThreeDsChallengeSession);

                    modelService.save(sessionCart);
                }
            );
    }

    @Override
    public String getAndRemoveThreeDSecureCookie() {
        final WorldpayCartThreeDSChallengeSessionModel threeDSChallengeSession = cartService.getSessionCart().getThreeDSChallengeSession();
        final String cookie = threeDSChallengeSession.getCookie();
        threeDSChallengeSession.setCookie(null);

        modelService.save(threeDSChallengeSession);
        return cookie;
    }

    @Override
    public String getAndRemoveAdditionalDataSessionId() {
        final WorldpayCartThreeDSChallengeSessionModel threeDSChallengeSession = cartService.getSessionCart().getThreeDSChallengeSession();
        final String sessionId = threeDSChallengeSession.getSessionId();
        threeDSChallengeSession.setSessionId(null);

        modelService.save(threeDSChallengeSession);
        return sessionId;
    }

    @Override
    public ChallengeWindowSizeEnum getWindowSizeChallengeFromSession() {
        final WorldpayCartThreeDSChallengeSessionModel threeDSChallengeSession = cartService.getSessionCart().getThreeDSChallengeSession();
        final String challengeWindowSize = threeDSChallengeSession.getChallengeWindowSize();
        threeDSChallengeSession.setChallengeWindowSize(null);

        modelService.save(threeDSChallengeSession);
        return ChallengeWindowSizeEnum.valueOf(challengeWindowSize);
    }

    /**
     * Set the session id on the cart 3ds challenge. Assigns WorldpayCartThreeDSChallengeSessionModel to cart if not present.
     * Saves the session cart
     *
     * @param sessionId
     */
    public void setSessionIdFor3dSecure(final String sessionId) {
        final CartModel sessionCart = cartService.getSessionCart();
        WorldpayCartThreeDSChallengeSessionModel cartThreeDsChallengeSession = sessionCart.getThreeDSChallengeSession();
        if (Objects.isNull(cartThreeDsChallengeSession)) {
            cartThreeDsChallengeSession = modelService.create(WorldpayCartThreeDSChallengeSessionModel.class);
            sessionCart.setThreeDSChallengeSession(cartThreeDsChallengeSession);
        }
        cartThreeDsChallengeSession.setSessionId(sessionId);

        modelService.save(sessionCart);
    }
}
