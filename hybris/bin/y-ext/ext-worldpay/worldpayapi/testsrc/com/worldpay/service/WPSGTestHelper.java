package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.*;
import com.worldpay.service.response.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WPSGTestHelper {

    private static final String ENCRYPTED_DATA = "eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMjU2R0NNIiwia2lkIjoiMSIsImNvbS53b3JsZHBheS5hcGlWZXJzaW9uIjoiMS4wIiwiY29tLndvcmxkcGF5LmxpYlZlcnNpb24iOiIxLjAuMCIsImNvbS53b3JsZHBheS5jaGFubmVsIjoiamF2YXNjcmlwdCJ9.pnEzzJrmecQeLxHx3y2uUm_eLxED_W0VRVNPnTQUTwN9eNuEVkcFbIWhDh1IfPstYqoljPoQ_TsQ99ixIH9DNU-Q6rmfnNe70C1qXYVUIp64__E0VcN-e6kDlTobz4JhJybOSWlWra0KjckKPaH1YegI6NMgQhNO-O19UUn0NV_zGcI8AQzeympkC4aLWNQbzeGdtnqjF5RWFbNucr_c6uCubeC3-r4ndtkSaQ2JCEhxonR-J7fa3xBSQSFAUZfP3DFGT53_FGDuzjA4i83mh028DDgtO0X3wcH5pa4uhDybTLW9GdKQie3kMv7drfNcpykUfT4Nzziz1zKRmdcqHw.PapnHYpZX1GRVfCT.qV87L9evHyG_xiB7iPaSC69Etir_rLLOV4T-TBIni4qniHfrxOIaUX8NpIKKYNfbW3oRd17erT44iv2Si6G5LoLihdxF1jqVbPMxTyY0qwbSYZgn134658JjlZ70chYmlMBPyp4O4AyhbU_IRJyWv63uZJok.mzc7jo8aTAxZQYuAvB59sA";
    private static String expiryYear;

    static {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        expiryYear = new SimpleDateFormat("YYYY").format(cal.getTime());
    }

    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String SHOPPER_EMAIL = "jshopper@myprovider.com";

    private static final Date EXPIRY_DATE = new Date("09", expiryYear);
    private static final Session SESSION = new Session("192.168.1.1", "sessionId1234");
    private static final Browser BROWSER = new Browser("text/html,application/xhtml+xml,application/xml;q=0. 9,*/*;q=0.8", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)", "0");
    private static final Address ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Shopper SHOPPER = new Shopper(SHOPPER_EMAIL, null, BROWSER, SESSION);
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");

    public static DirectAuthoriseServiceResponse directAuthorise(final WorldpayServiceGateway gateway, final WorldpayConfig config, final MerchantInfo merchantInfo, String orderCode) throws WorldpayException {
        final BasicOrderInfo orderInfo = new BasicOrderInfo(orderCode, "Your Order & Order desc", new Amount("100", "EUR", "2"));
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(config, merchantInfo, orderInfo, payment,
                SHOPPER, SESSION, ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);
        return gateway.directAuthorise(request);
    }

    public static CreateTokenResponse createToken(final WorldpayServiceGateway gateway, final WorldpayConfig config, final MerchantInfo merchantInfo,
                                                  final TokenRequest tokenRequest, final String authenticatedShopperId) throws WorldpayException {
        final Payment payment = PaymentBuilder.createCSE(ENCRYPTED_DATA, ADDRESS);
        final CreateTokenServiceRequest request = CreateTokenServiceRequest.createTokenRequest(config, merchantInfo, authenticatedShopperId, payment, tokenRequest);
        return gateway.createToken(request);
    }

    public static UpdateTokenResponse updateToken(final WorldpayServiceGateway gateway, final WorldpayConfig config, final MerchantInfo merchantInfo,
                                                  final TokenRequest tokenRequest, final String paymentTokenId, final CardDetails cardDetails, final String authenticatedShopperId) throws WorldpayException {
        final UpdateTokenServiceRequest request = UpdateTokenServiceRequest.updateTokenRequest(config, merchantInfo, authenticatedShopperId, paymentTokenId, tokenRequest, cardDetails);
        return gateway.updateToken(request);
    }

    public static DeleteTokenResponse deleteToken(final WorldpayServiceGateway gateway, final WorldpayConfig config, final MerchantInfo merchantInfo,
                                                  final TokenRequest tokenRequest, final String paymentTokenId, final String authenticatedShopperId) throws WorldpayException {
        final DeleteTokenServiceRequest request = DeleteTokenServiceRequest.deleteTokenRequest(config, merchantInfo, authenticatedShopperId, paymentTokenId, tokenRequest);
        return gateway.deleteToken(request);
    }

    public static CaptureServiceResponse capture(WorldpayServiceGateway gateway, WorldpayConfig config, MerchantInfo merchantInfo, String orderCode) throws WorldpayException {
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(config, merchantInfo, orderCode, new Amount("100", "EUR", "2"), null);

        return gateway.capture(request);
    }
}
