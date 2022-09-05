package com.worldpay.facades.order.converters.populators;

import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAPMPaymentInfoPopulatorTest {

    private static final String APM_NAME = "apmName";
    private static final String SUBSCRIPTION_ID = "subscriptionId";

    @InjectMocks
    private WorldpayAPMPaymentInfoPopulator testObj;

    @Mock
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private AbstractOrderData abstractOrderDataMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Captor
    private ArgumentCaptor<WorldpayAPMPaymentInfoData> worldpayAPMPaymentInfoDataCaptor;

    @Test
    public void populate_ShouldNotSetApmName_WhenThereIsNoApm() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(null);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(abstractOrderDataMock, never()).setWorldpayAPMPaymentInfo(worldpayAPMPaymentInfoDataCaptor.capture());

    }

    @Test
    public void populate_ShouldSetApmName_WhenAPMIsPresent() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMConfigurationModelMock.getName()).thenReturn(APM_NAME);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(abstractOrderDataMock).setWorldpayAPMPaymentInfo(worldpayAPMPaymentInfoDataCaptor.capture());
        var worldpayAPMPaymentInfoData = worldpayAPMPaymentInfoDataCaptor.getValue();
        assertThat(worldpayAPMPaymentInfoData.getName()).isEqualTo(APM_NAME);
    }

    @Test
    public void populate_ShouldSetSubscriptionId_WhenAPMHasToken() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(abstractOrderDataMock).setWorldpayAPMPaymentInfo(worldpayAPMPaymentInfoDataCaptor.capture());
        var worldpayAPMPaymentInfoData = worldpayAPMPaymentInfoDataCaptor.getValue();
        assertThat(worldpayAPMPaymentInfoData.getSubscriptionId()).isEqualTo(SUBSCRIPTION_ID);
    }
}
