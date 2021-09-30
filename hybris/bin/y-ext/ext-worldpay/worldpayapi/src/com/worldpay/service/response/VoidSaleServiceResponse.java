package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#voidSale(com.worldpay.service.request.VoidSaleServiceRequest) voidSale()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>No further parameters are returned on top of the standard ones</p>
 */
public class VoidSaleServiceResponse extends CancelServiceResponse {

}
