package com.worldpay.service.model.klarna;

public class KlarnaMerchantUrls {
    private String checkoutURL;
    private String confirmationURL;

    public KlarnaMerchantUrls(final String checkoutURL, final String confirmationURL) {
        this.checkoutURL = checkoutURL;
        this.confirmationURL = confirmationURL;
    }

    public String getCheckoutURL() {
        return checkoutURL;
    }

    public void setCheckoutURL(final String checkoutURL) {
        this.checkoutURL = checkoutURL;
    }

    public String getConfirmationURL() {
        return confirmationURL;
    }

    public void setConfirmationURL(final String confirmationURL) {
        this.confirmationURL = confirmationURL;
    }
}
