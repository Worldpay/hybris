package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import com.worldpay.service.request.transform.ServiceRequestTransformer;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.service.response.ServiceResponse;
import com.worldpay.service.response.transform.ServiceResponseTransformer;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.config.Environment.TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayServiceGatewayTest {

    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    private static final String COOKIE = "Cookie";

    @InjectMocks
    private DefaultWorldpayServiceGateway testObj;

    @Mock
    private WorldpayConnector worldpayConnectorMock;
    @Mock
    private WorldpayXMLValidator worldpayXMLValidatorMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SecondThreeDSecurePaymentRequest serviceRequestMock;
    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock
    private Configuration configurationMock;
    @Mock
    private ServiceRequestTransformer directAuthoriseRequestTransformerMock;
    @Mock
    private ServiceResponseTransformer directAuthoriseResponseTransformerMock;
    @Mock
    private PaymentService paymentRequestMock, paymentResponseService;
    @Mock
    private ServiceReply paymentResponseServiceMock;
    @Mock
    private ServiceResponse paymentResponseMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    private Map<String, ServiceRequestTransformer> requestTransformerStrategyMap = new HashMap<>();
    private Map<String, ServiceResponseTransformer> responseTransformerStrategyMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        Whitebox.setInternalState(testObj, "requestTransformerStrategyMap", requestTransformerStrategyMap);
        Whitebox.setInternalState(testObj, "responseTransformerStrategyMap", responseTransformerStrategyMap);
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn(TEST.name());
        requestTransformerStrategyMap.put(serviceRequestMock.getClass().getName(), directAuthoriseRequestTransformerMock);
        responseTransformerStrategyMap.put(serviceRequestMock.getClass().getName(), directAuthoriseResponseTransformerMock);
        when(directAuthoriseRequestTransformerMock.transform(serviceRequestMock)).thenReturn(paymentRequestMock);
        when(directAuthoriseResponseTransformerMock.transform(paymentResponseServiceMock)).thenReturn(paymentResponseMock);
        when(paymentResponseServiceMock.getPaymentService()).thenReturn(paymentResponseService);
        when(serviceRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(serviceRequestMock.getCookie()).thenReturn(COOKIE);
        when(worldpayConnectorMock.send(paymentRequestMock, merchantInfoMock, COOKIE)).thenReturn(paymentResponseServiceMock);
    }

    @Test
    public void service_WhenPayloadValid_ShouldSendTheTransformedRequestToWorlpayAndTransformTheResponse() throws WorldpayException {
        final ServiceResponse result = testObj.service(serviceRequestMock);

        final InOrder inOrder = Mockito.inOrder(worldpayConnectorMock, worldpayXMLValidatorMock);
        inOrder.verify(worldpayConnectorMock).logXMLOut(paymentRequestMock);
        inOrder.verify(worldpayXMLValidatorMock).validate(paymentRequestMock);
        inOrder.verify(worldpayConnectorMock).send(paymentRequestMock, merchantInfoMock, COOKIE);
        inOrder.verify(worldpayConnectorMock).logXMLOut(paymentResponseService);
        assertThat(result).isEqualTo(paymentResponseMock);
    }
}
