package com.worldpay.fraud.symptoms;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayRiskGuardianFraudSymptomTest {

    private static final String CONFIGURED_LIMIT = "40";

    @InjectMocks
    private WorldpayRiskGuardianFraudSymptom testObj;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel worldpayPaymentAuthoriseTransactionModelMock;
    @Mock
    private WorldpayRiskScoreModel worldpayRiskScoreModelMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    private final Double configuredLimitValue = Double.valueOf(CONFIGURED_LIMIT);

    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration().getString(WorldpayapiConstants.EXTENSIONNAME + ".fraud.scoreLimit")).thenReturn(CONFIGURED_LIMIT);
    }

    @Test
    public void recognizeSymptomShouldReturnAPositiveFraudServiceResponseWhenRiskGuardianInPaymentTransactionHasScoreHigherThanConfiguredLimit() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        final double positiveFinalScore = configuredLimitValue + 10;
        when(worldpayRiskScoreModelMock.getFinalScore()).thenReturn(positiveFinalScore);

        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(positiveFinalScore, fraudServiceResponse.getScore(), 0.0);
        assertEquals(positiveFinalScore, fraudServiceResponse.getSymptoms().get(0).getScore(), 0.0);
        assertEquals(positiveFinalScore, testObj.getIncrement(), 0.0);
        verify(worldpayRiskScoreModelMock).getFinalScore();
    }

    @Test
    public void recognizeSymptomShouldReturnTheSameFraudServiceResponseWhenRiskScoreIsRiskGuardianAndScoreIsLowerThanConfiguredLimit() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        final double negativeFinalScore = configuredLimitValue - 10;
        when(worldpayRiskScoreModelMock.getFinalScore()).thenReturn(negativeFinalScore);

        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0D, fraudServiceResponse.getScore(), 0.0);
        assertEquals(Collections.emptyList(), fraudServiceResponse.getSymptoms());
        verify(worldpayRiskScoreModelMock).getFinalScore();
    }

    @Test
    public void recognizeSymptomShouldReturnTheSameFraudServiceResponseWhenFinalScoreIsNull() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        when(worldpayRiskScoreModelMock.getFinalScore()).thenReturn(null);

        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0D, fraudServiceResponse.getScore(), 0.0);
        assertEquals(Collections.emptyList(), fraudServiceResponse.getSymptoms());
        verify(worldpayRiskScoreModelMock).getFinalScore();
    }

    @Test
    public void recognizeSymptomWithNullRiskScore() {
        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(null);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0d, fraudServiceResponse.getScore(), 0.0);
    }
}
