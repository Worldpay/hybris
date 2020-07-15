package com.worldpay.worldpayextocc.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentDetailsDTOValidatorTest {

    @InjectMocks
    private PaymentDetailsDTOValidator testObj = new PaymentDetailsDTOValidator();

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private Validator paymentAddressValidator;

    private PaymentDetailsWsDTO paymentDetailsWsDTO;
    private Errors errors;

    @Before
    public void setUp() {
        paymentDetailsWsDTO = new PaymentDetailsWsDTO();
        errors = new BeanPropertyBindingResult(paymentDetailsWsDTO, "paymentDetails");
    }

    @Test
    public void shouldNotFailOnValid() {
        // Given valid data
        updatePaymentDetails("Bo Nielsen", createCardTypeWsDTO(), "ksdjhfskdfhsdkhfsdkfhsdkf", "01", "2017", "01", "2019");

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("No errors expected", 0, errors.getErrorCount());
    }

    @Test
    public void shouldFailOnMissingExpiryYear() {
        // Given no expiryYear
        updatePaymentDetails("Bo Nielsen", createCardTypeWsDTO(), "ksdjhfskdfhsdkhfsdkfhsdkf", null, null, "01", null);

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("Only errors is expected", 1, errors.getErrorCount());
        String[] codes = errors.getAllErrors().get(0).getCodes();
        Assert.assertTrue("The first code is expected to contain expiryYear", codes[0].contains("expiryYear"));
    }

    @Test
    public void shouldFailOnMissingExpiryMonth() {
        // Given no expiryMonth
        updatePaymentDetails("Bo Nielsen", createCardTypeWsDTO(), "ksdjhfskdfhsdkhfsdkfhsdkf", null, null, null, "2016");

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("Only errors is expected", 1, errors.getErrorCount());
        String[] codes = errors.getAllErrors().get(0).getCodes();
        Assert.assertTrue("The first code is expected to contain expiryMonth", codes[0].contains("expiryMonth"));
    }

    @Test
    public void shouldFailOnMissingCseToken() {
        // Given missing card number
        updatePaymentDetails("Bo Nielsen", createCardTypeWsDTO(), null, null, null, "01", "2016");

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("Only errors is expected", 1, errors.getErrorCount());
        String[] codes = errors.getAllErrors().get(0).getCodes();
        Assert.assertTrue("The first code is expected to contain cseToken", codes[0].contains("cseToken"));
    }

    @Test
    public void shouldFailOnMissingCardTypeCode() {
        // Given no cardType.code
        updatePaymentDetails("Bo Nielsen", new CardTypeWsDTO(), "ksdjhfskdfhsdkhfsdkfhsdkf", null, null, "01", "2016");

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("Only errors is expected", 1, errors.getErrorCount());
        String[] codes = errors.getAllErrors().get(0).getCodes();
        Assert.assertTrue("The first code is expected to contain cardType.code", codes[0].contains("cardType.code"));
    }

    @Test
    public void shouldFailOnMissingAccountHolderName() {
        // Given no accountHolderName
        updatePaymentDetails(null, createCardTypeWsDTO(), "ksdjhfskdfhsdkhfsdkfhsdkf", null, null, "01", "2016");

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("Only errors is expected", 1, errors.getErrorCount());
        String[] codes = errors.getAllErrors().get(0).getCodes();
        Assert.assertTrue("The first code is expected to contain accountHolderName", codes[0].contains("accountHolderName"));
    }

    @Test
    public void shouldFailOnStartDateAfterExpiration() {
        // Given start date after expiration
        updatePaymentDetails("Bo Nielsen", createCardTypeWsDTO(), "ksdjhfskdfhsdkhfsdkfhsdkf", "04", "2016", "01", "2016");

        // When
        testObj.validate(paymentDetailsWsDTO, errors);

        // Then
        Assert.assertEquals("Only errors is expected", 1, errors.getErrorCount());
        String[] codes = errors.getAllErrors().get(0).getCodes();
        Assert.assertTrue("The first code is expected to contain startMonth", codes[0].contains("startMonth"));
    }

    protected void updatePaymentDetails(final String name, final CardTypeWsDTO cardType, final String cseToken, final String startMonth,
                                        final String startYear, final String expiryMonth, final String expiryYear) {
        paymentDetailsWsDTO.setAccountHolderName(name);
        paymentDetailsWsDTO.setCardType(cardType);
        paymentDetailsWsDTO.setCseToken(cseToken);
        paymentDetailsWsDTO.setExpiryMonth(expiryMonth);
        paymentDetailsWsDTO.setExpiryYear(expiryYear);
        paymentDetailsWsDTO.setStartMonth(startMonth);
        paymentDetailsWsDTO.setStartYear(startYear);
    }


    protected CardTypeWsDTO createCardTypeWsDTO() {
        CardTypeWsDTO cardTypeWsDTO = new CardTypeWsDTO();
        cardTypeWsDTO.setCode("visa");
        return cardTypeWsDTO;
    }

}
