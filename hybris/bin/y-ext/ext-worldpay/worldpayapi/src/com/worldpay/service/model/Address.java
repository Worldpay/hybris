package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.*;
import com.worldpay.service.request.transform.InternalModelTransformer;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * POJO representation of an address
 */
public class Address implements InternalModelTransformer, Serializable {

    private String firstName;
    private String lastName;
    private String street;
    private String houseName;
    private String houseNumber;
    private String houseNumberExtension;
    private String address1;
    private String address2;
    private String address3;
    private String postalCode;
    private String city;
    private String state;
    private String countryCode;
    private String telephoneNumber;

    /**
     * Empty constructor to allow Address to be created
     */
    public Address() {
    }

    /**
     * Constructor with full list of fields
     *
     * @param firstName
     * @param lastName
     * @param address1
     * @param address2
     * @param address3
     * @param postalCode
     * @param city
     * @param countryCode
     */
    public Address(String firstName, String lastName, String address1, String address2, String address3, String postalCode, String city, String countryCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.postalCode = postalCode;
        this.city = city;
        this.countryCode = countryCode;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        com.worldpay.internal.model.Address intAddress = new com.worldpay.internal.model.Address();
        if (firstName != null) {
            intAddress.setFirstName(firstName);
        }
        if (lastName != null) {
            intAddress.setLastName(lastName);
        }
        List<Object> addressDetails = intAddress.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();
        if (street != null) {
            Street intStreet = new Street();
            intStreet.setvalue(street);
            addressDetails.add(intStreet);
        }
        if (houseName != null) {
            HouseName intHouseName = new HouseName();
            intHouseName.setvalue(houseName);
            addressDetails.add(intHouseName);
        }
        if (houseNumber != null) {
            HouseNumber intHouseNumber = new HouseNumber();
            intHouseNumber.setvalue(houseNumber);
            addressDetails.add(intHouseNumber);
        }
        if (houseNumberExtension != null) {
            HouseNumberExtension intHouseNumberExt = new HouseNumberExtension();
            intHouseNumberExt.setvalue(houseNumberExtension);
            addressDetails.add(intHouseNumberExt);
        }
        if (address1 != null) {
            Address1 intAddress1 = new Address1();
            intAddress1.setvalue(address1);
            addressDetails.add(intAddress1);
        }
        if (address2 != null) {
            Address2 intAddress2 = new Address2();
            intAddress2.setvalue(address2);
            addressDetails.add(intAddress2);
        }
        if (address3 != null) {
            Address3 intAddress3 = new Address3();
            intAddress3.setvalue(address3);
            addressDetails.add(intAddress3);
        }
        if (postalCode != null) {
            intAddress.setPostalCode(postalCode);
        }
        if (city != null) {
            intAddress.setCity(city);
        }
        if (state != null) {
            intAddress.setState(state);
        }
        if (countryCode != null) {
            intAddress.setCountryCode(countryCode);
        }
        if (StringUtils.isNotBlank(telephoneNumber)) {
            intAddress.setTelephoneNumber(telephoneNumber);
        }

        return intAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getHouseNumberExtension() {
        return houseNumberExtension;
    }

    public void setHouseNumberExtension(String houseNumberExtension) {
        this.houseNumberExtension = houseNumberExtension;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Override
    public String toString() {
        return "Address{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", houseName='" + houseName + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", houseNumberExtension='" + houseNumberExtension + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                '}';
    }
}
