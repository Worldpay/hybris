package com.worldpay.service.model.threeds2;

import com.worldpay.service.model.Date;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationRiskDataTest {

    private static final String AUTHENTICATION_METHOD = "authenticationMethod";

    @InjectMocks
    private AuthenticationRiskData testObj;

    @Mock
    private Date dateMock;
    @Mock
    private com.worldpay.internal.model.Date transformedDate;

    @Before
    public void setUp() {
        testObj.setAuthenticationMethod(AUTHENTICATION_METHOD);
        testObj.setAuthenticationTimestamp(dateMock);

        when(dateMock.transformToInternalModel()).thenReturn(transformedDate);
    }

    @Test
    public void transformToInternalModel_ShouldTransformDataIntoInternalModel() {
        final com.worldpay.internal.model.AuthenticationRiskData result = testObj.transformToInternalModel();

        assertThat(result.getAuthenticationMethod()).isEqualTo(AUTHENTICATION_METHOD);
        assertThat(result.getAuthenticationTimestamp().getDate()).isEqualTo(transformedDate);
    }

    @Test
    public void transformToInternalModel_ShouldNotPopulateAuthenticationTimestamp_WhenNull() {
        testObj.setAuthenticationTimestamp(null);

        final com.worldpay.internal.model.AuthenticationRiskData result = testObj.transformToInternalModel();

        assertThat(result.getAuthenticationMethod()).isEqualTo(AUTHENTICATION_METHOD);
        assertThat(result.getAuthenticationTimestamp()).isNull();
    }
}
