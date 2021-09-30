package com.worldpay.interceptors.beforeview;

import com.worldpay.config.Environment;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.config.javascript.JavaScriptVariableDataFactory;
import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.worldpay.config.Environment.PROD;

public class WorldpayJsVariablesBeforeViewHandler implements BeforeViewHandlerAdaptee {

    private static final int SECONDS = 2;
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String PROFILING_DOMAIN = "profilingDomain";
    private static final String HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "hop.decline.message.wait.timer.seconds";
    private static final String WORLDPAY_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "worldpayDeclineMessageWaitTimerSeconds";
    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    private static final String WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD = "worldpay.config.profile.domain.prod";
    private static final String WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST = "worldpay.config.profile.domain.test";
    private static final String WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID = "worldpay.merchant.organization.id";
    private static final String IS_FS_ENABLED = "isFSEnabled";

    protected final SiteConfigService siteConfigService;
    protected final WorldpayFraudSightStrategy worldpayFraudSightStrategy;

    public WorldpayJsVariablesBeforeViewHandler(final SiteConfigService siteConfigService,
                                                final WorldpayFraudSightStrategy worldpayFraudSightStrategy) {
        this.siteConfigService = siteConfigService;
        this.worldpayFraudSightStrategy = worldpayFraudSightStrategy;
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

        final Boolean isFSEnabled = worldpayFraudSightStrategy.isFraudSightEnabled();
        final JavaScriptVariableData javaScriptFSEnabledVariableData = JavaScriptVariableDataFactory.create(IS_FS_ENABLED, isFSEnabled.toString());
        JavaScriptVariableDataFactory.getVariables(model).add(javaScriptFSEnabledVariableData);

        if (BooleanUtils.isTrue(isFSEnabled)) {
            final String environment = siteConfigService.getProperty(WORLDPAY_CONFIG_ENVIRONMENT);
            final JavaScriptVariableData javaScriptProfilingDomainVariableData;
            if (PROD == Environment.valueOf(environment)) {
                javaScriptProfilingDomainVariableData = JavaScriptVariableDataFactory.create(PROFILING_DOMAIN, siteConfigService.getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD));
            } else {
                javaScriptProfilingDomainVariableData = JavaScriptVariableDataFactory.create(PROFILING_DOMAIN, siteConfigService.getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST));
            }
            JavaScriptVariableDataFactory.getVariables(model).add(javaScriptProfilingDomainVariableData);

            final JavaScriptVariableData javaScriptOrgIdVariableData = JavaScriptVariableDataFactory.create(ORGANIZATION_ID,
                siteConfigService.getProperty(WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID));
            JavaScriptVariableDataFactory.getVariables(model).add(javaScriptOrgIdVariableData);
        }

        return viewName;
    }
}
