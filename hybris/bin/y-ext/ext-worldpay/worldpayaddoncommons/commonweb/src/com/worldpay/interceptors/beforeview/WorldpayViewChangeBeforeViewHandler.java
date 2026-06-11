package com.worldpay.interceptors.beforeview;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;


public class WorldpayViewChangeBeforeViewHandler implements BeforeViewHandlerAdaptee {

    protected final Map<String, String> viewMap;

    public WorldpayViewChangeBeforeViewHandler(final Map<String, String> viewMap) {
        this.viewMap = viewMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final String viewName) {
        if (StringUtils.isNotBlank(viewName) && viewMap.containsKey(viewName)) {
            return viewMap.get(viewName);
        }
        return viewName;
    }
}
