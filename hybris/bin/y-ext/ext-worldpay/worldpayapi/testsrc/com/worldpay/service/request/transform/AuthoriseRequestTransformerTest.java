package com.worldpay.service.request.transform;

import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.worldpay.data.Address;
import com.worldpay.data.Amount;
import com.worldpay.data.BasicOrderInfo;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.Order;
import com.worldpay.data.Shopper;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.AuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class AuthoriseRequestTransformerTest {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String ORDER_CONTENT = "orderContent";
    private static final String SHOPPER_EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String AUTH_SHOPPER_ID = "authShopperID";
    private static final String ORDER_CODE = "DS1347889928107_3";
    private static final String DESCRIPTION = "Your Order & Order desc";
    private static final String MERCHANT_CODE = "MERCHANT_CODE";
    private static final String MERCHANT_PASSWORD = "MERCHANT_PASSWORD";
    private static final String VERSION = "1.4";
    private static final List<PaymentType> INCLUDED_PTS = singletonList(ONLINE);

    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    private Address shippingAddress;
    private Address billingAddress;
    private TokenRequest tokenRequest;
    private Shopper shopper;

    @InjectMocks
    private AuthoriseRequestTransformer testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private Converter<Order, com.worldpay.internal.model.Order> internalOrderConverterMock;

    @Mock
    private com.worldpay.internal.model.Order intOrderMock;

    @BeforeEach
    void setUp() {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_REFERENCE);

        final Amount amount = new Amount();
        amount.setExponent("2");
        amount.setCurrencyCode("EUR");
        amount.setValue("100");

        basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setOrderCode(ORDER_CODE);
        basicOrderInfo.setDescription(DESCRIPTION);
        basicOrderInfo.setAmount(amount);

        final Address address = new Address();
        address.setFirstName(FIRST_NAME);
        address.setLastName(LAST_NAME);
        address.setAddress1(SHOPPER_ADDRESS_1);
        address.setAddress2(SHOPPER_ADDRESS_2);
        address.setAddress3(SHOPPER_ADDRESS_3);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);

        this.shippingAddress = address;
        this.billingAddress = address;

        shopper = new Shopper();
        shopper.setShopperEmailAddress(SHOPPER_EMAIL_ADDRESS);
        shopper.setAuthenticatedShopperID(AUTH_SHOPPER_ID);

    }

    @Test
    void populate_WhenRequestIsNull_ShouldThrowAnException() {
        assertThatThrownBy(() -> testObj.transform(null))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage("Request provided to do the authorise is invalid.");
    }

    @Test
    void populate_WhenMerchantInfoIsNull_ShouldThrowAnException() {
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(null)
                .withOrderInfo(basicOrderInfo)
                .withOrderContent(ORDER_CONTENT)
                .withIncludedPTs(INCLUDED_PTS)
                .withShopper(shopper)
                .withShippingAddress(shippingAddress)
                .withBillingAddress(billingAddress)
                .withTokenRequest(tokenRequest)
                .withStatementNarrative(STATEMENT_NARRATIVE_TEXT)
                .build();

        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        assertThatThrownBy(() -> testObj.transform(request)).isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage("Request provided to do the authorise is invalid.");
    }

    @Test
    void populate_WhenOrderCodeIsNull_ShouldThrowAnException() {
        basicOrderInfo.setOrderCode(null);
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withOrderContent(ORDER_CONTENT)
                .withIncludedPTs(INCLUDED_PTS)
                .withShopper(shopper)
                .withShippingAddress(shippingAddress)
                .withBillingAddress(billingAddress)
                .withTokenRequest(tokenRequest)
                .withStatementNarrative(STATEMENT_NARRATIVE_TEXT)
                .build();

        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        assertThatThrownBy(() -> testObj.transform(request))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage("Request provided to do the authorise is invalid.");
    }

    @Test
    void transform_WhenEveryFieldIsNotEmpty_ShouldTransformRequest() throws WorldpayModelTransformationException {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);

        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withOrderContent(ORDER_CONTENT)
                .withIncludedPTs(INCLUDED_PTS)
                .withShopper(shopper)
                .withShippingAddress(shippingAddress)
                .withBillingAddress(billingAddress)
                .withTokenRequest(tokenRequest)
                .withStatementNarrative(STATEMENT_NARRATIVE_TEXT)
                .build();
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);
        when(internalOrderConverterMock.convert(request.getOrder())).thenReturn(intOrderMock);

        final PaymentService result = testObj.transform(request);

        assertThat(result.getMerchantCode()).isEqualTo(merchantInfo.getMerchantCode());
        assertThat(result.getVersion()).isEqualTo(VERSION);

        final List<Object> submitList = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Submit submit = (Submit) submitList.get(0);
        final List<Object> orderList = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallengeOrCreateAccessToken();
        final com.worldpay.internal.model.Order intOrder = (com.worldpay.internal.model.Order) orderList.get(0);

        assertThat(intOrder).isEqualTo(intOrderMock);
    }
}
