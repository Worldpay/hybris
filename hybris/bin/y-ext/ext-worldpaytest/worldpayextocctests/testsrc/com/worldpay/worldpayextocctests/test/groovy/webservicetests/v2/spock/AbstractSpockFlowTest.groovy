package com.worldpay.worldpayextocctests.test.groovy.webservicetests.v2.spock

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED
import static org.apache.http.entity.ContentType.APPLICATION_JSON

abstract class AbstractSpockFlowTest extends AbstractSpockTest {

    protected static final String CUSTOMER_USERNAME = 'democustomer'
    protected static final String CUSTOMER_PASSWORD_STRONG = 'PAss1234!'
    protected static final String CUSTOMER_TITLE_CODE = 'dr'
    protected static final String CUSTOMER_FIRST_NAME = 'Sven'
    protected static final String CUSTOMER_LAST_NAME = 'Haiges'
    protected static final String CUSTOMER_ADDRESS_LINE1 = 'Nymphenburger Str. 86 - Maillingerstrasse'
    protected static final String CUSTOMER_ADDRESS_LINE2 = 'floor 2'
    protected static final String CUSTOMER_ADDRESS_POSTAL_CODE = '80331'
    protected static final String CUSTOMER_ADDRESS_TOWN = 'Muenchen'
    protected static final String CUSTOMER_ADDRESS_COUNTRY_ISO_CODE = 'DE'

    protected static final String FIELD_SET_LEVEL_FULL = "FULL"

    /**
     * This method registers customer without any kind of authorization beforehand.
     * @param client REST client to be used
     * @param format format to be used, defaults to JSON, does not need to be provided
     * @return map containing id and password of registered user to be used later on
     */
    def registerCustomer(RESTClient client, format = APPLICATION_JSON, basePathWithSite = getBasePathWithSite()) {
        def username = System.currentTimeMillis() + '@sven.de'
        def password = CUSTOMER_PASSWORD_STRONG

        HttpResponseDecorator response = client.post(
            path: basePathWithSite + '/users',
            body: [
                'login'    : username,
                'password' : password,
                'titleCode': CUSTOMER_TITLE_CODE,
                'firstName': CUSTOMER_FIRST_NAME,
                'lastName' : CUSTOMER_LAST_NAME
            ],
            contentType: format,
            requestContentType: APPLICATION_FORM_URLENCODED)

        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
        }
        return [
            'id'      : username,
            'password': password
        ]
    }
    /**
     * This method creates user with given data.
     * @param customer A map representing the customer
     * @param client REST client to use
     * @param format Data format to be used, defaults to JSON, does not need to be provided
     */
    def registerCustomer(customer, RESTClient client, format = APPLICATION_JSON, basePathWithSite = getBasePathWithSite()) {
        HttpResponseDecorator response = client.post(
            path: basePathWithSite + '/users',
            body: [
                'login'    : customer.login,
                'password' : customer.password,
                'titleCode': customer.titleCode,
                'firstName': customer.firstName,
                'lastName' : customer.lastName,
                'currency' : customer.currency,
                'language' : customer.language
            ],
            contentType: format,
            requestContentType: APPLICATION_FORM_URLENCODED)

        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
        }
    }

    /**
     *
     * @param client REST client to use
     * @param user
     * @param format Data format to be used, defaults to JSON, does not need to be provided
     * @return
     */
    def createAddress(RESTClient client, user, format = APPLICATION_JSON, basePathWithSite = getBasePathWithSite()) {
        HttpResponseDecorator response = client.post(
            path: basePathWithSite + '/users/' + user.id + '/addresses',
            body: [
                'titleCode'      : CUSTOMER_TITLE_CODE,
                'firstName'      : CUSTOMER_FIRST_NAME,
                'lastName'       : CUSTOMER_LAST_NAME,
                'line1'          : CUSTOMER_ADDRESS_LINE1,
                'line2'          : CUSTOMER_ADDRESS_LINE2,
                'postalCode'     : CUSTOMER_ADDRESS_POSTAL_CODE,
                'town'           : CUSTOMER_ADDRESS_TOWN,
                'country.isocode': CUSTOMER_ADDRESS_COUNTRY_ISO_CODE,
                'fields'         : FIELD_SET_LEVEL_FULL
            ],
            contentType: format,
            requestContentType: APPLICATION_FORM_URLENCODED)

        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
        }

        return response.data
    }

    /**
     * This method authorizes customer. If null (default) is provided as customer, it will authorize default customer.
     * @param customer customer to be authorized, defaults to null
     * @param client REST client to use
     */
    protected void authorizeCustomer(RESTClient client, customer = null) {
        def username = CUSTOMER_USERNAME
        def password = CUSTOMER_PASSWORD_STRONG

        if (customer) {
            username = customer.id
            password = customer.password
        }

        def token = getOAuth2TokenUsingPassword(client, getClientId(), getClientSecret(), username, password)
        addAuthorization(client, token)
    }

    /**
     * This method creates a cart
     * @param client REST client to use
     * @param customer customer for whom cart should be created
     * @param format data format to be used
     * @return created cart
     */
    protected createCart(RESTClient client, customer, format, basePathWithSite = getBasePathWithSite()) {
        def cart = returningWith(client.post(
            path: basePathWithSite + '/users/' + customer.id + '/carts',
            query: ['fields': FIELD_SET_LEVEL_FULL],
            contentType: format,
            requestContentType: APPLICATION_FORM_URLENCODED), {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
        }).data
        return cart
    }
}
