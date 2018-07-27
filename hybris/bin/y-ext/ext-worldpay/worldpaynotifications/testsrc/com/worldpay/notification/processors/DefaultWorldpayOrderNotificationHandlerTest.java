package com.worldpay.notification.processors;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderNotificationHandlerTest {


    @InjectMocks
    private DefaultWorldpayOrderNotificationHandler testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private OrderModificationDao orderModificationDaoMock;


    @Mock
    private WorldpayOrderModificationModel orderModificationModelMock;
    @Mock
    private Exception exceptionMock;
    @Mock
    private WorldpayOrderModificationModel existingModificationModelMock;

    @Test
    public void setDefectiveModification() {

        testObj.setDefectiveModification(orderModificationModelMock, exceptionMock, true);

        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setProcessed(true);
        verify(modelServiceMock).save(orderModificationModelMock);
    }

    @Test
    public void shouldSetDefectiveReasonAndIncreaseCounter() {
        when(existingModificationModelMock.getDefectiveCounter()).thenReturn(1);
        when(orderModificationDaoMock.findExistingModifications(orderModificationModelMock)).thenReturn(singletonList(existingModificationModelMock));

        testObj.setDefectiveReason(orderModificationModelMock, DefectiveReason.PROCESSING_ERROR);

        verify(orderModificationModelMock).setDefectiveReason(DefectiveReason.PROCESSING_ERROR);
        verify(orderModificationModelMock).setDefectiveCounter(2);
        verify(modelServiceMock).remove(existingModificationModelMock);
    }

    @Test
    public void shouldSetDefectiveReasonAndSetCounterTo1() {
        when(existingModificationModelMock.getDefectiveCounter()).thenReturn(null);
        when(orderModificationDaoMock.findExistingModifications(orderModificationModelMock)).thenReturn(singletonList(existingModificationModelMock));

        testObj.setDefectiveReason(orderModificationModelMock, DefectiveReason.PROCESSING_ERROR);

        verify(orderModificationModelMock).setDefectiveReason(DefectiveReason.PROCESSING_ERROR);
        verify(orderModificationModelMock).setDefectiveCounter(1);
        verify(modelServiceMock).remove(existingModificationModelMock);
    }

    @Test
    public void setNonDefectiveAndProcessed() {
        testObj.setNonDefectiveAndProcessed(orderModificationModelMock);

        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.FALSE);
        verify(modelServiceMock).save(orderModificationModelMock);
    }
}
