package com.worldpay.test.groovy.webservicetests.v2.spock.users

import com.worldpay.test.groovy.webservicetests.config.TestConfigFactory
import com.worldpay.test.groovy.webservicetests.v2.spock.AbstractWorldpaySpockTest
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class UserPaymentsTest extends AbstractWorldpaySpockTest {

    def "User gets his payment info : #format"() {
        given: "user with payment info"
        def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient)
        def customer = customerWithPaymentInfo[0]
        def info = customerWithPaymentInfo[1]

        when: "user attempts to retrieve his info"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/users/' + customer.id + '/paymentdetails/' + info.id,
                query: ["fields": FIELD_SET_LEVEL_FULL],
                contentType: format,
                requestContentType: URLENC)
        then: "he is able to do so"
        with(response) {
            status == SC_OK
            data.accountHolderName == "Sven Johnson"
        }
        where:
        format << [JSON,XML]
    }

}
