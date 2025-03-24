package com.worldpay.service.response;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.RedirectReference;
import com.worldpay.data.Request3DInfo;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.enums.order.ThreeDSecureVersionEnum;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#directAuthorise(DirectAuthoriseServiceRequest) directAuthorise()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the {@link Request3DInfo} (if 3D authentication is required), {@link PaymentReply} or {@link RedirectReference} (if
 * the user should be redirected to a third party site to take payment - this happens for some forms of alternative payments)</p>
 */
public class DirectAuthoriseServiceResponse extends AbstractServiceResponse {

    private static final Logger LOG = LogManager.getLogger(DirectAuthoriseServiceResponse.class);
    private static final String V1 = "1";
    private static final String V2 = "2";
    private Request3DInfo request3DInfo;
    private PaymentReply paymentReply;
    private RedirectReference redirectReference;
    private TokenReply token;
    private String echoData;
    private ExemptionResponseInfo exemptionResponseInfo;

    /**
     * Determines if the response needs 3d secure authentication.
     *
     * @return
     */
    public Optional<ThreeDSecureVersionEnum> get3DSecureVersion() {
        if (getRequest3DInfo() != null) {
            if (V1.equals(getRequest3DInfo().getMajor3DSVersion())) {
                return Optional.of(ThreeDSecureVersionEnum.V1);
            }
            if (V2.equals(getRequest3DInfo().getMajor3DSVersion())) {
                return Optional.of(ThreeDSecureVersionEnum.V2);
            }
        }
        return Optional.empty();
    }

    /**
     * @return
     */
    public Optional<ThreeDSecureFlowEnum> get3DSecureFlow() {
        final Request3DInfo request = getRequest3DInfo();
        if (request != null) {
            if (isLegacyThreeDSecureFlow(request)) {
                return Optional.of(ThreeDSecureFlowEnum.LEGACY_FLOW);
            } else if (isThreeDSecureFlexFlow(request)) {
                return Optional.of(ThreeDSecureFlowEnum.THREEDSFLEX_FLOW);
            } else {
                LOG.warn("{} is incomplete", request);
            }
        }

        return Optional.empty();
    }

    private static boolean isLegacyThreeDSecureFlow(final Request3DInfo request) {
        return request.getIssuerUrl() != null && request.getPaRequest() != null;
    }

    private static boolean isThreeDSecureFlexFlow(final Request3DInfo request) {
        return request.getIssuerUrl() != null &&
                request.getMajor3DSVersion() != null &&
                request.getTransactionId3DS() != null &&
                request.getIssuerPayload() != null;
    }

    public Request3DInfo getRequest3DInfo() {
        return request3DInfo;
    }

    public void setRequest3DInfo(final Request3DInfo request3DInfo) {
        this.request3DInfo = request3DInfo;
    }

    public PaymentReply getPaymentReply() {
        return paymentReply;
    }

    public void setPaymentReply(final PaymentReply paymentReply) {
        this.paymentReply = paymentReply;
    }

    public RedirectReference getRedirectReference() {
        return redirectReference;
    }

    public void setRedirectReference(final RedirectReference redirectReference) {
        this.redirectReference = redirectReference;
    }

    public String getEchoData() {
        return echoData;
    }

    public void setEchoData(final String echoData) {
        this.echoData = echoData;
    }

    public TokenReply getToken() {
        return token;
    }

    public void setToken(final TokenReply token) {
        this.token = token;
    }

    public ExemptionResponseInfo getExemptionResponseInfo() {
        return exemptionResponseInfo;
    }

    public void setExemptionResponseInfo(final ExemptionResponseInfo exemptionResponseInfo) {
        this.exemptionResponseInfo = exemptionResponseInfo;
    }
}
