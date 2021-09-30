package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.payment.WorldpayLevel23DataValidator;
import com.worldpay.service.payment.WorldpayLevel23Strategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.lang3.NotImplementedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.valueOf;

/**
 * Default implementation of {@link WorldpayLevel23Strategy}.
 */
public class DefaultWorldpayLevel23Strategy extends AbstractWorldpayLevel23Strategy implements WorldpayAdditionalDataRequestStrategy {

    private static final String US_COUNTRY_ISO_CODE = "US";
    private static final String CA_COUNTRY_ISO_CODE = "CA";
    private static final String NOT_IMPLEMENTED_ERROR_MESSAGE = "Implement this method based on business requirements";

    protected final WorldpayMerchantStrategy worldpayMerchantStrategy;
    protected final WorldpayOrderService worldpayOrderService;
    protected final WorldpayLevel23DataValidator worldpayLevel23DataValidator;

    public DefaultWorldpayLevel23Strategy(final WorldpayMerchantStrategy worldpayMerchantStrategy,
                                          final WorldpayOrderService worldpayOrderService,
                                          final WorldpayLevel23DataValidator worldpayLevel23DataValidator) {
        this.worldpayMerchantStrategy = worldpayMerchantStrategy;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayLevel23DataValidator = worldpayLevel23DataValidator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        if (isLevel3Enabled(cart) || isLevel2Enabled(cart)) {
            final BranchSpecificExtension level23Data = createLevel23Data(cart);
            if (worldpayLevel23DataValidator.isValidLevel3Data(level23Data.getPurchase()) || worldpayLevel23DataValidator.isValidLevel2Data(level23Data.getPurchase())) {
                authoriseRequestParametersCreator.withLevel23Data(level23Data);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BranchSpecificExtension createLevel23Data(final AbstractOrderModel abstractOrder) {
        validateParameterNotNull(abstractOrder, "The cart is null");

        final Purchase purchase = new Purchase();
        setCustomerReference(abstractOrder, purchase);
        purchase.setCardAcceptorTaxId(worldpayMerchantStrategy.getMerchant().getCardAcceptorTaxID());

        final CurrencyModel currency = abstractOrder.getCurrency();
        setSalesTaxAndTaxExempt(purchase, currency, abstractOrder);

        purchase.setDiscountAmount(worldpayOrderService.createAmount(currency, abstractOrder.getTotalDiscounts()));
        purchase.setShippingAmount(worldpayOrderService.createAmount(currency, abstractOrder.getDeliveryCost()));
        setDutyAmount(abstractOrder, purchase);

        final AddressModel deliveryAddress = abstractOrder.getDeliveryAddress();
        purchase.setDestinationCountryCode(deliveryAddress.getCountry().getIsocode());
        purchase.setDestinationPostalCode(deliveryAddress.getPostalcode());

        final LocalDateTime orderDate = Instant.ofEpochMilli(abstractOrder.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        purchase.setOrderDate(WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(orderDate));

        final List<Item> items = abstractOrder.getEntries().stream()
            .map(orderEntry -> createItem(orderEntry, currency))
            .collect(Collectors.toList());
        purchase.setItem(items);

        final BranchSpecificExtension branchSpecificExtension = new BranchSpecificExtension();
        branchSpecificExtension.setPurchase(List.of(purchase));

        return branchSpecificExtension;
    }

    /**
     * Populates the customer reference. Override this method with real values based on business requirements
     */
    @Override
    protected void setCustomerReference(final AbstractOrderModel order, final Purchase purchase) {
        throw new NotImplementedException(NOT_IMPLEMENTED_ERROR_MESSAGE);
    }

    /**
     * Populates the product description. Override this method with real values based on business requirements
     */
    @Override
    protected void setProductDescription(final ProductModel product, final Item item) {
        throw new NotImplementedException(NOT_IMPLEMENTED_ERROR_MESSAGE);
    }

    /**
     * Populates the duty amount. Override this method with real values based on business requirements
     */
    @Override
    protected void setDutyAmount(final AbstractOrderModel order, final Purchase purchase) {
        throw new NotImplementedException(NOT_IMPLEMENTED_ERROR_MESSAGE);
    }

    /**
     * Checks if the Level 2 is enabled for the cart
     *
     * @param abstractOrder the cart
     * @return true if enabled, false otherwise
     */
    protected boolean isLevel2Enabled(final AbstractOrderModel abstractOrder) {
        return abstractOrder.getSite().getEnableLevel2()
            && isBillingAddressUSorCA(abstractOrder);
    }

    /**
     * Checks if the Level 3 is enabled for the cart
     *
     * @param abstractOrder the cart
     * @return true if enabled, false otherwise
     */
    protected boolean isLevel3Enabled(final AbstractOrderModel abstractOrder) {
        return abstractOrder.getSite().getEnableLevel3()
            && isBillingAddressUSorCA(abstractOrder);
    }

    /**
     * Sets the sales tax of the order on the purchase item.
     *
     * @param purchase      the purchase item
     * @param currency      the order currency
     * @param abstractOrder the order
     */
    protected void setSalesTaxAndTaxExempt(final Purchase purchase, final CurrencyModel currency, final AbstractOrderModel abstractOrder) {
        final Double totalTax = abstractOrder.getTotalTax();
        purchase.setSalesTax(worldpayOrderService.createAmount(currency, totalTax));
        purchase.setTaxExempt(BigDecimal.ZERO.compareTo(BigDecimal.valueOf(totalTax)) == 0);
    }

    /**
     * Create an Item for every order entry
     *
     * @param orderEntry the order entry
     * @param currency   currency of the order
     * @return {@link Item}
     */
    protected Item createItem(final AbstractOrderEntryModel orderEntry, final CurrencyModel currency) {
        final Item item = new Item();
        final ProductModel product = orderEntry.getProduct();
        item.setProductCode(product.getCode());
        setProductDescription(product, item);
        item.setCommodityCode(product.getCommodityCode());
        item.setQuantity(valueOf(orderEntry.getQuantity()));
        item.setUnitCost(worldpayOrderService.createAmount(currency, orderEntry.getBasePrice()));
        item.setUnitOfMeasure(orderEntry.getUnit().getCode());

        final Double totalPriceWithTax = orderEntry.getTotalPrice();
        item.setItemTotalWithTax(worldpayOrderService.createAmount(currency, totalPriceWithTax));
        final Double appliedTax = getAppliedTaxValue(orderEntry);
        item.setTaxAmount(worldpayOrderService.createAmount(currency, appliedTax));
        item.setItemTotal(worldpayOrderService.createAmount(currency, totalPriceWithTax - appliedTax));
        item.setItemDiscountAmount(worldpayOrderService.createAmount(currency, getDiscountValue(orderEntry)));

        return item;
    }

    /**
     * Returns true if shipping address of the cart is Canada or United States
     *
     * @param abstractOrder the cart
     * @return true if address in US or CA, false otherwise
     */
    protected boolean isBillingAddressUSorCA(final AbstractOrderModel abstractOrder) {
        final AddressModel paymentAddress = abstractOrder.getPaymentAddress();
        if (Objects.nonNull(paymentAddress)) {
            final String countryIsoCode = paymentAddress.getCountry().getIsocode();
            return US_COUNTRY_ISO_CODE.equals(countryIsoCode) || CA_COUNTRY_ISO_CODE.equals(countryIsoCode);
        }
        return false;
    }

    /**
     * Sums the discount values applied to an order entry
     *
     * @param orderEntry the order entry
     * @return the discount value
     */
    protected Double getDiscountValue(final AbstractOrderEntryModel orderEntry) {
        return orderEntry.getDiscountValues().stream()
            .map(DiscountValue::getAppliedValue)
            .reduce(0d, Double::sum);
    }

    /**
     * Returns the sum of applied tax values for the order entry
     *
     * @param orderEntry the order entry
     * @return the applied tax value
     */
    protected double getAppliedTaxValue(final AbstractOrderEntryModel orderEntry) {
        return orderEntry.getTaxValues().stream()
            .map(TaxValue::getAppliedValue)
            .reduce(0d, Double::sum);
    }
}
