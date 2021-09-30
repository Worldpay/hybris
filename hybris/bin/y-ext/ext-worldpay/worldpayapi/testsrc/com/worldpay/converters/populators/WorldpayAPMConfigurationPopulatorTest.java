package com.worldpay.converters.populators;

import com.worldpay.data.apm.WorldpayAPMConfigurationData;
import com.worldpay.facades.BankConfigurationData;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAPMConfigurationPopulatorTest {

    private static final String APM_CODE = "code";
    private static final String APM_NAME = "name";
    private static final String APM_DESCRIPTION = "description";

    @InjectMocks
    private WorldpayAPMConfigurationPopulator testObj;

    @Mock
    private WorldpayBankConfigurationFacade worldpayBankConfigurationFacadeMock;

    @Mock
    private WorldpayAPMConfigurationModel sourceMock;
    @Mock
    private BankConfigurationData bankConfigurationData1Mock, bankConfigurationData2Mock;

    @Before
    public void setUp() throws Exception {
        when(sourceMock.getCode()).thenReturn(APM_CODE);
        when(sourceMock.getName()).thenReturn(APM_NAME);
        when(sourceMock.getDescription()).thenReturn(APM_DESCRIPTION);
        when(sourceMock.getAutoCancelPendingTimeoutInMinutes()).thenReturn(1);
        when(sourceMock.getBank()).thenReturn(TRUE);
        when(sourceMock.getAutomaticRefunds()).thenReturn(FALSE);
        when(sourceMock.getBankTransferRefunds()).thenReturn(TRUE);
        when(worldpayBankConfigurationFacadeMock.getBankConfigurationForAPMCode(APM_CODE)).thenReturn(List.of(bankConfigurationData1Mock, bankConfigurationData2Mock));
    }

    @Test
    public void populate_ShouldPopulateApmData() {
        final WorldpayAPMConfigurationData target = new WorldpayAPMConfigurationData();

        testObj.populate(sourceMock, target);

        assertThat(target.getCode()).isEqualTo(APM_CODE);
        assertThat(target.getName()).isEqualTo(APM_NAME);
        assertThat(target.getDescription()).isEqualTo(APM_DESCRIPTION);
        assertThat(target.getAutoCancelPendingTimeoutInMinutes()).isEqualTo(1);
        assertThat(target.getBank()).isEqualTo(TRUE);
        assertThat(target.getAutomaticRefunds()).isEqualTo(FALSE);
        assertThat(target.getBankTransferRefunds()).isEqualTo(TRUE);
        assertThat(target.getBankConfigurations()).isEqualTo(List.of(bankConfigurationData1Mock, bankConfigurationData2Mock));
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new WorldpayAPMConfigurationData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }
}
