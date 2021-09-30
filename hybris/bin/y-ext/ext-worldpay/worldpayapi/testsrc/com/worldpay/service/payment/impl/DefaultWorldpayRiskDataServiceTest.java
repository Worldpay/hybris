package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.threeds2.RiskData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRiskDataServiceTest {

    @InjectMocks
    private DefaultWorldpayRiskDataService testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private CartModel cartModelMock;

    @Test
    public void testCreateRiskData() {
        final RiskData result = testObj.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock);

        assertNotNull(result);
        assertNotNull(result.getAuthenticationRiskData());
        assertNotNull(result.getShopperAccountRiskData());
        assertNotNull(result.getTransactionRiskData());
    }
}
