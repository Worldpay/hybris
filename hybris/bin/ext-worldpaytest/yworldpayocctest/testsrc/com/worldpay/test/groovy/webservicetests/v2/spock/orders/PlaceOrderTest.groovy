package com.worldpay.test.groovy.webservicetests.v2.spock.orders

import com.worldpay.test.groovy.webservicetests.config.TestConfigFactory
import com.worldpay.test.groovy.webservicetests.v2.spock.AbstractWorldpaySpockTest
import de.hybris.bootstrap.annotations.ManualTest
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class PlaceOrderTest extends AbstractWorldpaySpockTest {

	def "Customer places order with valid cart and securityCode : #format"() {
		given: "customer with payment info"
		def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient)
		def customer = customerWithPaymentInfo[0]
		def cart = customerWithPaymentInfo[2]

		when: "Placing an order"
		def orderResponse = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders',
				body: [
						'cartId'      : cart.code,
						'securityCode': '123'
				],
				contentType: format,
				requestContentType: URLENC), {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			isNotEmpty(data.order.code)
		}).data

		then: "Order is placed"
		isNotEmpty(orderResponse.order.code)
		isNotEmpty(orderResponse.order.paymentInfo)

		where:
		format << [JSON, XML]
	}

    def "Customer places order with valid cart and MISSING securityCode : #format"() {
        given: "customer with payment info"
        def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient)
        def customer = customerWithPaymentInfo[0]
        def cart = customerWithPaymentInfo[2]

        when: "Placing an order without securityCode"
        def noOrder = returningWith(restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders',
                body: [
                        'cartId': cart.code,
                ],
                contentType: format,
                requestContentType: URLENC), {
            if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
            status == SC_OK
        }).data

        then: "Order is NOT placed"
        noOrder.errors.type[0] == 'MissingServletRequestParameterError'
        noOrder.errors.message[0] == "Required String parameter 'securityCode' is not present"

        where:
        format << [JSON,XML]
    }

    def "Customer places order with valid cart and gets refused using cardHolderName : #name"() {
        given: "customer with payment info"
        def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient, name)
        def customer = customerWithPaymentInfo[0]
        def cart = customerWithPaymentInfo[2]

        when: "Placing an order without securityCode"
        def placeOrderResponse = returningWith(restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders',
                body: [
                        'cartId': cart.code,
                        'securityCode': '123'
                ],
                contentType: JSON,
                requestContentType: URLENC), {
            if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
            status == SC_OK
        }).data

        then: "Order is NOT placed and return reflects why"
        placeOrderResponse.transactionStatus == status
        placeOrderResponse.returnCode == returnCode

        where:
        name        | status    | returnCode
        'REFUSED85' | 'REFUSED' | '85'
        'REFUSED51' | 'REFUSED' | '51'
    }

    def "Customer places order with valid cart and gets gateway error using cardHolderName : #name"() {
        given: "customer with payment info"
        def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient, name)
        def customer = customerWithPaymentInfo[0]
        def cart = customerWithPaymentInfo[2]

        when: "Placing an order without securityCode"
        def placeOrderResponse = returningWith(restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders',
                body: [
                        'cartId': cart.code,
                        'securityCode': '123'
                ],
                contentType: JSON,
                requestContentType: URLENC), {
            if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
            status == SC_OK
        }).data

        then: "Order is NOT placed and error reflects why"
        placeOrderResponse.errors.type[0] == type
        placeOrderResponse.errors.message[0] == message

        where:
        name    | type            | message
        'ERROR' | 'WorldpayError' | 'There was an error in the service gateway:  [Gateway error]'
    }

	def "Place order with 3D secure needed : #format"() {
		given: "customer with payment info"
		def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient, "3D")
		def customer = customerWithPaymentInfo[0]
		def cart = customerWithPaymentInfo[2]

		when: "Placing an order"
		def orderResponse = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders',
				body: [
						'cartId'      : cart.code,
						'securityCode': '123'
				],
				contentType: format,
				requestContentType: URLENC), {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}).data

		then: "3D secure is required"
		orderResponse.threeDSecureNeeded == true
		isNotEmpty(orderResponse.threeDSecureInfo.issuerURL)
		isNotEmpty(orderResponse.threeDSecureInfo.paRequest)
		isNotEmpty(orderResponse.threeDSecureInfo.merchantData)

		where:
		format << [JSON, XML]
	}

	def "Customer places order that passes 3D secure authorization : #format"() {
		given: "customer has placed an order where 3D is needed"
		def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient, "3D")
		def customer = customerWithPaymentInfo[0]
		def cart = customerWithPaymentInfo[2]

		def placeOrderResponse = placeWorldpayOrder(restClient, customer, cart.code, '123')
		def issuerUrl = placeOrderResponse.threeDSecureInfo.issuerURL
		def md = placeOrderResponse.threeDSecureInfo.merchantData
		def paRequest = placeOrderResponse.threeDSecureInfo.paRequest

		def paRes = handleThreeDSecureInBrowser(issuerUrl, paRequest, md, 'IDENTIFIED')

		when: "handling 3D secure response"
		def order = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders/3dresponse',
				body: [
						'cartId'	  : cart.code,
						'paRes'       : paRes,
						'merchantData': md
				],
				contentType: format,
				requestContentType: URLENC), {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}).data

		then: "Order is placed"
		isNotEmpty(order.code)

		where:
		format << [XML,JSON]
	}

	def "Customer places order that fails 3D secure authorization : #authorisationResponse"() {
		given: "customer has placed an order where 3D is needed"
		def customerWithPaymentInfo = createCustomerWithPaymentInfo(restClient, "3D")
		def customer = customerWithPaymentInfo[0]
		def cart = customerWithPaymentInfo[2]

		def placeOrderResponse = placeWorldpayOrder(restClient, customer, cart.code, '123')
		def issuerUrl = placeOrderResponse.threeDSecureInfo.issuerURL
		def md = placeOrderResponse.threeDSecureInfo.merchantData
		def paRequest = placeOrderResponse.threeDSecureInfo.paRequest

		def paRes = handleThreeDSecureInBrowser(issuerUrl, paRequest, md, authorisationResponse)

		when: "handling 3D secure response"
		def reply = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/worldpayorders/3dresponse',
				body: [
						'cartId'	  : cart.code,
						'paRes'       : paRes,
						'merchantData': md
				],
				contentType: JSON,
				requestContentType: URLENC), {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}).data

		then: "Order is NOT placed and return reflects why"
		reply.errors.type[0] == type
		reply.errors.message[0] == message

		where:
		authorisationResponse | type                | message
		'UNKNOWN_IDENTITY'    | 'ThreeDSecureError' | 'Failed to handle authorisation for 3DSecure. Received REFUSED as transactionStatus'
		'CANCELLED_BY_SHOPPER'| 'ThreeDSecureError' | 'Failed to handle authorisation for 3DSecure. Received REFUSED as transactionStatus'
	}
}