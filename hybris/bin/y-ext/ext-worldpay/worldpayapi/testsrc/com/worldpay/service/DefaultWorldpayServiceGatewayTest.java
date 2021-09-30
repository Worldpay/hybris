package com.worldpay.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.model.PayloadModel;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import com.worldpay.service.request.transform.ServiceRequestTransformer;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.service.response.ServiceResponse;
import com.worldpay.service.response.transform.ServiceResponseTransformer;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.config.Environment.PROD;
import static com.worldpay.config.Environment.TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayServiceGatewayTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String PAYMENT_REQUEST_PAYLOAD = "paymentRequestString";
    private static final String PAYMENT_RESPONSE_PAYLOAD = "paymentResponseString";
    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    private static final String COOKIE = "Cookie";

    @Spy
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
    private GenericDao<AbstractOrderModel> abstractOrderGenericDaoMock;
    @Mock
    private ModelService modelServiceMock;

    @Mock
    private Configuration configurationMock;
    @Mock
    private ServiceRequestTransformer directAuthoriseRequestTransformerMock;
    @Mock
    private ServiceResponseTransformer directAuthoriseResponseTransformerMock;
    @Mock
    private PaymentService paymentRequestMock, paymentResponseServiceMock;
    @Mock
    private ServiceReply paymentResponseServiceReplyMock;
    @Mock
    private ServiceResponse paymentResponseMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private AbstractOrderModel orderMock;
    @Mock
    private PayloadModel requestPayloadMock, responsePayloadMock;

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
        when(directAuthoriseResponseTransformerMock.transform(paymentResponseServiceReplyMock)).thenReturn(paymentResponseMock);
        when(paymentResponseServiceReplyMock.getPaymentService()).thenReturn(paymentResponseServiceMock);
        when(serviceRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(serviceRequestMock.getCookie()).thenReturn(COOKIE);
        when(worldpayConnectorMock.send(paymentRequestMock, merchantInfoMock, COOKIE)).thenReturn(paymentResponseServiceReplyMock);
        when(serviceRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(abstractOrderGenericDaoMock.find(ImmutableMap.of(AbstractOrderModel.WORLDPAYORDERCODE, ORDER_CODE))).thenReturn(ImmutableList.of(orderMock));
        doReturn(requestPayloadMock).when(testObj).createPayloadModel(PAYMENT_REQUEST_PAYLOAD);
        doReturn(responsePayloadMock).when(testObj).createPayloadModel(PAYMENT_RESPONSE_PAYLOAD);
    }

    @Test
    public void service_WhenPayloadValid_ShouldSendTheTransformedRequestToWorlpayAndTransformTheResponse() throws WorldpayException {
        final ServiceResponse result = testObj.service(serviceRequestMock);

        final InOrder inOrder = Mockito.inOrder(worldpayConnectorMock, worldpayXMLValidatorMock);
        inOrder.verify(worldpayConnectorMock).logXMLOut(paymentRequestMock);
        inOrder.verify(worldpayXMLValidatorMock).validate(paymentRequestMock);
        inOrder.verify(worldpayConnectorMock).send(paymentRequestMock, merchantInfoMock, COOKIE);
        inOrder.verify(worldpayConnectorMock).logXMLOut(paymentResponseServiceMock);
        assertThat(result).isEqualTo(paymentResponseMock);
    }

    @Test
    public void service_WhenPayloadValid_ShouldSetPayloadToOrder() throws WorldpayException {
        when(worldpayConnectorMock.logXMLOut(paymentRequestMock)).thenReturn(PAYMENT_REQUEST_PAYLOAD);
        when(worldpayConnectorMock.logXMLOut(paymentResponseServiceMock)).thenReturn(PAYMENT_RESPONSE_PAYLOAD);

        testObj.service(serviceRequestMock);

        verify(orderMock).setRequestsPayload(ImmutableList.of(requestPayloadMock));
        verify(orderMock).setResponsesPayload(ImmutableList.of(responsePayloadMock));
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void service_WhenProdEnvironment_ShouldNotSetPayloadToOrder() throws WorldpayException {
        when(configurationMock.getString(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn(PROD.name());

        testObj.service(serviceRequestMock);

        verify(orderMock, never()).setRequestsPayload(ImmutableList.of(requestPayloadMock));
        verify(orderMock, never()).setResponsesPayload(ImmutableList.of(responsePayloadMock));
        verify(worldpayConnectorMock, never()).logXMLOut(any());
    }

    @Test
    public void service_WhenNoOrderFound_ShouldNotSetPayloadToOrder() throws WorldpayException {
        when(abstractOrderGenericDaoMock.find(ImmutableMap.of(AbstractOrderModel.WORLDPAYORDERCODE, ORDER_CODE))).thenReturn(ImmutableList.of());

        testObj.service(serviceRequestMock);

        verify(orderMock, never()).setRequestsPayload(any());
        verify(orderMock, never()).setResponsesPayload(any());
        verify(modelServiceMock, never()).save(orderMock);
    }
}
