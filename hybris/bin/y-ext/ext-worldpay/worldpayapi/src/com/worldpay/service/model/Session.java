package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

/**
 * POJO representation of a session
 */
public class Session implements InternalModelTransformer, Serializable {

    private String shopperIPAddress;
    private String id;

    /**
     * Constructor with full list of fields
     *
     * @param shopperIPAddress
     * @param id
     */
    public Session(String shopperIPAddress, String id) {
        super();
        this.shopperIPAddress = shopperIPAddress;
        this.id = id;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        com.worldpay.internal.model.Session intSession = new com.worldpay.internal.model.Session();
        if (shopperIPAddress != null) {
            intSession.setShopperIPAddress(shopperIPAddress);
        }
        if (id != null) {
            intSession.setId(id);
        }
        return intSession;
    }

    public String getShopperIPAddress() {
        return shopperIPAddress;
    }

    public void setShopperIPAddress(String shopperIPAddress) {
        this.shopperIPAddress = shopperIPAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Session [shopperIPAddress=" + shopperIPAddress + ", id=" + id + "]";
    }
}
