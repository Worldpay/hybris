package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.http.WorldpayConnectorImpl;
import com.worldpay.service.request.*;
import com.worldpay.service.request.transform.ServiceRequestTransformer;
import com.worldpay.service.request.transform.ServiceRequestTransformerFactory;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.service.request.validation.WorldpayXMLValidatorImpl;
import com.worldpay.service.response.*;
import com.worldpay.service.response.transform.ServiceResponseTransformer;
import com.worldpay.service.response.transform.ServiceResponseTransformerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;

import static com.worldpay.config.Environment.EnvironmentRole.PROD;
import static com.worldpay.util.WorldpayConstants.JAXB_CONTEXT;
import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;

/**
 * This class forms the main gateway for users into the Worldpay back-end systems.
 * <p/>
 * <p>There are convenience methods for making the following calls through to Worldpay:
 * <ul>
 * <li>Direct Authorise - To be used to authorise funds if your site is PCI compliant. Expects the payment details to be provided along with all the other authorisation
 * details like amount, shopper and shipping details. Use the convenience methods in {@link DirectAuthoriseServiceRequest} to create the request. The same call should be used
 * to pass in the 3D details if 3D authentication is needed.</li>
 * <li>Redirect Authorise - To be used to initialise the authorisation of funds if your site is not PCI compliant. Relevant payment methods can be passed in as a
 * payment method mask either as an include list, an exclude list or both. If mac validation is enabled then this is also carried out to ensure the mac code in the returned
 * url has not been tampered with. Use the convenience method in {@link RedirectAuthoriseServiceRequest} to create the request.</li>
 * <li>Capture - To be used to capture the funds. Provide the order code and the amount. Use the convenience method in {@link CaptureServiceRequest} to create the request.</li>
 * <li>Cancel - To be used to cancel the authorisation of funds. Cannot be invoked once the capture has taken place. Simply provide the order code. Use the convenience method in
 * {@link CancelServiceRequest} to create the request.</li>
 * <li>Refund - To be used to refund the captured funds. Any amount up to the original authorised amount can be refunded. Provide the order code and the amount to be refunded.
 * Use the convenience method in {@link RefundServiceRequest} to create the request.</li>
 * <li>Add Back Office Code - To be used to add the back office code. Provide the order code and the back office code. Use the convenience method in {@link AddBackOfficeCodeServiceRequest}
 * to create the request.</li>
 * <li>Authorisation Code - To be used to add the authorisation code. Provide the order code and the authorisation code. Use the convenience method in {@link AuthorisationCodeServiceRequest}
 * to create the request. </li>
 * <li>Order Inquiry - To be used to make inquiry against an order and find out the latest status in Worldpay. Provide the order code. Use the convenience method in
 * {@link OrderInquiryServiceResponse} to create the request.</li>
 * </ul>
 * Each call follows roughly the same steps of transforming the {@link ServiceRequest} object into an internal model representation of the payment service.
 * Then validating that this will produce well formed xml against the schema. Sending this xml to Worldpay. Receiving the response xml and building this into
 * an internal model representation of the reply. Finally the internal model is transformed back into a {@link ServiceResponse} to be returned to any implementers</p>
 * <p/>
 * <p>The same framework can be extended by injecting a {@link ServiceRequestTransformer} into the {@link ServiceRequestTransformerFactory}, and a {@link ServiceResponseTransformer}
 * into the {@link ServiceResponseTransformerFactory}. This allows further methods to be added and still use all the underlying framework for transforming, validating, sending
 * and receiving the xml</p>
 */
@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class WorldpayServiceGateway {

    private static WorldpayServiceGateway singletonObject = null;
    private ServiceRequestTransformerFactory serviceRequestTransformerFactory = new ServiceRequestTransformerFactory();
    private ServiceResponseTransformerFactory serviceResponseTransformerFactory = new ServiceResponseTransformerFactory();

    private WorldpayServiceGateway() {
    }

    public static synchronized WorldpayServiceGateway getInstance() {
        if (singletonObject == null) {
            singletonObject = new WorldpayServiceGateway();
        }
        return singletonObject;
    }

    /**
     * Make an authorisation request when using the direct payment model. The same method is also used for validating the 3D PaResponse if this is needed
     *
     * @param request Use the convenience methods in {@link DirectAuthoriseServiceRequest} to create the request depending on the action that is required. Ensures
     *                the correct details are set before making the call
     * @return {@link DirectAuthoriseServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public DirectAuthoriseServiceResponse directAuthorise(final DirectAuthoriseServiceRequest request) throws WorldpayException {
        return (DirectAuthoriseServiceResponse) service(request);
    }

    /**
     * Make an authorisation request when using the redirect payment model
     *
     * @param request Use the convenience method in {@link RedirectAuthoriseServiceRequest} to create the request. Ensures the correct details are set before making
     *                the call
     * @return {@link RedirectAuthoriseServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public RedirectAuthoriseServiceResponse redirectAuthorise(final RedirectAuthoriseServiceRequest request) throws WorldpayException {
        return (RedirectAuthoriseServiceResponse) service(request);
    }

    /**
     * Create a token in Worldpay
     *
     * @param request Use the convenience method in {@link CreateTokenServiceRequest} to create the tokenRequest.
     * @return {@link CreateTokenResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public CreateTokenResponse createToken(final CreateTokenServiceRequest request) throws WorldpayException {
        return (CreateTokenResponse) service(request);
    }

    /**
     * Updates a token in Worldpay
     *
     * @param request Use the convenience method in {@link UpdateTokenServiceRequest} to create the tokenRequest.
     * @return {@link UpdateTokenResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public UpdateTokenResponse updateToken(final UpdateTokenServiceRequest request) throws WorldpayException {
        return (UpdateTokenResponse) service(request);
    }

    /**
     * Delete a token in Worldpay
     *
     * @param request Use the convenience method in {@link DeleteTokenServiceRequest} to create the tokenRequest.
     * @return {@link DeleteTokenResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public DeleteTokenResponse deleteToken(final DeleteTokenServiceRequest request) throws WorldpayException {
        return (DeleteTokenResponse) service(request);
    }

    /**
     * Make a capture request with Worldpay
     *
     * @param request Use the convenience method in {@link CaptureServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link CaptureServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public CaptureServiceResponse capture(final CaptureServiceRequest request) throws WorldpayException {
        return (CaptureServiceResponse) service(request);
    }

    /**
     * Make a cancel request with Worldpay. Can only be invoked if the funds have not yet been captured
     *
     * @param request Use the convenience method in {@link CancelServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link CancelServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public CancelServiceResponse cancel(final CancelServiceRequest request) throws WorldpayException {
        return (CancelServiceResponse) service(request);
    }

    /**
     * Make a refund request with Worldpay. Can only be invoked if the funds have been captured
     *
     * @param request Use the convenience method in {@link RefundServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link RefundServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public RefundServiceResponse refund(final RefundServiceRequest request) throws WorldpayException {
        return (RefundServiceResponse) service(request);
    }

    /**
     * Make an add back office code request with Worldpay
     *
     * @param request Use the convenience method in {@link AddBackOfficeCodeServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link AddBackOfficeCodeServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public AddBackOfficeCodeServiceResponse addBackOfficeCode(final AddBackOfficeCodeServiceRequest request) throws WorldpayException {
        return (AddBackOfficeCodeServiceResponse) service(request);
    }

    /**
     * Make an authorisation code request with Worldpay
     *
     * @param request Use the convenience method in {@link AuthorisationCodeServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link AuthorisationCodeServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public AuthorisationCodeServiceResponse authorisationCode(final AuthorisationCodeServiceRequest request) throws WorldpayException {
        return (AuthorisationCodeServiceResponse) service(request);
    }

    /**
     * Make an order inquiry request with Worldpay
     *
     * @param request Use the convenience method in {@link AbstractServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link OrderInquiryServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    public OrderInquiryServiceResponse orderInquiry(final AbstractServiceRequest request) throws WorldpayException {
        return (OrderInquiryServiceResponse) service(request);
    }

    private ServiceResponse service(final ServiceRequest request) throws WorldpayException {
        final ServiceRequestTransformerFactory factory = getServiceRequestTransformerFactory();
        final ServiceRequestTransformer requestTransformer = factory.getServiceRequestTransformer(request);
        final PaymentService paymentService = requestTransformer.transform(request);
        final WorldpayConfig worldpayConfig = request.getWorldpayConfig();
        final String endpoint = worldpayConfig.getEnvironment().getEndpoint();
        final WorldpayConnector connector = new WorldpayConnectorImpl(endpoint);

        final WorldpayXMLValidator xmlValidator = new WorldpayXMLValidatorImpl();
        try {
            if (!worldpayConfig.getEnvironment().getRole().equals(PROD)) {
                logPaymentServiceXML(paymentService, connector);
            }
            xmlValidator.validate(paymentService);
        } catch (WorldpayValidationException e) {
            throw new WorldpayValidationException("Error validating XML: " + e.getMessage(), e);
        }

        final ServiceReply reply = connector.send(paymentService, request.getMerchantInfo(), request.getCookie());

        final ServiceResponseTransformerFactory responseFactory = getServiceResponseTransformerFactory();
        final ServiceResponseTransformer responseTransformer = responseFactory.getServiceResponseTransformer(request);
        if (!worldpayConfig.getEnvironment().getRole().equals(PROD)) {
            logPaymentServiceXML(reply.getPaymentService(), connector);
        }
        return responseTransformer.transform(reply);
    }

    private void logPaymentServiceXML(PaymentService paymentService, WorldpayConnector connector) throws WorldpayValidationException {
        try {
            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(JAXB_FRAGMENT, TRUE);
            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            connector.logXMLOut(xof, marshaller, paymentService);
        } catch (final JAXBException jaxbException) {
            throw new WorldpayValidationException(jaxbException.getMessage(), jaxbException);
        }
    }

    public ServiceRequestTransformerFactory getServiceRequestTransformerFactory() {
        return serviceRequestTransformerFactory;
    }

    public ServiceResponseTransformerFactory getServiceResponseTransformerFactory() {
        return serviceResponseTransformerFactory;
    }
}
