package com.worldpay.customer.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCustomerAccountServiceTest {

    private static final String SUBSCRIPTION_ID = "subscriptionId";

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
    @Mock
    private WorldpayAPMPaymentInfoModel apmPaymentInfoModel;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(creditCardPaymentInfoModelMock, apmPaymentInfoModel));
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(apmPaymentInfoModel.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
    }

    @Test
    public void deleteCCPaymentInfo_ShouldDeleteToken_WhenItBelongsToCustomer() throws WorldpayException {
        testObj.deleteCCPaymentInfo(customerModelMock, creditCardPaymentInfoModelMock);

        verify(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);
        verify(modelService).save(creditCardPaymentInfoModelMock);
        verify(modelService).refresh(customerModelMock);
    }

    @Test
    public void deleteAPMPaymentInfo_ShouldDeleteToken_WhenItBelongsToCustomer() throws WorldpayException {
        testObj.deleteAPMPaymentInfo(customerModelMock, apmPaymentInfoModel);

        verify(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, apmPaymentInfoModel, SUBSCRIPTION_ID);
        verify(modelService).save(apmPaymentInfoModel);
        verify(modelService).refresh(customerModelMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCCPaymentInfo_ShouldNotDeleteTokenAndThrowIllegalArgumentException_WhenItDoesNotBelongToCustomer() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(apmPaymentInfoModel));

        testObj.deleteCCPaymentInfo(customerModelMock, creditCardPaymentInfoModelMock);

        verifyNoInteractions(worldpayDirectOrderServiceMock);
        verifyNoInteractions(modelService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteAPMPaymentInfo_ShouldNotDeleteTokenAndThrowIllegalArgumentException_WhenItDoesNotBelongToCustomer() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(creditCardPaymentInfoModelMock));

        testObj.deleteAPMPaymentInfo(customerModelMock, apmPaymentInfoModel);

        verifyNoInteractions(worldpayDirectOrderServiceMock);
        verifyNoInteractions(modelService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteAPMPaymentInfo_ShouldThrowIllegalArgumentException_WhenCustomerIsNuLL() {
        testObj.deleteAPMPaymentInfo(null, apmPaymentInfoModel);

        verifyNoInteractions(worldpayDirectOrderServiceMock);
        verifyNoInteractions(modelService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCCPaymentInfo_ShouldThrowIllegalArgumentException_WhenCustomerIsNuLL() {
        testObj.deleteCCPaymentInfo(null, creditCardPaymentInfoModelMock);

        verifyNoInteractions(worldpayDirectOrderServiceMock);
        verifyNoInteractions(modelService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteAPMPaymentInfo_ShouldThrowIllegalArgumentException_WhenPaymentInfoIsNuLL() {
        testObj.deleteAPMPaymentInfo(customerModelMock, null);

        verifyNoInteractions(worldpayDirectOrderServiceMock);
        verifyNoInteractions(modelService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCCPaymentInfo_ShouldThrowIllegalArgumentException_WhenPaymentInfoIsNuLL() {
        testObj.deleteCCPaymentInfo(customerModelMock, null);

        verifyNoInteractions(worldpayDirectOrderServiceMock);
        verifyNoInteractions(modelService);
    }

    @Test
    public void deleteCCPaymentInfo_WhenDeleteTokenCallThrowsAnException_ShouldHandleIt() throws WorldpayException {
        doThrow(new WorldpayException("Exception in delete token")).when(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);

        testObj.deleteCCPaymentInfo(customerModelMock, creditCardPaymentInfoModelMock);
    }

    @Test
    public void deleteAPMPaymentInfo_WhenDeleteTokenCallThrowsAnException_ShouldHandleIt() throws WorldpayException {
        doThrow(new WorldpayException("Exception in delete token")).when(worldpayDirectOrderServiceMock).deleteToken(merchantInfoMock, apmPaymentInfoModel, SUBSCRIPTION_ID);

        testObj.deleteAPMPaymentInfo(customerModelMock, apmPaymentInfoModel);
    }
}
