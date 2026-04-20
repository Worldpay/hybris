package com.worldpay.converters.populators;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAPMPaymentInfoPopulatorTest {

    private static final String APM_NAME = "apmName";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final long PK = 1234L;
    private static final String OBFUSCATED_CART_NUMBER = "obfuscatedCartNumber";
    private static final String EXPIRY_MONTH = "1";
    private static final String EXPIRY_YEAR = "2025";
    private static final String APM_CODE = "APM_CODE";

    @InjectMocks
    private DefaultWorldpayAPMPaymentInfoPopulator testObj;

    @Mock
    private Converter<AddressModel, AddressData> addressConverterMock;

    @Mock
    private CCPaymentInfoData targetMock;
    @Mock
    private WorldpayAPMPaymentInfoModel sourceMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private AddressData addressDataMock;


    @Before
    public void setUp() {
        when(sourceMock.getApmConfiguration()).thenReturn(apmConfigurationMock);
        when(sourceMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(sourceMock.getPk()).thenReturn(de.hybris.platform.core.PK.fromLong(PK));
        when(sourceMock.getObfuscatedCardNumber()).thenReturn(OBFUSCATED_CART_NUMBER);
        when(sourceMock.getExpiryMonth()).thenReturn(EXPIRY_MONTH);
        when(sourceMock.getExpiryYear()).thenReturn(EXPIRY_YEAR);
        when(apmConfigurationMock.getName()).thenReturn(APM_NAME);
        when(apmConfigurationMock.getCode()).thenReturn(APM_CODE);
        when(sourceMock.getBillingAddress()).thenReturn(addressModelMock);
        when(addressConverterMock.convert(addressModelMock)).thenReturn(addressDataMock);
        when(sourceMock.isSaved()).thenReturn(true);
    }

    @Test
    public void populate_ShouldSetCartType() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setCardType(APM_CODE);
    }

    @Test
    public void populate_ShouldMarkTargetAsSaved() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setSaved(true);
    }

    @Test
    public void populate_ShouldAddAddressData() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setBillingAddress(addressDataMock);
    }

    @Test
    public void populate_ShouldMarkTargetAsAPM() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setIsAPM(true);
    }

    @Test
    public void populate_ShouldAddIdUsingPK() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setId(String.valueOf(PK));
    }

    @Test
    public void populate_ShouldAddAPMName() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setApmName(APM_NAME);
    }

    @Test
    public void populate_ShouldAddSubscriptionId() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setSubscriptionId(SUBSCRIPTION_ID);
    }

    @Test
    public void populate_ShouldAddCreditCardNumber() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setCardNumber(OBFUSCATED_CART_NUMBER);
    }

    @Test
    public void populate_ShouldAddExpiryDateAndYear() {
        testObj.populate(sourceMock, targetMock);

        verify(targetMock).setExpiryMonth(EXPIRY_MONTH);
        verify(targetMock).setExpiryYear(EXPIRY_YEAR);
    }
}
