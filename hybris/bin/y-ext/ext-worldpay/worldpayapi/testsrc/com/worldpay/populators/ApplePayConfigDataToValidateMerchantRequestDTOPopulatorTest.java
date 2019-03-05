package com.worldpay.populators;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApplePayConfigDataToValidateMerchantRequestDTOPopulatorTest {

    private static final String MERCHANT_ID = "MERCHANT_ID";
    private static final String MERCHANT_NAME = "MERCHANT_NAME";
    private static final String WEB = "web";
    private static final String WWW_APPLE_COM = "www.apple.com";
    private static final String HTTPS_WWW_APPLE_COM_VALID_PATH = "https://www.apple.com/validPath";
    private static final String HTTPS_INVALID_URL_007 = "https:/invalidUrl/007";

    private ApplePayConfigData applePayConfigData;
    private ValidateMerchantRequestDTO validateMerchantRequestDTO;

    @InjectMocks
    private ApplePayConfigDataToValidateMerchantRequestDTOPopulator testObj;

    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;

    @Before
    public void setup() {
        validateMerchantRequestDTO = new ValidateMerchantRequestDTO();
        applePayConfigData = new ApplePayConfigData();
        applePayConfigData.setMerchantId(MERCHANT_ID);
        applePayConfigData.setMerchantName(MERCHANT_NAME);
    }


    @Test
    public void populateWithValidUrlPopulatesEverything() {
        when(worldpayUrlServiceMock.getWebsiteUrlForCurrentSite()).thenReturn(HTTPS_WWW_APPLE_COM_VALID_PATH);
        testObj.populate(applePayConfigData, validateMerchantRequestDTO);
        assertThat(validateMerchantRequestDTO.getInitiative()).isEqualTo(WEB);
        assertThat(validateMerchantRequestDTO.getDisplayName()).isEqualTo(MERCHANT_NAME);
        assertThat(validateMerchantRequestDTO.getMerchantIdentifier()).isEqualTo(MERCHANT_ID);
        assertThat(validateMerchantRequestDTO.getInitiativeContext()).isEqualTo(WWW_APPLE_COM);
    }

    @Test
    public void populateInvalidUrlDoesNotPopulatesTheInitiativeContextButDoesPopulatesOtherValues() {
        when(worldpayUrlServiceMock.getWebsiteUrlForCurrentSite()).thenReturn(HTTPS_INVALID_URL_007);
        testObj.populate(applePayConfigData, validateMerchantRequestDTO);
        assertThat(validateMerchantRequestDTO.getInitiative()).isEqualTo(WEB);
        assertThat(validateMerchantRequestDTO.getDisplayName()).isEqualTo(MERCHANT_NAME);
        assertThat(validateMerchantRequestDTO.getMerchantIdentifier()).isEqualTo(MERCHANT_ID);
        assertThat(validateMerchantRequestDTO.getInitiativeContext()).isNullOrEmpty();
    }

}
