ACC.applePay = {

    _autoload: ['setupApplePay'],

    session: null,

    setupApplePay: function () {
        const checkbox = $('#paymentMethod_applePay');

        if (checkbox.length === 0 || !window.ApplePaySession) {
            return;
        }

        ACC.applePay = Object.assign(this, window.applePaySettings);

        if (ApplePaySession.canMakePayments()) {
            this.showApplePaymentButton();
            this.deferRequestPaymentMethods();
        } else {
            this.applePayNotSupported();
        }
    },

    applePayNotSupported: function () {
        ACC.worldpay.resetPaymentFlow();

        $('#paymentMethod_applePay').hide()
            .next().hide();
    },

    enableApplePayFlow: function () {
        const originalButton = $('#worldpay-pay-button');
        let applePayButton = $('#applepay-button');

        if (applePayButton.length === 0) {
            applePayButton = originalButton.clone(false);
            applePayButton
                .attr('id', 'applepay-button')
                .on('click', this.onApplePayButtonClicked.bind(this))
                .insertBefore(originalButton);
        }

        $('#wpBillingCountrySelector')
            .hide()
            .prev().hide();
        $('#wpBillingAddress').hide();

        $('.checkout-next').hide();

        applePayButton.show();
    },

    showApplePaymentButton: function () {
        $('[for=paymentMethod_applePay]').show();
    },

    onApplePayButtonClicked: function () {
        const terms = $('#termsAndConditions');
        terms.find('.help-block').remove();
        terms.removeClass('has-error');

        if (!terms.find('input[type="checkbox"]').is(':checked')) {
            return ACC.worldpay.requireTermsAndConditions();
        }

        const request = this.paymentRequest;

        try {
            this.session = new ApplePaySession(5, request);
        } catch (err) {
            this.applePayNotSupported();

            return;
        }

        this.session.oncancel = function () {
            console.log('cancelled payment');
            ACC.worldpay.pageSpinner.end();
            this.showPaymentError();
        }.bind(this);

        this.session.onvalidatemerchant = function (event) {
            this.performAuthorizeMerchantRequest(event.validationURL)
                .then(function (merchantSession) {
                    this.session.completeMerchantValidation(merchantSession);
                }.bind(this))
                .catch(this.showPaymentError.bind(this));
        }.bind(this);

        this.session.onpaymentauthorized = function (event) {
            console.log('Authorized payment by customer', event);

            this.performAuthorizePaymentRequest(event.payment)
                .then(function (response) {
                    console.log('Authorized payment by backend', response);
                    const statusCode = response.transactionStatus === 'AUTHORISED' ?
                        ApplePaySession.STATUS_SUCCESS :
                        ApplePaySession.STATUS_FAILURE;

                    this.session.completePayment({
                        status: statusCode
                    });

                    if (statusCode === ApplePaySession.STATUS_SUCCESS) {
                        ACC.worldpay.redirectToConfirmationPage(response.orderData);
                    } else {
                        throw new Error('payment not approved');
                    }
                }.bind(this))
                .catch(this.showPaymentError.bind(this));
        }.bind(this);

        this.session.onpaymentmethodselected = function (event) {
            const paymentMethod = event.paymentMethod.type;
            console.log('onpaymentmethodselected called', event, paymentMethod);

            this.performPaymentMethodUpdate(paymentMethod)
                .then(function () {
                    return this.getOrderUpdate();
                }.bind(this))
                .then(function (update) {
                    this.session.completePaymentMethodSelection(update);
                }.bind(this))
                .catch(function (err) {
                    console.log(err, this);
                });
        }.bind(this);

        this.session.onshippingmethodselected = function (event) {
            const shippingMethod = event.shippingMethod.identifier;
            console.log('onshippingmethodselected called', event, shippingMethod);

            this.performShippingMethodUpdate(shippingMethod)
                .then(function () {
                    return this.getOrderUpdate();
                }.bind(this))
                .then(function (update) {
                    this.session.completeShippingMethodSelection(update);
                }.bind(this))
                .catch(function (error) {
                    console.log('Failed update shipping method', error);
                });
        }.bind(this);

        ACC.worldpay.pageSpinner.start();

        this.session.begin();
    },

    showPaymentError: function () {
        const container = $('.main__inner-wrapper');
        const globalAlerts = container.find('> .global-alerts').length >= 1
            ? container.find('> .global-alerts')
            : container.prepend($('<div class="global-alerts"></div>'))
                .find('> .global-alerts');

        globalAlerts.find('.alert.alert-danger').remove();

        $('<div class="alert alert-danger">' + ACC.addons.worldpayaddon['worldpayaddon.checkout.error.applepay.failed'] + '</div>')
            .prependTo(globalAlerts);
    },

    requestPaymentMethods: function () {
        return new Promise(function (resolve, reject) {
            $.getJSON(ACC.config.encodedContextPath + '/checkout/worldpay/payment/api/delivery-modes', {})
                .done(resolve)
                .fail(reject);
        });
    },

    deferRequestPaymentMethods: function () {
        this.requestPaymentMethods().then(function (deliveryModes) {
            const paymentRequest = ACC.applePay.paymentRequest;
            paymentRequest.shippingMethods = [];

            $.each(deliveryModes, function (i, deliveryMode) {
                paymentRequest.shippingMethods.push({
                    label: deliveryMode.name,
                    detail: deliveryMode.description,
                    amount: deliveryMode.deliveryCost.value,
                    identifier: deliveryMode.code
                });
            })
        });
    },

    performAuthorizeMerchantRequest: function (validationURL) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                type: 'POST',
                url: ACC.config.encodedContextPath + '/checkout/multi/worldpay/applepay/request-session',
                data: JSON.stringify({validationURL: validationURL}),
                dataType: 'json',
                contentType: 'application/json',
                success: resolve,
                error: reject
            });
        });
    },

    performAuthorizePaymentRequest: function (payment) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                type: 'POST',
                url: ACC.config.encodedContextPath + '/checkout/multi/worldpay/applepay/authorise-order',
                data: JSON.stringify(payment),
                dataType: 'json',
                contentType: 'application/json',
                success: resolve,
                error: reject,
                always: function () {
                    ACC.worldpay.pageSpinner.end()
                },
                timeout: 30000 /* call this.session.completePayment() within 30 seconds or the payment is cancelled */
            });
        });
    },

    performPaymentMethodUpdate: function (paymentMethod) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                type: 'POST',
                url: ACC.config.encodedContextPath + '/checkout/multi/worldpay/applepay/update-payment-method',
                data: JSON.stringify({paymentMethod: paymentMethod}),
                dataType: 'json',
                contentType: 'application/json',
                success: resolve,
                error: reject,
                timeout: 30000
            });
        });
    },

    performShippingMethodUpdate: function (shippingMethod) {
        return new Promise(function (resolve, reject) {
            $.post(ACC.config.encodedContextPath + '/checkout/worldpay/payment/api/delivery-modes/select/' + shippingMethod)
                .done(resolve)
                .fail(reject);
        });
    },


    performGetCart: function () {
        return new Promise(function (resolve, reject) {
            $.getJSON(ACC.config.encodedContextPath + '/checkout/worldpay/payment/api/cart/')
                .done(resolve)
                .fail(reject);
        });
    },


    performShippingAddressUpdate: function (shippingAddress) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                type: 'POST',
                url: ACC.config.encodedContextPath + '/checkout/worldpay/payment/api/delivery-address',
                data: JSON.stringify(shippingAddress),
                dataType: 'json',
                contentType: 'application/json',
                success: resolve,
                error: reject,
                timeout: 30000
            });
        });
    },

    getOrderUpdate: function () {
        return new Promise(function (resolve, reject) {
            this.performGetCart().then(function (cartData) {
                const update = {
                    "newTotal": {
                        "type": 'final',
                        "amount": "" + cartData.totalPrice.value,
                        "label": ACC.applePay.paymentRequest.total.label
                    },
                    "newLineItems": [{
                        label: ACC.addons.worldpayaddon['worldpayaddon.checkout.applepay.request.cart.subtotal'],
                        type: 'final',
                        amount: cartData.subTotal.value.toString()
                    }, {
                        label: ACC.addons.worldpayaddon['worldpayaddon.checkout.applepay.request.delivery'],
                        type: 'final',
                        amount: cartData.deliveryCost.value.toString()
                    }, {
                        label: ACC.addons.worldpayaddon['worldpayaddon.checkout.applepay.request.discount'],
                        type: 'final',
                        amount: cartData.totalDiscounts.value.toString()
                    }, {
                        label: ACC.addons.worldpayaddon['worldpayaddon.checkout.applepay.request.tax'],
                        type: 'final',
                        amount: cartData.totalTax.value.toString()
                    }]
                };

                resolve(update);
            }.bind(this)).catch(function (error) {
                reject('Failed get cart', error)
            });
        }.bind(this));
    }
};
