package com.worldpay.service.request.transform;


import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.request.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Class is a factory class for determining the correct {@link ServiceRequestTransformer} for a given {@link ServiceRequest}
 * <p/>
 * <p>The class encapsulates a registry of request names against an instance of their associated request transformer. The registry is extensible so there are convenience
 * methods for adding new entries into the registry</p>
 */
public class ServiceRequestTransformerFactory {

    private static Map<String, ServiceRequestTransformer> requestTransformerRegistry = new HashMap<>();

    static {
        requestTransformerRegistry.put(DirectAuthoriseServiceRequest.class.getName(), new AuthoriseRequestTransformer());
        requestTransformerRegistry.put(CreateTokenServiceRequest.class.getName(), new CreateTokenRequestTransformer());
        requestTransformerRegistry.put(UpdateTokenServiceRequest.class.getName(), new UpdateTokenRequestTransformer());
        requestTransformerRegistry.put(DeleteTokenServiceRequest.class.getName(), new DeleteTokenRequestTransformer());
        requestTransformerRegistry.put(RedirectAuthoriseServiceRequest.class.getName(), new AuthoriseRequestTransformer());
        requestTransformerRegistry.put(CaptureServiceRequest.class.getName(), new CaptureRequestTransformer());
        requestTransformerRegistry.put(CancelServiceRequest.class.getName(), new CancelRequestTransformer());
        requestTransformerRegistry.put(RefundServiceRequest.class.getName(), new RefundRequestTransformer());
        requestTransformerRegistry.put(AddBackOfficeCodeServiceRequest.class.getName(), new AddBackOfficeCodeRequestTransformer());
        requestTransformerRegistry.put(AuthorisationCodeServiceRequest.class.getName(), new AuthorisationCodeRequestTransformer());
        requestTransformerRegistry.put(OrderInquiryServiceRequest.class.getName(), new OrderInquiryRequestTransformer());
        requestTransformerRegistry.put(KlarnaOrderInquiryServiceRequest.class.getName(), new OrderInquiryRequestTransformer());
    }

    /**
     * Get the {@link ServiceRequestTransformer} for a given {@link ServiceRequest}
     *
     * @param request request to retrieve the transformer for
     * @return instance of the ServiceRequestTransformer from the registry
     * @throws WorldpayModelTransformationException if no entry exists in the registry for the given request
     */
    public ServiceRequestTransformer getServiceRequestTransformer(ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("No request provided to the ServiceRequestTransformerFactory");
        }

        return requestTransformerRegistry.get(request.getClass().getName());
    }

    /**
     * Add an entry into the registry of request class name and the related transformer
     *
     * @param request     request to add to the registry
     * @param transformer transformer to add to the registry
     */
    public static void addServiceRequestTransformer(ServiceRequest request, ServiceRequestTransformer transformer) {
        requestTransformerRegistry.put(request.getClass().getName(), transformer);
    }

    /**
     * Add an entry into the registry of request class name and the related transformer
     *
     * @param serviceRequestClass serviceRequestClass to add to the registry
     * @param transformer         transformer to add to the registry
     */
    public static void addServiceRequestTransformer(Class<ServiceRequest> serviceRequestClass, ServiceRequestTransformer transformer) {
        requestTransformerRegistry.put(serviceRequestClass.getName(), transformer);
    }
}
