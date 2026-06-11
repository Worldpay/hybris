package com.worldpay.service.impl;

import com.worldpay.core.dao.WorldpayIntegrationVersionDao;
import com.worldpay.model.IntegrationVersionModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayIntegrationVersionServiceTest {

    private static final String VERSION_2 = "2.0";
    private static final String VERSION_1 = "1.0";
    private static final String VERSION_3 = "3.0";

    @Spy
    @InjectMocks
    private DefaultWorldpayIntegrationVersionService testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock
    private WorldpayIntegrationVersionDao worldpayIntegrationVersionDao;
    @Mock
    private Configuration configurationMock;

    @Mock
    private IntegrationVersionModel integrationVersionModelMock;


    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
    }

    @Test
    public void recordCurrentIntegrationVersion_shouldNotSave_whenVersionIsBlank() {
        when(configurationMock.getString(anyString())).thenReturn(StringUtils.EMPTY);
        testObj.recordCurrentIntegrationVersion();
        verify(modelServiceMock, never()).save(any());
    }

    @Test
    public void recordCurrentIntegrationVersion_shouldSave_whenVersionIsNew() {
        when(configurationMock.getString(anyString())).thenReturn(VERSION_2);
        final IntegrationVersionModel model = new IntegrationVersionModel();
        when(modelServiceMock.create(IntegrationVersionModel.class)).thenReturn(model);

        testObj.recordCurrentIntegrationVersion();

        verify(modelServiceMock).save(model);
        assertEquals(VERSION_2, model.getVersionNumber());
        assertNotNull(model.getDate());
    }

    @Test
    public void getLastThreeIntegrationVersions_shouldReturnEmpty_whenNoVersions() {
        when(worldpayIntegrationVersionDao.findLastThreeVersions()).thenReturn(Collections.emptyList());
        final String result = testObj.getPreviousThreeIntegrationVersions();
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void getLastThreeIntegrationVersions_shouldReturnEmpty_whenOnlyOneVersion() {
        final IntegrationVersionModel model = new IntegrationVersionModel();
        model.setVersionNumber(VERSION_1);
        when(worldpayIntegrationVersionDao.findLastThreeVersions()).thenReturn(Collections.singletonList(model));
        final String result = testObj.getPreviousThreeIntegrationVersions();
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void getLastThreeIntegrationVersions_shouldReturnJoinedVersions_whenMultipleVersions() {
        final IntegrationVersionModel m1 = new IntegrationVersionModel();
        m1.setVersionNumber(VERSION_1);
        final IntegrationVersionModel m2 = new IntegrationVersionModel();
        m2.setVersionNumber(VERSION_2);
        final IntegrationVersionModel m3 = new IntegrationVersionModel();
        m3.setVersionNumber(VERSION_3);
        final List<IntegrationVersionModel> models = Arrays.asList(m1, m2, m3);
        when(worldpayIntegrationVersionDao.findLastThreeVersions()).thenReturn(models);
        final String result = testObj.getPreviousThreeIntegrationVersions();
        assertEquals("1.0,2.0,3.0", result);
    }

    @Test
    public void getIntegrationVersionByNumber_shouldReturnModel_whenFound() {
        final IntegrationVersionModel model = new IntegrationVersionModel();
        when(worldpayIntegrationVersionDao.findByVersionNumber(VERSION_1)).thenReturn(model);
        assertEquals(model, testObj.getIntegrationVersionByNumber(VERSION_1));
    }

    @Test
    public void getIntegrationVersionByNumber_shouldReturnNull_whenNotFound() {
        when(worldpayIntegrationVersionDao.findByVersionNumber(VERSION_1)).thenReturn(null);
        assertNull(testObj.getIntegrationVersionByNumber(VERSION_1));
    }

    @Test
    public void getIntegrationVersionByNumber_shouldCallRecordCurrentIntegrationVersionAndReturnModel_whenNotFoundAndPropertyExists() {
        doThrow(new ModelNotFoundException(VERSION_1)).when(worldpayIntegrationVersionDao).findByVersionNumber(VERSION_1);
        doReturn(integrationVersionModelMock).when(testObj).recordCurrentIntegrationVersion();
        final IntegrationVersionModel result = testObj.getIntegrationVersionByNumber(VERSION_1);
        verify(testObj).recordCurrentIntegrationVersion();
        assertEquals(integrationVersionModelMock, result);
    }

    @Test
    public void getIntegrationVersionByNumber_shouldCallRecordCurrentIntegrationVersionAndReturnModel_whenNotFoundAndNotPropertyExists() {
        doThrow(new ModelNotFoundException(VERSION_1)).when(worldpayIntegrationVersionDao).findByVersionNumber(VERSION_1);
        doReturn(null).when(testObj).recordCurrentIntegrationVersion();
        final IntegrationVersionModel result = testObj.getIntegrationVersionByNumber(VERSION_1);
        verify(testObj).recordCurrentIntegrationVersion();
        assertNull(result);
    }

    @Test
    public void getIntegrationVersionByNumber_shouldReturnNull_whenThrowAmbiguousIdentifierException() {
        doThrow(new AmbiguousIdentifierException(VERSION_1)).when(worldpayIntegrationVersionDao).findByVersionNumber(VERSION_1);
        final IntegrationVersionModel result = testObj.getIntegrationVersionByNumber(VERSION_1);
        assertNull(result);
    }

    @Test
    public void getCurrentIntegrationVersionValue_shouldReturnEmpty_whenNotFound() {
        when(configurationMock.getString(anyString())).thenReturn(VERSION_1);
        when(worldpayIntegrationVersionDao.findByVersionNumber(VERSION_1)).thenReturn(null);
        final String result = testObj.getCurrentIntegrationVersionValue();
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void getCurrentIntegrationVersionValue_shouldReturnVersion_whenFound() {
        when(configurationMock.getString(anyString())).thenReturn(VERSION_2);
        final IntegrationVersionModel model = new IntegrationVersionModel();
        model.setVersionNumber(VERSION_2);
        when(worldpayIntegrationVersionDao.findByVersionNumber(VERSION_2)).thenReturn(model);
        final String result = testObj.getCurrentIntegrationVersionValue();
        assertEquals(VERSION_2, result);
    }

}
