package com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.users

import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.AbstractWorldpaySpockTest
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static org.apache.http.entity.ContentType.*

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
                requestContentType: org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED)
        then: "he is able to do so"
        with(response) {
            status == org.apache.http.HttpStatus.SC_OK
            data.accountHolderName == "Sven Johnson"
        }
        where:
        format << [org.apache.http.entity.ContentType.APPLICATION_JSON, org.apache.http.entity.ContentType.APPLICATION_XML]
    }

}
