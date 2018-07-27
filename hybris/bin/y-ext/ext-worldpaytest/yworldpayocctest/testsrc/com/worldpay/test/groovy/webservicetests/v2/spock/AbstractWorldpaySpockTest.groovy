package com.worldpay.test.groovy.webservicetests.v2.spock

import geb.Browser
import groovy.json.JsonSlurper
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.firefox.FirefoxDriver

import static java.time.LocalDate.now
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED
import static org.apache.http.entity.ContentType.APPLICATION_JSON
import static org.openqa.selenium.By.className

class AbstractWorldpaySpockTest extends AbstractSpockFlowTest {

    protected createCustomerWithPaymentInfo(RESTClient client, accountHolderName = "Sven Johnson", format = APPLICATION_JSON) {
        def customer = registerCustomerWithTrustedClient(client, format)
        authorizeCustomer(client, customer)

        def cart = createCart(client, customer, format)
        def address = createAddress(client, customer, format)
        addProductToCart(client, customer, cart.code, '3429337')
        setDeliveryAddress(client, customer, cart.code, address)
        setDeliveryMode(client, customer, cart.code, 'standard-gross')

        def expiryMonth = "04"
        def expiryYear = String.valueOf(now().plusYears(2).getYear())
        def cseToken = getCseToken("123", accountHolderName, "4111111111111111", expiryMonth, expiryYear)
        def paymentBody = getPaymentBody(accountHolderName, "Sven", "Johnson", cseToken, expiryMonth, expiryYear)
        def info = createWorldpayPaymentInfo(client, customer, cart.code, paymentBody, format)

        return [customer, info, cart]
    }

    protected registerCustomerWithTrustedClient(RESTClient client, format, basePathWithSite = getBasePathWithSite()) {
        authorizeTrustedClient(client)
        def customer = registerCustomer(client, format, basePathWithSite)
        return customer
    }

    protected addProductToCart(RESTClient client, customer, cartId, productId, format = APPLICATION_JSON) {
        HttpResponseDecorator response = client.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartId + '/entries',
                body: [
                        'code': productId
                ],
                contentType: format,
                requestContentType: APPLICATION_FORM_URLENCODED)
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
            data.quantityAdded == 1
            isNotEmpty(data.entry)
            data.entry.entryNumber == 0
        }
    }

    protected setDeliveryAddress(RESTClient client, customer, cartId, address, format = APPLICATION_JSON) {
        HttpResponseDecorator response = client.put(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartId + '/addresses/delivery',
                body: [
                        'addressId': address.id,
                ],
                contentType: format,
                requestContentType: APPLICATION_FORM_URLENCODED)
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
        }
    }

    protected setDeliveryMode(RESTClient client, customer, cartId, deliveryMode, format = APPLICATION_JSON) {
        HttpResponseDecorator response = client.put(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartId + '/deliverymode',
                body: [
                        'deliveryModeId': deliveryMode,
                ],
                contentType: format,
                requestContentType: APPLICATION_FORM_URLENCODED)
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
        }
    }

    protected createWorldpayPaymentInfo(RESTClient client, customer, cartId, body, format = APPLICATION_JSON) {
        HttpResponseDecorator response = client.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartId + '/worldpaypaymentdetails',
                body: body,
                contentType: format,
                requestContentType: APPLICATION_JSON)
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
        }
        return response.data
    }

    protected placeWorldpayOrder(RESTClient client, customer, cartId, securityCode, format = APPLICATION_JSON) {
        HttpResponseDecorator response = client.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders',
                body: [
                        'cartId'      : cartId,
                        'securityCode': securityCode
                ],
                contentType: format,
                requestContentType: APPLICATION_FORM_URLENCODED)
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
        }
        return response.data
    }

    // CSE utility methods

    protected getCseToken(cvc, cardHolderName, cardNumber, expiryMonth, expiryYear) {
        def cseToken

        def browser = new Browser(driver: new FirefoxDriver())

        browser.go "file://" + (String) config.HTML_PATH + "/cseTest.html"

        cseToken = browser.js.generateCseToken("1#10001#c745fe13416ffc5f9283f47f7b18e58a55a1e152d873cf7e31cd87e04dda905570b53bd6996c54d2f90a7ade6e65ba45853617472b1ad78d02f0bd9183af22d8dd6002a7857d0c4f5c102bd29864ae9b5b2caf3ef22932a7b2c6f00f819f6ac92905d9662d0905526f0a99160e49dd613b07212fb9429535a28b54a087fc3541a8fc214e46a07ebacab0f5b6a60331cd66168548c097c716df09332d95faf3d9717107a5db5ce553406688a368d6d44f79eb4c3366068e7c4dbe1f1987ef6ac54bc4e1195021ceac831141553986db5a5b8206abc0e0b36ed4adf31ae692829057dbb0c99270825335405e816f40fe3a3051c323695e52bf97fccda813c45a31",
                cvc,
                cardHolderName,
                cardNumber,
                expiryMonth,
                expiryYear)

        try {
            browser.close()
        }
        catch (UnhandledAlertException e) {

        }

        return cseToken
    }

    protected getPaymentBody(accountHolderName, firstName, lastName, cseToken, expiryMonth, expiryYear) {
        def jsonSlurper = new JsonSlurper()
        def body = jsonSlurper.parseText('''{
            "accountHolderName": "''' + accountHolderName + '''",
            "cseToken": "''' + cseToken + '''",
            "cardType":{
                "code":"visa"
            },
            "expiryMonth": "''' + expiryMonth + '''",
            "expiryYear": "''' + expiryYear + '''",
            "billingAddress":{
                "titleCode":"Dr",
                "firstName":"''' + firstName + '''",
                "lastName":"''' + lastName + '''",
                "line1":"Vestergade 1000",
                "postalCode":"8000",
                "town":"Aarhus",
                "country":{
                    "isocode":"DK"
                }
            }
        }''')
        return body
    }

    // 3D security utility methods

    protected handleThreeDSecureInBrowser(issuerUrl, paRequest, merchantData, authorisationResponse) {

        def browser = new Browser(driver: new FirefoxDriver())

        def termUrl = getDefaultHttpsUri() + "/worldpayresponsemock/3dresponse"
        def autoSubmitUrl = "file://" + (String) config.HTML_PATH + "/threeDSecureTest.html?" +
                "IssuerUrl=" + URLEncoder.encode(issuerUrl, "UTF-8") +
                "&PaReq=" + URLEncoder.encode(paRequest, "UTF-8") +
                "&MD=" + URLEncoder.encode(merchantData, "UTF-8") +
                "&TermUrl=" + URLEncoder.encode(termUrl, "UTF-8")

        browser.go autoSubmitUrl

        // The threeDSecureTest.html page auto submits and forwards to the
        // worldpay 3D simulator page (the issuer url)
        browser.$("form").paResMagicValues = authorisationResponse

        // On the worldpay 3D simulator we select the given authorisationResponse and click the button
        browser.getPage().$(className("lefty")).click()

        // We are now on a mock endpoint in the worldpayresponsemock extension which collect the Pa response
        def paRes = browser.getPage().$(className("PaRes")).value()
        try {
            browser.close()
        }
        catch (UnhandledAlertException e) {

        }
        return paRes
    }


}
