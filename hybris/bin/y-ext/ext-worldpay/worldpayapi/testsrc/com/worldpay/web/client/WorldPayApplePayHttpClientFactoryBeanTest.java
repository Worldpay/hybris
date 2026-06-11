package com.worldpay.web.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldPayApplePayHttpClientFactoryBeanTest {

    @InjectMocks
    private WorldPayApplePayHttpClientFactoryBean testObj;

    @Mock
    private Resource resourceMock;

    @BeforeEach
    void setup() {
        testObj.setKeyStoreType("PKCS12");
        testObj.setPassword("password");
    }

    @Test
    void createInstanceDoesNotFail() throws Exception {
        when(resourceMock.exists()).thenReturn(true);
        when(resourceMock.isReadable()).thenReturn(true);

        final HttpClient result = testObj.createInstance();

        assertThat(result).isNotNull();
    }

    @Test
    void createInstanceWithNonExistentFileThrowsAnException() throws Exception {
        when(resourceMock.exists()).thenReturn(false);

        assertThatThrownBy(() -> testObj.createInstance())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Certificate in path [" + resourceMock.getFilename() + "] file does not exists");
    }
}
