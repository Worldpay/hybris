package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.data.Exemption;
import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.internal.model.ExemptionResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExemptionResponseReversePopulatorTest {

    private static final String REASON = "ACCEPTED";
    private static final String RESULT = "SUCCESS";

    @InjectMocks
    private ExemptionResponseReversePopulator testObj;

    @Mock
    private Converter<com.worldpay.internal.model.Exemption, Exemption> internalExemptionReverseConverterMock;
    @Mock
    private ExemptionResponse exemptionResponseMock;
    @Mock
    private com.worldpay.internal.model.Exemption intExemptionMock;
    @Mock
    private Exemption exemptionMock;

    @Test
    public void populate_shouldPopulateExemptionResponseInfo_WhenExemptionResponseContainsData() {
        when(internalExemptionReverseConverterMock.convert(intExemptionMock)).thenReturn(exemptionMock);
        when(exemptionResponseMock.getReason()).thenReturn(REASON);
        when(exemptionResponseMock.getResult()).thenReturn(RESULT);
        when(exemptionResponseMock.getExemption()).thenReturn(intExemptionMock);

        final ExemptionResponseInfo exemptionResponseInfo = new ExemptionResponseInfo();
        testObj.populate(exemptionResponseMock, exemptionResponseInfo);

        assertEquals(REASON, exemptionResponseInfo.getReason());
        assertEquals(RESULT, exemptionResponseInfo.getResult());
        assertEquals(exemptionMock, exemptionResponseInfo.getExemption());
    }
}
