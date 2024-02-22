package com.worldpay.service;

import com.worldpay.data.payment.Cse;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.data.*;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.*;
import com.worldpay.service.response.*;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import org.apache.commons.lang.StringUtils;

public class WPSGTestHelper {

    private static final String ENCRYPTED_DATA = "eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMjU2R0NNIiwia2lkIjoiMSIsImNvbS53b3JsZHBheS5hcGlWZXJzaW9uIjoiMS4wIiwiY29tLndvcmxkcGF5LmxpYlZlcnNpb24iOiIxLjAuNCIsImNvbS53b3JsZHBheS5jaGFubmVsIjoiamF2YXNjcmlwdCJ9.XD83Pqg4iil3m6oEcFPzlAamYFiEN6o6aY53qD1mF4skwhZxQKylrG-H-eURaqUyXxh2fVyGgjmhj6LnL4AsdthY_o9Q88aTCLSqypjm8eHIYdQGFbsa-9h-5zk1CpLlciksVkddxSIGOEuOYSddKza5RvoGbud4ZBVmRLX_CvUdzvvKFA2GXldLvpIYzze6ttncYq6fHQp52BMjKYfGv7ialjvgJaLpEN_La5PPAd89gbVzYjKLqzKV1EAjOYxMDVEOnvRwuDTmHXE9D0ofHjxjrINmbzfXwZsqM2cIBtYSZdExmdSqBuDMnKA6wzfuV5SOps6XXNYnPYRxn0yTQA.6Daee4eI0fFCi86X.1eF-Sh03FyLrHR-vwPKSYTAF26C3XFToBS9RYCjjVivJPI2lDLmW2oTUQfOZMZEgLnmfDAhFeXYKdjFPBL-zvU--El5geAs_FvzjyjJhT55q6d1EbLbnzrT3PUilctZyldhCARUXEVhPdY3o.SsST7iOU-lDwbzOH_kVWHw";

    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String SHOPPER_EMAIL = "jshopper@myprovider.com";

    private static final String SESSION_IP = "192.168.1.1";
    private static final String SESSION_ID = "sessionId1234";
    private static final String ORDER_DESCRIPTION = "Your Order & Order desc";
    private static final String HEADER = "text/html,application/xhtml+xml,application/xml;q=0. 9,*/*;q=0.8";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)";
    private static final String DEVICE_TYPE = "0";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String NAME = "John";
    private static final String SHOPPER = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "Postal code";
    private static final Address ADDRESS = createAddress();
    private static final Address BILLING_ADDRESS = createAddress();
    private static final String EUR = "EUR";
    private static final String EXPONENT = "2";

    public static DirectAuthoriseServiceResponse directAuthorise(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo, final String orderCode) throws WorldpayException {
        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("100");

        final BasicOrderInfo orderInfo = new BasicOrderInfo();
        orderInfo.setOrderCode(orderCode);
        orderInfo.setDescription(ORDER_DESCRIPTION);
        orderInfo.setAmount(amount);

        final Payment payment = WorldpayInternalModelTransformerUtil
            .createAlternativeShopperBankCodePayment(PaymentType.IDEAL, "ASN", "successURL", "failureURL",  "cancelURL", StringUtils.EMPTY, StringUtils.EMPTY);

        final Session session =  new Session();
        session.setShopperIPAddress(SESSION_IP);
        session.setId(SESSION_ID);

        final Browser browser = new Browser();
        browser.setDeviceType(DEVICE_TYPE);
        browser.setAcceptHeader(HEADER);
        browser.setUserAgentHeader(USER_AGENT);

        final Shopper shopper = new Shopper();
        shopper.setSession(session);
        shopper.setShopperEmailAddress(SHOPPER_EMAIL);
        shopper.setBrowser(browser);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(orderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(ADDRESS)
            .withBillingAddress(BILLING_ADDRESS)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);
        return gateway.directAuthorise(request);
    }

    public static CreateTokenResponse createShopperToken(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo,
                                                         final TokenRequest tokenRequest, final String authenticatedShopperId) throws WorldpayException {
        final Cse cse = new Cse();
        cse.setEncryptedData(ENCRYPTED_DATA);
        cse.setAddress(ADDRESS);
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());
        final CreateTokenServiceRequest request = CreateTokenServiceRequest.createTokenRequestForShopperToken(merchantInfo, authenticatedShopperId, cse, tokenRequest);
        return gateway.createToken(request);
    }

    public static CreateTokenResponse createMerchantToken(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo,
                                                          final TokenRequest tokenRequest) throws WorldpayException {
        final Cse cse = new Cse();
        cse.setEncryptedData(ENCRYPTED_DATA);
        cse.setAddress(ADDRESS);
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());
        final CreateTokenServiceRequest request = CreateTokenServiceRequest.createTokenRequestForMerchantToken(merchantInfo, cse, tokenRequest);
        return gateway.createToken(request);
    }

    public static UpdateTokenResponse updateTokenWithShopperScope(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo,
                                                                  final TokenRequest tokenRequest, final String paymentTokenId, final CardDetails cardDetails, final String authenticatedShopperId) throws WorldpayException {
        final UpdateTokenServiceRequest request = UpdateTokenServiceRequest.updateTokenRequestWithShopperScope(merchantInfo, authenticatedShopperId, paymentTokenId, tokenRequest, cardDetails);
        return gateway.updateToken(request);
    }

    public static UpdateTokenResponse updateTokenWithMerchantScope(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo,
                                                                   final TokenRequest tokenRequest, final String paymentTokenId, final CardDetails cardDetails) throws WorldpayException {
        final UpdateTokenServiceRequest request = UpdateTokenServiceRequest.updateTokenRequestWithMerchantScope(merchantInfo, paymentTokenId, tokenRequest, cardDetails);
        return gateway.updateToken(request);
    }

    public static DeleteTokenResponse deleteToken(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo,
                                                  final TokenRequest tokenRequest, final String paymentTokenId, final String authenticatedShopperId) throws WorldpayException {
        final DeleteTokenServiceRequest request = DeleteTokenServiceRequest.deleteTokenRequest(merchantInfo, authenticatedShopperId, paymentTokenId, tokenRequest);
        return gateway.deleteToken(request);
    }

    public static CaptureServiceResponse capture(final WorldpayServiceGateway gateway, final MerchantInfo merchantInfo, final String orderCode) throws WorldpayException {
        final Amount amount = new Amount();
        amount.setValue("100");
        amount.setCurrencyCode(EUR);
        amount.setExponent(EXPONENT);
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, orderCode, amount, null, null);

        return gateway.capture(request);
    }

    private static Address createAddress() {
        final Address address = new Address();
        address.setFirstName(NAME);
        address.setLastName(SHOPPER);
        address.setAddress1(SHOPPER_ADDRESS_1);
        address.setAddress2(SHOPPER_ADDRESS_2);
        address.setAddress3(SHOPPER_ADDRESS_3);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);

        return address;
    }
}
