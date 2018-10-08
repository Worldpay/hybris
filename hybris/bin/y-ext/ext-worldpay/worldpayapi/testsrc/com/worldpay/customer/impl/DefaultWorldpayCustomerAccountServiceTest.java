package com.worldpay.customer.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCustomerAccountServiceTest {

    @InjectMocks
    private DefaultWorldpayCustomerAccountService testObj;

    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private WorldpayDirectOrderService worldpayDirectOrderServiceMock;
    @Mock
    private ModelService modelService;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(customerModelMock.getPaymentInfos()).thenReturn(Collections.singletonList(creditCardPaymentInfoModelMock));
    }

    @Test
    public void testDeleteTokenCalled() throws WorldpayException {
        testObj.deleteCCPaymentInfo(customerModelMock, creditCardPaymentInfoModelMock);

        verify(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock);
        verify(modelService).save(creditCardPaymentInfoModelMock);
        verify(modelService).refresh(customerModelMock);
    }

    @Test
    public void testHandleExceptionsFromDeleteTokenCall() throws WorldpayException {
        doThrow(new WorldpayException("Exception in delete token")).when(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock);

        testObj.deleteCCPaymentInfo(customerModelMock, creditCardPaymentInfoModelMock);
    }
}
