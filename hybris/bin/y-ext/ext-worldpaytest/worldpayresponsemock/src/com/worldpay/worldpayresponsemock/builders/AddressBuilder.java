package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.*;

import java.util.List;

/**
 * Builder for the internal Address model generated from the Worldpay DTD
 */
public final class AddressBuilder {

    private static final String DEFAULT_ADDRESS_1 = "default address 1";
    private static final String DEFAULT_ADDRESS_2 = "default Address 2";
    private static final String DEFAULT_ADDRESS_3 = "default address 3";
    private static final String DEFAULT_POST_CODE = "postCode";
    private static final String DEFAULT_CITY = "city";
    private static final String DEFAULT_COUNTRY_CODE = "GB";
    private static final String DEFAULT_LAST_NAME = "lastName";

    private String address1 = DEFAULT_ADDRESS_1;
    private String address2 = DEFAULT_ADDRESS_2;
    private String address3 = DEFAULT_ADDRESS_3;
    private String postalCode = DEFAULT_POST_CODE;
    private String city = DEFAULT_CITY;
    private String countryCode = DEFAULT_COUNTRY_CODE;
    private String lastName = DEFAULT_LAST_NAME;

    private AddressBuilder() {
    }

    /**
     * Factory method to create a builder
     * @return an Address builder object
     */
    public static AddressBuilder anAddressBuilder() {
        return new AddressBuilder();
    }

    /**
     * Build with this given value
     * @param address1
     * @return this builder
     */
    public AddressBuilder withAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    /**
     * Build with this given value
     * @param address2
     * @return this builder
     */
    public AddressBuilder withAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    /**
     * Build with this given value
     * @param address3
     * @return this builder
     */
    public AddressBuilder withAddress3(String address3) {
        this.address3 = address3;
        return this;
    }

    /**
     * Build with this given value
     * @param lastName
     * @return this builder
     */
    public AddressBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * Build with this given value
     * @param postalCode
     * @return this builder
     */
    public AddressBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    /**
     * Build with this given value
     * @param city
     * @return this builder
     */
    public AddressBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    /**
     * Build with this given value
     * @param countryCode
     * @return this builder
     */
    public AddressBuilder withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    /**
     * Build the Address object based on the builders internal state
     * @return the internal Address model
     */
    public Address build() {
        final Address address = new Address();

        final List<Object> addressDetails = address.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();
        final Address1 addressDetailsAddress1 = new Address1();
        addressDetailsAddress1.setvalue(this.address1);
        addressDetails.add(addressDetailsAddress1);
        final Address2 addressDetailsAddress2 = new Address2();
        addressDetailsAddress2.setvalue(this.address2);
        addressDetails.add(addressDetailsAddress2);
        final Address3 addressDetailsAddress3 = new Address3();
        addressDetailsAddress3.setvalue(this.address3);
        addressDetails.add(addressDetailsAddress3);
        address.setLastName(this.lastName);
        address.setPostalCode(this.postalCode);
        address.setCity(this.city);

        final CountryCode internalCountryCode = new CountryCode();
        internalCountryCode.setvalue(this.countryCode);
        address.setCountryCode(internalCountryCode);
        return address;
    }

}
