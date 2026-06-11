package com.worldpay.service.payment.impl;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.bootstrap.annotations.UnitTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
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
    private Date issuedAt;

    @BeforeEach
    public void setUp() {
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDSJsonWebTokenCredentialsMock);
        when(threeDSJsonWebTokenCredentialsMock.getIss()).thenReturn(ISS_VALUE);
        when(threeDSJsonWebTokenCredentialsMock.getJwtMacKey()).thenReturn(JWT_MAC_KEY_VALUE);
        when(threeDSJsonWebTokenCredentialsMock.getOrgUnitId()).thenReturn(ORG_UNIT_ID_VALUE);
        when(threeDSJsonWebTokenCredentialsMock.getAlg()).thenReturn(HS_256);
        doReturn(issuedAt).when(testObj).getIssuedAt();
    }

    @Test
    public void createJsonWebTokenForDDCShouldReturnAValidJsonWebToken() {

        final String result = testObj.createJsonWebTokenForDDC(merchantConfigDataMock);

        assertThat(result).isNotEmpty();
        final SecretKey signingKey = new SecretKeySpec(JWT_MAC_KEY_VALUE.getBytes(), "HmacSHA256");
        final Jws<Claims> jws = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(result);
        final Claims claims = jws.getPayload();
        final JwsHeader headers = jws.getHeader();
        assertThat(headers.getAlgorithm()).isEqualTo(HS_256);
        assertThat(claims.get(ISS)).isEqualTo(ISS_VALUE);
        assertThat(claims.get(ORG_UNIT_ID)).isEqualTo(ORG_UNIT_ID_VALUE);
        assertThat(claims.getIssuedAt()).isEqualTo(issuedAt);
    }

    @Test
    public void generateJsonWebTokenFor3DSecureFlexChallengeIframe() throws WorldpayConfigurationException {
        when(directResponseDataMock.getIssuerPayload()).thenReturn(ISSUER_PAYLOAD_VALUE);
        when(directResponseDataMock.getTransactionId3DS()).thenReturn(TRANSACTION_ID_3DS_VALUE);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL_VALUE);
        when(worldpayUrlServiceMock.getFullThreeDSecureFlexFlowReturnUrl()).thenReturn(RETURN_URL_VALUE);

        final String result = testObj.createJsonWebTokenFor3DSecureFlexChallengeIframe(merchantConfigDataMock, directResponseDataMock);

        final SecretKey signingKey = new SecretKeySpec(JWT_MAC_KEY_VALUE.getBytes(), "HmacSHA256");
        final Jws<Claims> jws = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(result);
        final Claims claims = jws.getPayload();
        final JwsHeader headers = jws.getHeader();
        assertThat(headers.getAlgorithm()).isEqualTo(HS_256);
        assertThat(claims.get(ISS)).isEqualTo(ISS_VALUE);
        assertThat(claims.get(ORG_UNIT_ID)).isEqualTo(ORG_UNIT_ID_VALUE);
        assertThat(claims.get(RETURN_URL)).isEqualTo(RETURN_URL_VALUE);
        assertThat(claims.get(PAYLOAD)).isEqualTo(ImmutableMap.of(ACS_URL, ISSUER_URL_VALUE, PAYLOAD, ISSUER_PAYLOAD_VALUE, TRANSACTION_ID, TRANSACTION_ID_3DS_VALUE));
        assertThat(Boolean.valueOf(String.valueOf(claims.get(OBJECTIFY_PAYLOAD)))).isTrue();
        assertThat(claims.getIssuedAt()).isEqualTo(issuedAt);
    }
}
