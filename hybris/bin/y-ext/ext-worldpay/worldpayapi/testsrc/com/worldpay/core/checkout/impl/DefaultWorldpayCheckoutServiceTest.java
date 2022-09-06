package com.worldpay.core.checkout.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCheckoutServiceTest {

    @InjectMocks
    private DefaultWorldpayCheckoutService testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private AddressService addressServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AddressModel addressModelMock, clonedAddressMock;

    @Test
    public void setPaymentAddress_ShouldSetPaymentAddressAndSaveCart() {

        testObj.setPaymentAddress(cartModelMock, addressModelMock);

        final InOrder inOrder = Mockito.inOrder(cartModelMock, modelServiceMock);
        inOrder.verify(cartModelMock).setPaymentAddress(addressModelMock);
        inOrder.verify(modelServiceMock).save(cartModelMock);
        inOrder.verify(modelServiceMock).refresh(cartModelMock);
    }

    @Test
    public void setShippingAndPaymentAddress_ShouldSetShippingAndPaymentAddressAndSaveCart() {
        when(addressServiceMock.cloneAddress(addressModelMock)).thenReturn(clonedAddressMock);

        testObj.setShippingAndPaymentAddress(cartModelMock, addressModelMock);

        final InOrder inOrder = Mockito.inOrder(cartModelMock, modelServiceMock);
        inOrder.verify(cartModelMock).setDeliveryAddress(addressModelMock);
        inOrder.verify(cartModelMock).setPaymentAddress(clonedAddressMock);
        inOrder.verify(modelServiceMock).saveAll(clonedAddressMock, cartModelMock);
        inOrder.verify(modelServiceMock).refresh(cartModelMock);
    }
}
