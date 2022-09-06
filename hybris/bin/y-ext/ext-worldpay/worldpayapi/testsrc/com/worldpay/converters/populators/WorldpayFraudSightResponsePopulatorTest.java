package com.worldpay.converters.populators;

import com.worldpay.data.FraudSightResponse;
import com.worldpay.model.WorldpayFraudSightModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayFraudSightResponsePopulatorTest {

    private static final String FS_ID = "fs_id";
    private static final String REASON_1 = "High risk email";
    private static final String REASON_2 = "Card unfamiliarity";
    private static final String REVIEW = "REVIEW";

    @InjectMocks
    private WorldpayFraudSightResponsePopulator testObj;

    @Mock
    private FraudSightResponse fraudSightResponseMock;
    @Mock
    private WorldpayFraudSightModel worldpayFraudSightMock;

    @Before
    public void setUp() {
        when(fraudSightResponseMock.getId()).thenReturn(FS_ID);
        when(fraudSightResponseMock.getMessage()).thenReturn(REVIEW);
        when(fraudSightResponseMock.getScore()).thenReturn(100d);
        when(fraudSightResponseMock.getReasonCodes()).thenReturn(List.of(REASON_1, REASON_2));
    }

    @Test
    public void populate_ShouldPopulateTheWorldpayFraudSightModel() {
        testObj.populate(fraudSightResponseMock, worldpayFraudSightMock);

        verify(worldpayFraudSightMock).setId(FS_ID);
        verify(worldpayFraudSightMock).setMessage(REVIEW);
        verify(worldpayFraudSightMock).setScore(100d);
        verify(worldpayFraudSightMock).setReasonCodes(List.of(REASON_1, REASON_2));
    }
}
