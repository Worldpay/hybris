package com.worldpay.service;

import com.worldpay.config.Environment;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.request.*;
import com.worldpay.service.request.transform.ServiceRequestTransformer;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.service.response.*;
import com.worldpay.service.response.transform.ServiceResponseTransformer;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Map;

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

    public DefaultWorldpayServiceGateway(final WorldpayConnector worldpayConnector,
                                         final WorldpayXMLValidator worldpayXMLValidator,
                                         final ConfigurationService configurationService,
                                         final Map<String, ServiceResponseTransformer> responseTransformerStrategyMap,
                                         final Map<String, ServiceRequestTransformer> requestTransformerStrategyMap) {
        this.worldpayConnector = worldpayConnector;
        this.worldpayXMLValidator = worldpayXMLValidator;
        this.configurationService = configurationService;
        this.responseTransformerStrategyMap = responseTransformerStrategyMap;
        this.requestTransformerStrategyMap = requestTransformerStrategyMap;
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
        try {
            logPaymentServiceXML(paymentService);
            worldpayXMLValidator.validate(paymentService);
        } catch (final WorldpayValidationException e) {
            throw new WorldpayValidationException("Error validating XML: " + e.getMessage(), e);
        }

        final ServiceReply reply = worldpayConnector.send(paymentService, request.getMerchantInfo(), request.getCookie());

        final ServiceResponseTransformer responseTransformer = responseTransformerStrategyMap.get(request.getClass().getName());
        logPaymentServiceXML(reply.getPaymentService());
        return responseTransformer.transform(reply);
    }

    private void logPaymentServiceXML(PaymentService paymentService) throws WorldpayValidationException {
        final String environment = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT);
        if (PROD != Environment.valueOf(environment)) {
            try {
                final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
                marshaller.setProperty(JAXB_FRAGMENT, TRUE);
                worldpayConnector.logXMLOut(paymentService);
            } catch (final JAXBException jaxbException) {
                throw new WorldpayValidationException(jaxbException.getMessage(), jaxbException);
            }
        }
    }
}
