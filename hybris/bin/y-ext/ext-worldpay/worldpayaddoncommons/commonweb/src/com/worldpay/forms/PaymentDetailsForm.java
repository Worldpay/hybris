package com.worldpay.forms;

import java.time.LocalDate;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import org.springframework.format.annotation.DateTimeFormat;

public class PaymentDetailsForm {

    private boolean termsCheck;
    private boolean saveInAccount;
    private boolean useDeliveryAddress;
    private String paymentMethod;
    private AddressForm billingAddress;
    private String shopperBankCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    private boolean dobRequired;
    private ACHForm achForm;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean getSaveInAccount() {
        return saveInAccount;
    }

    public void setSaveInAccount(final boolean saveInAccount) {
        this.saveInAccount = saveInAccount;
    }

    public boolean getUseDeliveryAddress() {
        return useDeliveryAddress;
    }

    public void setUseDeliveryAddress(final boolean useDeliveryAddress) {
        this.useDeliveryAddress = useDeliveryAddress;
    }

    public AddressForm getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final AddressForm billingAddress) {
        this.billingAddress = billingAddress;
    }

    public boolean isTermsCheck() {
        return termsCheck;
    }

    public void setTermsCheck(final boolean termsCheck) {
        this.termsCheck = termsCheck;
    }


    public String getShopperBankCode() {
        return shopperBankCode;
    }

    public void setShopperBankCode(String shopperBankCode) {
        this.shopperBankCode = shopperBankCode;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isDobRequired() {
        return dobRequired;
    }

    public void setDobRequired(final boolean dobRequired) {
        this.dobRequired = dobRequired;
    }

    public ACHForm getAchForm() {
        return achForm;
    }

    public void setAchForm(ACHForm achForm) {
        this.achForm = achForm;
    }
}
