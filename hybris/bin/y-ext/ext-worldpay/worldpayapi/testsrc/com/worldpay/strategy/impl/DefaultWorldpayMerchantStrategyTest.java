package com.worldpay.strategy.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantStrategyTest {

    @InjectMocks
    private DefaultWorldpayMerchantStrategy testObj;

    @Mock
    private AssistedServiceService assistedServiceServiceMock;
    @Mock
    private WorldpayMerchantConfigurationService worldpayMerchantConfigurationServiceMock;
    @Mock
    private WorldpayMerchantConfigurationModel websiteMerchantConfigMock, asmMerchantConfigMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AssistedServiceSession asmSessionMock;
    @Mock
    private UserModel userModelMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayMerchantConfigurationServiceMock.getCurrentWebConfiguration()).thenReturn(websiteMerchantConfigMock);
        when(worldpayMerchantConfigurationServiceMock.getCurrentAsmConfiguration()).thenReturn(asmMerchantConfigMock);
    }

    @Test
    public void getMerchant_whenIsNotAsmSession_shouldReturnWebMerchant() {
        when(assistedServiceServiceMock.getAsmSession()).thenReturn(null);

        final WorldpayMerchantConfigurationModel result = testObj.getMerchant();

        assertEquals(websiteMerchantConfigMock, result);
    }

    @Test
    public void getMerchant_whenIsAsmSession_shouldReturnAsmMerchant() {
        when(assistedServiceServiceMock.getAsmSession()).thenReturn(asmSessionMock);
        when(assistedServiceServiceMock.getAsmSession().getAgent()).thenReturn(userModelMock);

        final WorldpayMerchantConfigurationModel result = testObj.getMerchant();

        assertEquals(asmMerchantConfigMock, result);
    }
}
