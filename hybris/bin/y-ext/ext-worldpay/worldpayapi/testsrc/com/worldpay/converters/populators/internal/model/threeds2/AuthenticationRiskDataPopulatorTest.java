package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.Date;
import com.worldpay.data.threeds2.AuthenticationRiskData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationRiskDataPopulatorTest {

    private static final String AUTHENTICATION_METHOD = "authenticationMethod";

    @InjectMocks
    private AuthenticationRiskDataPopulator testObj;

    @Mock
    private Converter<Date, com.worldpay.internal.model.Date> internalDateConverterMock;

    @Mock
    private AuthenticationRiskData sourceMock;
    @Mock
    private Date authenticationTimestampMock;
    @Mock
    private com.worldpay.internal.model.Date internalDateMock;


    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.AuthenticationRiskData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateAuthenticationRiskData() {
        when(sourceMock.getAuthenticationMethod()).thenReturn(AUTHENTICATION_METHOD);
        when(sourceMock.getAuthenticationTimestamp()).thenReturn(authenticationTimestampMock);
        when(internalDateConverterMock.convert(authenticationTimestampMock)).thenReturn(internalDateMock);

        final com.worldpay.internal.model.AuthenticationRiskData targetMock = new com.worldpay.internal.model.AuthenticationRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticationMethod()).isEqualTo(AUTHENTICATION_METHOD);
        assertThat(targetMock.getAuthenticationTimestamp().getDate()).isEqualTo(internalDateMock);
    }

    @Test
    public void populate_WhenDateIsNull_ShouldNotPopulateInternalDate() {
        when(sourceMock.getAuthenticationTimestamp()).thenReturn(null);
        when(internalDateConverterMock.convert(sourceMock.getAuthenticationTimestamp())).thenReturn(internalDateMock);

        final com.worldpay.internal.model.AuthenticationRiskData targetMock = new com.worldpay.internal.model.AuthenticationRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticationTimestamp()).isNull();
    }
}
