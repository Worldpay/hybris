package com.worldpay.support.appender;

/**
 * Interface that exposes methods to append content to the support email.
 */
public abstract class WorldpaySupportEmailAppender {

    public static final String ONE_TAB = "\t";
    public static final String TWO_TABS = "\t\t";
    public static final String THREE_TABS = "\t\t\t";
    public static final String FOUR_TABS = "\t\t\t\t";

    /**
     * Returns the content to be appended to the email support service
     *
     * @return
     */
    public abstract String appendContent();
}
