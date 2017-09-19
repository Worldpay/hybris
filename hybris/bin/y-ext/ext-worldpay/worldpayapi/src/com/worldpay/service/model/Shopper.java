package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

/**
 * POJO representation of a shopper
 */
public class Shopper implements InternalModelTransformer, Serializable {

    private String shopperEmailAddress;
    private String authenticatedShopperID;
    private Browser browser;
    private Session session;

    public Shopper(final String shopperEmailAddress, final String authenticatedShopperID, final Browser browser, final Session session) {
        this.shopperEmailAddress = shopperEmailAddress;
        this.authenticatedShopperID = authenticatedShopperID;
        this.browser = browser;
        this.session = session;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.Shopper intShopper = new com.worldpay.internal.model.Shopper();
        if (shopperEmailAddress != null) {
            intShopper.setShopperEmailAddress(shopperEmailAddress);
        }
        if (authenticatedShopperID != null) {
            intShopper.setAuthenticatedShopperID(authenticatedShopperID);
        }
        if (browser != null) {
            intShopper.setBrowser((com.worldpay.internal.model.Browser) browser.transformToInternalModel());
        }
        if (session != null) {
            intShopper.setSession((com.worldpay.internal.model.Session) session.transformToInternalModel());
        }
        return intShopper;
    }

    public String getShopperEmailAddress() {
        return shopperEmailAddress;
    }

    public void setShopperEmailAddress(String shopperEmailAddress) {
        this.shopperEmailAddress = shopperEmailAddress;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public String getAuthenticatedShopperID() {
        return authenticatedShopperID;
    }

    public void setAuthenticatedShopperID(final String authenticatedShopperID) {
        this.authenticatedShopperID = authenticatedShopperID;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Shopper{" +
                "shopperEmailAddress='" + shopperEmailAddress + '\'' +
                ", authenticatedShopperID='" + authenticatedShopperID + '\'' +
                ", browser=" + browser +
                ", session=" + session +
                '}';
    }
}
