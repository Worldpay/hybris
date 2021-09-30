package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;

@IntegrationTest
public class DirectAuthoriseServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();
    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway testObj;

    @Test
    public void createDirectAuthoriseRequestShouldRaiseWorldpayValidationExceptionWithMinimumValues() throws WorldpayException {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        thrown.expect(IllegalArgumentException.class);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(null)
            .withPayment(null)
            .withShopper(null)
            .withShippingAddress(null)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

        testObj.directAuthorise(request);
    }
}
