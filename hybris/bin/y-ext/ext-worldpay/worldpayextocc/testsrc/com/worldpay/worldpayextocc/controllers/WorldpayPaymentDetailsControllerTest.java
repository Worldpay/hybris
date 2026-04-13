package com.worldpay.worldpayextocc.controllers;

import com.worldpay.facades.WorldpayUserFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayPaymentDetailsControllerTest {

    private static final String PAYMENT_ID = "paymentId";
    private static final String FIELDS = "DEFAULT";

    @Spy
    @InjectMocks
    private WorldpayPaymentDetailsController testObj;

    @Mock
    private WorldpayUserFacade worldpayUserFacadeMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private PaymentDetailsListWsDTO paymentDetailsListWsDTOMock;
    @Mock
    private CCPaymentInfoData paymentInfoDataMock;


    @Test
    public void getPaymentDetailsList_ShouldReturnMappedDto_ForSavedFalse() {
        when(worldpayUserFacadeMock.getAvailableCCPaymentInfos(false)).thenReturn(Collections.emptyList());
        when(dataMapperMock.map(any(CCPaymentInfoDatas.class), eq(PaymentDetailsListWsDTO.class), anyString()))
                .thenReturn(paymentDetailsListWsDTOMock);

        PaymentDetailsListWsDTO result = testObj.getPaymentDetailsList(false, FIELDS);

        assertThat(result).isEqualTo(paymentDetailsListWsDTOMock);
        verify(worldpayUserFacadeMock).getAvailableCCPaymentInfos(false);
        verify(dataMapperMock).map(any(CCPaymentInfoDatas.class), eq(PaymentDetailsListWsDTO.class), eq(FIELDS));
    }

    @Test
    public void getPaymentDetailsList_ShouldReturnMappedDto_ForSavedTrue() {
        when(worldpayUserFacadeMock.getAvailableCCPaymentInfos(true)).thenReturn(Collections.emptyList());
        when(dataMapperMock.map(any(CCPaymentInfoDatas.class), eq(PaymentDetailsListWsDTO.class), anyString()))
                .thenReturn(paymentDetailsListWsDTOMock);

        PaymentDetailsListWsDTO result = testObj.getPaymentDetailsList(true, FIELDS);

        assertThat(result).isEqualTo(paymentDetailsListWsDTOMock);
        verify(worldpayUserFacadeMock).getAvailableCCPaymentInfos(true);
        verify(dataMapperMock).map(any(CCPaymentInfoDatas.class), eq(PaymentDetailsListWsDTO.class), eq(FIELDS));
    }

    @Test
    public void updatePaymentDetails_ShouldSetDefault_WhenSavedAndNotDefault() {
        doReturn(paymentInfoDataMock).when(testObj).callSuperGetPaymentInfo(PAYMENT_ID);
        when(paymentInfoDataMock.isDefaultPaymentInfo()).thenReturn(false);
        when(paymentInfoDataMock.isSaved()).thenReturn(true);

        testObj.updatePaymentDetails(PAYMENT_ID);

        verify(worldpayUserFacadeMock).setDefaultPaymentInfo(paymentInfoDataMock);
    }

    @Test
    public void updatePaymentDetails_ShouldNotSetDefault_WhenNotSaved() {
        doReturn(paymentInfoDataMock).when(testObj).callSuperGetPaymentInfo(PAYMENT_ID);
        when(paymentInfoDataMock.isDefaultPaymentInfo()).thenReturn(false);
        when(paymentInfoDataMock.isSaved()).thenReturn(false);

        testObj.updatePaymentDetails(PAYMENT_ID);

        verify(worldpayUserFacadeMock, never()).setDefaultPaymentInfo(any());
    }

    @Test
    public void updatePaymentDetails_ShouldNotSetDefault_WhenAlreadyDefault() {
        doReturn(paymentInfoDataMock).when(testObj).callSuperGetPaymentInfo(PAYMENT_ID);
        when(paymentInfoDataMock.isDefaultPaymentInfo()).thenReturn(true);
        when(paymentInfoDataMock.isSaved()).thenReturn(true);

        testObj.updatePaymentDetails(PAYMENT_ID);

        verify(worldpayUserFacadeMock, never()).setDefaultPaymentInfo(any());
    }
}
