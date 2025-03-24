package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpaySepaMandateStrategyTest {

    private static final String MANDATE_TYPE = "ONE-OFF";

    @InjectMocks
    private DefaultWorldpaySepaMandateStrategy testObj;

    @Mock
    private AbstractOrderModel cartMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;

    private final List<PaymentType> listWithSEPAPayment = List.of(PaymentType.SEPA);
    private final List<PaymentType> listWithoutSEPAPayment = List.of(PaymentType.KLARNAV2SSL);
    private final List<PaymentType> listWitNullModelClass = List.of(PaymentType.ONLINE);

    @Test
    public void populateRequestWithAdditionalData_WhenGetIncludedPTsIsNull_ShouldNotPopulateMandateType() {
        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withIncludedPTs(null);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMandateType()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenGetIncludedPTsIsEmpty_ShouldNotPopulateMandateType() {
        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withIncludedPTs(List.of());

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMandateType()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenIsNotSEPAAPM_ShouldNotPopulateMandateType() {
        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withIncludedPTs(listWithoutSEPAPayment);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMandateType()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenPaymentTypeModelClassIsNull_ShouldNotPopulateMandateType() {
        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withIncludedPTs(listWitNullModelClass);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMandateType()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenIsSEPAAPM_ShouldPopulateMandateType() {
        final AuthoriseRequestParametersCreator authoriseRequestParametersCreator = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withIncludedPTs(listWithSEPAPayment);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMandateType()).isEqualTo(MANDATE_TYPE);
    }
}
