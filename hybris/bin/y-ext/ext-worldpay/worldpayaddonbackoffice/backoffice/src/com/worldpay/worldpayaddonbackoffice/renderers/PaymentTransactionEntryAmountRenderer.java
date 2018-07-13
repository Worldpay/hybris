package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

/**
 * Backoffice amount render for PaymentTransactionEntry
 */
public class PaymentTransactionEntryAmountRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentTransactionEntryAmountRenderer.class);
    private static final String PAYMENT_TRANSACTION_ENTRY = "PaymentTransactionEntry";

    private TypeFacade typeFacade;
    private PropertyValueService propertyValueService;
    private LabelService labelService;
    private PermissionFacade permissionFacade;

    /**
     * Render the amounts
     *
     * @param listCell
     * @param columnConfiguration
     * @param object
     * @param dataType
     * @param widgetInstanceManager
     */
    public void render(final Listcell listCell, final ListColumn columnConfiguration, final Object object, final DataType dataType, final WidgetInstanceManager widgetInstanceManager) {
        final String qualifier = columnConfiguration.getQualifier();

        try {
            final DataType paymentTransactionEntryDataType = typeFacade.load(PAYMENT_TRANSACTION_ENTRY);
            if (paymentTransactionEntryDataType != null && permissionFacade.canReadProperty(paymentTransactionEntryDataType.getCode(), qualifier)) {
                final Object amount = propertyValueService.readValue(object, qualifier);
                if (amount == null) {
                    listCell.setLabel(EMPTY);
                } else {
                    listCell.setLabel(getPaymentTransactionEntryAmountValue((PaymentTransactionEntryModel) object, amount));
                }
            }
        } catch (final TypeNotFoundException e) {
            LOG.error("Could not render row......", e);
        }
    }

    private String getPaymentTransactionEntryAmountValue(final PaymentTransactionEntryModel object, final Object amount) {
        final BigDecimal paymentTransactionAmount = ((BigDecimal) amount).setScale(object.getCurrency().getDigits(), BigDecimal.ROUND_HALF_DOWN);
        final String amountValue = labelService.getObjectLabel(paymentTransactionAmount);
        return defaultIfBlank(amountValue, amount.toString());
    }

    @Required
    public void setPropertyValueService(final PropertyValueService propertyValueService) {
        this.propertyValueService = propertyValueService;
    }

    @Required
    public void setLabelService(final LabelService labelService) {
        this.labelService = labelService;
    }

    @Required
    public void setPermissionFacade(final PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    @Required
    public void setTypeFacade(final TypeFacade typeFacade) {
        this.typeFacade = typeFacade;
    }
}
