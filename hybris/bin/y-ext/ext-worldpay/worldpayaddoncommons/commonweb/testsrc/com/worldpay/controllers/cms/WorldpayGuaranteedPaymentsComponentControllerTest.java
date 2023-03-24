package com.worldpay.controllers.cms;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayGuaranteedPaymentsComponentControllerTest {

    private static final String IS_ENABLED = "isEnabled";
    private static final String SCRIPT = "script";
    private static final String SCRIPT_URL = "worldpay.guaranteed.payments.script.url";
    private static final String SESSION_ID = "sessionId";
    private static final String URL = "url";
    private static final String CHECKOUT_ID = "checkoutId";

    @Spy
    @InjectMocks
    private WorldpayGuaranteedPaymentsComponentController testObj;

    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private ConfigurationService configurationServiceMock;

    @Mock
    private Configuration configurationMock;
    @Mock
    private Model model;

    @Test
    public void testDataAreFilled_whenGPIsEnable_shouldReturnTrue() {
        when(worldpayPaymentCheckoutFacadeMock.isGPEnabled()).thenReturn(Boolean.TRUE);
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(SCRIPT_URL)).thenReturn(URL);
        when(worldpayPaymentCheckoutFacadeMock.createCheckoutId()).thenReturn(CHECKOUT_ID);
        doNothing().when(testObj).invokeSuperFillModel(null, model, null);

        testObj.fillModel(null, model, null);

        verify(model).addAttribute(IS_ENABLED, Boolean.TRUE);
        verify(model).addAttribute(SESSION_ID, CHECKOUT_ID);
        verify(model).addAttribute(SCRIPT, URL);
    }

    @Test
    public void testDataAreFilled_whenGPIsNoptEnable_shouldReturnFalse() {
        when(worldpayPaymentCheckoutFacadeMock.isGPEnabled()).thenReturn(Boolean.FALSE);
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(SCRIPT_URL)).thenReturn(URL);
        when(worldpayPaymentCheckoutFacadeMock.createCheckoutId()).thenReturn(CHECKOUT_ID);
        doNothing().when(testObj).invokeSuperFillModel(null, model, null);

        testObj.fillModel(null, model, null);

        verify(model).addAttribute(IS_ENABLED, Boolean.FALSE);
        verify(model).addAttribute(SESSION_ID, CHECKOUT_ID);
        verify(model).addAttribute(SCRIPT, URL);

    }
}
