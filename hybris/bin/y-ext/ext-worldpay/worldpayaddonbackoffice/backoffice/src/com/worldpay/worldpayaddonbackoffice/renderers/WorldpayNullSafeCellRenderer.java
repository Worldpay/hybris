package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import de.hybris.platform.omsbackoffice.renderers.InvalidNestedAttributeException;
import de.hybris.platform.omsbackoffice.renderers.NestedAttributeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.zkoss.zul.Listcell;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

/**
 * Class containing methods to safely (null safe) render a cell in a list
 */
public class WorldpayNullSafeCellRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object> {

    private static final Logger LOG = LogManager.getLogger(WorldpayNullSafeCellRenderer.class);
    protected final WidgetComponentRenderer<Listcell, ListColumn, Object> defaultListCellRenderer;
    protected final NestedAttributeUtils nestedAttributeUtils;

    public WorldpayNullSafeCellRenderer(final WidgetComponentRenderer<Listcell, ListColumn, Object> defaultListCellRenderer,
                                        final NestedAttributeUtils nestedAttributeUtils) {
        this.defaultListCellRenderer = defaultListCellRenderer;
        this.nestedAttributeUtils = nestedAttributeUtils;
    }

    /**
     * Null safe rendering of a cell in a list
     *
     * @param parent
     * @param columnConfiguration
     * @param object
     * @param dataType
     * @param widgetInstanceManager
     */
    @Override
    public void render(final Listcell parent, final ListColumn columnConfiguration, final Object object, final DataType dataType, final WidgetInstanceManager widgetInstanceManager) {
        final String qualifier = columnConfiguration.getQualifier();
        Object nestedObject = object;
        Object targetField = object;

        try {
            final List<String> tokenMap = this.getNestedAttributeUtils().splitQualifier(qualifier);

            int e;
            for (e = 0; e < tokenMap.size() - 1; ++e) {
                nestedObject = this.getNestedAttributeUtils().getNestedObject(nestedObject, tokenMap.get(e));
            }

            if (nestedObject != null) {
                for (e = 0; e < tokenMap.size(); ++e) {
                    targetField = this.getNestedAttributeUtils().getNestedObject(targetField, tokenMap.get(e));
                }
            }

            if (nestedObject != null && targetField != null && !this.checkIfObjectIsEmptyCollection(targetField)) {
                this.getDefaultListCellRenderer().render(parent, columnConfiguration, object, dataType, widgetInstanceManager);
            } else {
                LOG.warn("Either Property " + nestedObject + " is null or the field " + qualifier + " is null, skipping render of " + qualifier);
            }
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InvalidNestedAttributeException e) {
            LOG.info(e.getMessage(), e);

        }
    }

    protected WidgetComponentRenderer<Listcell, ListColumn, Object> getDefaultListCellRenderer() {
        return this.defaultListCellRenderer;
    }

    protected boolean checkIfObjectIsEmptyCollection(final Object object) {
        return object instanceof Collection && CollectionUtils.isEmpty((Collection) object);
    }

    protected NestedAttributeUtils getNestedAttributeUtils() {
        return this.nestedAttributeUtils;
    }

}

