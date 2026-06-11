package com.worldpay.interceptors;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;



/**
 * Adds a unique generated code to object if none exists.
 * This is done when ItemModel is saved using {@link ModelService#save(Object)}.
 */
public class WorldpayItemCodePrepareInterceptor implements PrepareInterceptor<ItemModel> {

    protected final KeyGenerator keyGenerator;
    protected final TypeService typeService;
    protected final String fieldName;

    public WorldpayItemCodePrepareInterceptor(final KeyGenerator keyGenerator, final TypeService typeService, final String fieldName) {
        this.keyGenerator = keyGenerator;
        this.typeService = typeService;
        this.fieldName = fieldName;
    }

    /**
     * Adds a unique code to model before save if model.save is called on object
     *
     * @param itemModel                 Item model to be saved
     * @param interceptorContext        Interceptor context
     */
    @Override
    public void onPrepare(final ItemModel itemModel, final InterceptorContext interceptorContext) {

        final ComposedTypeModel type = typeService.getComposedTypeForCode(itemModel.getItemtype());
        if (typeService.hasAttribute(type, fieldName)) {
            final ModelService modelService = interceptorContext.getModelService();
            if (modelService.getAttributeValue(itemModel, fieldName) == null) {
                modelService.setAttributeValue(itemModel, fieldName, keyGenerator.generate());
            }
        }
    }

}
