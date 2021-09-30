package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.Address;
import com.worldpay.data.CustomNumericFields;
import com.worldpay.data.CustomStringFields;
import com.worldpay.data.ShopperFields;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTestWorldpayFraudSightStrategyTest {

    private static final String ADDRESS_LINE_2 = "Address Line 2";

    @Spy
    @InjectMocks
    private DefaultTestWorldpayFraudSightStrategy testObj;

    @Mock
    private ShopperFields shopperFieldsMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private AddressModel addressMock;
    @Mock
    private Address convertedAddressMock;

    @Test
    public void createShopperFields_ShouldAddTheAddressLine3() {
        doReturn(shopperFieldsMock).when(testObj).callSuperCreateShopperFields(cartMock, worldpayAdditionalInfoDataMock);
        when(shopperFieldsMock.getShopperAddress()).thenReturn(convertedAddressMock);
        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(addressMock.getLine2()).thenReturn(ADDRESS_LINE_2);

        testObj.createShopperFields(cartMock, worldpayAdditionalInfoDataMock);

        verify(convertedAddressMock).setAddress3(ADDRESS_LINE_2);
    }

    @Test
    public void createCustomNumericFields_ShouldCreateCustomNumericFields() {
        final CustomNumericFields result = testObj.createCustomNumericFields();

        assertThat(result).isNotNull();
        assertThat(result.getCustomNumericField1()).isEqualTo(123);
    }

    @Test
    public void createCustomStringFields_ShouldCreateCustomStringFields() {
        final CustomStringFields result = testObj.createCustomStringFields();

        assertThat(result).isNotNull();
        assertThat(result.getCustomStringField1()).isEqualTo("abc");
    }
}
