package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the current user display name
 */
public class WorldpayUserDisplayNameAppender extends WorldpaySupportEmailAppender {

    protected final UserService userService;

    public WorldpayUserDisplayNameAppender(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final String userName = userService.getCurrentUser().getDisplayName();
        return System.lineSeparator() + "User: " + userName + System.lineSeparator();
    }
}
