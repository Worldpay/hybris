package com.worldpay.service.model.payment;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract class that templates part of the transformToInternalModel method for {@link Payment} implementors. Obviously each type of payment will have different
 * fields so reflection is used to get the list of methods for an implementation and then it's up to the subclass to implement the correct logic for any setter
 * methods
 */
public abstract class AbstractPayment implements Payment, Serializable {

    protected PaymentType paymentType;

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        try {
            final Class<?> modelClass = paymentType.getModelClass();
            final InternalModelObject instance = (InternalModelObject) modelClass.getDeclaredConstructor().newInstance();
            final Method[] declaredMethods = modelClass.getDeclaredMethods();
            for (final Method method : declaredMethods) {
                invokeSetter(method, instance);
            }

            return instance;
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new WorldpayModelTransformationException("Exception while attempting to transform Card", e);
        }
    }

    /**
     * Set the relevant field on the internal model object from the external model representation.
     * <p/>
     * The {@link #transformToInternalModel()} method uses reflection to find all the methods supported by the internal model object and then invokes this method to
     * correctly set the details of each field against the target object
     *
     * @param method       Method that can be invoked on the internal model object targetObject
     * @param targetObject internal model object that we are trying to transform to
     * @throws IllegalAccessException    if the method is not accessible
     * @throws InvocationTargetException if method cannot be invoked against the supplied target object
     */
    public abstract void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException, WorldpayModelTransformationException;

    @Override
    public PaymentType getPaymentType() {
        return paymentType;
    }

    @Override
    public void setPaymentType(final PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
