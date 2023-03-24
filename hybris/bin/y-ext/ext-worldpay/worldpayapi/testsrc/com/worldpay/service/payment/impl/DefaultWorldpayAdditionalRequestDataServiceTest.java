package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAdditionalRequestDataServiceTest {

    private DefaultWorldpayAdditionalRequestDataService testObj;

    @Mock
    private WorldpayAdditionalDataRequestStrategy primeRoutingStrategyMock, level23StrategyMock, fraudSightStrategyMock;
    @Mock
    private WorldpayAdditionalDataRequestStrategy worldpayGuaranteedPaymentsStrategyMock;
    @Mock
    private AbstractOrderModel cartMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;

    @Before
    public void setUp() {
        testObj = new DefaultWorldpayAdditionalRequestDataService(ImmutableList.of(primeRoutingStrategyMock, level23StrategyMock, fraudSightStrategyMock), ImmutableList.of(level23StrategyMock, fraudSightStrategyMock), worldpayGuaranteedPaymentsStrategyMock);
    }

    @Test
    public void populateDirectRequestAdditionalData_ShouldCallTheDirectStrategies() {
        testObj.populateDirectRequestAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(primeRoutingStrategyMock).populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);
        verify(level23StrategyMock).populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);
        verify(fraudSightStrategyMock).populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);
    }

    @Test
    public void populateDirectRequestAdditionalData_ShouldCallTheRedirectStrategies() {
        testObj.populateRedirectRequestAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(primeRoutingStrategyMock, never()).populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);
        verify(level23StrategyMock).populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);
        verify(fraudSightStrategyMock).populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);
    }
}
