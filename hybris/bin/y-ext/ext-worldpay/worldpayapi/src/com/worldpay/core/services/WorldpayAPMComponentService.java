package com.worldpay.core.services;

import com.worldpay.model.WorldpayAPMComponentModel;

import java.util.List;

/**
 * Exposes a method for getting all available Worldpay apm components.
 */
public interface WorldpayAPMComponentService {

    /**
     * Get all available {@link WorldpayAPMComponentModel}.
     *
     * @return a list with all available WorldpayAPMComponentModel.
     */
    List<WorldpayAPMComponentModel> getAllAvailableWorldpayAPMComponents();
}
