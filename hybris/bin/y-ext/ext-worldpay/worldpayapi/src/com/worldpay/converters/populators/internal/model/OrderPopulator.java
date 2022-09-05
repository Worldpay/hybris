package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.*;
import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.FraudSightData;
import com.worldpay.data.Order;
import com.worldpay.data.OrderLines;
import com.worldpay.data.token.TokenRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Order} with the information of a {@link Order}.
 */
public class OrderPopulator implements Populator<Order, com.worldpay.internal.model.Order> {

    protected final Converter<BranchSpecificExtension, com.worldpay.internal.model.BranchSpecificExtension> internalBranchSpecificExtensionConverter;
    protected final Converter<TokenRequest, CreateToken> internalTokenRequestConverter;
    protected final Converter<OrderLines, com.worldpay.internal.model.OrderLines> internalOrderLinesConverter;
    protected final Converter<FraudSightData, com.worldpay.internal.model.FraudSightData> internalFraudSightDataConverter;
    protected final PaymentOrderConvertersWrapper paymentOrderConvertersWrapper;
    protected final ThreeDS2OrderConvertersWrapper threeDS2OrderConvertersWrapper;
    protected final BasicOrderConvertersWrapper basicOrderConvertersWrapper;

    public OrderPopulator(final Converter<BranchSpecificExtension, com.worldpay.internal.model.BranchSpecificExtension> internalBranchSpecificExtensionConverter,
                          final Converter<TokenRequest, CreateToken> internalTokenRequestConverter,
                          final Converter<OrderLines, com.worldpay.internal.model.OrderLines> internalOrderLinesConverter,
                          final Converter<FraudSightData, com.worldpay.internal.model.FraudSightData> internalFraudSightDataConverter,
                          final PaymentOrderConvertersWrapper paymentOrderConvertersWrapper,
                          final ThreeDS2OrderConvertersWrapper threeDS2OrderConvertersWrapper,
                          final BasicOrderConvertersWrapper basicOrderConvertersWrapper) {
        this.internalBranchSpecificExtensionConverter = internalBranchSpecificExtensionConverter;
        this.internalTokenRequestConverter = internalTokenRequestConverter;
        this.internalOrderLinesConverter = internalOrderLinesConverter;
        this.internalFraudSightDataConverter = internalFraudSightDataConverter;
        this.paymentOrderConvertersWrapper = paymentOrderConvertersWrapper;
        this.threeDS2OrderConvertersWrapper = threeDS2OrderConvertersWrapper;
        this.basicOrderConvertersWrapper = basicOrderConvertersWrapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final Order source, final com.worldpay.internal.model.Order target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final List<Object> childElements = target.getDescriptionOrAmountOrRiskOrOrderContentOrOrderChannelOrCheckoutIdOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrPaymentTokenIDOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrExtendedOrderDetailOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrDynamicCurrencyConversionOrOverrideNarrativeOrGuaranteedPaymentsDataOrInfo3DSecureOrSession();

        Optional.ofNullable(source.getOrderCode())
            .ifPresent(target::setOrderCode);

        Optional.ofNullable(source.getDescription()).ifPresent(description -> {
            final Description intDescription = new Description();
            intDescription.setvalue(source.getDescription());
            childElements.add(intDescription);
        });

        Optional.ofNullable(source.getAmount())
            .map(basicOrderConvertersWrapper.internalAmountConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getInstallationId())
            .ifPresent(target::setInstallationId);

        Optional.ofNullable(source.getOrderContent()).ifPresent(orderContent -> {
            final OrderContent intOrderContent = new OrderContent();
            intOrderContent.setvalue(orderContent);
            childElements.add(intOrderContent);
        });

        populatePaymentRequestDetails(source, childElements);

        Optional.ofNullable(source.getShopper())
            .map(basicOrderConvertersWrapper.internalShopperConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getShippingAddress())
            .map(basicOrderConvertersWrapper.internalAddressConverter::convert)
            .ifPresent(address -> {
                final ShippingAddress intShippingAddress = new ShippingAddress();
                intShippingAddress.setAddress(address);
                childElements.add(intShippingAddress);
            });

        Optional.ofNullable(source.getBillingAddress())
            .map(basicOrderConvertersWrapper.internalAddressConverter::convert)
            .ifPresent(address -> {
                final BillingAddress intBillingAddress = new BillingAddress();
                intBillingAddress.setAddress(address);
                childElements.add(intBillingAddress);
            });

        Optional.ofNullable(source.getMandateType())
            .ifPresent(mandateType -> {
                final Mandate mandate = new Mandate();
                mandate.setMandateType(mandateType);
                childElements.add(mandate);
            });

        Optional.ofNullable(source.getBranchSpecificExtension())
            .map(internalBranchSpecificExtensionConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getPaymentMethodAttributes())
            .stream()
            .flatMap(Collection::stream)
            .map(paymentOrderConvertersWrapper.internalPaymentMethodAttributeConverter::convert)
            .forEach(childElements::add);

        Optional.ofNullable(source.getStatementNarrative())
            .ifPresent(statementNarrative -> {
                final StatementNarrative intStatementNarrative = new StatementNarrative();
                intStatementNarrative.setvalue(statementNarrative);
                childElements.add(intStatementNarrative);
            });

        Optional.ofNullable(source.getEchoData())
            .ifPresent(echoData -> {
                final EchoData intEchoData = new EchoData();
                intEchoData.setvalue(echoData);
                childElements.add(intEchoData);
            });

        Optional.ofNullable(source.getTokenRequest())
            .map(internalTokenRequestConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getPaResponse())
            .ifPresent(paResponse -> {
                final Info3DSecure intInfo3dSecure = new Info3DSecure();
                final PaResponse intPaResponse = new PaResponse();
                intPaResponse.setvalue(paResponse);
                intInfo3dSecure.getPaResponseOrMpiProviderOrMpiResponseOrAttemptedAuthenticationOrCompletedAuthenticationOrThreeDSVersionOrMerchantNameOrXidOrDsTransactionIdOrCavvOrEciOrDelegatedAuthentication().add(intPaResponse);
                childElements.add(intInfo3dSecure);
            });

        Optional.ofNullable(source.getSession())
            .map(basicOrderConvertersWrapper.internalSessionConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getOrderLines())
            .map(internalOrderLinesConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getDynamicInteractionType())
            .map(Enum::name)
            .ifPresent(dynamicInteractionType -> {
                final DynamicInteractionType intDynamicInteractionType = new DynamicInteractionType();
                intDynamicInteractionType.setType(dynamicInteractionType);
                childElements.add(intDynamicInteractionType);
            });

        Optional.ofNullable(source.getRiskData())
            .map(threeDS2OrderConvertersWrapper.internalRiskDataConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getAdditional3DSData())
            .map(threeDS2OrderConvertersWrapper.internalAdditional3DSDataConverter::convert)
            .ifPresent(childElements::add);

        Optional.ofNullable(source.getFraudSightData())
            .map(internalFraudSightDataConverter::convert)
            .ifPresent(childElements::add);

        if (StringUtils.isNotBlank(source.getDeviceSession())) {
            final DeviceSession intDeviceSession = new DeviceSession();
            intDeviceSession.setSessionId(source.getDeviceSession());
            childElements.add(intDeviceSession);
        }
    }

    private void populatePaymentRequestDetails(final Order source, final List<Object> childElements) {
        if (source.getPaymentMethodMask() != null) {
            childElements.add(paymentOrderConvertersWrapper.internalPaymentMethodMaskConverter.convert(source.getPaymentMethodMask()));
        } else if (source.getPaymentDetails() != null) {
            childElements.add(paymentOrderConvertersWrapper.internalPaymentDetailsConverter.convert(source.getPaymentDetails()));
        } else if (source.getPayAsOrder() != null) {
            childElements.add(paymentOrderConvertersWrapper.internalPayAsOrderConverter.convert(source.getPayAsOrder()));
        }
    }
}
