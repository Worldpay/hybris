package com.worldpay.service.payment.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayEFTPOSRoutingStrategyTest {

    private static final String ROUTING_MID = "routingMID";

    @InjectMocks
    private DefaultWorldpayEFTPOSRoutingStrategy testObj;

    @Mock
    private WorldpayMerchantConfigurationService worldpayMerchantConfigurationServiceMock;
    @Mock
    private AbstractOrderModel cartMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;
    @Mock
    private WorldpayMerchantConfigurationModel worldpayMerchantConfigurationModelMock;

    @Test
    public void populateRequestWithAdditionalData_shouldPopulate_WhenRoutingEnabled() {
        when(worldpayMerchantConfigurationServiceMock.getCurrentWebConfiguration()).thenReturn(worldpayMerchantConfigurationModelMock);
        when(worldpayMerchantConfigurationModelMock.getRoutingEnabled()).thenReturn(true);
        when(worldpayMerchantConfigurationModelMock.getRoutingMID()).thenReturn(ROUTING_MID);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withRoutingMID(ROUTING_MID);
    }

    @Test
    public void populateRequestWithAdditionalData_shouldNotPopulate_WhenCurrentWebConfigurationIsNull() {
        when(worldpayMerchantConfigurationServiceMock.getCurrentWebConfiguration()).thenReturn(worldpayMerchantConfigurationModelMock);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock, never()).withRoutingMID(ROUTING_MID);
    }

    @Test
    public void populateRequestWithAdditionalData_shouldNotPopulate_WhenRoutingIsDisabled() {
        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock, never()).withRoutingMID(ROUTING_MID);
    }
}
