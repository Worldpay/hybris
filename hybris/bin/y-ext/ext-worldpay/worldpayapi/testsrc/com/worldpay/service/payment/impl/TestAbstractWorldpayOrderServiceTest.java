package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractWorldpayOrderServiceTest {

    private AbstractWorldpayOrderService testObj;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;

    @Before
    public void setUp() throws Exception {
        testObj = Mockito.mock(AbstractWorldpayOrderService.class, Mockito.CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(testObj, "worldpayPaymentInfoService", worldpayPaymentInfoServiceMock);
    }

    @Test
    public void cloneAndSetBillingAddressFromCart_ShouldCloneAndSetBillingAddressFromCart_WhenItIsCalled() {
        testObj.cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);

        verify(worldpayPaymentInfoServiceMock).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
    }
}
