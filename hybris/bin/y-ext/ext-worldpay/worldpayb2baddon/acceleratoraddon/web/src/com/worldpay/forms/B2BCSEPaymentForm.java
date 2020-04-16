package com.worldpay.forms;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.cronjob.enums.DayOfWeek;

import java.util.Date;
import java.util.List;

public class B2BCSEPaymentForm extends CSEPaymentForm {

    private boolean replenishmentOrder;
    private boolean requestSecurityCode;
    private Date replenishmentStartDate;
    private String nDays;
    private String nWeeks;
    private String nthDayOfMonth;
    private B2BReplenishmentRecurrenceEnum replenishmentRecurrence;
    private List<DayOfWeek> nDaysOfWeek;

    public boolean isReplenishmentOrder() {
        return replenishmentOrder;
    }

    public void setReplenishmentOrder(final boolean replenishmentOrder) {
        this.replenishmentOrder = replenishmentOrder;
    }

    public Date getReplenishmentStartDate() {
        return replenishmentStartDate;
    }

    public void setReplenishmentStartDate(final Date replenishmentStartDate) {
        this.replenishmentStartDate = replenishmentStartDate;
    }

    public String getnDays() {
        return nDays;
    }

    public void setnDays(final String nDays) {
        this.nDays = nDays;
    }

    public String getnWeeks() {
        return nWeeks;
    }

    public void setnWeeks(final String nWeeks) {
        this.nWeeks = nWeeks;
    }

    public String getNthDayOfMonth() {
        return nthDayOfMonth;
    }

    public void setNthDayOfMonth(final String nthDayOfMonth) {
        this.nthDayOfMonth = nthDayOfMonth;
    }

    public B2BReplenishmentRecurrenceEnum getReplenishmentRecurrence() {
        return replenishmentRecurrence;
    }

    public void setReplenishmentRecurrence(final B2BReplenishmentRecurrenceEnum replenishmentRecurrence) {
        this.replenishmentRecurrence = replenishmentRecurrence;
    }

    public List<DayOfWeek> getnDaysOfWeek() {
        return nDaysOfWeek;
    }

    public void setnDaysOfWeek(final List<DayOfWeek> nDaysOfWeek) {
        this.nDaysOfWeek = nDaysOfWeek;
    }

    public void setRequestSecurityCode(final boolean requestSecurityCode) {
        this.requestSecurityCode = requestSecurityCode;
    }

    public boolean isRequestSecurityCode() {
        return requestSecurityCode;
    }
}
