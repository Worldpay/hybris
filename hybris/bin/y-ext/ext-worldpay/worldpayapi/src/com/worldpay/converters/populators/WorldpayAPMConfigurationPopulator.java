package com.worldpay.converters.populators;

import com.worldpay.data.apm.WorldpayAPMConfigurationData;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.converters.Populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populates the {@link WorldpayAPMConfigurationData} from {@link WorldpayAPMConfigurationModel}
 */
public class WorldpayAPMConfigurationPopulator implements Populator<WorldpayAPMConfigurationModel, WorldpayAPMConfigurationData> {

    protected final WorldpayBankConfigurationFacade worldpayBankConfigurationFacade;

    public WorldpayAPMConfigurationPopulator(final WorldpayBankConfigurationFacade worldpayBankConfigurationFacade) {
        this.worldpayBankConfigurationFacade = worldpayBankConfigurationFacade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final WorldpayAPMConfigurationModel source, final WorldpayAPMConfigurationData target) {
        validateParameterNotNull(source, "source must not be null!");
        validateParameterNotNull(target, "target must not be null!");

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setAutoCancelPendingTimeoutInMinutes(source.getAutoCancelPendingTimeoutInMinutes());
        target.setBank(source.getBank());
        target.setAutomaticRefunds(source.getAutomaticRefunds());
        target.setBankTransferRefunds(source.getBankTransferRefunds());
        target.setBankConfigurations(worldpayBankConfigurationFacade.getBankConfigurationForAPMCode(source.getCode()));
    }
}
