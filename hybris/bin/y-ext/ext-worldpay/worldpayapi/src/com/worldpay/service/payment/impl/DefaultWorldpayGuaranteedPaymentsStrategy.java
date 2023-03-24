package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.internal.model.Memberships;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.payment.WorldpayGuaranteedPaymentsStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Default implementation of {@link WorldpayGuaranteedPaymentsStrategy}.
 * Extend this strategy if you want to customize the Guaranteed Payments Data payload
 */
public class DefaultWorldpayGuaranteedPaymentsStrategy extends AbstractWorldpayGuaranteedPaymentsStrategy implements WorldpayAdditionalDataRequestStrategy {

    private static final String IN_STORE_PICKUP = "IN_STORE_PICKUP";
    private static final String DELIVERY = "DELIVERY";
    private static final String PICKUP = "pickup";
    private static final String NULL = "null";
    private static final String ACTION = "Action[";
    public static final String ZERO_VALUE = "0.00";
    //private static final List<> VALUES = List.of(PaymentType.CARD_SSL.getMethodCode(), PaymentType.TOKENSSL.getMethodCode(), PaymentType.PAYWITHGOOGLESSL.getMethodCode(), PaymentType.APPLEPAYSSL.getMethodCode())

    protected final WorldpayCartService worldpayCartService;
    protected final BaseSiteService baseSiteService;
    protected final Converter<java.util.Date, Date> worldpayDateConverter;
    protected final Converter<AbstractOrderEntryModel, Product> worldpayProductConverter;

    public DefaultWorldpayGuaranteedPaymentsStrategy(final WorldpayCartService worldpayCartService,
                                                     final BaseSiteService baseSiteService,
                                                     final Converter<java.util.Date, Date> worldpayDateConverter,
                                                     final Converter<AbstractOrderEntryModel, Product> worldpayProductConverter) {
        this.worldpayCartService = worldpayCartService;
        this.baseSiteService = baseSiteService;
        this.worldpayDateConverter = worldpayDateConverter;
        this.worldpayProductConverter = worldpayProductConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        if (isGuaranteedPaymentsEnabled()) {
            if (isNotBlank(worldpayAdditionalInfoData.getDeviceSession())) {
                authoriseRequestParametersCreator.withDeviceSession(worldpayAdditionalInfoData.getDeviceSession());
            }

            authoriseRequestParametersCreator.withGuaranteedPaymentsData(createGuaranteedPaymentsData(cart, worldpayAdditionalInfoData));
            authoriseRequestParametersCreator.withCheckoutId(createCheckoutId(cart));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuaranteedPaymentsData createGuaranteedPaymentsData(final AbstractOrderModel abstractOrder,
                                                               final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        validateParameterNotNull(abstractOrder, "The cart is null");

        final GuaranteedPaymentsData guaranteedPaymentsData = new GuaranteedPaymentsData();

        guaranteedPaymentsData.setDiscountCodes(createDiscountCodes(abstractOrder));
        guaranteedPaymentsData.setMemberships(createMemberships());
        guaranteedPaymentsData.setProductDetails(crateProductDetails(abstractOrder));
        guaranteedPaymentsData.setSecondaryAmount(createSecondaryAmount());
        guaranteedPaymentsData.setSurchargeAmount(createSurchargeAmount());
        guaranteedPaymentsData.setUserAccount(createUserAccount(abstractOrder));
        guaranteedPaymentsData.setFulfillmentMethodType(createFulfillmentMethodType(abstractOrder));
        guaranteedPaymentsData.setTotalShippingCost(createTotalShippingCost(abstractOrder));

        return guaranteedPaymentsData;
    }


    /**
     * Creates the checkout ID based on user and cart
     *
     * @return the value based on user and cart
     */
    private String createCheckoutId(final AbstractOrderModel abstractOrder) {
        return ((CustomerModel) abstractOrder.getUser()).getCustomerID() + "_" + abstractOrder.getCode();
    }

    /**
     * Creates and populates the FulfillmentMethodType.
     *
     * @return the value of FulfillmentMethodType
     */
    @Override
    protected String createFulfillmentMethodType(final AbstractOrderModel abstractOrder) {
        if (Objects.nonNull(abstractOrder.getDeliveryMode()) && PICKUP.equals(abstractOrder.getDeliveryMode().getCode())) {
            return IN_STORE_PICKUP;
        } else {
            return DELIVERY;
        }
    }

    /**
     * Creates and populates the ProductDetails.
     *
     * @return the {@link List<Product>}
     */
    @Override
    protected List<Product> crateProductDetails(final AbstractOrderModel abstractOrder) {
        if (CollectionUtils.isNotEmpty(abstractOrder.getEntries())) {
            return worldpayProductConverter.convertAll(abstractOrder.getEntries());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Creates and populates the DiscountCodes.
     *
     * @return the {@link List<PurchaseDiscount>}
     */
    @Override
    protected List<PurchaseDiscount> createDiscountCodes(final AbstractOrderModel abstractOrder) {
        if (CollectionUtils.isNotEmpty(abstractOrder.getGlobalDiscountValues())) {
            return abstractOrder.getGlobalDiscountValues().stream()
                .map(this::createDiscountCode)
                .collect(Collectors.toList());
        } else {
            final PurchaseDiscount purchaseDiscount = new PurchaseDiscount();
            purchaseDiscount.setPurchaseDiscountCode(NULL);
            purchaseDiscount.setPurchaseDiscountAmount(ZERO_VALUE);
            return Collections.singletonList(purchaseDiscount);
        }
    }

    private PurchaseDiscount createDiscountCode(final DiscountValue discountValue) {
        final PurchaseDiscount purchaseDiscount = new PurchaseDiscount();
        final String code = discountValue.getCode();

        if (code.startsWith(ACTION)) {
            purchaseDiscount.setPurchaseDiscountCode(code.substring(ACTION.length(), code.length() - 1));
        } else {
            purchaseDiscount.setPurchaseDiscountCode(code);
        }

        if (discountValue.isAbsolute()) {
            purchaseDiscount.setPurchaseDiscountAmount(String.valueOf(discountValue.getAppliedValue()));
        } else {
            purchaseDiscount.setPurchaseDiscountPercentage(String.valueOf(discountValue.getAppliedValue()));
        }

        return purchaseDiscount;
    }

    /**
     * Creates and populates the UserAccount.
     *
     * @return the {@link UserAccount}
     */
    @Override
    protected UserAccount createUserAccount(final AbstractOrderModel abstractOrder) {
        final UserAccount userAccount = new UserAccount();

        validateParameterNotNull(abstractOrder.getUser(), "The customer is null");

        Optional.ofNullable(abstractOrder.getUser()).ifPresent(userModel -> {
            userAccount.setUserAccountCreatedDate(worldpayDateConverter.convert(userModel.getCreationtime()));
            userAccount.setUserAccountNumber(worldpayCartService.getAuthenticatedShopperId(abstractOrder));

            if (userModel.getUid().contains("|")) {
                final String email = userModel.getUid().split("\\|")[1];
                userAccount.setUserAccountEmailAddress(email);
                userAccount.setUserAccountUserName(NULL);
            } else {
                userAccount.setUserAccountEmailAddress(userModel.getUid());
                userAccount.setUserAccountUserName(userModel.getName());
            }

            userAccount.setUserAccountPhoneNumber(userModel.getAddresses().stream()
                .findFirst()
                .map(AddressModel::getPhone1)
                .orElse(NULL));
        });

        return userAccount;
    }

    /**
     * Creates and populates the TotalShippingCost.
     *
     * @return the TotalShippingCost
     */
    @Override
    protected String createTotalShippingCost(final AbstractOrderModel abstractOrder) {
        if (Objects.nonNull(abstractOrder.getDeliveryCost()) && !abstractOrder.getDeliveryCost().isNaN()) {
            return abstractOrder.getDeliveryCost().toString();
        }

        return ZERO_VALUE;
    }

    /**
     * Creates and populates the Memberships. Override this method with Real values based on business requirements
     *
     * @return the {@link Memberships}
     */
    @Override
    protected List<Membership> createMemberships() {
        return Collections.emptyList();
    }

    /**
     * Creates and populates the SecondaryAmount. Override this method with Real values based on business requirements
     *
     * @return the value
     */
    @Override
    protected String createSecondaryAmount() {
        return ZERO_VALUE;
    }

    /**
     * Creates and populates the SurchargeAmount. Override this method with Real values based on business requirements
     *
     * @return the value
     */
    @Override
    protected String createSurchargeAmount() {
        return ZERO_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuaranteedPaymentsEnabled() {
        return baseSiteService.getCurrentBaseSite().getEnableGP();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuaranteedPaymentsEnabled(final BaseSiteModel baseSite) {
        return baseSite.getEnableGP();
    }
}
