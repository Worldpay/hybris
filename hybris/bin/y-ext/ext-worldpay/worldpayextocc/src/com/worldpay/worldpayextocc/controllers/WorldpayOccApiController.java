package com.worldpay.worldpayextocc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/{baseSiteId}/worldpayapi")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class WorldpayOccApiController {
    private static final Logger LOG = LogManager.getLogger(WorldpayOccApiController.class);
    private static final String DDC_URL_ATTR = "ddcUrl";
    private static final String DDC_JWT_ATTR = "jwt";
    private static final String CHALLENGE_HTML_SNIPPET = "<html><head><script>window.parent.postMessage({0},\"*\");</script></head><body onload>Loading...</body></html>";

    @Resource
    protected WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Resource
    protected WorldpayDDCFacade worldpayDDCFacade;
    @Resource(name = "occWorldpayDirectOrderFacade")
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;

    @GetMapping(value = "/cse-public-key", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCsePublicKey() {
        return ResponseEntity.ok().body(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCsePublicKey());
    }

    @GetMapping(value = "/ddc-3ds-jwt", produces = "application/json")
    public ResponseEntity<Map<String, String>> getThreeDsDDCInfo() {
        final String ddcUrl = Optional.ofNullable(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData())
            .map(WorldpayMerchantConfigData::getThreeDSFlexJsonWebTokenSettings)
            .map(ThreeDSFlexJsonWebTokenCredentials::getDdcUrl)
            .orElse(null);

        return ResponseEntity.ok(Map.of(
            DDC_URL_ATTR, ddcUrl,
            DDC_JWT_ATTR, worldpayDDCFacade.createJsonWebTokenForDDC()
        ));
    }

    @PostMapping(value = "/challenge/submit", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> handleChallengeSubmit(@RequestParam(value = "TransactionId", required = false) final String transactionId,
                                                        @RequestParam(value = "Response", required = false) final String response,
                                                        @RequestParam("MD") final String worldpayOrderCode,
                                                        final HttpServletResponse servletResponse) {
        boolean status = false;
        String orderCode = null;
        try {
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.executeSecondPaymentAuthorisation3DSecure(worldpayOrderCode);
            if (directResponseData != null && directResponseData.getOrderData() != null) {
                orderCode = directResponseData.getOrderData().getCode();
                status = true;
            }
        } catch (InvalidCartException | WorldpayException e) {
            LOG.error("Failed to process 3ds challenge", e.getCause());
        }

        return ResponseEntity.ok().body(createChallengeResponse(status, orderCode));
    }

    private String createChallengeResponse(final boolean orderCreated, final String orderCode) {

        final String message;
        try {
            message = new ObjectMapper()
                .writeValueAsString(Map.of("accepted", orderCreated,
                    "orderCode", StringUtils.defaultString(orderCode)));
        } catch (JsonProcessingException e) {
            LOG.error("failed creating challenge response", e);
            throw new IllegalStateException(e);
        }

        return MessageFormat.format(CHALLENGE_HTML_SNIPPET, message);
    }
}
