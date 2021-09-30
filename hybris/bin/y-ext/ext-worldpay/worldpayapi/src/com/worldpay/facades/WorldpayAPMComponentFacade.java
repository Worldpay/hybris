package com.worldpay.facades;

import com.worldpay.data.cms.WorldpayAPMComponentData;

import java.util.List;

/**
 * Exposes a method for getting all available Worldpay apm components.
 */
public interface WorldpayAPMComponentFacade {

    /**
     * Get all available {@link WorldpayAPMComponentData}.
     *
     * @return a list with all available WorldpayAPMComponentData.
     */
    List<WorldpayAPMComponentData> getAllAvailableWorldpayAPMComponents();
}
