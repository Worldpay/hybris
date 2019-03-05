package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.Exclude;
import com.worldpay.internal.model.Include;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * POJO representation of the payment method mask
 */
public class PaymentMethodMask implements InternalModelTransformer, Serializable {

    private List<PaymentType> includes;
    private List<PaymentType> excludes;

    public PaymentMethodMask() {
    }

    /**
     * Constructor with full list of fields
     *
     * @param includes
     * @param excludes
     */
    public PaymentMethodMask(List<PaymentType> includes, List<PaymentType> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        com.worldpay.internal.model.PaymentMethodMask intPaymentMethodMask = new com.worldpay.internal.model.PaymentMethodMask();
        List<Object> includeOrExclude = intPaymentMethodMask.getStoredCredentialsOrIncludeOrExclude();
        if (includes != null) {
            for (PaymentType paymentType : includes) {
                Include include = new Include();
                include.setCode(paymentType.getMethodCode());
                includeOrExclude.add(include);
            }
        }
        if (excludes != null) {
            for (PaymentType paymentType : excludes) {
                Exclude exclude = new Exclude();
                exclude.setCode(paymentType.getMethodCode());
                includeOrExclude.add(exclude);
            }
        }
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

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "PaymentMethodMask [includes=" + includes + ", excludes=" + excludes + "]";
    }
}
