package com.worldpay.service.mac.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

@UnitTest
public class MD5MacValidatorTest {

    private final MacValidator testObj = new MD5MacValidator();

    @Test
    public void testValidateResponse() throws WorldpayMacValidationException {
        boolean result = testObj.validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "25eefe952a6bbd09fe1c2c09bca4fa09", "1400", "GBP", AuthorisedStatus.AUTHORISED, "@p-plepie");
        assertTrue("Mac validation code correct", result);
    }

    @Test
    public void testValidateResponseInvalid() throws WorldpayMacValidationException {
        boolean result = testObj.validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "25eefe952a6bbd09fe1c2c09bca4fa08", "1400", "GBP", AuthorisedStatus.AUTHORISED, "@p-plepie");
        assertFalse("Mac validation code incorrect", result);
    }

    @Test
    public void testValidateResponseInvalidNulls() {
        assertThatThrownBy(() -> testObj.validateResponse(null, null, null, null, null, null))
                .isInstanceOf(WorldpayMacValidationException.class)
                .hasMessage("No mac found in the response url provided by Worldpay");
    }

    @Test
    public void testValidateResponseInvalidPartialNulls() throws WorldpayMacValidationException {
        boolean result = testObj.validateResponse(null, "25eefe952a6bbd09fe1c2c09bca4fa08", null, null, null, null);
        assertFalse("Mac validation code correct", result);
    }
}
