package com.worldpay.web.client;

import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.http.client.HttpClient;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldPayApplePayHttpClientFactoryBeanTest {

    private static final String PKCS_12 = "PKCS12";
    private static final String CHANGEIT = "changeit";

    @InjectMocks
    private WorldPayApplePayHttpClientFactoryBean testObj;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Resource resourceMock;

    @Before
    public void setup() {
        when(resourceMock.getFilename()).thenReturn("/path/certificate.p12");
    }

    @Test
    public void createInstanceDoesNotFail() throws Exception {
        when(resourceMock.exists()).thenReturn(true);
        when(resourceMock.isReadable()).thenReturn(true);
        testObj.setCertificateFile(resourceMock);
        testObj.setKeyStoreType(PKCS_12);
        testObj.setPassword(CHANGEIT);

        final HttpClient result = testObj.createInstance();

        assertThat(result).isNotNull();
    }

    @Test
    public void createInstanceWithNonExistentFileThrowsAnException() throws Exception {
        when(resourceMock.exists()).thenReturn(false);
        testObj.setCertificateFile(resourceMock);
        testObj.setKeyStoreType(PKCS_12);
        testObj.setPassword(CHANGEIT);

        expectedException.expectMessage("Certificate in path [" + resourceMock.getFilename() + "] file does not exists");
        expectedException.expect(ResourceNotFoundException.class);
        testObj.createInstance();
    }
}
