package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;

import java.util.List;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#capture(CaptureServiceRequest) capture()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the amount and date that need to be sent</p>
 */
public class CaptureServiceRequest extends AbstractServiceRequest {

    private Amount amount;
    private Date date;
    private List<String> trackingIds;

    protected CaptureServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the CaptureServiceRequest
     *
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderCode orderCode to be used in the Worldpay call
     * @param amount    amount to be used in the Worldpay call
     * @param date      date to be used in the Worldpay call
     * @param trackingIds shipping tracking Ids used in the Worldpay call
     * @return new instance of the CaptureServiceRequest initialised with input parameters
     */
    public static CaptureServiceRequest createCaptureRequest(final MerchantInfo merch, final String orderCode, final Amount amount, final Date date, final List<String> trackingIds) {
        if (merch == null || orderCode == null || amount == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
        }
        final CaptureServiceRequest captureRequest = new CaptureServiceRequest(merch, orderCode);
        captureRequest.setAmount(amount);
        captureRequest.setDate(date);
        captureRequest.setTrackingIds(trackingIds);

        return captureRequest;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTrackingIds(List<String> trackingIds) {
        this.trackingIds = trackingIds;
    }

    public List<String> getTrackingIds() {
        return trackingIds;
    }
}
