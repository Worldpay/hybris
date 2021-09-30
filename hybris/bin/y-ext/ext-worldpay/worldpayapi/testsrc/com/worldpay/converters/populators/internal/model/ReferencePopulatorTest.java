package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.Reference;
import com.worldpay.data.LineItemReference;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReferencePopulatorTest {

    private static final String ID = "id";
    private static final String VALUE = "value";

    @InjectMocks
    private ReferencePopulator testObj;

    @Mock
    private LineItemReference sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowException() {
        testObj.populate(null, new Reference());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populator_ShouldPopulateReference() {
        when(sourceMock.getId()).thenReturn(ID);
        when(sourceMock.getValue()).thenReturn(VALUE);

        final Reference targetMock = new Reference();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getId()).isEqualTo(ID);
        assertThat(targetMock.getvalue()).isEqualTo(VALUE);
    }
}

