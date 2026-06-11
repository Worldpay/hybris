package com.worldpay.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultWorldpayCustomerIpAddressStrategyTest {

    private static final String HEADER_NAME = "headerName";
    private static final String HEADER_VALUE = "customerIp";
    private static final String REMOTE_ADDRESS = "remoteAddress";

    @Mock
    private HttpServletRequest httpRequestMock;

    public DefaultWorldpayCustomerIpAddressStrategyTest() {
        super();
    }

    @InjectMocks
    private DefaultWorldpayCustomerIpAddressStrategy testObj;

    @BeforeEach
    void setUp() {
        testObj = new DefaultWorldpayCustomerIpAddressStrategy(HEADER_NAME);
    }

    @Test
    void testShouldGetCustomerIpFromSpecifiedHeaderAttribute() throws Exception {
        when(httpRequestMock.getHeader(HEADER_NAME)).thenReturn(HEADER_VALUE);

        final String result = testObj.getCustomerIp(httpRequestMock);

        assertEquals(HEADER_VALUE, result);
    }

    @Test
    void testShouldGetCustomerIpFromRemoteAddrIfHeaderValueIsEmpty() throws Exception {
        when(httpRequestMock.getHeader(HEADER_NAME)).thenReturn(null);
        when(httpRequestMock.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);

        final String result = testObj.getCustomerIp(httpRequestMock);

        assertEquals(REMOTE_ADDRESS, result);
    }
}
