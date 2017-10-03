package com.worldpay.service.request;

import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.Order;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.token.Token;

/**
 * This class adds the {@link Order} details to the AbstractServiceRequest and forms the superclass of all athorise requests. It's not used directly as the
 * Direct and Redirect authorise calls both add their own specific parameters on top of what is supplied here
 * <p/>
 * <p>On top of the standard parameters it provides the order that needs to be sent in authorise calls</p>
 */
public class AuthoriseServiceRequest extends AbstractServiceRequest {

    private Order order;

    protected AuthoriseServiceRequest(final MerchantInfo merchantInfo, String orderCode) {
        super(merchantInfo, orderCode);
    }

    protected static void checkInstanceOfToken(final Payment payment) {
        if (!(payment instanceof Token)) {
            throw new IllegalArgumentException("Payment type needs to be a type of Token");
        }
    }

    public Order getOrder() {
        return order;
    }

    protected void setOrder(Order order) {
        this.order = order;
    }
}
