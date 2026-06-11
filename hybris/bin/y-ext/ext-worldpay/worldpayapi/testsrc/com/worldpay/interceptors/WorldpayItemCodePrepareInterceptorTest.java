package com.worldpay.interceptors;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayItemCodePrepareInterceptorTest {

    private static final String TYPE = "type";
    private static final String FIELD_NAME = "field";
    private static final String GENERATED_KEY = "key";

    private WorldpayItemCodePrepareInterceptor testObj;

    @Mock
    private KeyGenerator keyGeneratorMock;
    @Mock
    private TypeService typeServiceMock;

    @Mock
    private InterceptorContext interceptorContextMock;
    @Mock
    private ItemModel itemModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private ComposedTypeModel composedTypeModelMock;

    @BeforeEach
    void setUp() {
        when(itemModelMock.getItemtype()).thenReturn(TYPE);
        when(typeServiceMock.getComposedTypeForCode(TYPE)).thenReturn(composedTypeModelMock);

        testObj = new WorldpayItemCodePrepareInterceptor(keyGeneratorMock, typeServiceMock, FIELD_NAME);
    }

    @Test
    void onPrepareShouldSetKeyIfItemHasFieldName() {
        when(typeServiceMock.hasAttribute(composedTypeModelMock, FIELD_NAME)).thenReturn(true);
        when(interceptorContextMock.getModelService()).thenReturn(modelServiceMock);
        when(modelServiceMock.getAttributeValue(itemModelMock, FIELD_NAME)).thenReturn(null);
        when(keyGeneratorMock.generate()).thenReturn(GENERATED_KEY);

        testObj.onPrepare(itemModelMock, interceptorContextMock);

        verify(modelServiceMock).setAttributeValue(itemModelMock, FIELD_NAME, GENERATED_KEY);
    }

    @Test
    void onPrepareShouldNotSetKeyIfItemDoesNotHaveFieldName() {
        testObj.onPrepare(itemModelMock, interceptorContextMock);

        verify(modelServiceMock, never()).setAttributeValue(eq(itemModelMock), anyString(), anyString());
    }

    @Test
    void onPrepareShouldNotSetKeyIfItemHasFieldAlreadySet() {
        when(typeServiceMock.hasAttribute(composedTypeModelMock, FIELD_NAME)).thenReturn(true);
        when(interceptorContextMock.getModelService()).thenReturn(modelServiceMock);
        when(modelServiceMock.getAttributeValue(itemModelMock, FIELD_NAME)).thenReturn(GENERATED_KEY);

        testObj.onPrepare(itemModelMock, interceptorContextMock);

        verify(modelServiceMock, never()).setAttributeValue(eq(itemModelMock), anyString(), anyString());
    }
}
