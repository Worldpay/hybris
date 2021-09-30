package com.worldpay.security;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A redirect strategy used in
 * {@link de.hybris.platform.acceleratorstorefrontcommons.security.StorefrontAuthenticationSuccessHandler} to handle
 * express checkout case.
 */
public class WorldpayCommerceRedirectStrategy extends DefaultRedirectStrategy {

    private static final String EXPRESS_CHECKOUT_ENABLED = "expressCheckoutEnabled";

    private final CheckoutFlowFacade checkoutFlowFacade;
    private final String expressTargetUrl;

    public WorldpayCommerceRedirectStrategy(final CheckoutFlowFacade checkoutFlowFacade, final String expressTargetUrl) {
        this.checkoutFlowFacade = checkoutFlowFacade;
        this.expressTargetUrl = expressTargetUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url)
        throws IOException {
        super.sendRedirect(request, response, determineRedirectUrl(request, url));
    }

    /**
     * Determine the redirect url depending on if express checkout is enabled and request has expressCheckoutEnabled parameter.
     *
     * @param request     the request.
     * @param originalUrl the original url.
     * @return the calculated url.
     */
    protected String determineRedirectUrl(final HttpServletRequest request, final String originalUrl) {
        if (checkoutFlowFacade.isExpressCheckoutEnabledForStore()
            && StringUtils.isNotEmpty(request.getParameter(EXPRESS_CHECKOUT_ENABLED))) {
            return expressTargetUrl;
        } else {
            return originalUrl;
        }
    }
}
