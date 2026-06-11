package com.worldpay.converters.populators.internal.model.applepay;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.converters.populators.ApplePayConfigDataToValidateMerchantRequestDTOPopulator;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ApplePayConfigDataToValidateMerchantRequestDTOPopulatorTest {

    private static final String MERCHANT_ID = "MERCHANT_ID";
    private static final String MERCHANT_NAME = "MERCHANT_NAME";
    private static final String WEB = "web";
    private static final String WWW_APPLE_COM = "www.apple.com";
    private static final String HTTPS_WWW_APPLE_COM_VALID_PATH = "https://www.apple.com/validPath";
    private static final String MALFORMED_URL = "unknownscheme://host/path";
    private static final String HTTP_INVALID_URL = "ht p:/invalidUrl url";

    private ApplePayConfigData applePayConfigData;
    private ValidateMerchantRequestDTO validateMerchantRequestDTO;

    @InjectMocks
    private ApplePayConfigDataToValidateMerchantRequestDTOPopulator testObj;

    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;

    @BeforeEach
    void setup() {
        validateMerchantRequestDTO = new ValidateMerchantRequestDTO();
        applePayConfigData = new ApplePayConfigData();
        applePayConfigData.setMerchantId(MERCHANT_ID);
        applePayConfigData.setMerchantName(MERCHANT_NAME);
    }

    @Test
    void populate_ShouldPopulateEverything_WhenUrlIsValid() {
        when(worldpayUrlServiceMock.getWebsiteUrlForCurrentSite()).thenReturn(HTTPS_WWW_APPLE_COM_VALID_PATH);

        assertDoesNotThrow(() -> testObj.populate(applePayConfigData, validateMerchantRequestDTO));

        assertThat(validateMerchantRequestDTO.getInitiative()).isEqualTo(WEB);
        assertThat(validateMerchantRequestDTO.getDisplayName()).isEqualTo(MERCHANT_NAME);
        assertThat(validateMerchantRequestDTO.getMerchantIdentifier()).isEqualTo(MERCHANT_ID);
        assertThat(validateMerchantRequestDTO.getInitiativeContext()).isEqualTo(WWW_APPLE_COM);
    }

    @Test
    void populate_ShouldKeepBaseFieldsAndLeaveInitiativeContextEmpty_WhenUrlIsInvalid() {
        when(worldpayUrlServiceMock.getWebsiteUrlForCurrentSite()).thenReturn(HTTP_INVALID_URL);

        assertDoesNotThrow(() -> testObj.populate(applePayConfigData, validateMerchantRequestDTO));

        assertThat(validateMerchantRequestDTO.getInitiative()).isEqualTo(WEB);
        assertThat(validateMerchantRequestDTO.getDisplayName()).isEqualTo(MERCHANT_NAME);
        assertThat(validateMerchantRequestDTO.getMerchantIdentifier()).isEqualTo(MERCHANT_ID);
        assertThat(validateMerchantRequestDTO.getInitiativeContext()).isNullOrEmpty();
    }

    @Test
    void populate_ShouldKeepBaseFieldsAndLeaveInitiativeContextEmpty_WhenUrlIsMalformed() {
        when(worldpayUrlServiceMock.getWebsiteUrlForCurrentSite()).thenReturn(MALFORMED_URL);

        assertDoesNotThrow(() -> testObj.populate(applePayConfigData, validateMerchantRequestDTO));

        assertThat(validateMerchantRequestDTO.getInitiative()).isEqualTo(WEB);
        assertThat(validateMerchantRequestDTO.getDisplayName()).isEqualTo(MERCHANT_NAME);
        assertThat(validateMerchantRequestDTO.getMerchantIdentifier()).isEqualTo(MERCHANT_ID);
        assertThat(validateMerchantRequestDTO.getInitiativeContext()).isNullOrEmpty();
    }
}
