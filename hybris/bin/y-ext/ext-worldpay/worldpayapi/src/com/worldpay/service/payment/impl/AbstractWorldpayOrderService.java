package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Abstract implementation of the {@link WorldpayRedirectOrderService} allows configuration of standard services required
 * by implementors
 */
public abstract class AbstractWorldpayOrderService {

    protected final WorldpayPaymentInfoService worldpayPaymentInfoService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final WorldpayOrderService worldpayOrderService;
    protected final WorldpayServiceGateway worldpayServiceGateway;

    protected AbstractWorldpayOrderService(final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                           final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                           final WorldpayOrderService worldpayOrderService,
                                           final WorldpayServiceGateway worldpayServiceGateway) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayServiceGateway = worldpayServiceGateway;
    }

    /**
     * Workaround: Extra address created when an order is placed
     * Potential bug in class: DefaultCommercePlaceOrderStrategy
     * Method: public CommerceOrderResult placeOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {...}
     * Logic: if(cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null) {...}
     *
     * @param cartModel        holding the source address
     * @param paymentInfoModel holding the address owner
     * @return the cloned address model
     */
    protected AddressModel cloneAndSetBillingAddressFromCart(final CartModel cartModel, final PaymentInfoModel paymentInfoModel) {
        return worldpayPaymentInfoService.cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
    }
}
