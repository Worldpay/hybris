
package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Date;
import com.worldpay.data.UserAccount;
import com.worldpay.internal.model.UserAccountCreatedDate;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.UserAccount} with the information of a {@link UserAccount}
 */
public class UserAccountPopulator implements Populator<UserAccount, com.worldpay.internal.model.UserAccount> {

    protected final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter;

    public UserAccountPopulator(final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter) {
        this.internalDateConverter = internalDateConverter;
    }

    /**
     * Populates the data from the {@link UserAccount} to a {@link com.worldpay.internal.model.UserAccount}
     *
     * @param source a {@link UserAccount} from Worldpay
     * @param target a {@link com.worldpay.internal.model.UserAccount} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final UserAccount source, final com.worldpay.internal.model.UserAccount target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getUserAccountCreatedDate())
            .ifPresent(date -> {
                final UserAccountCreatedDate userAccountCreatedDate = new UserAccountCreatedDate();
                userAccountCreatedDate.setDate(internalDateConverter.convert(date));
                target.setUserAccountCreatedDate(userAccountCreatedDate);
            });

        Optional.ofNullable(source.getUserAccountEmailAddress())
            .ifPresent(target::setUserAccountEmailAddress);

        Optional.ofNullable(source.getUserAccountNumber())
            .ifPresent(target::setUserAccountNumber);

        Optional.ofNullable(source.getUserAccountUserName())
            .ifPresent(target::setUserAccountUserName);

        Optional.ofNullable(source.getUserAccountPhoneNumber())
            .ifPresent(target::setUserAccountPhoneNumber);
    }

}
