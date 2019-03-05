package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the current time.
 */
public class WorldpayCurrentTimeAppender implements WorldpaySupportEmailAppender {
    @Override
    public String appendContent() {
        final StringBuilder currentTime = new StringBuilder();
        final LocalDateTime dateTime = LocalDateTime.now();
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMMM, H:mm:ss, zzzz (z)").withZone(ZoneId.systemDefault());
        currentTime.append(System.lineSeparator()).append("Time: ");
        currentTime.append(ONE_TAB).append(dateTime.format(fmt)).append(System.lineSeparator());
        return currentTime.toString();
    }
}
