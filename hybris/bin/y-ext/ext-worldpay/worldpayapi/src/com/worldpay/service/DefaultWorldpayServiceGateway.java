package com.worldpay.service;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.Environment;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.model.PayloadModel;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.request.*;
import com.worldpay.service.request.transform.ServiceRequestTransformer;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.service.response.*;
import com.worldpay.service.response.transform.ServiceResponseTransformer;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worldpay.config.Environment.PROD;
import static com.worldpay.util.WorldpayConstants.JAXB_CONTEXT;
import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayServiceGateway implements WorldpayServiceGateway {

    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";

    protected final WorldpayConnector worldpayConnector;
    protected final WorldpayXMLValidator worldpayXMLValidator;
    protected final ConfigurationService configurationService;
    protected final Map<String, ServiceResponseTransformer> responseTransformerStrategyMap;
    protected final Map<String, ServiceRequestTransformer> requestTransformerStrategyMap;
    protected final GenericDao<AbstractOrderModel> abstractOrderGenericDao;
    protected final ModelService modelService;


    public DefaultWorldpayServiceGateway(final WorldpayConnector worldpayConnector,
                                         final WorldpayXMLValidator worldpayXMLValidator,
                                         final ConfigurationService configurationService,
                                         final Map<String, ServiceResponseTransformer> responseTransformerStrategyMap,
                                         final Map<String, ServiceRequestTransformer> requestTransformerStrategyMap,
                                         final GenericDao<AbstractOrderModel> abstractOrderGenericDao,
                                         final ModelService modelService) {
        this.worldpayConnector = worldpayConnector;
        this.worldpayXMLValidator = worldpayXMLValidator;
        this.configurationService = configurationService;
        this.responseTransformerStrategyMap = responseTransformerStrategyMap;
        this.requestTransformerStrategyMap = requestTransformerStrategyMap;
        this.abstractOrderGenericDao = abstractOrderGenericDao;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse directAuthorise(final DirectAuthoriseServiceRequest request) throws WorldpayException {
        return (DirectAuthoriseServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedirectAuthoriseServiceResponse redirectAuthorise(final RedirectAuthoriseServiceRequest request) throws WorldpayException {
        return (RedirectAuthoriseServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateTokenResponse createToken(final CreateTokenServiceRequest request) throws WorldpayException {
        return (CreateTokenResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateTokenResponse updateToken(final UpdateTokenServiceRequest request) throws WorldpayException {
        return (UpdateTokenResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteTokenResponse deleteToken(final DeleteTokenServiceRequest request) throws WorldpayException {
        return (DeleteTokenResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaptureServiceResponse capture(final CaptureServiceRequest request) throws WorldpayException {
        return (CaptureServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CancelServiceResponse cancel(final CancelServiceRequest request) throws WorldpayException {
        return (CancelServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoidSaleServiceResponse voidSale(final VoidSaleServiceRequest request) throws WorldpayException {
        return (VoidSaleServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundServiceResponse refund(final RefundServiceRequest request) throws WorldpayException {
        return (RefundServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddBackOfficeCodeServiceResponse addBackOfficeCode(final AddBackOfficeCodeServiceRequest request) throws WorldpayException {
        return (AddBackOfficeCodeServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorisationCodeServiceResponse authorisationCode(final AuthorisationCodeServiceRequest request) throws WorldpayException {
        return (AuthorisationCodeServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderInquiryServiceResponse orderInquiry(final AbstractServiceRequest request) throws WorldpayException {
        return (OrderInquiryServiceResponse) service(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse sendSecondThreeDSecurePayment(final SecondThreeDSecurePaymentRequest request) throws WorldpayException {
        return (DirectAuthoriseServiceResponse) service(request);
    }

    protected ServiceResponse service(final ServiceRequest request) throws WorldpayException {
        final ServiceRequestTransformer requestTransformer = requestTransformerStrategyMap.get(request.getClass().getName());
        final PaymentService paymentService = requestTransformer.transform(request);
        String paymentRequest;

        try {
            paymentRequest = logPaymentServiceXML(paymentService);
            worldpayXMLValidator.validate(paymentService);
        } catch (final WorldpayValidationException e) {
            throw new WorldpayValidationException("Error validating XML: " + e.getMessage(), e);
        }

        final ServiceReply reply = worldpayConnector.send(paymentService, request.getMerchantInfo(), request.getCookie());

        final ServiceResponseTransformer responseTransformer = responseTransformerStrategyMap.get(request.getClass().getName());
        final String paymentResponse = logPaymentServiceXML(reply.getPaymentService());
        saveRequestAndResponseInOrder(request.getOrderCode(), paymentRequest, paymentResponse);
        return responseTransformer.transform(reply);
    }

    private String logPaymentServiceXML(final PaymentService paymentService) throws WorldpayValidationException {
        final String environment = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT);
        if (PROD != Environment.valueOf(environment)) {
            try {
                final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
                marshaller.setProperty(JAXB_FRAGMENT, TRUE);
                return worldpayConnector.logXMLOut(paymentService);
            } catch (final JAXBException jaxbException) {
                throw new WorldpayValidationException(jaxbException.getMessage(), jaxbException);
            }
        }
        return null;
    }

    private void saveRequestAndResponseInOrder(final String orderCode, final String request, final String response) {
        if (StringUtils.isNotBlank(orderCode)) {
            final List<AbstractOrderModel> results = abstractOrderGenericDao.find(ImmutableMap.of(
                AbstractOrderModel.WORLDPAYORDERCODE, orderCode
            ));
            if (!results.isEmpty()) {
                final AbstractOrderModel abstractOrder = results.get(0);
                setPaymentRequestPayload(request, abstractOrder);
                setPaymentResponsePayload(response, abstractOrder);
                modelService.save(abstractOrder);
            }
        }
    }

    private void setPaymentRequestPayload(final String request, final AbstractOrderModel abstractOrder) {
        Optional.ofNullable(request).ifPresent(paymentRequest -> {
            final List<PayloadModel> requestList = new ArrayList<>(abstractOrder.getRequestsPayload());
            requestList.add(createPayloadModel(request));
            abstractOrder.setRequestsPayload(requestList);
        });
    }

    private void setPaymentResponsePayload(final String response, final AbstractOrderModel abstractOrder) {
        Optional.ofNullable(response).ifPresent(paymentResponse -> {
            final List<PayloadModel> requestList = new ArrayList<>(abstractOrder.getResponsesPayload());
            requestList.add(createPayloadModel(response));
            abstractOrder.setResponsesPayload(requestList);

        });
    }

    protected PayloadModel createPayloadModel(final String payload) {
        final PayloadModel payloadModel = modelService.create(PayloadModel.class);
        payloadModel.setPayload(payload);
        modelService.save(payloadModel);
        return payloadModel;
    }
}
