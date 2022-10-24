package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPrimeRoutingService;
import com.worldpay.enums.PaymentAction;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPrimeRoutingStrategyTest {

    @InjectMocks
    private DefaultWorldpayPrimeRoutingStrategy testObj;

    @Mock
    private WorldpayPrimeRoutingService worldpayPrimeRoutingServiceMock;

    @Mock
    private CartModel cartMock;
    @Mock
    private AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;

    @Test
    public void populateRequestWithAdditionalData_WhenPrimeRoutingIsDisabled_ShouldDoNothing() {
        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verifyNoInteractions(authoriseRequestParametersCreatorMock);
        verify(worldpayPrimeRoutingServiceMock, never()).setAuthorisedWithPrimeRoutingOnCart(cartMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenPrimeRoutingIsEnabled_ShouldSetSaleAction() {
        when(worldpayPrimeRoutingServiceMock.isPrimeRoutingEnabled(cartMock)).thenReturn(Boolean.TRUE);
        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withPaymentDetailsAction(PaymentAction.SALE);
        verify(worldpayPrimeRoutingServiceMock).setAuthorisedWithPrimeRoutingOnCart(cartMock);
    }
}
