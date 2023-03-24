package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Membership;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Membership} with the information of a {@link Membership}
 */
public class MembershipPopulator implements Populator<Membership, com.worldpay.internal.model.Membership> {

    /**
     * Populates the data from the {@link Membership} to a {@link com.worldpay.internal.model.Membership}
     *
     * @param source a {@link Membership} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Membership} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Membership source, final com.worldpay.internal.model.Membership target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getMembershipId()).ifPresent(target::setMembershipId);
        Optional.ofNullable(source.getMembershipName()).ifPresent(target::setMembershipName);
        Optional.ofNullable(source.getMembershipEmailAddress()).ifPresent(target::setMembershipEmailAddress);
        Optional.ofNullable(source.getMembershipPhoneNumber()).ifPresent(target::setMembershipPhoneNumber);
    }

}
