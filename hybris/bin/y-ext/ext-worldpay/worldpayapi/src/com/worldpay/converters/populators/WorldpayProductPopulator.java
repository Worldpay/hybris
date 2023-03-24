package com.worldpay.converters.populators;


import com.worldpay.data.Product;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills data from an {@link AbstractOrderEntryModel } to a {@link Product}
 */
public class WorldpayProductPopulator implements Populator<AbstractOrderEntryModel, Product> {

    protected Converter<ProductModel, ProductData> productPriceConverter;

    public WorldpayProductPopulator(final Converter<ProductModel, ProductData> productPriceConverter) {
        this.productPriceConverter = productPriceConverter;
    }

    /**
     * Fills the necessary fields from an {@link AbstractOrderEntryModel} into a {@link Product}
     *
     * @param source an {@link AbstractOrderEntryModel} that contains the information
     * @param target an {@link Product} that receives the information
     */
    @Override
    public void populate(final AbstractOrderEntryModel source, final Product target) {
        validateParameterNotNull(source, "Parameter source (AbstractOrderEntryModel) cannot be null");
        validateParameterNotNull(target, "Parameter target (ProductModel) cannot be null");

        Optional.ofNullable(source.getProduct())
            .ifPresent(productModel -> {
                target.setItemId(source.getProduct().getCode());
                target.setItemName(source.getProduct().getName());
                target.setItemIsDigital(isDigitalItem(source.getProduct()));

                Optional.ofNullable(productPriceConverter.convert(source.getProduct()))
                    .map(ProductData::getPrice)
                    .map(PriceData::getValue)
                    .map(String::valueOf)
                    .ifPresent(target::setItemPrice);

                if (CollectionUtils.isNotEmpty(productModel.getSupercategories())) {
                    final Optional<CategoryModel> first = productModel.getSupercategories().stream().findFirst();
                    first.ifPresent(subcategoryModel -> {
                        target.setItemSubCategory(subcategoryModel.getName());
                        subcategoryModel.getSupercategories().stream()
                            .findFirst().map(CategoryModel::getName).ifPresent(target::setItemCategory);
                    });
                }
            });

        Optional.ofNullable(source.getQuantity())
            .ifPresent(value -> target.setItemQuantity(value.toString()));
    }

    /**
     * Return true or false is Product is Digital. In OOTB not exists this attribute in {@link ProductModel}
     *
     * @return the String value of Boolean value
     */
    @SuppressWarnings("squid:S1172")
    protected String isDigitalItem(final ProductModel productModel) {
        return Boolean.FALSE.toString();
    }
}
