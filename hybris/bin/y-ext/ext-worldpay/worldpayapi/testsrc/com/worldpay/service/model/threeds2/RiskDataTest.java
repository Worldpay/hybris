package com.worldpay.service.model.threeds2;

import com.worldpay.exception.WorldpayModelTransformationException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RiskDataTest {

    @InjectMocks
    private RiskData testObj;

    @Mock
    private TransactionRiskData transactionRiskDataMock;
    @Mock
    private ShopperAccountRiskData shopperAccountRiskDataMock;
    @Mock
    private AuthenticationRiskData authenticationRiskDataMock;

    @Mock
    private com.worldpay.internal.model.TransactionRiskData intTransactionRiskDataMock;
    @Mock
    private com.worldpay.internal.model.ShopperAccountRiskData intShopperAccountRiskDataMock;
    @Mock
    private com.worldpay.internal.model.AuthenticationRiskData intAuthenticationRiskDataMock;

    @Before
    public void setUp() throws Exception {
        when(transactionRiskDataMock.transformToInternalModel()).thenReturn(intTransactionRiskDataMock);
        when(shopperAccountRiskDataMock.transformToInternalModel()).thenReturn(intShopperAccountRiskDataMock);
        when(authenticationRiskDataMock.transformToInternalModel()).thenReturn(intAuthenticationRiskDataMock);
    }

    @Test
    public void transformToInternalModel_ShouldSetAllMembersOfRiskData() throws WorldpayModelTransformationException {
        final com.worldpay.internal.model.RiskData result = testObj.transformToInternalModel();

        assertThat(result.getAuthenticationRiskData()).isEqualTo(intAuthenticationRiskDataMock);
        assertThat(result.getShopperAccountRiskData()).isEqualTo(intShopperAccountRiskDataMock);
        assertThat(result.getTransactionRiskData()).isEqualTo(intTransactionRiskDataMock);
    }

    @Test
    public void transformToInternalModel_ShouldNotAddTransactionRiskData_WhenNull() throws WorldpayModelTransformationException {
        testObj.setTransactionRiskData(null);

        final com.worldpay.internal.model.RiskData result = testObj.transformToInternalModel();

        assertThat(result.getTransactionRiskData()).isEqualTo(null);
    }

    @Test
    public void transformToInternalModel_ShouldNotAddShopperAccountRiskData_WhenNull() throws WorldpayModelTransformationException {
        testObj.setShopperAccountRiskData(null);

        final com.worldpay.internal.model.RiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountRiskData()).isEqualTo(null);
    }

    @Test
    public void transformToInternalModel_ShouldNotAddAuthenticationRiskData_WhenNull() throws WorldpayModelTransformationException {
        testObj.setAuthenticationRiskData(null);

        final com.worldpay.internal.model.RiskData result = testObj.transformToInternalModel();

        assertThat(result.getAuthenticationRiskData()).isEqualTo(null);
    }
}
