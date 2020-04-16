package com.worldpay.service.model.threeds2;

import com.worldpay.service.model.Date;

import java.io.Serializable;

public class RiskDateData implements Serializable {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RiskDateData{" +
                "date=" + date +
                '}';
    }
}
