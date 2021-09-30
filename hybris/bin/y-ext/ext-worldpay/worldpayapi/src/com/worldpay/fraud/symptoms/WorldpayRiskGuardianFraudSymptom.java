package com.worldpay.fraud.symptoms;

import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * The Worldpay Risk Guardian Fraud Symptom class creates the {@link WorldpayRiskGuardianFraudSymptom} symptom with the fraud final score.
 */
public class WorldpayRiskGuardianFraudSymptom extends AbstractWorldpayOrderFraudSymptomDetection {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayRiskGuardianFraudSymptom.class);

    @Override
    public FraudServiceResponse recognizeSymptom(final FraudServiceResponse fraudServiceResponse, final AbstractOrderModel abstractOrderModel) {
        final List<PaymentTransactionModel> paymentTransactions = abstractOrderModel.getPaymentTransactions();
        // At this moment only the Authorisation transaction will be in the list of paymentTransactions
        final double scoreLimit = getScoreLimit();
        paymentTransactions.stream().filter(Objects::nonNull).forEach(paymentTransaction -> {
            final WorldpayRiskScoreModel riskScore = paymentTransaction.getRiskScore();
            if (riskScore == null) {
                LOG.warn(MessageFormat.format("We did not get a risk score back, skipping risk check for: {0}", paymentTransaction));
                return;
            }
            final Double finalScore = riskScore.getFinalScore();
            // we only use the Risk Guardian data if finalScore is set - see WorldpayRiskScoreFraudSymptom
            if (finalScore != null && Double.compare(finalScore, scoreLimit) > 0) {
                setIncrement(finalScore);
                fraudServiceResponse.addSymptom(createSymptom(riskScore.getMessage(), true));
            }
        });
        return fraudServiceResponse;
    }
}
