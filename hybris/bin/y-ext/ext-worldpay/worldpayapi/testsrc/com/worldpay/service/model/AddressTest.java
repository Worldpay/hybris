package com.worldpay.service.model;

import com.worldpay.internal.model.*;
import de.hybris.bootstrap.annotations.UnitTest;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;

@UnitTest
public class AddressTest {

    private static final String STATE = "State";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String STREET = "Street";
    private static final String HOUSE_NAME = "HouseName";
    private static final String HOUSE_NUMBER_EXTENSION = "HouseNumberExtension";
    private static final String ADDRESS_1 = "Address1";
    private static final String ADDRESS_2 = "Address2";
    private static final String ADDRESS_3 = "Address3";
    private static final String POSTAL_CODE = "PostalCode";
    private static final String CITY = "City";
    private static final String COUNTRY_CODE = "CountryCode";
    private static final String TELEPHONE_NUMBER = "TelephoneNumber";
    private static final String VALUE = "value";
    private static final String HOUSE_NUMBER = "houseNumber";
    private Address testObj = new Address();

    @Test
    public void testTransformToInternalModel() throws Exception {
        testObj.setState(STATE);
        testObj.setFirstName(FIRST_NAME);
        testObj.setLastName(LAST_NAME);
        testObj.setStreet(STREET);
        testObj.setHouseName(HOUSE_NAME);
        testObj.setHouseNumberExtension(HOUSE_NUMBER_EXTENSION);
        testObj.setAddress1(ADDRESS_1);
        testObj.setAddress2(ADDRESS_2);
        testObj.setAddress3(ADDRESS_3);
        testObj.setPostalCode(POSTAL_CODE);
        testObj.setCity(CITY);
        testObj.setCountryCode(COUNTRY_CODE);
        testObj.setTelephoneNumber(TELEPHONE_NUMBER);
        testObj.setHouseNumber(HOUSE_NUMBER);

        final com.worldpay.internal.model.Address result = (com.worldpay.internal.model.Address) testObj.transformToInternalModel();

        assertEquals(STATE, result.getState());
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        final List<Object> addressDetails = result.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();

        assertThat(addressDetails, hasItems(instanceOf(Street.class), instanceOf(HouseName.class), CoreMatchers.instanceOf(HouseNumber.class), instanceOf(HouseNumberExtension.class), instanceOf(Address1.class), instanceOf(Address2.class), instanceOf(Address3.class)));
        assertThat(addressDetails, hasItems(hasProperty(VALUE, is(STREET)), hasProperty(VALUE, is(HOUSE_NAME)), hasProperty(VALUE,is(HOUSE_NUMBER)), hasProperty(VALUE, is(HOUSE_NUMBER_EXTENSION)), hasProperty(VALUE, is(ADDRESS_1)), hasProperty(VALUE, is(ADDRESS_2)), hasProperty(VALUE, is(ADDRESS_3))));

        assertEquals(POSTAL_CODE, result.getPostalCode());
        assertEquals(CITY, result.getCity());
        assertEquals(COUNTRY_CODE, result.getCountryCode());
        assertEquals(TELEPHONE_NUMBER, result.getTelephoneNumber());
    }
}
