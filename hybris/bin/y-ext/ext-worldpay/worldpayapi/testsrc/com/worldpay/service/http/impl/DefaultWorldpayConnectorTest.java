package com.worldpay.service.http.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.worldpay.util.WorldpayConstants.XML_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayConnectorTest {

    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    private static final String WORLDPAY_CONFIG_DOMAIN = "worldpay.config.domain";
    private static final String WORLDPAY_CONFIG_CONTEXT = "worldpay.config.context";
    private static final String ENDPOINT = "https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp";
    private static final String BODY = "body";
    private static final String COOKIE = "cookie";
    private static final String XML = "xml";

    @InjectMocks
    private DefaultWorldpayConnector testObj;

    @Mock
    private PaymentServiceMarshaller paymentServiceMarshallerMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationService;
    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private PaymentService paymentServiceRequestMock, paymentServiceReplyMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Captor
    private ArgumentCaptor<URI> uriArgumentCaptor;
    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<InputStream> inputStreamArgumentCaptor;
    @Mock
    private ResponseEntity<String> responseEntityMock;
    @Mock
    private HttpHeaders httpHeadersMock;

    @Before
    public void setUp() throws Exception {

        when(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn("environment");
        when(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_DOMAIN + "." + "environment")).thenReturn("https://secure-test.worldpay.com");
        when(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_CONTEXT + "." + "environment")).thenReturn("/jsp/merchant/xml/paymentService.jsp");
        when(merchantInfoMock.getMerchantCode()).thenReturn("merchantCode");
        when(merchantInfoMock.getMerchantPassword()).thenReturn("merchantPassword");
    }

    @Test
    public void send_WhenThereAreNoErrors_ShouldSendThePaymentServiceTransformedInXML() throws Exception {
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get("Set-Cookie")).thenReturn(ImmutableList.of("cookie"));
        when(responseEntityMock.getBody()).thenReturn("someValue");
        when(paymentServiceMarshallerMock.unmarshal(IOUtils.toInputStream("someValue"))).thenReturn(paymentServiceReplyMock);
        when(paymentServiceMarshallerMock.marshalAsFragment(paymentServiceRequestMock)).thenReturn("someXML");
        when(restTemplateMock.postForEntity(uriArgumentCaptor.capture(), httpEntityArgumentCaptor.capture(), eq(String.class))).thenReturn(responseEntityMock);

        testObj.send(paymentServiceRequestMock, merchantInfoMock, "cookie");

        final URI uri = uriArgumentCaptor.getValue();
        assertThat(uri.toString()).isEqualTo(ENDPOINT);

        final HttpEntity<String> request = httpEntityArgumentCaptor.getValue();
        final byte[] plainCreds = ("merchantCode" + ":" + "merchantPassword").getBytes(StandardCharsets.UTF_8);
        assertThat(request.getHeaders()).containsEntry(HttpHeaders.AUTHORIZATION, Collections.singletonList("Basic " + new String(Base64.getEncoder().encode(plainCreds))));
        assertThat(request.getHeaders()).containsEntry(HttpHeaders.HOST, Collections.singletonList(uri.getHost()));
        assertThat(request.getHeaders()).containsEntry(HttpHeaders.COOKIE, Collections.singletonList("cookie"));
        assertThat(request.getBody()).startsWith(XML_HEADER);
        assertThat(request.getBody()).contains("someXML");
        verify(restTemplateMock).postForEntity(eq(URI.create(ENDPOINT)), anyObject(), eq(String.class));
    }

    @Test
    public void send_WhenThereAreErrors_ShouldSendThePaymentServiceTransformedInXML3Times() throws Exception {
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get("Set-Cookie")).thenReturn(ImmutableList.of("cookie"));
        when(responseEntityMock.getBody()).thenReturn("someValue");
        when(paymentServiceMarshallerMock.unmarshal(IOUtils.toInputStream("someValue"))).thenReturn(paymentServiceReplyMock);
        when(paymentServiceMarshallerMock.marshalAsFragment(paymentServiceRequestMock)).thenReturn("someXML");
        when(restTemplateMock.postForEntity(uriArgumentCaptor.capture(), httpEntityArgumentCaptor.capture(), eq(String.class))).thenThrow(new ResourceAccessException("error"));

        testObj.sendOutboundXML(paymentServiceRequestMock, merchantInfoMock, "cookie");

        verify(restTemplateMock, atMost(3)).postForEntity(eq(URI.create(ENDPOINT)), anyObject(), eq(String.class));
    }

    @Test
    public void logXMLShouldMarshalPaymentService() throws WorldpayException {
        testObj.logXMLOut(paymentServiceRequestMock);
        verify(paymentServiceMarshallerMock).marshal(paymentServiceRequestMock);
    }

    @Test
    public void send_WhenTheResponseIsDelayedByRetries_ShouldSendThePaymentServiceTransformedInXML() throws Exception {

        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get(HttpHeaders.SET_COOKIE)).thenReturn(List.of(COOKIE));
        when(responseEntityMock.getBody()).thenReturn(BODY);
        when(paymentServiceMarshallerMock.unmarshal(inputStreamArgumentCaptor.capture())).thenReturn(paymentServiceReplyMock);
        when(paymentServiceMarshallerMock.marshalAsFragment(paymentServiceRequestMock)).thenReturn(XML);
        when(restTemplateMock.postForEntity(uriArgumentCaptor.capture(), httpEntityArgumentCaptor.capture(), eq(String.class)))
            .thenThrow(new ResourceAccessException(""))
            .thenThrow(new ResourceAccessException(""))
            .thenReturn(responseEntityMock);

        assertThat(testObj.send(paymentServiceRequestMock, merchantInfoMock, COOKIE))
            .extracting(ServiceReply::getPaymentService, ServiceReply::getCookie)
            .containsExactly(paymentServiceReplyMock, COOKIE);

        final var uri = uriArgumentCaptor.getValue();
        assertThat(uri.toString()).hasToString(ENDPOINT);

        final byte[] plainCreds = ("merchantCode" + ":" + "merchantPassword").getBytes(StandardCharsets.UTF_8);
        final var request = httpEntityArgumentCaptor.getValue();
        assertThat(request.getHeaders()).containsAllEntriesOf(Map.of(
            HttpHeaders.AUTHORIZATION, List.of("Basic " + new String(Base64.getEncoder().encode(plainCreds))),
            HttpHeaders.HOST, List.of(uri.getHost()),
            HttpHeaders.COOKIE, List.of(COOKIE)));
        assertThat(request.getBody()).startsWith(XML_HEADER).contains(XML);
        assertThat(inputStreamArgumentCaptor.getValue()).hasSameContentAs(IOUtils.toInputStream(BODY, StandardCharsets.UTF_8));

        verify(restTemplateMock, times(3)).postForEntity(eq(URI.create(ENDPOINT)), anyObject(), eq(String.class));
    }
}
