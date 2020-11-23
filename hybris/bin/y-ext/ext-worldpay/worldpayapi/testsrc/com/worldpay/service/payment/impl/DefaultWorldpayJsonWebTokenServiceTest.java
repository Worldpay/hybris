package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.bootstrap.annotations.UnitTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayJsonWebTokenServiceTest {

    private static final String ISS_VALUE = "5bd9e0e4444dce153428c940";
    private static final String JWT_MAC_KEY_VALUE = "fa2daee2-1fbb-45ff-4444-52805d5cd9e0";
    private static final String ORG_UNIT_ID_VALUE = "5bd9b55e4444761ac0af1c80";
    private static final String HS_256 = "HS256";
    private static final String ISS = "iss";
    private static final String ORG_UNIT_ID = "OrgUnitId";
    private static final String ISSUER_PAYLOAD_VALUE = "issuerPayloadValue";
    private static final String TRANSACTION_ID_3DS_VALUE = "transactionId3DSValue";
    private static final String ISSUER_URL_VALUE = "acsURLValue";
    private static final String RETURN_URL_VALUE = "ReturnURLValue";
    private static final String RETURN_URL = "ReturnUrl";
    private static final String OBJECTIFY_PAYLOAD = "ObjectifyPayload";
    private static final String PAYLOAD = "Payload";
    private static final String ACS_URL = "ACSUrl";
    private static final String TRANSACTION_ID = "TransactionId";

    @Spy
    @InjectMocks
    private DefaultWorldpayJsonWebTokenService testObj;

    @Mock
    private WorldpayMerchantConfigData merchantConfigDataMock;
    @Mock
    private ThreeDSFlexJsonWebTokenCredentials threeDSJsonWebTokenCredentialsMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private Date dateMock;

    @Before
    public void setUp() {
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDSJsonWebTokenCredentialsMock);
        when(threeDSJsonWebTokenCredentialsMock.getIss()).thenReturn(ISS_VALUE);
        when(threeDSJsonWebTokenCredentialsMock.getJwtMacKey()).thenReturn(JWT_MAC_KEY_VALUE);
        when(threeDSJsonWebTokenCredentialsMock.getOrgUnitId()).thenReturn(ORG_UNIT_ID_VALUE);
        when(threeDSJsonWebTokenCredentialsMock.getAlg()).thenReturn(HS_256);
        doReturn(dateMock).when(testObj).getIssuedAt();
    }

    @Test
    public void createJsonWebTokenForDDCShouldReturnAValidJsonWebToken() {

        final String result = testObj.createJsonWebTokenForDDC(merchantConfigDataMock);

        assertThat(result).isNotEmpty();
        final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        final Key signingKey = new SecretKeySpec(JWT_MAC_KEY_VALUE.getBytes(),
                signatureAlgorithm.getJcaName());
        final Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(result).getBody();
        final JwsHeader<?> headers = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(result).getHeader();
        assertThat(headers.getAlgorithm()).isEqualTo(HS_256);
        assertThat(claims.get(ISS)).isEqualTo(ISS_VALUE);
        assertThat(claims.get(ORG_UNIT_ID)).isEqualTo(ORG_UNIT_ID_VALUE);
        assertThat(claims.getIssuedAt()).isEqualTo(dateMock);
    }

    @Test
    public void generateJsonWebTokenFor3DSecureFlexChallengeIframe() throws WorldpayConfigurationException {
        when(directResponseDataMock.getIssuerPayload()).thenReturn(ISSUER_PAYLOAD_VALUE);
        when(directResponseDataMock.getTransactionId3DS()).thenReturn(TRANSACTION_ID_3DS_VALUE);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL_VALUE);
        when(worldpayUrlServiceMock.getFullThreeDSecureFlexFlowReturnUrl()).thenReturn(RETURN_URL_VALUE);

        final String result = testObj.createJsonWebTokenFor3DSecureFlexChallengeIframe(merchantConfigDataMock, directResponseDataMock);

        final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        final Key signingKey = new SecretKeySpec(JWT_MAC_KEY_VALUE.getBytes(),
                signatureAlgorithm.getJcaName());
        final Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(result).getBody();
        final JwsHeader<?> headers = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(result).getHeader();
        assertThat(headers.getAlgorithm()).isEqualTo(HS_256);
        assertThat(claims.get(ISS)).isEqualTo(ISS_VALUE);
        assertThat(claims.get(ORG_UNIT_ID)).isEqualTo(ORG_UNIT_ID_VALUE);
        assertThat(claims.get(RETURN_URL)).isEqualTo(RETURN_URL_VALUE);
        assertThat(claims.get(PAYLOAD)).isEqualTo(ImmutableMap.of(ACS_URL, ISSUER_URL_VALUE, PAYLOAD, ISSUER_PAYLOAD_VALUE, TRANSACTION_ID, TRANSACTION_ID_3DS_VALUE));
        assertThat(Boolean.valueOf(String.valueOf(claims.get(OBJECTIFY_PAYLOAD)))).isTrue();
        assertThat(claims.getIssuedAt()).isEqualTo(dateMock);
    }
}
