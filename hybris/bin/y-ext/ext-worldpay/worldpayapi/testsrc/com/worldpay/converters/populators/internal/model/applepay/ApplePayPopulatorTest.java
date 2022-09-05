package com.worldpay.converters.populators.internal.model.applepay;

import com.worldpay.internal.model.APPLEPAYSSL;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.applepay.Header;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApplePayPopulatorTest {

    private static final String DATA = "data";
    private static final String SIGNATURE = "signature";
    private static final String TOKEN_REQUESTOR_ID = "tokenRequestorID";
    private static final String VERSION = "version";

    @InjectMocks
    private ApplePayPopulator testObj;

    @Mock
    private Converter<Header, com.worldpay.internal.model.Header> internalHeaderConverter;

    @Mock
    private ApplePay sourceMock;
    @Mock
    private Header headerMock;
    @Mock
    private com.worldpay.internal.model.Header internalHeaderMock;


    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new APPLEPAYSSL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenHeaderIsNull_ShouldNotPopulateInternalHeader() {
        when(sourceMock.getHeader()).thenReturn(null);

        final APPLEPAYSSL targetMock = new APPLEPAYSSL();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getHeader()).isNull();
    }

    @Test
    public void populate_ShouldPopulateApplePay() {
        when(sourceMock.getHeader()).thenReturn(headerMock);
        when(sourceMock.getData()).thenReturn(DATA);
        when(sourceMock.getSignature()).thenReturn(SIGNATURE);
        when(sourceMock.getTokenRequestorID()).thenReturn(TOKEN_REQUESTOR_ID);
        when(sourceMock.getVersion()).thenReturn(VERSION);
        when(internalHeaderConverter.convert(headerMock)).thenReturn(internalHeaderMock);

        final APPLEPAYSSL targetMock = new APPLEPAYSSL();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getHeader()).isEqualTo(internalHeaderMock);
        assertThat(targetMock.getData()).isEqualTo(DATA);
        assertThat(targetMock.getSignature()).isEqualTo(SIGNATURE);
        assertThat(targetMock.getTokenRequestorID()).isEqualTo(TOKEN_REQUESTOR_ID);
        assertThat(targetMock.getVersion()).isEqualTo(VERSION);
    }
}
