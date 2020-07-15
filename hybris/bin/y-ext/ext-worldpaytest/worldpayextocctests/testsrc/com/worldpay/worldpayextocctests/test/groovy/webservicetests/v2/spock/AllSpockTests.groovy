package com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock

import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.carts.AddPaymentInfoTest
import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.orders.PlaceOrderTest
import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.users.UserPaymentsTest
import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([AddPaymentInfoTest, UserPaymentsTest, PlaceOrderTest])
@IntegrationTest
class AllSpockTests {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AllSpockTests.class)

    @BeforeClass
    public static void setUpClass() {
        TestSetupUtils.loadData();
        TestSetupUtils.startServer();
    }

    @AfterClass
    public static void tearDown() {
        TestSetupUtils.stopServer();
        TestSetupUtils.cleanData();
    }

    @Test
    public static void testing() {
        //dummy test class
    }
}
