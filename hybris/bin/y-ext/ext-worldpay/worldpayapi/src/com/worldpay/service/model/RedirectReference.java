package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of the reference returned as a result of a redirect authorise
 *
 */
public class RedirectReference implements Serializable {

    private String id;
    private String value;

    /**
     * Constructor with full list of fields
     *
     * @param id id of the reference sent by Worldpay
     * @param value this property can be a URL or a Base64-encoded HTML content.
     */
    public RedirectReference(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "RedirectReference [id=" + id + ", value=" + value + "]";
    }
}
