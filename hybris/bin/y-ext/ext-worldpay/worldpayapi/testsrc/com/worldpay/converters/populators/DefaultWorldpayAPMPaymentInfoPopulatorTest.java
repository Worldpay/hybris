package com.worldpay.converters.populators;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAPMPaymentInfoPopulatorTest {

    private static final String APM_NAME = "apmName";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final long PK = 1234L;
    private static final String OBFUSCATED_CART_NUMBER = "obfuscatedCartNumber";

    @InjectMocks
    private DefaultWorldpayAPMPaymentInfoPopulator testObj;
    @Mock
    private CCPaymentInfoData targetMock;
    @Mock
    private WorldpayAPMPaymentInfoModel sourceMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;

    @Before
    public void setUp() throws Exception {
        when(sourceMock.getApmConfiguration()).thenReturn(apmConfigurationMock);
        when(sourceMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(sourceMock.getPk()).thenReturn(de.hybris.platform.core.PK.fromLong(PK));
        when(sourceMock.getObfuscatedCardNumber()).thenReturn(OBFUSCATED_CART_NUMBER);
        when(apmConfigurationMock.getName()).thenReturn(APM_NAME);
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
}
