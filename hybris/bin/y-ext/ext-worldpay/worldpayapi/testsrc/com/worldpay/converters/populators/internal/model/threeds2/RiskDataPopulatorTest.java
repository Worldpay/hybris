package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.threeds2.AuthenticationRiskData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.threeds2.ShopperAccountRiskData;
import com.worldpay.data.threeds2.TransactionRiskData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RiskDataPopulatorTest {

    @Spy
    @InjectMocks
    private RiskDataPopulator testObj;

    @Mock
    private Converter<AuthenticationRiskData, com.worldpay.internal.model.AuthenticationRiskData> internalAuthenticationRiskDataConverterMock;
    @Mock
    private Converter<ShopperAccountRiskData, com.worldpay.internal.model.ShopperAccountRiskData> internalShopperAccountRiskDataConverterMock;
    @Mock
    private Converter<TransactionRiskData, com.worldpay.internal.model.TransactionRiskData> internalTransactionRiskDataConverterMock;

    @Mock
    private RiskData sourceMock;
    @Mock
    private com.worldpay.internal.model.AuthenticationRiskData internalAuthenticationRiskDataMock;
    @Mock
    private com.worldpay.internal.model.ShopperAccountRiskData internalShopperAccountRiskDataMock;
    @Mock
    private com.worldpay.internal.model.TransactionRiskData internalTransactionRiskDataMock;
    @Mock
    private AuthenticationRiskData authenticationRiskDataMock;
    @Mock
    private ShopperAccountRiskData shopperAccountRiskData;
    @Mock
    private TransactionRiskData transactionRiskData;

    @Before
    public void setup() {
        testObj = new RiskDataPopulator(internalAuthenticationRiskDataConverterMock, internalShopperAccountRiskDataConverterMock, internalTransactionRiskDataConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.RiskData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenAuthenticationRiskDataIsNull_ShouldNotPopulate() {
        when(sourceMock.getAuthenticationRiskData()).thenReturn(null);
        when(internalAuthenticationRiskDataConverterMock.convert(sourceMock.getAuthenticationRiskData())).thenReturn(null);

        final com.worldpay.internal.model.RiskData targetMock = new com.worldpay.internal.model.RiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticationRiskData()).isNull();
    }

    @Test
    public void populate_WhenAuthenticationShopperAccountRiskDataIsNull_ShouldNotPopulate() {
        when(sourceMock.getShopperAccountRiskData()).thenReturn(null);
        when(internalShopperAccountRiskDataConverterMock.convert(sourceMock.getShopperAccountRiskData())).thenReturn(null);

        final com.worldpay.internal.model.RiskData targetMock = new com.worldpay.internal.model.RiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getShopperAccountRiskData()).isNull();
    }

    @Test
    public void populate_WhenAuthenticationTransactionRiskDataIsNull_ShouldNotPopulate() {
        when(sourceMock.getTransactionRiskData()).thenReturn(null);
        when(internalTransactionRiskDataConverterMock.convert(sourceMock.getTransactionRiskData())).thenReturn(null);

        final com.worldpay.internal.model.RiskData targetMock = new com.worldpay.internal.model.RiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getTransactionRiskData()).isNull();
    }

    @Test
    public void populate_ShouldPopulatRiskData() {
        when(sourceMock.getAuthenticationRiskData()).thenReturn(authenticationRiskDataMock);
        when(sourceMock.getShopperAccountRiskData()).thenReturn(shopperAccountRiskData);
        when(sourceMock.getTransactionRiskData()).thenReturn(transactionRiskData);
        given(internalAuthenticationRiskDataConverterMock.convert(authenticationRiskDataMock)).willReturn(internalAuthenticationRiskDataMock);
        given(internalShopperAccountRiskDataConverterMock.convert(shopperAccountRiskData)).willReturn(internalShopperAccountRiskDataMock);
        given(internalTransactionRiskDataConverterMock.convert(transactionRiskData)).willReturn(internalTransactionRiskDataMock);

        final com.worldpay.internal.model.RiskData targetMock = new com.worldpay.internal.model.RiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticationRiskData()).isEqualTo(internalAuthenticationRiskDataMock);
        assertThat(targetMock.getShopperAccountRiskData()).isEqualTo(internalShopperAccountRiskDataMock);
        assertThat(targetMock.getTransactionRiskData()).isEqualTo(internalTransactionRiskDataMock);
    }
}
