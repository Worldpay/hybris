package com.worldpay.service.model.payment;

import com.worldpay.internal.model.BirthDate;
import com.worldpay.internal.model.CreditScoring;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Date;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link Payment} type for bank account payments
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class BankAccount extends AbstractPayment {

    private String accountHolderName;
    private String accountNumber;
    private String bankName;
    private String bankLocation;
    private String bankLocationId;
    private Date birthDate;
    private Address address;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param accountHolderName
     * @param accountNumber
     * @param bankName
     * @param bankLocation
     * @param bankLocationId
     * @param birthDate
     * @param address
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public BankAccount(final PaymentType paymentType, final String accountHolderName, final String accountNumber, final String bankName, final String bankLocation, final String bankLocationId, final Date birthDate, final Address address) {
        this.setPaymentType(paymentType);
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.bankLocation = bankLocation;
        this.bankLocationId = bankLocationId;
        this.birthDate = birthDate;
        this.address = address;
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        boolean methodInvoked = false;
        final String methodName = method.getName();
        if (methodName.startsWith("set")) {
            if ("setAccountHolderName".equals(methodName) && accountHolderName != null) {
                method.invoke(targetObject, accountHolderName);
                methodInvoked = true;
            }
            if ("setBankAccountNr".equals(methodName) && accountNumber != null) {
                method.invoke(targetObject, accountNumber);
                methodInvoked = true;
            }
            if ("setBankName".equals(methodName) && bankName != null) {
                method.invoke(targetObject, bankName);
                methodInvoked = true;
            }
            if ("setBankLocation".equals(methodName) && bankLocation != null) {
                method.invoke(targetObject, bankLocation);
                methodInvoked = true;
            }
            if ("setBankLocationId".equals(methodName) && bankLocationId != null) {
                method.invoke(targetObject, bankLocationId);
                methodInvoked = true;
            }
            if ("setCreditScoring".equals(methodName) && birthDate != null && address != null) {
                final CreditScoring intCreditScoring = new CreditScoring();
                intCreditScoring.setAddress((com.worldpay.internal.model.Address) address.transformToInternalModel());
                final BirthDate intBirthDate = new BirthDate();
                intBirthDate.setDate((com.worldpay.internal.model.Date) address.transformToInternalModel());
                intCreditScoring.setBirthDate(intBirthDate);
                method.invoke(targetObject, intCreditScoring);
                methodInvoked = true;
            }
        }

        if (!methodInvoked) {
            invokeExtraSetters(method, targetObject);
        }
    }

    /**
     * Method to be used by overriding classes in order to ensure that extra fields that they implement get set when the {@link #transformToInternalModel()} method is
     * invoked. Default implementation does nothing so just provides the hook for overriding classes
     *
     * @param method       Method that can be invoked on the internal model object targetObject
     * @param targetObject internal model object that we are trying to transform to
     * @throws IllegalArgumentException  if the method is invoked with incorrect parameters
     * @throws IllegalAccessException    if the method is not accessible
     * @throws InvocationTargetException if method cannot be invoked against the supplied target object
     */
    protected void invokeExtraSetters(Method method, Object targetObject) throws IllegalAccessException, InvocationTargetException {
        // Do nothing. This provides a hook for subclasses to add extra functionality
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(final String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }

    public String getBankLocation() {
        return bankLocation;
    }

    public void setBankLocation(final String bankLocation) {
        this.bankLocation = bankLocation;
    }

    public String getBankLocationId() {
        return bankLocationId;
    }

    public void setBankLocationId(final String bankLocationId) {
        this.bankLocationId = bankLocationId;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "BankAccount [accountHolderName=" + accountHolderName + ", accountNumber=" + accountNumber + ", bankName=" + bankName + ", bankLocation="
                + bankLocation + ", bankLocationId=" + bankLocationId + ", birthDate=" + birthDate + ", address=" + address + "]";
    }
}
