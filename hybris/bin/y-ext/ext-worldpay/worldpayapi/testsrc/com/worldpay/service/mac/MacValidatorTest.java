package com.worldpay.service.mac;

import com.worldpay.exception.WorldpayMacValidationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;


public class MacValidatorTest {

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test method for {@link com.worldpay.service.mac.MacValidator#validateResponse(String, String, String, String, String, String)}.
     */
    @Test
    public void testValidateResponse() throws WorldpayMacValidationException {
        boolean valid = MacValidator.getInstance().validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "25eefe952a6bbd09fe1c2c09bca4fa09", "1400", "GBP", "AUTHORISED", "@p-plepie");
        assertTrue("Mac validation code incorrect", valid);
    }

    /**
     * Test method for {@link com.worldpay.service.mac.MacValidator#validateResponse(String, String, String, String, String, String)}.
     */
    @Test
    public void testValidateResponseInvalid() throws WorldpayMacValidationException {
        boolean valid = MacValidator.getInstance().validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "25eefe952a6bbd09fe1c2c09bca4fa08", "1400", "GBP", "AUTHORISED", "@p-plepie");
        assertFalse("Mac validation code correct", valid);
    }

    /**
     * Test method for {@link com.worldpay.service.mac.MacValidator#validateResponse(String, String, String, String, String, String)}.
     */
    @Test
    public void testValidateResponseInvalidNulls() throws WorldpayMacValidationException {
        thrown.expect(WorldpayMacValidationException.class);

        MacValidator.getInstance().validateResponse(null, null, null, null, null, null);
        fail("Mac validation should fail with caught exception");
    }

    /**
     * Test method for {@link com.worldpay.service.mac.MacValidator#validateResponse(String, String, String, String, String, String)}.
     */
    @Test
    public void testValidateResponseInvalidPartialNulls() throws WorldpayMacValidationException {
        boolean valid = MacValidator.getInstance().validateResponse(null, "25eefe952a6bbd09fe1c2c09bca4fa08", null, null, null, null);
        assertFalse("Mac validation code correct", valid);
    }
}
