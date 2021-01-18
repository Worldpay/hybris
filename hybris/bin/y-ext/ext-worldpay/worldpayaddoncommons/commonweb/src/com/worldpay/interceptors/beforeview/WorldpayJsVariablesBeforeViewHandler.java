package com.worldpay.interceptors.beforeview;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.config.javascript.JavaScriptVariableDataFactory;
import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WorldpayJsVariablesBeforeViewHandler implements BeforeViewHandlerAdaptee {

    private static final int SECONDS = 2;
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "hop.decline.message.wait.timer.seconds";
    private static final String WORLDPAY_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "worldpayDeclineMessageWaitTimerSeconds";

    protected final SiteConfigService siteConfigService;

    public WorldpayJsVariablesBeforeViewHandler(final SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final String viewName) {
        final JavaScriptVariableData worldpayDeclineMessageWaitTimerSeconds = JavaScriptVariableDataFactory.create(WORLDPAY_DECLINE_MESSAGE_WAIT_TIMER_SECONDS,
            String.valueOf(siteConfigService.getInt(HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS, SECONDS)));
        JavaScriptVariableDataFactory.getVariables(model).add(worldpayDeclineMessageWaitTimerSeconds);
        final JavaScriptVariableData javaScriptVariableData = JavaScriptVariableDataFactory.create(PAYMENT_STATUS, (String) model.get(PAYMENT_STATUS));
        JavaScriptVariableDataFactory.getVariables(model).add(javaScriptVariableData);
        return viewName;
    }
}
