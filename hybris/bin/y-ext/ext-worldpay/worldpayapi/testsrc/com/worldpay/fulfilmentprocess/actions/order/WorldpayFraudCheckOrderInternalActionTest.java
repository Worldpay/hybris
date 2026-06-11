package com.worldpay.fulfilmentprocess.actions.order;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static de.hybris.platform.basecommerce.enums.FraudStatus.CHECK;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.FraudService;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.impl.FraudSymptom;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayFraudCheckOrderInternalActionTest {

    private static final String OK_STRING = "OK";
    private static final String DESCRIPTION = "description";
    private static final String PROVIDER_NAME = "providerName";
    private static final String FRAUD_REPORT_CODE = "fraudReportCode";

    @Spy
    @InjectMocks
    private WorldpayFraudCheckOrderInternalAction testObj;

    @Mock
    private FraudService fraudServiceMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private TimeService timeServiceMock;

    @Mock
    private FraudServiceResponse fraudServiceResponseMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private FraudReportModel fraudReportModelMock;
    @Mock
    private FraudSymptom fraudSymptomMock;
    @Mock
    private FraudSymptomScoringModel fraudSymptomScoringModelMock;
    @Mock
    private OrderHistoryEntryModel orderHistoryEntryModelMock;
    @Mock
    private OrderProcessModel orderProcessModelMock;

    @BeforeEach
    void setUp() {
        testObj.setProviderName(PROVIDER_NAME);
    }

    @Test
    void testCreateFraudReport() {
        when(modelServiceMock.create(FraudReportModel.class)).thenReturn(fraudReportModelMock);
        when(modelServiceMock.create(FraudSymptomScoringModel.class)).thenReturn(fraudSymptomScoringModelMock);
        final Set<FraudReportModel> fraudReportModels = new HashSet<>();
        fraudReportModels.add(fraudReportModelMock);
        when(orderModelMock.getFraudReports()).thenReturn(fraudReportModels);

        when(fraudServiceResponseMock.getSymptoms()).thenReturn(Collections.singletonList(fraudSymptomMock));

        testObj.createFraudReport(PROVIDER_NAME, fraudServiceResponseMock, orderModelMock, CHECK);

        verify(fraudReportModelMock).setOrder(orderModelMock);
        verify(fraudReportModelMock).setStatus(CHECK);
        verify(fraudReportModelMock).setProvider(PROVIDER_NAME);
        verify(fraudReportModelMock).setTimestamp(timeServiceMock.getCurrentTime());
        verify(fraudSymptomScoringModelMock).setFraudReport(fraudReportModelMock);
        verify(fraudSymptomScoringModelMock).setName(fraudSymptomMock.getSymptom());
        verify(fraudSymptomScoringModelMock).setExplanation(fraudSymptomMock.getExplanation());
        verify(fraudSymptomScoringModelMock).setScore(fraudSymptomMock.getScore());
    }

    @Test
    void testCreateHistoryLog() {
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);

        testObj.createHistoryLog(PROVIDER_NAME, orderModelMock, CHECK, FRAUD_REPORT_CODE);

        verify(orderHistoryEntryModelMock).setTimestamp(timeServiceMock.getCurrentTime());
        verify(orderHistoryEntryModelMock).setOrder(orderModelMock);
        verify(orderHistoryEntryModelMock).setDescription(endsWith(FRAUD_REPORT_CODE));
    }

    @Test
    void testCreateHistoryLogOkStatus() {
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);

        testObj.createHistoryLog(PROVIDER_NAME, orderModelMock, FraudStatus.OK, FRAUD_REPORT_CODE);

        verify(orderHistoryEntryModelMock).setTimestamp(timeServiceMock.getCurrentTime());
        verify(orderHistoryEntryModelMock).setOrder(orderModelMock);
        verify(orderHistoryEntryModelMock).setDescription(endsWith(OK_STRING));
    }

    @Test
    void testCreateHistoryLogEntryModel() {
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);

        testObj.createHistoryLog(DESCRIPTION, orderModelMock);

        verify(orderHistoryEntryModelMock).setDescription(DESCRIPTION);
        verify(orderHistoryEntryModelMock).setOrder(orderModelMock);
        verify(orderHistoryEntryModelMock).setTimestamp(timeServiceMock.getCurrentTime());
    }

    @Test
    void testExecuteActionShouldReturnTransactionOK() {
        when(testObj.getProviderName()).thenReturn(PROVIDER_NAME);
        when(modelServiceMock.create(FraudReportModel.class)).thenReturn(fraudReportModelMock);
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);
        final Set<FraudReportModel> fraudReportModels = new HashSet<>();
        fraudReportModels.add(fraudReportModelMock);
        when(orderModelMock.getFraudReports()).thenReturn(fraudReportModels);

        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(fraudServiceMock.recognizeOrderSymptoms(PROVIDER_NAME, orderModelMock)).thenReturn(fraudServiceResponseMock);
        when(fraudServiceResponseMock.getSymptoms()).thenReturn(Collections.emptyList());

        final String result = testObj.executeAction(orderProcessModelMock);

        assertThat(result).isEqualTo("OK");
        verify(orderModelMock).setFraudulent(FALSE);
        verify(orderModelMock).setPotentiallyFraudulent(FALSE);
        verify(orderModelMock).setStatus(OrderStatus.FRAUD_CHECKED);
    }

    @Test
    void testExecuteActionShouldReturnTransactionFraud() {
        when(modelServiceMock.create(FraudReportModel.class)).thenReturn(fraudReportModelMock);
        when(modelServiceMock.create(FraudSymptomScoringModel.class)).thenReturn(fraudSymptomScoringModelMock);
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);
        final Set<FraudReportModel> fraudReportModels = new HashSet<>();
        fraudReportModels.add(fraudReportModelMock);
        when(orderModelMock.getFraudReports()).thenReturn(fraudReportModels);

        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(fraudServiceMock.recognizeOrderSymptoms(PROVIDER_NAME, orderModelMock)).thenReturn(fraudServiceResponseMock);
        when(fraudServiceResponseMock.getSymptoms()).thenReturn(Collections.singletonList(fraudSymptomMock));

        final String result = testObj.executeAction(orderProcessModelMock);

        assertThat(result).isEqualTo("POTENTIAL");
        verify(testObj).createFraudReport(PROVIDER_NAME, fraudServiceResponseMock, orderModelMock, CHECK);
        verify(orderModelMock).setFraudulent(FALSE);
        verify(orderModelMock).setPotentiallyFraudulent(TRUE);
        verify(orderModelMock).setStatus(OrderStatus.FRAUD_CHECKED);
    }
}
