package com.worldpay.test.groovy.webservicetests.v2.spock

import com.worldpay.test.groovy.webservicetests.v2.spock.carts.AddPaymentInfoTest
import com.worldpay.test.groovy.webservicetests.v2.spock.orders.PlaceOrderTest
import com.worldpay.test.groovy.webservicetests.v2.spock.users.UserPaymentsTest
import de.hybris.bootstrap.annotations.IntegrationTest
import org.apache.log4j.Logger
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([AddPaymentInfoTest, UserPaymentsTest, PlaceOrderTest])
@IntegrationTest
class AllSpockTests {

    private static final Logger LOG = Logger.getLogger(AllSpockTests.class)

    @Test
    static void testing() {
        //dummy test class
    }
}
