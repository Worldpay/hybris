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
    public Card(PaymentType paymentType, String cardNumber, String cvc, Date expiryDate, String cardHolderName, Address cardAddress,
                Date birthDate, Date startDate, String issueNumber) {
        this.setPaymentType(paymentType);
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
    public void invokeSetter(Method method, Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        boolean methodInvoked = false;
        String methodName = method.getName();
        if (methodName.startsWith("set")) {
            if ("setCardNumber".equals(methodName) && cardNumber != null) {
                CardNumber intCardNumber = new CardNumber();
                intCardNumber.setvalue(cardNumber);
                method.invoke(targetObject, intCardNumber);
                methodInvoked = true;
            }
            if ("setCvc".equals(methodName) && cvc != null) {
                Cvc intCvc = new Cvc();
                intCvc.setvalue(cvc);
                method.invoke(targetObject, intCvc);
                methodInvoked = true;
            }
            if ("setExpiryDate".equals(methodName) && expiryDate != null) {
                ExpiryDate intExpiryDate = new ExpiryDate();
                intExpiryDate.setDate((com.worldpay.internal.model.Date) expiryDate.transformToInternalModel());
                method.invoke(targetObject, intExpiryDate);
                methodInvoked = true;
            }
            if ("setCardHolderName".equals(methodName) && cardHolderName != null) {
                CardHolderName intCardHolderName = new CardHolderName();
                intCardHolderName.setvalue(cardHolderName);
                method.invoke(targetObject, intCardHolderName);
                methodInvoked = true;
            }
            if ("setCardAddress".equals(methodName) && cardAddress != null) {
                CardAddress intCardAddress = new CardAddress();
                intCardAddress.setAddress((com.worldpay.internal.model.Address) cardAddress.transformToInternalModel());
                method.invoke(targetObject, intCardAddress);
                methodInvoked = true;
            }
            if ("setBirthDate".equals(methodName) && birthDate != null) {
                BirthDate intBirthDate = new BirthDate();
                intBirthDate.setDate((com.worldpay.internal.model.Date) birthDate.transformToInternalModel());
                method.invoke(targetObject, intBirthDate);
                methodInvoked = true;
            }
            if ("setStartDate".equals(methodName) && startDate != null) {
                StartDate intStartDate = new StartDate();
                intStartDate.setDate((com.worldpay.internal.model.Date) startDate.transformToInternalModel());
                method.invoke(targetObject, intStartDate);
                methodInvoked = true;
            }
            if ("setIssueNumber".equals(methodName) && issueNumber != null) {
                IssueNumber intIssueNumber = new IssueNumber();
                intIssueNumber.setvalue(issueNumber);
                method.invoke(targetObject, intIssueNumber);
                methodInvoked = true;
            }
        } else if ("getIssueNumberOrStartDate".equals(methodName) && (issueNumber != null || startDate != null)) {
            List<Object> issueNumberOrStartDate = (List<Object>) method.invoke(targetObject);
            if (issueNumber != null) {
                IssueNumber intIssueNumber = new IssueNumber();
                intIssueNumber.setvalue(issueNumber);
                issueNumberOrStartDate.add(intIssueNumber);
            }
            if (startDate != null) {
                StartDate intStartDate = new StartDate();
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
    protected void invokeExtraSetters(Method method, Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // Do nothing. This provides a hook for subclasses to add extra functionality
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(Address cardAddress) {
        this.cardAddress = cardAddress;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
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
