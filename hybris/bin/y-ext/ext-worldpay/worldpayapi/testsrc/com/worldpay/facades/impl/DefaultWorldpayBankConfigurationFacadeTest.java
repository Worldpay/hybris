package com.worldpay.facades.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.facades.BankConfigurationData;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayBankConfigurationFacadeTest {

    private static final String APM_CODE = "apmCode";
    private static final String BANK_TRANSFER_APM = "bankTransferAPM";
    private static final String PAYMENT_METHOD = "payment";

    @InjectMocks
    private DefaultWorldpayBankConfigurationFacade testObj;
    
    @Mock
    private WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupServiceMock;
    @Mock
    private Converter<WorldpayBankConfigurationModel, BankConfigurationData> converterMock;
    @Mock
    private WorldpayBankConfigurationModel worldpayBankConfigurationModelMock;
    @Mock
    private BankConfigurationData bankConfigurationDataMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationModelMock;

    @Test
    public void testGetBankConfigurationForAPMCode() {
        when(converterMock.convert(eq(worldpayBankConfigurationModelMock))).thenReturn(bankConfigurationDataMock);
        when(worldpayBankConfigurationLookupServiceMock.getActiveBankConfigurationsForCode(APM_CODE)).thenReturn(singletonList(worldpayBankConfigurationModelMock));

        final List<BankConfigurationData> bankConfigurationForAPMCode = testObj.getBankConfigurationForAPMCode(APM_CODE);

        assertTrue(bankConfigurationForAPMCode.contains(bankConfigurationDataMock));
    }

    @Test
    public void isBankTransferApmShouldReturnTrueIfPaymentMethodIsApmThatSupportsBankTransfer() {
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(BANK_TRANSFER_APM)).thenReturn(apmConfigurationModelMock);
        when(apmConfigurationModelMock.getBank()).thenReturn(true);

        final boolean result = testObj.isBankTransferApm(BANK_TRANSFER_APM);

        assertTrue(result);
    }

    @Test
    public void isBankTransferApmShouldReturnFalseIfPaymentMethodIsNotApmThatSupportsBankTransfer() {
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_CODE)).thenReturn(apmConfigurationModelMock);
        when(apmConfigurationModelMock.getBank()).thenReturn(false);

        final boolean result = testObj.isBankTransferApm(APM_CODE);

        assertFalse(result);
    }

    @Test
    public void isBankTransferApmShouldReturnFalseIfPaymentMethodIsNotApm() {
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PAYMENT_METHOD)).thenReturn(null);

        final boolean result = testObj.isBankTransferApm(PAYMENT_METHOD);

        assertFalse(result);
    }

}
    

