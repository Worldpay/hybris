package com.worldpay.test.groovy.webservicetests.v2.spock.carts

import com.worldpay.test.groovy.webservicetests.v2.spock.AbstractWorldpaySpockTest
import de.hybris.bootstrap.annotations.ManualTest
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static java.time.LocalDate.now
import static org.apache.http.HttpStatus.SC_CREATED

@ManualTest
@Unroll
class AddPaymentInfoTest extends AbstractWorldpaySpockTest {

    protected static final WP_DEFAULT_PAYMENT = [
            "accountHolderName"             : "Sven Johnson",
            "cardType"                      : "visa",
            "expiryMonth"                   : "04",
            "expiryYear"                    : "2117",
            "defaultPaymentInfo"            : true,
            "saved"                         : true,
            "billingAddress.titleCode"      : "Dr",
            "billingAddress.firstName"      : "Sven",
            "billingAddress.lastName"       : "Johnson",
            "billingAddress.line1"          : "Vestergade 1000",
            "billingAddress.line2"          : "test2",
            "billingAddress.postalCode"     : "8000",
            "billingAddress.town"           : "Aarhus",
            "billingAddress.country.isocode": "DK"
    ]
    protected static
    final String WP_DEFAULT_PAYMENT_JSON = "{\"accountHolderName\" : \"Sven Johnson\", \"cseToken\" : \"CSE_TOKEN\", \"cardType\" : {\"code\":\"visa\"}, \"expiryMonth\" : \"04\", \"expiryYear\" : \"2117\", \"defaultPayment\" : true, \"saved\" : true,\"billingAddress\" : { \"titleCode\" : \"Dr\", \"firstName\" : \"Sven\", \"lastName\" : \"Johnson\", \"line1\" : \"Vestergade 1000\", \"line2\" : \"test2\", \"postalCode\" : \"8000\", \"town\" : \"Aarhus\",\"country\":{\"isocode\" : \"DK\"}}}";
    protected static
    final String WP_DEFAULT_PAYMENT_XML = "<paymentDetails><accountHolderName>Sven Johnson</accountHolderName><cseToken>CSE_TOKEN</cseToken><cardType><code>visa</code></cardType><expiryMonth>4</expiryMonth><expiryYear>2117</expiryYear><defaultPayment>true</defaultPayment><saved>true</saved><billingAddress><firstName>Sven</firstName><lastName>Johnson</lastName><titleCode>Dr</titleCode><country><isocode>DK</isocode></country><postalCode>8000</postalCode><town>Aarhus</town><line1>Vestergade 1000</line1><line2>test2</line2></billingAddress></paymentDetails>"

    def "User adds CSE payment info : #requestFormat, #responseFormat"() {
        given: "an authorized new customer"
        def customer = registerCustomerWithTrustedClient(restClient, responseFormat)
        authorizeCustomer(restClient, customer)

        and: "a new cart"
        def cart = createCart(restClient, customer, responseFormat)

        and: "Obtained encrypted credit card token"
        def year = String.valueOf(now().plusYears(2).getYear())
        String cseToken = getCseToken("123", "Sven Johnson", "4111111111111111", "04", year)

        if (postBody instanceof String) {
            postBody = postBody.replace("CSE_TOKEN", cseToken)
        } else {
            postBody.put("cseToken", cseToken)
        }

        when: "add payment details"
        def paymentDetails = returningWith(restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/worldpaypaymentdetails',
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat), {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
        }).data

        then: "details are added"
        paymentDetails.accountHolderName == 'Sven Johnson'
        isNotEmpty(paymentDetails.cardType)
        isNotEmpty(paymentDetails.subscriptionId)
        paymentDetails.cardType.code == 'visa'

        where:
        requestFormat | responseFormat | postBody
        URLENC        | XML            | WP_DEFAULT_PAYMENT
        URLENC        | JSON           | WP_DEFAULT_PAYMENT
        JSON          | JSON           | WP_DEFAULT_PAYMENT_JSON
        XML           | XML            | WP_DEFAULT_PAYMENT_XML
    }
}
