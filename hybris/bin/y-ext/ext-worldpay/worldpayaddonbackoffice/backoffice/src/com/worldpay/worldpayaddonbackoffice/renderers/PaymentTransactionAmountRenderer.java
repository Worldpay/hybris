package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.zkoss.zul.Listcell;

/**
 * Backoffice amount render for PaymentTransaction
 */
public class PaymentTransactionAmountRenderer extends AbstractAmountRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object> {


    public PaymentTransactionAmountRenderer(final TypeFacade typeFacade,
                                            final PropertyValueService propertyValueService,
                                            final LabelService labelService,
                                            final PermissionFacade permissionFacade) {
        super(typeFacade, propertyValueService, labelService, permissionFacade);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(final Listcell listCell, final ListColumn columnConfiguration, final Object object, final DataType dataType, final WidgetInstanceManager widgetInstanceManager) {
        renderAmount(listCell, columnConfiguration, object, PaymentTransactionModel._TYPECODE);
    }
}
