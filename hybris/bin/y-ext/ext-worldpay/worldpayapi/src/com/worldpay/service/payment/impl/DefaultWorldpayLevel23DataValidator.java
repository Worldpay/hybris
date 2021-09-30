package com.worldpay.service.payment.impl;

import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import com.worldpay.service.payment.WorldpayLevel23DataValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link WorldpayLevel23DataValidator}
 */
public class DefaultWorldpayLevel23DataValidator implements WorldpayLevel23DataValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidLevel2Data(final List<Purchase> purchaseList) {
        final Purchase purchaseData = purchaseList.get(0);

        boolean isValid = isValidField(purchaseData.getCustomerReference(), 17)
            && isValidField(purchaseData.getCardAcceptorTaxId(), 20);

        if (isValid) {
            if (!isValidField(purchaseData.getDestinationPostalCode(), 10)) {
                purchaseData.setDestinationPostalCode(null);
            }
            final List<Item> items = purchaseData.getItem();
            if (CollectionUtils.isEmpty(items)) {
                return true;
            }
            return items.stream()
                .filter(Objects::nonNull)
                .map(this::validateItemForLevel2AndRemoveInvalidOptionalData)
                .reduce(Boolean::logicalAnd).orElse(false);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidLevel3Data(final List<Purchase> purchaseList) {
        final Purchase purchaseData = purchaseList.get(0);
        return isValidField(purchaseData.getCustomerReference(), 17)
            && isValidField(purchaseData.getCardAcceptorTaxId(), 20)
            && isValidField(purchaseData.getDestinationPostalCode(), 10)
            && Objects.nonNull(purchaseData.getItem())
            && isItemObjectValidForLevel3Data(purchaseData.getItem());
    }

    /**
     * Validates the mandatory field for level 2 data , and removes invalid optional fields
     *
     * @param item the item to validate
     * @return true if item is valid for level 2, false otherwise
     */
    protected boolean validateItemForLevel2AndRemoveInvalidOptionalData(final Item item) {
        if (isValidField(item.getDescription(), 26)) {
            if (!isValidField(item.getProductCode(), 12)) {
                item.setProductCode(null);
            }
            if (!isValidField(item.getCommodityCode(), 12)) {
                item.setCommodityCode(null);
            }
            if (!isValidField(item.getUnitOfMeasure(), 12)) {
                item.setUnitOfMeasure(null);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the list of Item objects are valid for level 3 data
     *
     * @param items list of items
     * @return true if all valid, false otherwise
     */
    protected boolean isItemObjectValidForLevel3Data(final List<Item> items) {
        return items.stream()
            .map(this::validateItemForLevel3Data)
            .allMatch(item -> item.equals(true));
    }

    /**
     * Checks if the mandatory fields for level 3 data in the item object are present and valid
     *
     * @param item the item to validate
     * @return true if item is valid, false otherwise
     */
    protected boolean validateItemForLevel3Data(final Item item) {
        return Objects.nonNull(item)
            && isValidField(item.getDescription(), 26)
            && isValidField(item.getProductCode(), 12)
            && isValidField(item.getCommodityCode(), 12)
            && isValidField(item.getUnitOfMeasure(), 12);
    }

    /**
     * returns true if the field is not blank and within the max size
     *
     * @param object  the object to check
     * @param maxSize the max size allowed
     * @return returns true if field is valid, false otherwise
     */
    protected boolean isValidField(final String object, final int maxSize) {
        return StringUtils.isNotBlank(object)
            && object.length() <= maxSize;
    }
}
