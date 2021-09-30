package com.worldpay.service.payment.impl;

import com.worldpay.data.LineItem;
import com.worldpay.data.LineItemReference;
import com.worldpay.data.OrderLines;
import com.worldpay.enums.lineItem.LineItemType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayKlarnaService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.lang.StringEscapeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayKlarnaService implements WorldpayKlarnaService {

    private static final String ORDER_DISCOUNT = "ORDER_DISCOUNT";
    private static final String DISCOUNT_LINE_ITEM_REFERENCE = "DISCOUNT_LINE_ITEM_REFERENCE";
    private static final String SHIPPING_LINE_ITEM_REFERENCE = "SHIPPING_LINE_ITEM_REFERENCE";
    private static final String DISCOUNT = "discount";
    private static final String SHIPPING = "shipping";

    protected final CommonI18NService commonI18NService;
    protected final WorldpayUrlService worldpayUrlService;
    protected final List<String> klarnaPayments;

    public DefaultWorldpayKlarnaService(final CommonI18NService commonI18NService, final WorldpayUrlService worldpayUrlService,
                                        final List<String> klarnaPayments) {
        this.commonI18NService = commonI18NService;
        this.worldpayUrlService = worldpayUrlService;
        this.klarnaPayments = klarnaPayments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLines createOrderLines(final CartModel cartModel) throws WorldpayConfigurationException {
        final Integer digits = cartModel.getCurrency().getDigits();
        final String orderTaxAmount = convertDoubleToStringFormat(digits, cartModel.getTotalTax());
        final String termsURL = worldpayUrlService.getFullTermsUrl();

        final List<LineItem> lineItems = cartModel.getEntries().stream().map(this::createLineItem).collect(toList());

        if (cartModel.getDeliveryCost() > 0) {
            addShippingFeeLineItem(cartModel, digits, lineItems);
        }

        if (cartModel.getGlobalDiscountValues().stream().anyMatch(DiscountValue::isAbsolute)) {
            lineItems.add(createDiscountLineItem(cartModel, digits));
        }

        final OrderLines orderLines = new OrderLines();
        orderLines.setOrderTaxAmount(orderTaxAmount);
        orderLines.setTermsURL(termsURL);
        orderLines.setLineItems(lineItems);

        if (orderLines.getLineItems().size() > 1) {
            verifySweepPennies(cartModel, orderLines);
        }

        return orderLines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKlarnaPaymentType(final String paymentCode) {
        return Objects.nonNull(paymentCode) && klarnaPayments.stream().anyMatch(paymentCode::equals);
    }

    private void verifySweepPennies(final CartModel cartModel, final OrderLines orderLines) {
        final int digits = cartModel.getCurrency().getDigits();
        final double totalTax = cartModel.getTotalTax();
        final double calculatedTotalTaxAmount = orderLines.getLineItems().stream().mapToDouble(LineItem::getTotalTaxAmountValue).sum();
        final double roundedCalculatedTotalTaxAmount = BigDecimal.valueOf(calculatedTotalTaxAmount).setScale(cartModel.getCurrency().getDigits(), RoundingMode.HALF_UP).doubleValue();

        if (BigDecimal.valueOf(totalTax).compareTo(BigDecimal.valueOf(roundedCalculatedTotalTaxAmount)) != 0) {
            final double taxRoundingDifference = Math.abs(totalTax - roundedCalculatedTotalTaxAmount);
            orderLines.getLineItems().stream()
                .filter(lineItem1 -> LineItemType.DISCOUNT.equals(lineItem1.getLineItemType()))
                .findAny()
                .ifPresentOrElse(discountLine -> {
                    final double adjustedLineItemTaxAmount = discountLine.getTotalTaxAmountValue() - taxRoundingDifference;
                    discountLine.setTotalTaxAmount(convertDoubleToStringFormat(digits, adjustedLineItemTaxAmount));
                }, () -> orderLines.getLineItems().stream()
                    .filter(lineItem1 -> LineItemType.SHIPPING_FEE.equals(lineItem1.getLineItemType()))
                    .findAny()
                    .ifPresent(shippingLineItem -> {
                        final double adjustedLineItemTaxAmount = shippingLineItem.getTotalTaxAmountValue() - taxRoundingDifference;
                        shippingLineItem.setTotalTaxAmount(convertDoubleToStringFormat(digits, adjustedLineItemTaxAmount));
                    }));
        }
    }

    private LineItem createLineItem(final AbstractOrderEntryModel entry) {
        final Integer digits = entry.getOrder().getCurrency().getDigits();

        final LineItem lineItem = new LineItem();
        lineItem.setLineItemType(LineItemType.PHYSICAL);
        final LineItemReference lineItemReference = new LineItemReference();
        lineItemReference.setValue(String.valueOf(entry.getEntryNumber()));
        lineItem.setLineItemReference(lineItemReference);
        lineItem.setName(StringEscapeUtils.escapeXml(entry.getProduct().getName()));
        lineItem.setQuantity(String.valueOf(entry.getQuantity()));
        lineItem.setQuantityUnit(StringEscapeUtils.escapeXml(entry.getProduct().getUnit().getName()));
        lineItem.setUnitPrice(convertDoubleToStringFormat(digits, entry.getBasePrice()));
        final double totalAmount = calculateEntryTotalAmount(entry);
        lineItem.setTotalAmount(convertDoubleToStringFormat(digits, totalAmount));
        lineItem.setTaxRate(convertDoubleToStringFormat(digits, entry.getTaxValues().iterator().next().getValue()));
        final double totalTaxAmount = calculateEntryTotalTaxAmount(entry);
        lineItem.setTotalTaxAmount(convertDoubleToStringFormat(digits, totalTaxAmount));
        lineItem.setTotalTaxAmountValue(totalTaxAmount);
        lineItem.setTotalDiscountAmount(convertDoubleToStringFormat(digits, (entry.getBasePrice() * entry.getQuantity()) - totalAmount));
        return lineItem;
    }

    private void addShippingFeeLineItem(final CartModel cartModel, final Integer digits, final List<LineItem> lineItems) {
        final LineItem shippingFeeLineItem = new LineItem();
        shippingFeeLineItem.setLineItemType(LineItemType.SHIPPING_FEE);
        final LineItemReference lineItemReference = new LineItemReference();
        lineItemReference.setValue(SHIPPING_LINE_ITEM_REFERENCE);
        shippingFeeLineItem.setLineItemReference(lineItemReference);
        shippingFeeLineItem.setName(cartModel.getDeliveryMode().getName());
        shippingFeeLineItem.setQuantity("1");
        shippingFeeLineItem.setQuantityUnit(SHIPPING);
        shippingFeeLineItem.setUnitPrice(convertDoubleToStringFormat(digits, cartModel.getDeliveryCost()));
        shippingFeeLineItem.setTotalAmount(convertDoubleToStringFormat(digits, cartModel.getDeliveryCost()));
        shippingFeeLineItem.setTotalDiscountAmount("0");
        final double totalCartTaxRate = cartModel.getTotalTaxValues().stream().mapToDouble(TaxValue::getValue).sum();
        shippingFeeLineItem.setTaxRate(convertDoubleToStringFormat(digits, totalCartTaxRate));
        final double taxShipping = calculateVATAmount(cartModel.getDeliveryCost(), totalCartTaxRate, digits);
        shippingFeeLineItem.setTotalTaxAmount(convertDoubleToStringFormat(digits, taxShipping));
        shippingFeeLineItem.setTotalTaxAmountValue(taxShipping);
        lineItems.add(shippingFeeLineItem);
    }

    private LineItem createDiscountLineItem(final CartModel cartModel, final Integer digits) {
        final LineItem lineItem = new LineItem();
        lineItem.setLineItemType(LineItemType.DISCOUNT);
        final LineItemReference lineItemReference = new LineItemReference();
        lineItemReference.setValue(DISCOUNT_LINE_ITEM_REFERENCE);
        lineItem.setLineItemReference(lineItemReference);
        cartModel.getTotalTax();
        lineItem.setName(ORDER_DISCOUNT);
        lineItem.setQuantity("1");
        lineItem.setQuantityUnit(DISCOUNT);
        lineItem.setUnitPrice("0");
        final double discountValue = cartModel.getGlobalDiscountValues().stream().filter(DiscountValue::isAbsolute).mapToDouble(DiscountValue::getAppliedValue).sum();
        lineItem.setTotalAmount(convertDoubleToStringFormat(digits, -discountValue));
        lineItem.setTotalDiscountAmount(convertDoubleToStringFormat(digits, discountValue));
        final double totalCartTaxRate = cartModel.getTotalTaxValues().stream().mapToDouble(TaxValue::getValue).sum();
        lineItem.setTaxRate(convertDoubleToStringFormat(digits, totalCartTaxRate));
        final double discountTotalTaxAmount = calculateDiscountTotalTaxAmount(discountValue, totalCartTaxRate, digits, cartModel);
        lineItem.setTotalTaxAmount(convertDoubleToStringFormat(digits, -discountTotalTaxAmount));
        lineItem.setTotalTaxAmountValue(-discountTotalTaxAmount);
        return lineItem;
    }

    private double calculateDiscountTotalTaxAmount(final double discountValue, final double taxRate, final Integer digits, final CartModel cartModel) {
        final double calculatedEntriesTaxAmount = cartModel.getEntries().stream().mapToDouble(this::calculateEntryTotalTaxAmount).sum();
        final double calculatedDiscountTaxAmount = calculateVATAmount(discountValue, taxRate, digits);
        final double totalDeliveryCost = cartModel.getDeliveryCost();
        final double shippingTaxAmount = totalDeliveryCost > 0 ? calculateVATAmount(totalDeliveryCost, taxRate, digits) : 0;
        final double calculatedTotalTax = calculatedEntriesTaxAmount + shippingTaxAmount - calculatedDiscountTaxAmount;
        final double taxAmountDifference = cartModel.getTotalTax() - calculatedTotalTax;
        return (taxAmountDifference > 0) ? (calculatedDiscountTaxAmount - taxAmountDifference) : calculatedDiscountTaxAmount;
    }

    private double calculateVATAmount(final double amount, final double taxRate, final Integer digits) {
        final BigDecimal vatRate = BigDecimal.ONE.add(BigDecimal.valueOf(taxRate).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
        final double calculatedVAT = BigDecimal.valueOf(amount).divide(vatRate, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(amount)).negate().doubleValue();
        return commonI18NService.roundCurrency(calculatedVAT, digits);
    }

    private double calculateEntryTotalAmount(final AbstractOrderEntryModel entry) {
        final double entryTotalAmount = entry.getTotalPrice();
        return prorateRelativeGlobalDiscount(entry, entryTotalAmount);
    }

    private double calculateEntryTotalTaxAmount(final AbstractOrderEntryModel entry) {
        final double entryTaxAmount = entry.getTaxValues().stream().mapToDouble(TaxValue::getAppliedValue).sum();
        return prorateRelativeGlobalDiscount(entry, entryTaxAmount);
    }

    private double prorateRelativeGlobalDiscount(final AbstractOrderEntryModel entry, final double amount) {
        if (entry.getOrder().getGlobalDiscountValues().stream().anyMatch(gv -> !gv.isAbsolute())) {
            final double discountValue = entry.getOrder().getGlobalDiscountValues().stream().filter(dv -> !dv.isAbsolute()).mapToDouble(DiscountValue::getValue).sum();
            final Integer digits = entry.getOrder().getCurrency().getDigits();
            final double proratedAmount = BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(100 - discountValue).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)).setScale(digits, RoundingMode.HALF_UP).doubleValue();
            return commonI18NService.roundCurrency(proratedAmount, digits);
        } else {
            return amount;
        }
    }

    private String convertDoubleToStringFormat(final Integer digits, final double value) {
        return BigDecimal.valueOf(value).movePointRight(digits).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }
}
