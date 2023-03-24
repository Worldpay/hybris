package com.worldpay.controllers;

import com.worldpay.data.WorldpayBinRangeData;
import com.worldpay.data.WorldpayCardDetailsData;
import com.worldpay.facades.WorldpayBinRangeFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCardDetailsControllerTest {

    private static final String CARD_NUMBER_PREFIX = "cardNumberPrefix";
    private static final String CARD_NAME = "cardName";
    private static final String IMAGES_CARDNAME_PNG = "/images/cardname.png";

    @InjectMocks
    private WorldpayCardDetailsController testObj;

    @Mock
    private WorldpayBinRangeFacade worldpayBinRangeFacade;
    @Mock
    private WorldpayBinRangeData worldpayBinRangeDataMock;

    @Test
    public void shouldReturnCardDetailsWithCardNotes() {

        when(worldpayBinRangeFacade.getWorldpayBinRange(CARD_NUMBER_PREFIX)).thenReturn(worldpayBinRangeDataMock);
        when(worldpayBinRangeDataMock.getCardName()).thenReturn(CARD_NAME);
        when(worldpayBinRangeDataMock.getCardNotes()).thenReturn("cardNotes");

        final WorldpayCardDetailsData result = testObj.getCardDetails(CARD_NUMBER_PREFIX);

        assertThat(result.getCardName()).isEqualTo(CARD_NAME);
        assertThat(result.getCardNotes()).isEqualTo(" - cardNotes");
        assertThat(result.getImageLink()).isEqualTo(IMAGES_CARDNAME_PNG);
    }

    @Test
    public void shouldReturnCardDetailsWithoutCardNotes() {

        when(worldpayBinRangeFacade.getWorldpayBinRange(CARD_NUMBER_PREFIX)).thenReturn(worldpayBinRangeDataMock);
        when(worldpayBinRangeDataMock.getCardName()).thenReturn(CARD_NAME);
        when(worldpayBinRangeDataMock.getCardNotes()).thenReturn(null);

        final WorldpayCardDetailsData result = testObj.getCardDetails(CARD_NUMBER_PREFIX);

        assertThat(result.getCardName()).isEqualTo(CARD_NAME);
        assertThat(result.getCardNotes()).isEqualTo("");
        assertThat(result.getImageLink()).isEqualTo(IMAGES_CARDNAME_PNG);
    }
}
