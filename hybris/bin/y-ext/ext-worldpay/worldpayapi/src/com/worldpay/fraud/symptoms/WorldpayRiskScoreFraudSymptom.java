package com.worldpay.fraud.symptoms;

import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static java.text.MessageFormat.format;

/**
 * The Worldpay Risk Score Fraud Symptom class creates the {@link WorldpayRiskScoreFraudSymptom} symptom with the fraud risk value.
 */
public class WorldpayRiskScoreFraudSymptom extends AbstractWorldpayOrderFraudSymptomDetection {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayRiskScoreFraudSymptom.class);

    @Override
    public FraudServiceResponse recognizeSymptom(final FraudServiceResponse fraudServiceResponse, final AbstractOrderModel abstractOrderModel) {
        final List<PaymentTransactionModel> paymentTransactions = abstractOrderModel.getPaymentTransactions();
        // At this moment only the Authorisation transaction will be in the list of paymentTransactions
        final double scoreLimit = getScoreLimit();
        paymentTransactions.stream().filter(Objects::nonNull).forEach(paymentTransaction -> {
            final WorldpayRiskScoreModel riskScore = paymentTransaction.getRiskScore();
            if (riskScore == null) {
                LOG.warn(format("We did not get a risk score back, skipping risk check for: {0}", paymentTransaction));
                return;
            }
            final String riskScoreValue = riskScore.getValue();
            try {
                if (riskScoreValue != null && Double.compare(Double.valueOf(riskScoreValue), scoreLimit) > 0) {
                    setIncrement(Double.valueOf(riskScoreValue));
                    fraudServiceResponse.addSymptom(createSymptom("RiskValue", true));
                }
            } catch (final NumberFormatException e) {
                LOG.error(format("riskScoreValue for order with code [{0}] was not a number: [{1}]. The RiskScore was not checked for fraud.",
                        abstractOrderModel.getCode(), riskScoreValue));
            }
        });
        return fraudServiceResponse;
    }
}
