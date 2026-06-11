package com.worldpay.support.impl;

import com.worldpay.support.WorldpaySupportEmailService;
import com.worldpay.support.appender.WorldpaySupportEmailAppender;


import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpaySupportEmailService implements WorldpaySupportEmailService {

    protected final List<WorldpaySupportEmailAppender> emailAppenders;

    public DefaultWorldpaySupportEmailService(final List<WorldpaySupportEmailAppender> emailAppenders) {
        this.emailAppenders = emailAppenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEmailBody() {
        final StringBuilder bodyBuilder = new StringBuilder();
        for (final WorldpaySupportEmailAppender emailAppender : getEmailAppenders()) {
            bodyBuilder.append(emailAppender.appendContent());
        }
        return bodyBuilder.toString();
    }

    protected List<WorldpaySupportEmailAppender> getEmailAppenders() {
        return emailAppenders;
    }

}
