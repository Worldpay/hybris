package com.worldpay.interceptors.beforeview;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
