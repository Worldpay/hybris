package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.labels.LabelService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

/**
 * Abstract amount renderer
 */
public abstract class AbstractAmountRenderer {

    private static final Logger LOG = LogManager.getLogger(AbstractAmountRenderer.class);

    protected final TypeFacade typeFacade;
    protected final PropertyValueService propertyValueService;
    protected final LabelService labelService;
    protected final PermissionFacade permissionFacade;

    protected AbstractAmountRenderer(final TypeFacade typeFacade,
                                     final PropertyValueService propertyValueService,
                                     final LabelService labelService,
                                     final PermissionFacade permissionFacade) {
        this.typeFacade = typeFacade;
        this.propertyValueService = propertyValueService;
        this.labelService = labelService;
        this.permissionFacade = permissionFacade;
    }

    /**
     * Renders the amount for the given dataType
     *
     * @param listCell
     * @param columnConfiguration
     * @param object
     * @param dataType
     */
    protected void renderAmount(final Listcell listCell, final ListColumn columnConfiguration, final Object object, final String dataType) {
        final String qualifier = columnConfiguration.getQualifier();
        try {
            final DataType dataTypeObj = typeFacade.load(dataType);
            if (Objects.nonNull(dataTypeObj) && permissionFacade.canReadProperty(dataTypeObj.getCode(), qualifier)) {
                final Object amount = propertyValueService.readValue(object, qualifier);
                final String label = amount != null ? getPaymentTransactionAmountValue(object, amount) : EMPTY;
                listCell.setLabel(label);
            }
        } catch (final TypeNotFoundException e) {
            LOG.error("Could not render row......", e);
        }
    }

    /**
     * Gets the payment transaction amount
     *
     * @param object
     * @param amount
     * @return
     */
    protected String getPaymentTransactionAmountValue(final Object object, final Object amount) {
        final BigDecimal paymentTransactionAmount;
        if (object instanceof PaymentTransactionEntryModel) {
            paymentTransactionAmount = ((BigDecimal) amount).setScale(((PaymentTransactionEntryModel) object).getCurrency().getDigits(), RoundingMode.HALF_UP);

        } else {
            paymentTransactionAmount = ((BigDecimal) amount).setScale(((PaymentTransactionModel) object).getEntries().get(0).getCurrency().getDigits(), RoundingMode.HALF_UP);
        }

        final String amountValue = labelService.getObjectLabel(paymentTransactionAmount);
        return defaultIfBlank(amountValue, amount.toString());
    }
}
