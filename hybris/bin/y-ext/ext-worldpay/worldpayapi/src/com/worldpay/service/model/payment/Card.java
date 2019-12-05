package com.worldpay.service.model.payment;

import com.worldpay.internal.model.*;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Date;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * POJO representation of {@link Payment} type for card payments
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class Card extends AbstractPayment {

    private String cardNumber;
    private String cvc;
    private Date expiryDate;
    private String cardHolderName;
    private Address cardAddress;
    private Date birthDate;
    private Date startDate;
    private String issueNumber;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param cardNumber
     * @param cvc
     * @param expiryDate
     * @param cardHolderName
     * @param cardAddress
     * @param birthDate
     * @param startDate
     * @param issueNumber
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public Card(final PaymentType paymentType, final String cardNumber, final String cvc, final Date expiryDate, final String cardHolderName, final Address cardAddress,
                final Date birthDate, final Date startDate, final String issueNumber) {
        this.paymentType = paymentType;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expiryDate = expiryDate;
        this.cardHolderName = cardHolderName;
        this.cardAddress = cardAddress;
        this.birthDate = birthDate;
        this.startDate = startDate;
        this.issueNumber = issueNumber;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        boolean methodInvoked = false;
        final String methodName = method.getName();
        if (methodName.startsWith("set")) {
            if ("setCardNumber".equals(methodName) && cardNumber != null) {
                final CardNumber intCardNumber = new CardNumber();
                intCardNumber.setvalue(cardNumber);
                method.invoke(targetObject, intCardNumber);
                methodInvoked = true;
            }
            if ("setCvc".equals(methodName) && cvc != null) {
                final Cvc intCvc = new Cvc();
                intCvc.setvalue(cvc);
                method.invoke(targetObject, intCvc);
                methodInvoked = true;
            }
            if ("setExpiryDate".equals(methodName) && expiryDate != null) {
                final ExpiryDate intExpiryDate = new ExpiryDate();
                intExpiryDate.setDate((com.worldpay.internal.model.Date) expiryDate.transformToInternalModel());
                method.invoke(targetObject, intExpiryDate);
                methodInvoked = true;
            }
            if ("setCardHolderName".equals(methodName) && cardHolderName != null) {
                final CardHolderName intCardHolderName = new CardHolderName();
                intCardHolderName.setvalue(cardHolderName);
                method.invoke(targetObject, intCardHolderName);
                methodInvoked = true;
            }
            if ("setCardAddress".equals(methodName) && cardAddress != null) {
                final CardAddress intCardAddress = new CardAddress();
                intCardAddress.setAddress((com.worldpay.internal.model.Address) cardAddress.transformToInternalModel());
                method.invoke(targetObject, intCardAddress);
                methodInvoked = true;
            }
            if ("setBirthDate".equals(methodName) && birthDate != null) {
                final BirthDate intBirthDate = new BirthDate();
                intBirthDate.setDate((com.worldpay.internal.model.Date) birthDate.transformToInternalModel());
                method.invoke(targetObject, intBirthDate);
                methodInvoked = true;
            }
            if ("setStartDate".equals(methodName) && startDate != null) {
                final StartDate intStartDate = new StartDate();
                intStartDate.setDate((com.worldpay.internal.model.Date) startDate.transformToInternalModel());
                method.invoke(targetObject, intStartDate);
                methodInvoked = true;
            }
            if ("setIssueNumber".equals(methodName) && issueNumber != null) {
                final IssueNumber intIssueNumber = new IssueNumber();
                intIssueNumber.setvalue(issueNumber);
                method.invoke(targetObject, intIssueNumber);
                methodInvoked = true;
            }
        } else if ("getIssueNumberOrStartDate".equals(methodName) && (issueNumber != null || startDate != null)) {
            final List<Object> issueNumberOrStartDate = (List<Object>) method.invoke(targetObject);
            if (issueNumber != null) {
                final IssueNumber intIssueNumber = new IssueNumber();
                intIssueNumber.setvalue(issueNumber);
                issueNumberOrStartDate.add(intIssueNumber);
            }
            if (startDate != null) {
                final StartDate intStartDate = new StartDate();
                intStartDate.setDate((com.worldpay.internal.model.Date) startDate.transformToInternalModel());
                issueNumberOrStartDate.add(intStartDate);
            }
            methodInvoked = true;
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
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        // Do nothing. This provides a hook for subclasses to add extra functionality
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(final String cvc) {
        this.cvc = cvc;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(final Address cardAddress) {
        this.cardAddress = cardAddress;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(final String issueNumber) {
        this.issueNumber = issueNumber;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cvc='" + cvc + '\'' +
                ", expiryDate=" + expiryDate +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardAddress=" + cardAddress +
                ", birthDate=" + birthDate +
                ", startDate=" + startDate +
                ", issueNumber='" + issueNumber + '\'' +
                '}';
    }
}
