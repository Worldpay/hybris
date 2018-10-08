package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#directAuthorise(DirectAuthoriseServiceRequest) directAuthorise()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the {@link Request3DInfo} (if 3D authentication is required), {@link PaymentReply} or {@link RedirectReference} (if
 * the user should be redirected to a third party site to take payment - this happens for some forms of alternative payments)</p>
 */
public class DirectAuthoriseServiceResponse extends AbstractServiceResponse {

    private Request3DInfo request3DInfo;
    private PaymentReply paymentReply;
    private RedirectReference redirectReference;
    private TokenReply token;
    private String echoData;

    /**
     * Determines if the response needs 3d secure authentication.
     *
     * @return
     */
    public boolean is3DSecured() {
        return request3DInfo != null;
    }

    public Request3DInfo getRequest3DInfo() {
        return request3DInfo;
    }

    public void setRequest3DInfo(Request3DInfo request3DInfo) {
        this.request3DInfo = request3DInfo;
    }

    public PaymentReply getPaymentReply() {
        return paymentReply;
    }

    public void setPaymentReply(PaymentReply paymentReply) {
        this.paymentReply = paymentReply;
    }

    public RedirectReference getRedirectReference() {
        return redirectReference;
    }

    public void setRedirectReference(RedirectReference redirectReference) {
        this.redirectReference = redirectReference;
    }

    public String getEchoData() {
        return echoData;
    }

    public void setEchoData(String echoData) {
        this.echoData = echoData;
    }

    public TokenReply getToken() {
        return token;
    }

    public void setToken(final TokenReply token) {
        this.token = token;
    }
}
