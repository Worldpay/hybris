package com.worldpay.controllers;

import com.worldpay.data.WorldpayBinRangeData;
import com.worldpay.data.WorldpayCardDetailsData;
import com.worldpay.facades.WorldpayBinRangeFacade;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Controller to obtain card details based on bin range prefix
 */
@RestController
@RequestMapping(value = "/checkout/multi/worldpay/cse")
public class WorldpayCardDetailsController {

    @Resource
    private WorldpayBinRangeFacade worldpayBinRangeFacade;

    /**
     * Get card details based on bin range prefix
     *
     * @param prefix   a card prefix
     * @return card details
     */
    @GetMapping(value = "/cardDetails")
    public WorldpayCardDetailsData getCardDetails(@RequestParam final String prefix) {
        final WorldpayBinRangeData worldpayBinRange = worldpayBinRangeFacade.getWorldpayBinRange(prefix);
        if (worldpayBinRange == null) {
            return null;
        }
        final String imageLink = getImageLink(worldpayBinRange.getCardName());
        final WorldpayCardDetailsData worldpayCardDetailsData = new WorldpayCardDetailsData();
        worldpayCardDetailsData.setCardName(worldpayBinRange.getCardName());
        worldpayCardDetailsData.setImageLink(imageLink);
        worldpayCardDetailsData.setCardNotes(worldpayBinRange.getCardNotes() != null ? " - " + worldpayBinRange.getCardNotes() : "");
        return worldpayCardDetailsData;
    }

    /**
     * All but Cartebleue images are downloaded from https://www.shopify.com/blog/6335014-32-free-credit-card-icons
     * Cartebleue downloaded from https://www.iconfinder.com/icons/224422/bleue_carte_icon#size=60
     *
     * @param name
     * @return
     */
    private String getImageLink(final String name) {
        final String filename = name.toLowerCase().substring(0, name.indexOf(' ') != -1 ? name.indexOf(' ') : name.length());
        return "/images/" + filename + ".png";
    }
}
