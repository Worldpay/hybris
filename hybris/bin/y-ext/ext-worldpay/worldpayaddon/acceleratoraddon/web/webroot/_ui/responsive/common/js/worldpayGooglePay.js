ACC.googlePay = {
    _autoload: [
        "setupGooglePay"
    ],

    paymentClient: null,

    baseRequest: {
        apiVersion: 2,
        apiVersionMinor: 0
    },

    setupGooglePay: function () {
        const checkbox = $('#paymentMethod_googlePay');

        if (checkbox.length === 0) {
            return;
        }

        $.getScript('https://pay.google.com/gp/p/js/pay.js')
            .done(this.onGooglePayLoaded.bind(this, window.googlePaySettings))
            .fail(this.showUseDeliveryAddress.bind(this, true));
    },

    getCardPaymentMethod: function () {
        return Object.assign({
                tokenizationSpecification: {
                    type: 'PAYMENT_GATEWAY',
                    parameters: {
                        gateway: this.gateway,
                        gatewayMerchantId: this.gatewayMerchantId
                    }
                }
            },
            this.baseCardPaymentMethod
        );
    },

    getGoogleIsReadyToPayRequest: function () {
        return Object.assign(
            this.baseRequest,
            {
                allowedPaymentMethods: [this.baseCardPaymentMethod]
            }
        );
    },

    getGooglePaymentDataRequest: function () {
        return Object.assign(this.baseRequest, {
            allowedPaymentMethods: [this.getCardPaymentMethod()],
            merchantInfo: {
                merchantName: this.merchantName,
                merchantId: this.merchantId
            },
            transactionInfo: this.transactionInfo
        });
    },

    getGooglePaymentsClient: function () {
        return this.paymentsClient || (this.paymentsClient = new google.payments.api.PaymentsClient(this.clientSettings));
    },

    enableGooglePayFlow: function () {
        const originalButton = $('#worldpay-pay-button');
        let googlePayButton = $('#googlepay-button');

        if (googlePayButton.length === 0) {
            googlePayButton = originalButton.clone(false);
            googlePayButton
                .attr('id', 'googlepay-button')
                .on('click', this.onGooglePaymentButtonClicked.bind(this))
                .insertBefore(originalButton);
        }

        $('#wpBillingCountrySelector')
            .hide()
            .prev().hide();
        $('#wpBillingAddress').hide();

        $('.checkout-next').hide();

        googlePayButton.show();
    },

    onGooglePayLoaded: function (settings) {
        ACC.googlePay = Object.assign(this, settings);

        const paymentsClient = this.getGooglePaymentsClient();
        const isReadyToPayRequest = this.getGoogleIsReadyToPayRequest();

        new Promise(
            function(resolve, reject) {
                paymentsClient.isReadyToPay(isReadyToPayRequest)
                    .then(function (response) {
                        if (response.result) {
                            resolve();
                        } else {reject();}
                    })
                    .catch(function () {
                        reject();
                    });
                }
        )
        .then(function() {
            $('[for=paymentMethod_googlePay]').show();
        })
        .catch(function() {
            $('#paymentMethod_googlePay').hide()
                .next().hide();
        });
    },

    loadPayment: function () {
        const paymentsClient = this.getGooglePaymentsClient();
        const paymentDataRequest = this.getGooglePaymentDataRequest();

        paymentsClient.loadPaymentData(paymentDataRequest)
            .then(function (paymentData) {
                this.processPayment(paymentData);
            }.bind(this))
            .catch(this.handlePaymentFailure.bind(this));
    },

    onGooglePaymentButtonClicked: function (event) {
        event.stopPropagation();
        event.preventDefault();

        const terms = $('#termsAndConditions');
        terms.find('.help-block').remove();
        terms.removeClass('has-error');

        if (!terms.find('input[type="checkbox"]').is(':checked')) {
            return ACC.worldpay.requireTermsAndConditions();
        }

        this.loadPayment();
    },

    showUseDeliveryAddress: function (showOrHide) {
        const checkbox = $('#wpUseDeliveryAddress').closest('.checkbox');
        const countrySelector = $('#wpBillingCountrySelector');
        const billingAddress = $('#wpBillingCountrySelector');

        if (showOrHide) {
            checkbox.show();
            countrySelector.show();
            billingAddress.show();
        } else {
            checkbox.hide();
            countrySelector.hide();
            billingAddress.hide();
        }
    },

    showPaymentError: function () {
        const container = $('.main__inner-wrapper');
        const globalAlerts = container.find('> .global-alerts').length >= 1
            ? container.find('> .global-alerts')
            : container.prepend($('<div class="global-alerts"></div>'))
                .find('> .global-alerts');

        globalAlerts.find('.alert.alert-danger').remove();

        $('<div class="alert alert-danger">' + ACC.addons.worldpayaddon['worldpayaddon.checkout.error.googlepay.failed'] + '</div>')
            .prependTo(globalAlerts);
    },

    processPayment: function (paymentData) {
        const paymentMethodData = paymentData.paymentMethodData;
        const billingAddress = paymentMethodData.info.billingAddress;
        const token = JSON.parse(paymentMethodData.tokenizationData.token);

        this.performAuthorizePaymentRequest({billingAddress: billingAddress, token: token})
            .then(function(order) {
                ACC.worldpay.redirectToConfirmationPage(order);
            })
            .catch(function() {
                this.showPaymentError();
            }.bind(this));
    },

    performAuthorizePaymentRequest: function(data) {
        return new Promise(function(resolve, reject) {
            $.ajax({
                type: 'POST',
                url: ACC.config.encodedContextPath + '/checkout/multi/worldpay/googlepay/authorise-order',
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.transactionStatus === 'AUTHORISED') {
                        resolve(response.orderData);
                    } else {
                        reject();
                    }
                }.bind(this),
                error: function() {
                    reject();
                },
                beforeSend: function(){
                    ACC.worldpay.pageSpinner.start()
                },
                always: function(){
                    ACC.worldpay.pageSpinner.end()
                },
                dataType: 'json',
                contentType: 'application/json'
            });
        });
    },

    handlePaymentFailure: function () {
        this.showUseDeliveryAddress(true);
        this.showPaymentError();
    }
};
