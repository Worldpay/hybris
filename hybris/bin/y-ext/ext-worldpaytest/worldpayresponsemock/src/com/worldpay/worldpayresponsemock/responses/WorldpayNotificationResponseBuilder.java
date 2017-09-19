package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.exception.WorldpayException;
import com.worldpay.worldpayresponsemock.form.ResponseForm;

/**
 * Building Worldpay notification response
 */
public interface WorldpayNotificationResponseBuilder {

    /**
     *
     * @param responseForm
     * @return
     * @throws WorldpayException
     */
    String buildResponse(ResponseForm responseForm) throws WorldpayException;

    /**
     *
     * @param responseXML
     * @return
     */
    String prettifyXml(String responseXML);
}
