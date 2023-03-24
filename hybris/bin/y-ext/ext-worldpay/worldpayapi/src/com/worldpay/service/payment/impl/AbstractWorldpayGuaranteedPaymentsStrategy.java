package com.worldpay.service.payment.impl;

import com.worldpay.data.Membership;
import com.worldpay.data.Product;
import com.worldpay.data.PurchaseDiscount;
import com.worldpay.data.UserAccount;
import com.worldpay.internal.model.Memberships;
import com.worldpay.service.payment.WorldpayGuaranteedPaymentsStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

/**
 * Abstract implementation of {@link WorldpayGuaranteedPaymentsStrategy}
 */
public abstract class AbstractWorldpayGuaranteedPaymentsStrategy implements WorldpayGuaranteedPaymentsStrategy {

    /**
     * Creates and populates the UserAccount.
     *
     * @return the {@link UserAccount}
     */
    protected abstract UserAccount createUserAccount(final AbstractOrderModel abstractOrder);

    /**
     * Creates and populates the DiscountCodes.
     *
     * @return the {@link List<PurchaseDiscount>}
     */
    protected abstract List<PurchaseDiscount> createDiscountCodes(final AbstractOrderModel abstractOrder);

    /**
     * Creates and populates the ProductDetails.
     *
     * @return the {@link List<Product>}
     */
    protected abstract List<Product> crateProductDetails(final AbstractOrderModel abstractOrder);

    /**
     * Creates and populates the FulfillmentMethodType.
     *
     * @return the value of FulfillmentMethodType
     */
    protected abstract String createFulfillmentMethodType(final AbstractOrderModel abstractOrder);

    /**
     * Creates and populates the Memberships.
     *
     * @return the {@link Memberships}
     */
    protected abstract List<Membership> createMemberships();

    /**
     * Creates and populates the SecondaryAmount.
     *
     * @return the value
     */
    protected abstract String createSecondaryAmount();

    /**
     * Creates and populates the SurchargeAmount.
     *
     * @return the value
     */
    protected abstract String createSurchargeAmount();


    /**
     * Creates and populates the TotalShippingCost.
     *
     * @return the TotalShippingCost
     */
    protected abstract String createTotalShippingCost(final AbstractOrderModel abstractOrder);
}
