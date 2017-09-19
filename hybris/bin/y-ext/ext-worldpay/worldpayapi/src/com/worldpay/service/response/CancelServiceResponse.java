package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.request.CancelServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#cancel(CancelServiceRequest) cancel()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>No further parameters are returned on top of the standard ones</p>
 */
public class CancelServiceResponse extends AbstractServiceResponse {

}
