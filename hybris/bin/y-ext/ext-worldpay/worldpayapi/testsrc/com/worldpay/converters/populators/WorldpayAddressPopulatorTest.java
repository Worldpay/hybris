package com.worldpay.converters.populators;

import com.worldpay.service.model.Address;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAddressPopulatorTest {

    @InjectMocks
    private WorldpayAddressPopulator testObject;

    private static final String LINE_1 = "line1";
    private static final String LINE_2 = "line2";
    private static final String TOWN = "town";
    private static final String COUNTRY_ISO_CODE = "countryIsoCode";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String POSTALCODE = "postalcode";
    private static final String TELEPHONE = "telephone";
    private static final String STATE = "state";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AddressModel sourceMock;

    @Before
    public void setUp() {
        when(sourceMock.getLine1()).thenReturn(LINE_1);
        when(sourceMock.getLine2()).thenReturn(LINE_2);
        when(sourceMock.getTown()).thenReturn(TOWN);
        when(sourceMock.getCountry().getIsocode()).thenReturn(COUNTRY_ISO_CODE);
        when(sourceMock.getFirstname()).thenReturn(FIRSTNAME);
        when(sourceMock.getLastname()).thenReturn(LASTNAME);
        when(sourceMock.getPostalcode()).thenReturn(POSTALCODE);
        when(sourceMock.getPhone1()).thenReturn(TELEPHONE);
        when(sourceMock.getRegion().getIsocode()).thenReturn(STATE);
    }

    @Test
    public void testPopulateHappyFlow() {
        final Address target = new Address();

        testObject.populate(sourceMock, target);

        assertEquals(LINE_1, target.getAddress1());
        assertEquals(LINE_2, target.getAddress2());
        assertEquals(TOWN, target.getCity());
        assertEquals(COUNTRY_ISO_CODE, target.getCountryCode());
        assertEquals(FIRSTNAME, target.getFirstName());
        assertEquals(LASTNAME, target.getLastName());
        assertEquals(POSTALCODE, target.getPostalCode());
        assertEquals(TELEPHONE, target.getTelephoneNumber());
        assertEquals(STATE, target.getState());
    }

    @Test
    public void testThatStateIsNotSetWhenRegionIsNull() {
        when(sourceMock.getRegion()).thenReturn(null);
        final Address target = new Address();

        testObject.populate(sourceMock, target);

        assertEquals(LINE_1, target.getAddress1());
        assertEquals(LINE_2, target.getAddress2());
        assertEquals(TOWN, target.getCity());
        assertEquals(COUNTRY_ISO_CODE, target.getCountryCode());
        assertEquals(FIRSTNAME, target.getFirstName());
        assertEquals(LASTNAME, target.getLastName());
        assertEquals(POSTALCODE, target.getPostalCode());
        assertEquals(TELEPHONE, target.getTelephoneNumber());
        assertNull(target.getState());
    }

    @Test
    public void testThatCountryCodeIsNotSetWhenCountryIsNull() {
        when(sourceMock.getCountry()).thenReturn(null);
        final Address target = new Address();

        testObject.populate(sourceMock, target);

        assertEquals(LINE_1, target.getAddress1());
        assertEquals(LINE_2, target.getAddress2());
        assertEquals(TOWN, target.getCity());
        assertNull(target.getCountryCode());
        assertEquals(FIRSTNAME, target.getFirstName());
        assertEquals(LASTNAME, target.getLastName());
        assertEquals(POSTALCODE, target.getPostalCode());
        assertEquals(TELEPHONE, target.getTelephoneNumber());
        assertEquals(STATE, target.getState());
    }

    @Test
    public void testEscapeCharacters() {

        when(sourceMock.getLine1()).thenReturn("áéíóú");
        when(sourceMock.getLine2()).thenReturn("üöë");
        when(sourceMock.getTown()).thenReturn("ñÑçÇ");

        final Address target = new Address();

        testObject.populate(sourceMock, target);

        assertEquals(StringEscapeUtils.escapeXml("áéíóú"), target.getAddress1());
        assertEquals(StringEscapeUtils.escapeXml("üöë"), target.getAddress2());
        assertEquals(StringEscapeUtils.escapeXml("ñÑçÇ"), target.getCity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populateThrowsExceptionIfSourceIsNull() {
        final Address target = new Address();
        // Did this for Gold certification. sonar does not like null params
        final AddressModel source = null;
        testObject.populate(source, target);
    }
}
