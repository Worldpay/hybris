package com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock

import com.worldpay.worldpayextocctests.setup.WorldpayTestSetupUtils
import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.carts.AddPaymentInfoTest
import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.cms.WorldpayAPMComponentsTest
import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.orders.PlaceOrderTest
import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.users.UserPaymentsTest
import de.hybris.bootstrap.annotations.IntegrationTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([AddPaymentInfoTest, UserPaymentsTest, PlaceOrderTest, WorldpayAPMComponentsTest])
@IntegrationTest
class AllSpockTests {

    @BeforeClass
    public static void setUpClass() {
        WorldpayTestSetupUtils.loadData();
        WorldpayTestSetupUtils.loadExtensionDataInJunit();
        WorldpayTestSetupUtils.startServer();
    }

    @AfterClass
    public static void tearDown() {
        WorldpayTestSetupUtils.stopServer();
        WorldpayTestSetupUtils.cleanData();
    }

    @Test
    public static void testing() {
        //dummy test class
    }
}
