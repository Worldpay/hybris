package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.Exclude;
import com.worldpay.internal.model.Include;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.request.transform.InternalModelTransformer;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * POJO representation of the payment method mask
 */
public class PaymentMethodMask implements InternalModelTransformer, Serializable {

    private List<PaymentType> includes;
    private List<PaymentType> excludes;
    private StoredCredentials storedCredentials;

    public PaymentMethodMask() {
    }

    /**
     * Constructor with full list of fields
     *
     * @param includes
     * @param excludes
     */
    public PaymentMethodMask(final List<PaymentType> includes, final List<PaymentType> excludes, final StoredCredentials storedCredentials) {
        this.includes = includes;
        this.excludes = excludes;
        this.storedCredentials = storedCredentials;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final com.worldpay.internal.model.PaymentMethodMask intPaymentMethodMask = new com.worldpay.internal.model.PaymentMethodMask();
        final List<Object> includeOrExclude = intPaymentMethodMask.getStoredCredentialsOrIncludeOrExclude();

        Optional.ofNullable(storedCredentials)
                .map(StoredCredentials::transformToInternalModel)
                .ifPresent(includeOrExclude::add);

        CollectionUtils.emptyIfNull(excludes).stream()
                .map(PaymentType::getMethodCode)
                .map(this::createIntExclude)
                .forEach(includeOrExclude::add);

        CollectionUtils.emptyIfNull(includes).stream()
                .map(PaymentType::getMethodCode)
                .map(this::createIntInclude)
                .forEach(includeOrExclude::add);

        return intPaymentMethodMask;
    }

    /**
     * Add an item to the list of payment methods that are included
     *
     * @param paymentType PaymentType to be included
     */
    public void addInclude(PaymentType paymentType) {
        if (includes == null) {
            includes = new ArrayList<>();
        }
        includes.add(paymentType);
    }

    /**
     * Add an item to the list of payment methods that are excluded
     *
     * @param paymentType PaymentType to be excluded
     */
    public void addExclude(PaymentType paymentType) {
        if (excludes == null) {
            excludes = new ArrayList<>();
        }
        excludes.add(paymentType);
    }

    public List<PaymentType> getIncludes() {
        return includes;
    }

    public void setIncludes(List<PaymentType> includes) {
        this.includes = includes;
    }

    public List<PaymentType> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<PaymentType> excludes) {
        this.excludes = excludes;
    }

    public StoredCredentials getStoredCredentials() {
        return storedCredentials;
    }

    public void setStoredCredentials(final StoredCredentials storedCredentials) {
        this.storedCredentials = storedCredentials;
    }

    protected Exclude createIntExclude(final String methodCode) {
        final Exclude intExclude = new Exclude();
        intExclude.setCode(methodCode);
        return intExclude;
    }

    protected Include createIntInclude(final String methodCode) {
        final Include intInclude = new Include();
        intInclude.setCode(methodCode);
        return intInclude;
    }

    @Override
    public String toString() {
        return "PaymentMethodMask{" +
                "includes=" + includes +
                ", excludes=" + excludes +
                ", storedCredentials=" + storedCredentials +
                '}';
    }
}
