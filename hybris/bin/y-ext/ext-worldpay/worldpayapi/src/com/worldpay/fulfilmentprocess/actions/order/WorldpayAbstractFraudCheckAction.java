package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.exception.WorldpayException;
import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.impl.FraudSymptom;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Worldpay Abstract Fraud Check Action creates fraud reports and history logs.
 *
 * @param <T> extends {@link OrderProcessModel}
 */
public abstract class WorldpayAbstractFraudCheckAction<T extends OrderProcessModel> extends WorldpayAbstractOrderAction<T> {

    private static final String FRAUD_CHECK_TEXT_OK = "Fraud check [{0}]: OK";
    private static final String FRAUD_CHECK_TEXT_NOK = "Fraud check [{0}]: {1}. Check the fraud report : {2}";
    protected static final String OK = "OK";
    protected static final String POTENTIAL = "POTENTIAL";

    protected FraudReportModel createFraudReport(final String providerName, final FraudServiceResponse response, final OrderModel order, final FraudStatus status) {
        final FraudReportModel fraudReport = this.modelService.create(FraudReportModel.class);
        fraudReport.setOrder(order);
        fraudReport.setStatus(status);
        fraudReport.setProvider(providerName);
        fraudReport.setTimestamp(getTimeService().getCurrentTime());
        int reportNumber = 0;
        if (order.getFraudReports() != null && !order.getFraudReports().isEmpty()) {
            reportNumber = order.getFraudReports().size();
        }

        fraudReport.setCode(order.getCode() + "_FR" + reportNumber);

        final List<FraudSymptomScoringModel> symptomScoringList = new ArrayList<>();
        final List<FraudSymptom> symptoms = response.getSymptoms();
        for (final FraudSymptom fraudSymptom : symptoms) {
            final FraudSymptomScoringModel symptomScoring = this.modelService.create(FraudSymptomScoringModel.class);
            symptomScoring.setFraudReport(fraudReport);
            symptomScoring.setName(fraudSymptom.getSymptom());
            symptomScoring.setExplanation(fraudSymptom.getExplanation());
            symptomScoring.setScore(fraudSymptom.getScore());
            symptomScoringList.add(symptomScoring);
        }
        fraudReport.setFraudSymptomScorings(symptomScoringList);
        return fraudReport;
    }

    protected OrderHistoryEntryModel createHistoryLog(final String providerName, final OrderModel order, final FraudStatus status, final String code) {
        String description;
        if (status.equals(FraudStatus.OK)) {
            description = MessageFormat.format(FRAUD_CHECK_TEXT_OK, providerName);
        } else {
            description = MessageFormat.format(FRAUD_CHECK_TEXT_NOK, providerName, status.toString(), code);
        }

        return this.createHistoryLog(description, order);
    }

    @Override
    public Set<String> getTransitions() {
        return createTransitions(OK, POTENTIAL);
    }

    @Override
    public final String execute(final T process) throws WorldpayException {
        return this.executeAction(process);
    }

    /**
     * @param var1
     * @return
     * @throws WorldpayException
     */
    public abstract String executeAction(T var1) throws WorldpayException;


}
