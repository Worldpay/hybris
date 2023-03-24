package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.GuaranteedPaymentsData;
import com.worldpay.data.Membership;
import com.worldpay.data.Product;
import com.worldpay.data.PurchaseDiscount;
import com.worldpay.data.UserAccount;
import com.worldpay.internal.model.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.GuaranteedPaymentsData} with the information of a {@link GuaranteedPaymentsData}
 */
public class GuaranteedPaymentsDataPopulator implements Populator<GuaranteedPaymentsData, com.worldpay.internal.model.GuaranteedPaymentsData> {

    protected final Converter<PurchaseDiscount, com.worldpay.internal.model.PurchaseDiscount> internalPurchaseDiscountConverter;
    protected final Converter<Membership, com.worldpay.internal.model.Membership> internalMembershipConverter;
    protected final Converter<Product, com.worldpay.internal.model.Product> internalProductConverter;
    protected final Converter<UserAccount, com.worldpay.internal.model.UserAccount> internalUserAccountConverter;

    public GuaranteedPaymentsDataPopulator(final Converter<PurchaseDiscount, com.worldpay.internal.model.PurchaseDiscount> internalPurchaseDiscountConverter,
                                           final Converter<Membership, com.worldpay.internal.model.Membership> internalMembershipConverter,
                                           final Converter<Product, com.worldpay.internal.model.Product> internalProductConverter,
                                           final Converter<UserAccount, com.worldpay.internal.model.UserAccount> internalUserAccountConverter) {

        this.internalPurchaseDiscountConverter = internalPurchaseDiscountConverter;
        this.internalMembershipConverter = internalMembershipConverter;
        this.internalProductConverter = internalProductConverter;
        this.internalUserAccountConverter = internalUserAccountConverter;
    }

    /**
     * Populates the data from the {@link GuaranteedPaymentsData} to a {@link com.worldpay.internal.model.GuaranteedPaymentsData}
     *
     * @param source a {@link GuaranteedPaymentsData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.GuaranteedPaymentsData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final GuaranteedPaymentsData source,
                         final com.worldpay.internal.model.GuaranteedPaymentsData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final FulfillmentMethodType fulfillmentMethodType = new FulfillmentMethodType();
        target.setFulfillmentMethodType(fulfillmentMethodType);

        Optional.ofNullable(source.getFulfillmentMethodType()).ifPresent(fulfillmentMethodType::setValue);

        final SecondaryAmount secondaryAmount = new SecondaryAmount();
        target.setSecondaryAmount(secondaryAmount);
        Optional.ofNullable(source.getSecondaryAmount()).ifPresent(secondaryAmount::setValue);

        final SurchargeAmount surchargeAmount = new SurchargeAmount();
        target.setSurchargeAmount(surchargeAmount);
        Optional.ofNullable(source.getSurchargeAmount()).ifPresent(surchargeAmount::setValue);

        Optional.ofNullable(source.getUserAccount())
            .map(internalUserAccountConverter::convert)
            .ifPresent(target::setUserAccount);

        Optional.ofNullable(source.getTotalShippingCost()).ifPresent(target::setTotalShippingCost);

        if (CollectionUtils.isNotEmpty(source.getDiscountCodes())) {
            final DiscountCodes discountCodes = new DiscountCodes();
            target.setDiscountCodes(discountCodes);
            discountCodes.getPurchaseDiscount()
                .addAll(internalPurchaseDiscountConverter.convertAll(source.getDiscountCodes()));
        }

        if (CollectionUtils.isNotEmpty(source.getProductDetails())) {
            final ProductDetails productDetails = new ProductDetails();
            target.setProductDetails(productDetails);
            productDetails.getProduct().addAll(internalProductConverter.convertAll(source.getProductDetails()));
        }

        if (CollectionUtils.isNotEmpty(source.getMemberships())) {
            final Memberships memberships = new Memberships();
            target.setMemberships(memberships);
            memberships.getMembership().addAll(internalMembershipConverter.convertAll(source.getMemberships()));
        }
    }
}
