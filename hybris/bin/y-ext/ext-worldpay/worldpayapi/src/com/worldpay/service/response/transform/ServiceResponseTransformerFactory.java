package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.request.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Class is a factory class for determining the correct {@link ServiceResponseTransformer} for a given {@link ServiceRequest}
 * <p/>
 * <p>The class encapsulates a registry of request names against an instance of their associated response transformer. The registry is extensible so there are convenience
 * methods for adding new entries into the registry</p>
 */
public class ServiceResponseTransformerFactory {

    private static Map<String, ServiceResponseTransformer> responseTransformerRegistry = new HashMap<>();

    static {
        responseTransformerRegistry.put(DirectAuthoriseServiceRequest.class.getName(), new DirectAuthoriseResponseTransformer());
        responseTransformerRegistry.put(CreateTokenServiceRequest.class.getName(), new CreateTokenResponseTransformer());
        responseTransformerRegistry.put(UpdateTokenServiceRequest.class.getName(), new UpdateTokenResponseTransformer());
        responseTransformerRegistry.put(DeleteTokenServiceRequest.class.getName(), new DeleteTokenResponseTransformer());
        responseTransformerRegistry.put(RedirectAuthoriseServiceRequest.class.getName(), new RedirectAuthoriseResponseTransformer());
        responseTransformerRegistry.put(CaptureServiceRequest.class.getName(), new CaptureResponseTransformer());
        responseTransformerRegistry.put(CancelServiceRequest.class.getName(), new CancelResponseTransformer());
        responseTransformerRegistry.put(RefundServiceRequest.class.getName(), new RefundResponseTransformer());
        responseTransformerRegistry.put(AddBackOfficeCodeServiceRequest.class.getName(), new AddBackOfficeCodeResponseTransformer());
        responseTransformerRegistry.put(AuthorisationCodeServiceRequest.class.getName(), new AuthorisationCodeResponseTransformer());
        responseTransformerRegistry.put(OrderInquiryServiceRequest.class.getName(), new OrderInquiryResponseTransformer());
        responseTransformerRegistry.put(KlarnaOrderInquiryServiceRequest.class.getName(), new OrderInquiryResponseTransformer());
    }

    /**
     * Get the {@link ServiceResponseTransformer} for a given {@link ServiceRequest}
     *
     * @param request request to retrieve the transformer for
     * @return instance of the ServiceResponseTransformer from the registry
     * @throws WorldpayModelTransformationException if no entry exists in the registry for the given request
     */
    public ServiceResponseTransformer getServiceResponseTransformer(ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("No request provided to the ServiceResponseTransformerFactory");
        }

        return responseTransformerRegistry.get(request.getClass().getName());
    }

    /**
     * Add an entry into the registry of request class name and the related transformer
     *
     * @param request     request to add to the registry
     * @param transformer transformer to add to the registry
     */
    public static void addServiceResponseTransformer(ServiceRequest request, ServiceResponseTransformer transformer) {
        responseTransformerRegistry.put(request.getClass().getName(), transformer);
    }

    /**
     * Add an entry into the registry of request class name and the related transformer
     *
     * @param serviceRequestClass serviceRequestClass to add to the registry
     * @param transformer         transformer to add to the registry
     */
    public static void addServiceResponseTransformer(Class<ServiceRequest> serviceRequestClass, ServiceResponseTransformer transformer) {
        responseTransformerRegistry.put(serviceRequestClass.getName(), transformer);
    }
}
