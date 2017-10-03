package com.worldpay.strategy.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantStrategyTest {

    private static final String WEB = "web";
    private static final String ASM_MERCHANT = "asm";
    private static final String REPLENISHMENT_MERCHANT = "replenishment";

    @InjectMocks
    private DefaultWorldpayMerchantStrategy testObj;

    @Mock
    private AssistedServiceService assistedServiceServiceMock;
    @Mock
    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataServiceMock;
    @Mock
    private WorldpayMerchantConfigData websiteMerchantConfigDataMock, asmMerchantConfigDataMock, replenishmentMerchantConfigDataMock;
    @Mock
    private Map<String, WorldpayMerchantConfigData> merchantConfigDataMapMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AssistedServiceSession asmSessionMock;
    @Mock
    private UserModel userModelMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayMerchantConfigDataServiceMock.getMerchantConfiguration()).thenReturn(merchantConfigDataMapMock);
        when(merchantConfigDataMapMock.get(WEB)).thenReturn(websiteMerchantConfigDataMock);
        when(merchantConfigDataMapMock.get(ASM_MERCHANT)).thenReturn(asmMerchantConfigDataMock);
        when(merchantConfigDataMapMock.get(REPLENISHMENT_MERCHANT)).thenReturn(replenishmentMerchantConfigDataMock);
    }

    @Test
    public void shouldReturnWebMerchant() throws Exception {
        when(assistedServiceServiceMock.getAsmSession()).thenReturn(null);

        final WorldpayMerchantConfigData result = testObj.getMerchant();

        assertEquals(websiteMerchantConfigDataMock, result);
    }

    @Test
    public void shouldReturnAsmMerchant() throws Exception {
        when(assistedServiceServiceMock.getAsmSession()).thenReturn(asmSessionMock);
        when(assistedServiceServiceMock.getAsmSession().getAgent()).thenReturn(userModelMock);

        final WorldpayMerchantConfigData result = testObj.getMerchant();

        assertEquals(asmMerchantConfigDataMock, result);
    }

    @Test
    public void shouldReturnReplenishmentMerchant() throws Exception {
        final WorldpayMerchantConfigData result = testObj.getReplenishmentMerchant();

        assertEquals(replenishmentMerchantConfigDataMock, result);
    }
}
