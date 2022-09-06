package com.worldpay.core.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.worldpay.core.dao.impl.DefaultWorldpayCartDao.PARAM_WORLD_PAY_ORDER_CODE;
import static com.worldpay.core.dao.impl.DefaultWorldpayCartDao.QUERY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCartDaoTest {

    public static final String ORDER_CODE = "orderCode";
    @InjectMocks
    private DefaultWorldpayCartDao testObj;

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Captor
    private ArgumentCaptor<FlexibleSearchQuery> queryArgumentCaptor;

    @Test
    public void testGetCartsByWorldpayOrderCode() throws Exception {
        when(flexibleSearchServiceMock.searchUnique(queryArgumentCaptor.capture())).thenReturn(cartModelMock);

        final CartModel result = testObj.findCartByWorldpayOrderCode(ORDER_CODE);

        assertSame(cartModelMock, result);

        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();
        assertEquals(QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(ORDER_CODE, queryArgumentCaptorValue.getQueryParameters().get(PARAM_WORLD_PAY_ORDER_CODE));
    }
}
