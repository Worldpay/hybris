package com.worldpay.service.response;

import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.enums.order.ThreeDSecureVersionEnum;
import com.worldpay.service.model.Request3DInfo;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectAuthoriseServiceResponseTest {

    private static final String ISSUER_URL = "issuerUrl";
    private static final String PA_REQUEST = "paRequest";
    private static final String ISSUER_PAYLOAD = "issuerPayload";
    private static final String MAJOR_3DS_VERSION1 = "1";
    private static final String MAJOR_3DS_VERSION2 = "2";

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
        final Request3DInfo request3DInfo = new Request3DInfo(PA_REQUEST, ISSUER_URL);

        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureFlowEnum> result = testObj.get3DSecureFlow();

        assertThat(result.get()).isEqualTo(ThreeDSecureFlowEnum.LEGACY_FLOW);
    }

    @Test
    public void get3DSecureFlowWithIssuerUrlAnd3DMajorVersionAndIssuerPayloadAndTransactionIDReturns3DSecureFlexFlow() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Request3DInfo request3DInfo = new Request3DInfo(PA_REQUEST, ISSUER_URL, MAJOR_3DS_VERSION1, ISSUER_PAYLOAD);

        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureFlowEnum> result = testObj.get3DSecureFlow();

        assertThat(result.get()).isEqualTo(ThreeDSecureFlowEnum.THREEDSFLEX_FLOW);
    }

    @Test
    public void get3DSecureFlowWithIncompleteRequest3DInfoReturnsNull() {
        final DirectAuthoriseServiceResponse testObj = new DirectAuthoriseServiceResponse();
        final Request3DInfo request3DInfo = new Request3DInfo(PA_REQUEST, null);

        testObj.setRequest3DInfo(request3DInfo);

        final Optional<ThreeDSecureFlowEnum> result = testObj.get3DSecureFlow();

        assertThat(result).isEmpty();
    }

}
