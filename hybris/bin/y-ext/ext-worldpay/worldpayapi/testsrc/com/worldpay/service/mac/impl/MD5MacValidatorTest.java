package com.worldpay.service.mac.impl;

import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

@UnitTest
public class MD5MacValidatorTest {

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MacValidator testObj = new MD5MacValidator();

    @Test
    public void testValidateResponse() throws WorldpayMacValidationException {
        boolean result = testObj.validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "25eefe952a6bbd09fe1c2c09bca4fa09", "1400", "GBP", "AUTHORISED", "@p-plepie");
        assertTrue("Mac validation code correct", result);
    }

    @Test
    public void testValidateResponseInvalid() throws WorldpayMacValidationException {
        boolean result = testObj.validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "25eefe952a6bbd09fe1c2c09bca4fa08", "1400", "GBP", "AUTHORISED", "@p-plepie");
        assertFalse("Mac validation code incorrect", result);
    }

    @Test
    public void testValidateResponseInvalidNulls() throws WorldpayMacValidationException {
        thrown.expect(WorldpayMacValidationException.class);

        testObj.validateResponse(null, null, null, null, null, null);
        fail("Mac validation should fail with caught exception");
    }

    @Test
    public void testValidateResponseInvalidPartialNulls() throws WorldpayMacValidationException {
        boolean result = testObj.validateResponse(null, "25eefe952a6bbd09fe1c2c09bca4fa08", null, null, null, null);
        assertFalse("Mac validation code correct", result);
    }
}
