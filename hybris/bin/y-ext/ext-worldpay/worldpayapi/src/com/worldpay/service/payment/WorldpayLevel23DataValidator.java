package com.worldpay.service.payment;

import com.worldpay.data.Purchase;

import java.util.List;

public interface WorldpayLevel23DataValidator {

    /**
     * Checks if the Purchase is valid for level 3 data
     *
     * @param purchaseData list of Purchase objects
     * @return true if all data is valid, false otherwise
     */
    boolean isValidLevel3Data(final List<Purchase> purchaseData);

    /**
     * Checks if the Purchase is valid for level 2 data
     * Cleans up the invalid optional fields to make a valid level 2 request
     *
     * @param purchaseData list of Purchase objects
     * @return true if all data is valid, false otherwise
     */
    boolean isValidLevel2Data(final List<Purchase> purchaseData);
}
