package com.worldpay.service.payment.impl;

import com.worldpay.data.Exemption;
import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayExemptionStrategyTest {

    private static final String EXEMPTION_RESPONSE_REASON = "ACCEPTED";
    private static final String EXEMPTION_RESPONSE_RESULT = "AUTHORIZED";
    private static final String EXEMPTION_TYPE = "OP";
    private static final String EXEMPTION_PLACEMENT = "PLACED";

    @Spy
    @InjectMocks
    private DefaultWorldpayExemptionStrategy testObj;

    @Mock
    private ModelService modelService;
    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private BaseSiteModel baseSiteMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock
    private AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private ExemptionResponseInfo exemptionResponseInfoMock;
    @Mock
    private Exemption exemptionMock;

    @Before
    public void setUp() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(baseSiteMock);
        when(baseSiteMock.getEnableEE()).thenReturn(true);
    }

    @Test
    public void isExemptionEnabled_WhenEEIsEnabledForSite_ShouldReturnTrue() {
        final boolean result = testObj.isExemptionEnabled();

        assertThat(result).isTrue();
    }

    @Test
    public void isExemptionEnabled_WhenEEIsDisabledForSite_ShouldReturnFalse() {
        when(baseSiteMock.getEnableEE()).thenReturn(false);

        final boolean result = testObj.isExemptionEnabled();

        assertThat(result).isFalse();
    }

    @Test
    public void isExemptionEnabled_WhenEEIsEnabledForGivenSite_ShouldReturnTrue() {
        final boolean result = testObj.isExemptionEnabled(baseSiteMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isExemptionEnabled_WhenEEIsDisabledForGivenSite_ShouldReturnFalse() {
        when(baseSiteMock.getEnableEE()).thenReturn(false);

        final boolean result = testObj.isExemptionEnabled(baseSiteMock);

        assertThat(result).isFalse();
    }

    @Test
    public void populateRequestWithAdditionalData_WhenEEIsEnabled_ShouldAddExemption() {
        when(cartMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getPaymentType()).thenReturn(PaymentType.VISA.getMethodCode());
        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withExemption(any());
    }

    @Test
    public void populateRequestWithAdditionalData_WhenEEIsEnabledWithInvalidPaymentType_ShouldDoNothing() {
        when(cartMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getPaymentType()).thenReturn(PaymentType.KLARNAV2SSL.getMethodCode());
        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verifyNoInteractions(authoriseRequestParametersCreatorMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenEEIsEnabledWithNullPaymentInfoShouldDoNothing() {
        when(cartMock.getPaymentInfo()).thenReturn(null);
        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verifyNoInteractions(authoriseRequestParametersCreatorMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenEEIsDisabled_ShouldDoNothing() {
        when(baseSiteMock.getEnableEE()).thenReturn(false);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verifyNoInteractions(authoriseRequestParametersCreatorMock);
    }

    @Test
    public void addExemptionResponse_ShouldAddExemptionResponseToPaymentTransactionModelAndSaveIt_WhenExemptionResponseHasData() {
        when(paymentReplyMock.getExemptionResponseInfo()).thenReturn(exemptionResponseInfoMock);
        when(exemptionResponseInfoMock.getExemption()).thenReturn(exemptionMock);
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_RESULT);
        when(exemptionResponseInfoMock.getReason()).thenReturn(EXEMPTION_RESPONSE_REASON);
        when(exemptionMock.getPlacement()).thenReturn(EXEMPTION_PLACEMENT);
        when(exemptionMock.getType()).thenReturn(EXEMPTION_TYPE);

        testObj.addExemptionResponse(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock).setExemptionResponseReason(EXEMPTION_RESPONSE_REASON);
        verify(paymentTransactionModelMock).setExemptionPlacement(EXEMPTION_PLACEMENT);
        verify(paymentTransactionModelMock).setExemptionResponseResult(EXEMPTION_RESPONSE_RESULT);
        verify(paymentTransactionModelMock).setExemptionType(EXEMPTION_TYPE);
        verify(modelService).save(paymentTransactionModelMock);
    }

    @Test
    public void addExemptionResponse_ShouldNotAddExemptionResponseToPaymentTransactionModelAndSaveIt_WhenPaymentTransactionModelIsNull() {
        testObj.addExemptionResponse(null, paymentReplyMock);

        verify(modelService, never()).save(paymentTransactionModelMock);
    }

    @Test
    public void addExemptionResponse_ShouldNotAddExemptionResponseToPaymentTransactionModelAndSaveIt_WhenPaymentReplyIsNull() {
        testObj.addExemptionResponse(paymentTransactionModelMock, null);

        verify(modelService, never()).save(paymentTransactionModelMock);
    }

}
