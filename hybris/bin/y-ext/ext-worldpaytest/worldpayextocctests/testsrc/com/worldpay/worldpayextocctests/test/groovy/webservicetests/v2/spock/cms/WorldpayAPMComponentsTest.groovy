package com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.cms

import com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock.AbstractWorldpaySpockTest
import de.hybris.bootstrap.annotations.ManualTest
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class WorldpayAPMComponentsTest extends AbstractWorldpaySpockTest {

    def "Gets all available WorldpayAPMComponents for the given currency #currency, country #country and currency range #currencyRange"() {

        given: "an authorized new customer"
        def customer = registerCustomerWithTrustedClient(restClient, responseFormat)
        authorizeCustomer(restClient, customer)

        and: "a new cart"
        def cart = createCart(restClient, customer, responseFormat)
        if (currencyRange) {
            addProductToCart(restClient, customer, cart.code, '3429337', responseFormat)
        }
        def address = createAddress(restClient, customer, country, responseFormat)
        setDeliveryAddress(restClient, customer, cart.code, address)

        when: "requests to get all available WorldpayAPMComponents"
        def response = restClient.get(
            path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/cms/components/availableapmcomponents',
            query: ['fields': FIELD_SET_LEVEL_FULL,
                    'curr'  : currency],
        )

        then: "the expected WorldpayAPMComponents are getting"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
            data.apmComponents.collect { it.apmConfiguration.code } == expectedAPMs
        }

        where:
        responseFormat | currency     | country     | expectedAPMs                                                                                        | currencyRange
        JSON           | EUR_CURRENCY | DE_ISO_CODE | ['GIROPAY-SSL', 'SOFORT-SSL', 'KLARNA_PAYLATER-SSL', 'KLARNA_SLICEIT-SSL', 'SEPA_DIRECT_DEBIT-SSL'] | true
        JSON           | EUR_CURRENCY | DE_ISO_CODE | ['KLARNA_PAYLATER-SSL', 'KLARNA_SLICEIT-SSL']                                                       | false
        JSON           | EUR_CURRENCY | NL_ISO_CODE | ['IDEAL-SSL', 'KLARNA_PAYLATER-SSL', 'SEPA_DIRECT_DEBIT-SSL']                                       | true
        JSON           | GBP_CURRENCY | UK_ISO_CODE | ['KLARNA_PAYLATER-SSL', 'KLARNA_SLICEIT-SSL']                                                       | true
        JSON           | EUR_CURRENCY | AT_ISO_CODE | ['KLARNA_PAYLATER-SSL', 'KLARNA_SLICEIT-SSL']                                                       | false
    }
}
