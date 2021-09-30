package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Address;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddressPopulatorTest {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String TELEPHONE_NUMBER = "telephoneNumber";
    private static final String STREET = "street";
    private static final String HOUSE_NAME = "houseName";
    private static final String HOUSE_NUMBER = "houseNumber";
    private static final String HOUSE_NUMBER_EXTENSION = "houseNumberExtension";
    private static final String ADDRESS_1 = "address1";
    private static final String ADDRESS_2 = "address2";
    private static final String ADDRESS_3 = "address3";
    public static final int TOTAL_OF_FIELDS = 7;

    @InjectMocks
    private AddressPopulator testObj;

    @Mock
    private Address sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Address());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenSourceHasNullFields_ShouldNotPopulateStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3() {
        when(sourceMock.getStreet()).thenReturn(null);
        when(sourceMock.getHouseName()).thenReturn(null);
        when(sourceMock.getHouseNumber()).thenReturn(null);
        when(sourceMock.getHouseNumberExtension()).thenReturn(null);
        when(sourceMock.getAddress1()).thenReturn(null);
        when(sourceMock.getAddress2()).thenReturn(null);
        when(sourceMock.getAddress3()).thenReturn(null);

        final com.worldpay.internal.model.Address target = new com.worldpay.internal.model.Address();
        testObj.populate(sourceMock, target);

        assertThat(target.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3()).isEmpty();
    }

    @Test
    public void populate_WhenTelephoneNumberIsNull_ShouldNotPopulateTelephoneNumber() {
        when(sourceMock.getTelephoneNumber()).thenReturn(null);

        final com.worldpay.internal.model.Address target = new com.worldpay.internal.model.Address();
        testObj.populate(sourceMock, target);

        assertThat(target.getTelephoneNumber()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getFirstName()).thenReturn(FIRST_NAME);
        when(sourceMock.getLastName()).thenReturn(LAST_NAME);
        when(sourceMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(sourceMock.getCity()).thenReturn(CITY);
        when(sourceMock.getState()).thenReturn(STATE);
        when(sourceMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(sourceMock.getStreet()).thenReturn(STREET);
        when(sourceMock.getHouseName()).thenReturn(HOUSE_NAME);
        when(sourceMock.getHouseNumber()).thenReturn(HOUSE_NUMBER);
        when(sourceMock.getHouseNumberExtension()).thenReturn(HOUSE_NUMBER_EXTENSION);
        when(sourceMock.getAddress1()).thenReturn(ADDRESS_1);
        when(sourceMock.getAddress2()).thenReturn(ADDRESS_2);
        when(sourceMock.getAddress3()).thenReturn(ADDRESS_3);
        when(sourceMock.getTelephoneNumber()).thenReturn(TELEPHONE_NUMBER);

        final com.worldpay.internal.model.Address target = new com.worldpay.internal.model.Address();
        testObj.populate(sourceMock, target);

        assertThat(target.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(target.getLastName()).isEqualTo(LAST_NAME);
        assertThat(target.getPostalCode()).isEqualTo(POSTAL_CODE);
        assertThat(target.getCity()).isEqualTo(CITY);
        assertThat(target.getState()).isEqualTo(STATE);
        assertThat(target.getCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(target.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3().size()).isEqualTo(TOTAL_OF_FIELDS);
        assertThat(target.getTelephoneNumber()).isEqualTo(TELEPHONE_NUMBER);
    }
}
