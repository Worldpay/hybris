package com.worldpay.interceptors.beforeview;

import java.util.Optional;

import static com.worldpay.config.Environment.PROD;

import com.worldpay.config.Environment;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.config.javascript.JavaScriptVariableDataFactory;
import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.ui.ModelMap;

public class WorldpayJsVariablesBeforeViewHandler implements BeforeViewHandlerAdaptee {

    private static final int SECONDS = 2;
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String PROFILING_DOMAIN = "profilingDomain";
    private static final String FRAUD_SIGHT_PUBLISHABLE_KEY = "fraudSightPublishableKey";
    private static final String HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "hop.decline.message.wait.timer.seconds";
    private static final String WORLDPAY_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "worldpayDeclineMessageWaitTimerSeconds";
    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    private static final String WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD = "worldpay.config.profile.domain.prod";
    private static final String WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST = "worldpay.config.profile.domain.test";
    private static final String WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID = "worldpay.merchant.organization.id";
    private static final String WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY = "worldpay.config.fraudsight.publishableKey";
    private static final String IS_FS_ENABLED = "isFSEnabled";
    private static final String USER_ID = "userId";

    protected final SiteConfigService siteConfigService;
    protected final WorldpayFraudSightStrategy worldpayFraudSightStrategy;
    protected final CartService cartService;

    public WorldpayJsVariablesBeforeViewHandler(final SiteConfigService siteConfigService,
                                                final WorldpayFraudSightStrategy worldpayFraudSightStrategy,
                                                final CartService cartService) {
        this.siteConfigService = siteConfigService;
        this.worldpayFraudSightStrategy = worldpayFraudSightStrategy;
        this.cartService = cartService;
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

            final JavaScriptVariableData javaScriptFSPublishableKeyVariableData = JavaScriptVariableDataFactory.create(FRAUD_SIGHT_PUBLISHABLE_KEY,
                siteConfigService.getProperty(WORLDPAY_CONFIG_FRAUDSIGHT_PUBLISHABLE_KEY));
            JavaScriptVariableDataFactory.getVariables(model).add(javaScriptFSPublishableKeyVariableData);

            final Optional<CustomerModel> customerModel = Optional.ofNullable(cartService.getSessionCart()).stream()
                    .map(CartModel::getUser)
                    .filter(CustomerModel.class::isInstance)
                    .map(CustomerModel.class::cast)
                    .findFirst();

            if (customerModel.isPresent()) {
                final JavaScriptVariableData javaScriptUserIdKeyVariableData = JavaScriptVariableDataFactory.create(USER_ID,
                        customerModel.get().getCustomerID());
                JavaScriptVariableDataFactory.getVariables(model).add(javaScriptUserIdKeyVariableData);
            }

        }

        return viewName;
    }
}
