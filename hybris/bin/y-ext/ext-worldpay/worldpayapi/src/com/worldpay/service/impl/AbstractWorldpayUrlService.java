package com.worldpay.service.impl;


import com.worldpay.service.WorldpayUrlService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract implementation of the {@link WorldpayUrlService} allowing for success, pending, failure and cancel paths to
 * be configured
 */
public abstract class AbstractWorldpayUrlService implements WorldpayUrlService {

    private String successPath;
    private String pendingPath;
    private String failurePath;
    private String cancelPath;
    private String threeDSecureTermPath;
    private String threeDSecureFlexFlowReturnUrl;
    private String threeDSecureQuoteTermPath;
    private String errorPath;
    private String termsPath;
    private String klarnaConfirmationPath;
    private String threeDSecureFlexAuthSubmit;

    public String getSuccessPath() {
        return successPath;
    }

    @Required
    public void setSuccessPath(final String successPath) {
        this.successPath = successPath;
    }

    public String getPendingPath() {
        return pendingPath;
    }

    @Required
    public void setPendingPath(final String pendingPath) {
        this.pendingPath = pendingPath;
    }

    public String getFailurePath() {
        return failurePath;
    }

    @Required
    public void setFailurePath(final String failurePath) {
        this.failurePath = failurePath;
    }

    public String getCancelPath() {
        return cancelPath;
    }

    @Required
    public void setCancelPath(final String cancelPath) {
        this.cancelPath = cancelPath;
    }

    @Required
    public void setThreeDSecureTermPath(final String threeDSecureTermPath) {
        this.threeDSecureTermPath = threeDSecureTermPath;
    }

    public String getThreeDSecureTermPath() {
        return threeDSecureTermPath;
    }

    public String getThreeDSecureQuoteTermPath() {
        return threeDSecureQuoteTermPath;
    }

    @Required
    public void setThreeDSecureQuoteTermPath(final String threeDSecureQuoteTermPath) {
        this.threeDSecureQuoteTermPath = threeDSecureQuoteTermPath;
    }

    public String getErrorPath() {
        return errorPath;
    }

    @Required
    public void setErrorPath(final String errorPath) {
        this.errorPath = errorPath;
    }

    public String getTermsPath() {
        return termsPath;
    }

    @Required
    public void setTermsPath(final String termsPath) {
        this.termsPath = termsPath;
    }

    public String getKlarnaConfirmationPath() {
        return klarnaConfirmationPath;
    }

    @Required
    public void setKlarnaConfirmationPath(final String klarnaConfirmationPath) {
        this.klarnaConfirmationPath = klarnaConfirmationPath;
    }

    public String getThreeDSecureFlexFlowReturnUrl() {
        return threeDSecureFlexFlowReturnUrl;
    }

    @Required
    public void setThreeDSecureFlexFlowReturnUrl(final String threeDSecureFlexFlowReturnUrl) {
        this.threeDSecureFlexFlowReturnUrl = threeDSecureFlexFlowReturnUrl;
    }

    public String getThreeDSecureFlexAuthSubmit() {
        return threeDSecureFlexAuthSubmit;
    }

    @Required
    public void setThreeDSecureFlexAuthSubmit(final String threeDSecureFlexAuthSubmit) {
        this.threeDSecureFlexAuthSubmit = threeDSecureFlexAuthSubmit;
    }
}
