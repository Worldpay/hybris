package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayHOPNoReturnParamsStrategyTest {

    @InjectMocks
    private DefaultWorldpayHOPNoReturnParamsStrategy testObj;

    @Mock
    private CartService cartServiceMock;
    @Mock
    private CartModel cartModelMock;

    @Before
    public void setUp() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
    }

    @Test
    public void authoriseCart_shouldReturnARedirectAuthorisedResultWithAuthorisedStatusPaymentAmountOfCartAndSetPendingToTrue() {
        final RedirectAuthoriseResult result = testObj.authoriseCart();

        assertThat(result.getPaymentStatus()).isEqualTo(AuthorisedStatus.AUTHORISED);
        assertThat(result.getPaymentAmount()).isEqualTo(BigDecimal.valueOf(cartModelMock.getTotalPrice()));
        assertThat(result.getPending()).isTrue();
    }
}
