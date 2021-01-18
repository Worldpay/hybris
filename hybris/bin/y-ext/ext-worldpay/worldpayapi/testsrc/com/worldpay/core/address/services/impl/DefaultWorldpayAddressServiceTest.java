package com.worldpay.core.address.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAddressServiceTest {

    @InjectMocks
    private DefaultWorldpayAddressService testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AddressModel addressModelMock;

    @Test
    public void setCartPaymentAddress_ShouldUpdateTheCartCorrectly() {

        testObj.setCartPaymentAddress(cartModelMock, addressModelMock);

        verify(cartModelMock).setPaymentAddress(addressModelMock);
        verify(modelServiceMock).save(cartModelMock);
    }
}
