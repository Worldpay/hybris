package com.worldpay.test.groovy.webservicetests.v2.spock

import com.worldpay.test.groovy.webservicetests.v2.spock.carts.AddPaymentInfoTest
import com.worldpay.test.groovy.webservicetests.v2.spock.orders.PlaceOrderTest
import com.worldpay.test.groovy.webservicetests.v2.spock.users.UserPaymentsTest
import de.hybris.bootstrap.annotations.IntegrationTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([ AddPaymentInfoTest, UserPaymentsTest, PlaceOrderTest ])
@IntegrationTest
class AllSpockTests {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AllSpockTests.class)

	@Test
	public static void testing() {
		//dummy test class
	}
}
