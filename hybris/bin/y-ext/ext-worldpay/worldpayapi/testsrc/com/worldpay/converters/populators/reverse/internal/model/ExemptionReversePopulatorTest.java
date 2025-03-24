package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.data.Exemption;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExemptionReversePopulatorTest {

    private static final String TYPE = "OP";
    private static final String PLACEMENT = "PLACEMENT";

    @InjectMocks
    private ExemptionReversePopulator testObj;

    @Mock
    private com.worldpay.internal.model.Exemption intExemptionMock;

    @Test
    public void populate_shouldPopulateExemption_WhenInternalExemptionContainsData() {
        when(intExemptionMock.getType()).thenReturn(TYPE);
        when(intExemptionMock.getPlacement()).thenReturn(PLACEMENT);

        final Exemption exemption = new Exemption();

        testObj.populate(intExemptionMock, exemption);

        assertEquals(TYPE, exemption.getType());
        assertEquals(PLACEMENT, exemption.getPlacement());
    }
}
