package com.worldpay.service.payment.impl;

import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOpenBankingStrategyTest {

    @Spy
    @InjectMocks
    private DefaultWorldpayOpenBankingStrategy testObj;

    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;

    @Mock
    private AbstractOrderModel cartMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    private final List<PaymentType> listWithOBPayment = List.of(PaymentType.OPENBANKINGSSL);
    private final List<PaymentType> listWithoutOBPayment = List.of(PaymentType.KLARNAV2SSL);
    private final List<PaymentType> listWitNullModelClass = List.of(PaymentType.ONLINE);

    @Test
    public void populateRequestWithAdditionalData_WhenGetIncludedPTsIsNull_ShouldNotSetOpenBankingMerchant() {
        final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator =
                AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                        .withIncludedPTs(null);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMerchantInfo()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenGetIncludedPTsIsEmpty_ShouldNotPopulateOpenBankingMerchant() {
        final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator =
                AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                        .withIncludedPTs(List.of());

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMerchantInfo()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenIsNotSEPAAPM_ShouldNotPopulateOpenBankingMerchant() {
        final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator =
                AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                        .withIncludedPTs(listWithoutOBPayment);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMerchantInfo()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenPaymentTypeModelClassIsNull_ShouldNotPopulateOBMerchant() {
        final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator =
                AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                        .withIncludedPTs(listWitNullModelClass);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMerchantInfo()).isNull();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenIsSEPAAPM_ShouldPopulateOBMerchant() throws WorldpayConfigurationException {
        final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator =
                AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                        .withIncludedPTs(listWithOBPayment);

        when(worldpayMerchantInfoServiceMock.getCurrentSiteOpenBankingMerchant()).thenReturn(merchantInfoMock);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMerchantInfo()).isEqualTo(merchantInfoMock);
    }

    @Test
    public void addOpenBankingMerchant_WhenWorldpayConfigurationExceptionIsThrown_ShouldLogErrorAndNotSetMerchantInfo() throws WorldpayConfigurationException {
        final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator =
                AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                        .withIncludedPTs(listWithOBPayment);

        when(worldpayMerchantInfoServiceMock.getCurrentSiteOpenBankingMerchant())
                .thenThrow(new WorldpayConfigurationException("Error retrieving Open Banking merchant configuration"));

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreator);

        assertThat(authoriseRequestParametersCreator.build().getMerchantInfo()).isNull();
    }


}
