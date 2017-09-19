package com.worldpay.support.appender;

/**
 * Interface that exposes methods to append content to the support email.
 */
public interface WorldpaySupportEmailAppender {

    String ONE_TAB = "\t";
    String TWO_TABS = "\t\t";
    String THREE_TABS = "\t\t\t";
    String FOUR_TABS = "\t\t\t\t";

    /**
     * Returns the content to be appended to the email support service
     * @return
     */
    String appendContent();
}
