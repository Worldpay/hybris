package com.worldpay.service;

import com.worldpay.model.IPInformationModel;

/**
 * Service to get IP Information
 */
public interface IPLookupService {

    /**
     *  Method to call ip-api.com and return the IPInformationModel
     * @param ip
     * @return
     */
    IPInformationModel getIPInformation(String ip);
}
