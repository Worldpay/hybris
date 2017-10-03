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
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;

/**
 * Backoffice amount render for PaymentTransaction
 */
public class PaymentTransactionAmountRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentTransactionAmountRenderer.class);
    private static final String PAYMENT_TRANSACTION = "PaymentTransaction";

    private TypeFacade typeFacade;
    private PropertyValueService propertyValueService;
    private LabelService labelService;
    private PermissionFacade permissionFacade;

    /**
     * Render the amounts
     * @param listCell
     * @param columnConfiguration
     * @param object
     * @param dataType
     * @param widgetInstanceManager
     */
    public void render(Listcell listCell, ListColumn columnConfiguration, Object object, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        final String qualifier = columnConfiguration.getQualifier();

        try {
            final DataType paymentTransactionDataType = typeFacade.load(PAYMENT_TRANSACTION);
            if (paymentTransactionDataType != null && permissionFacade.canReadProperty(paymentTransactionDataType.getCode(), qualifier)) {
                final Object e = propertyValueService.readValue(object, qualifier);
                if (e == null) {
                    listCell.setLabel(StringUtils.EMPTY);
                } else {
                    listCell.setLabel(getPaymentTransactionAmountValue((PaymentTransactionModel) object, e));
                }
            }
        } catch (TypeNotFoundException e) {
            LOG.error("Could not render row......", e);
        }
    }

    private String getPaymentTransactionAmountValue(final PaymentTransactionModel object, final Object e) {
        final BigDecimal paymentTransactionAmount = ((BigDecimal) e).setScale(object.getEntries().get(0).getCurrency().getDigits(), BigDecimal.ROUND_HALF_DOWN);
        String amount = labelService.getObjectLabel(paymentTransactionAmount);
        return StringUtils.isBlank(amount) ? e.toString() : amount;
    }

    @Required
    public void setPropertyValueService(PropertyValueService propertyValueService) {
        this.propertyValueService = propertyValueService;
    }

    @Required
    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }

    @Required
    public void setPermissionFacade(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    @Required
    public void setTypeFacade(TypeFacade typeFacade) {
        this.typeFacade = typeFacade;
    }
}
