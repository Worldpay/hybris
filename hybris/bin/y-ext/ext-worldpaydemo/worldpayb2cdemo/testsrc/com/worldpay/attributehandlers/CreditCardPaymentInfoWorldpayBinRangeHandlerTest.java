package com.worldpay.attributehandlers;

import com.worldpay.constants.Worldpayb2cdemoConstants;
import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.service.WorldpayBinRangeService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreditCardPaymentInfoWorldpayBinRangeHandlerTest {

    @InjectMocks
    private CreditCardPaymentInfoWorldpayBinRangeHandler creditCardPaymentInfoWorldpayBinRangeHandler;
    @Mock
    private WorldpayBinRangeService worldpayBinRangeService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configurationMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoMock;
    @Mock
    private WorldpayBinRangeModel worldpayBinRangeModelMock;

    @Test
    public void shouldReturnOneBinRangeWithMaskingAfterFourDigits() throws Exception {
        Mockito.when(creditCardPaymentInfoMock.getNumber()).thenReturn("4111********1111");
        Mockito.when(worldpayBinRangeService.getBinRange("4111")).thenReturn(worldpayBinRangeModelMock);

        WorldpayBinRangeModel binRange = creditCardPaymentInfoWorldpayBinRangeHandler.get(creditCardPaymentInfoMock);

        Assert.assertEquals(worldpayBinRangeModelMock, binRange);
    }

    @Test
    public void shouldReturnOneBinRangeWithNoMasking() throws Exception {
        Mockito.when(creditCardPaymentInfoMock.getNumber()).thenReturn("4111111111111111");
        Mockito.when(worldpayBinRangeService.getBinRange("411111111111")).thenReturn(worldpayBinRangeModelMock);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configurationMock);
        Mockito.when(configurationMock.getInt(Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_PROPERTY)).thenReturn(12);

        WorldpayBinRangeModel binRange = creditCardPaymentInfoWorldpayBinRangeHandler.get(creditCardPaymentInfoMock);

        Assert.assertEquals(worldpayBinRangeModelMock, binRange);
    }

    @Test
    public void shouldReturnZeroBinRangeWithNoMasking() throws Exception {
        Mockito.when(creditCardPaymentInfoMock.getNumber()).thenReturn("5111********1111");
        Mockito.when(worldpayBinRangeService.getBinRange("5111")).thenReturn(null);

        WorldpayBinRangeModel binRange = creditCardPaymentInfoWorldpayBinRangeHandler.get(creditCardPaymentInfoMock);

        Assert.assertNull(binRange);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void shouldReturnUnsupportedOperationExceptionWhenCallingSet() {
        creditCardPaymentInfoWorldpayBinRangeHandler.set(creditCardPaymentInfoMock, worldpayBinRangeModelMock);
    }
}