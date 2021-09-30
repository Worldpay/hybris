package com.worldpay.service.response;

import com.worldpay.data.Request3DInfo;
import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.enums.order.ThreeDSecureVersionEnum;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectAuthoriseServiceResponseTest {

    private static final String ISSUER_URL = "issuerUrl";
    private static final String PA_REQUEST = "paRequest";
    private static final String ISSUER_PAYLOAD = "issuerPayload";
    private static final String MAJOR_3DS_VERSION1 = "1";
    private static final String MAJOR_3DS_VERSION2 = "2";
    private static final String TRANSACTION_ID = "TransactionID";

    @Test
    public void get3DSecureVersionReturnsAnEmptyOptionalIfThereIsNot3DSecureInfo() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Optional<ThreeDSecureVersionEnum> result = testObj.get3DSecureVersion();

        assertThat(result).isEmpty();
    }

    @Test
    public void get3DSecureVersionReturnsTheEnumVersionStoredInMajorVersionOfTheObject() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Request3DInfo request3DInfo = new Request3DInfo();
        request3DInfo.setMajor3DSVersion(MAJOR_3DS_VERSION2);
        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureVersionEnum> result = testObj.get3DSecureVersion();

        assertThat(result.get()).isEqualTo(ThreeDSecureVersionEnum.V2);
    }

    @Test
    public void get3DSecureFlowWithPaRequestAndIssuerUrlReturnsTheLegacyFlow() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Request3DInfo request3DInfo = new Request3DInfo();
        request3DInfo.setPaRequest(PA_REQUEST);
        request3DInfo.setIssuerUrl(ISSUER_URL);

        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureFlowEnum> result = testObj.get3DSecureFlow();

        assertThat(result.get()).isEqualTo(ThreeDSecureFlowEnum.LEGACY_FLOW);
    }

    @Test
    public void get3DSecureFlowWithIssuerUrlAnd3DMajorVersionAndIssuerPayloadAndTransactionIDReturns3DSecureFlexFlow() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Request3DInfo request3DInfo = new Request3DInfo();
        request3DInfo.setIssuerUrl(ISSUER_URL);
        request3DInfo.setMajor3DSVersion(MAJOR_3DS_VERSION1);
        request3DInfo.setIssuerPayload(ISSUER_PAYLOAD);
        request3DInfo.setTransactionId3DS(TRANSACTION_ID);

        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureFlowEnum> result = testObj.get3DSecureFlow();

        assertThat(result.get()).isEqualTo(ThreeDSecureFlowEnum.THREEDSFLEX_FLOW);
    }

    @Test
    public void get3DSecureFlowWithIncompleteRequest3DInfoReturnsNull() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Request3DInfo request3DInfo = new Request3DInfo();
        request3DInfo.setPaRequest(PA_REQUEST);

        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureFlowEnum> result = testObj.get3DSecureFlow();

        assertThat(result).isEmpty();
    }

}
