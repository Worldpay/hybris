ACC.worldpay = {

    _autoload: [
        "bindUseDeliveryAddress",
        "bindCountrySelector",
        "bindCreditCardAddressForm",
        "populateDeclineCodeTimeout",
        "hideOrShowSaveDetails",
        "bindBanks",
        "bindACH",
        ["bindDOBInput", ACC.isFSEnabled === 'true'],
        "checkPreviouslySelectedPaymentMethod",
        "onPaymentMethodChange"
    ],

    spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

    pageSpinner: {
        createSpinner: function () {
            const prefix = ACC.config.commonResourcePath.replace('/_ui/', '/_ui/addons/worldpayaddon/');
            return $('<img style="width: 32px; height: 32px; margin: auto;" src="' + prefix + '/images/oval-spinner.svg" />')
        },

        start: function () {
            this.overlay = $('<div id="cboxOverlay" style="opacity: 0.7; cursor: pointer; visibility: visible; display: flex;" />');
            this.createSpinner().appendTo(ACC.worldpay.pageSpinner.overlay);
            this.overlay.appendTo($('body'));
        },

        end: function () {
            this.overlay.fadeOut(400, function () {
                $(this).remove();
            });
        }
    },

    bindCountrySelector: function () {
        $('select[id^="billingAddress\\.country"]').on("change", function () {
            $('#wpUseDeliveryAddress').prop('checked', false);
        });
    },

    bindUseDeliveryAddress: function () {
        const wpUseDeliveryAddress = $("#wpUseDeliveryAddress");

        wpUseDeliveryAddress.on("change", function () {
            if (wpUseDeliveryAddress.is(":checked")) {
                const options = {
                    'countryIsoCode': $('#wpUseDeliveryAddress').data("countryisocode"),
                    'useDeliveryAddress': true
                };
                ACC.worldpay.enableAddressForm();
                ACC.worldpay.displayCreditCardAddressForm(options, ACC.worldpay.useDeliveryAddressSelected);
                ACC.worldpay.disableAddressForm();
            } else {
                ACC.worldpay.clearAddressForm();
                ACC.worldpay.enableAddressForm();
            }
        });
        if (wpUseDeliveryAddress.is(":checked")) {
            ACC.worldpay.disableAddressForm();
        }
    },

    disableAddressForm: function () {
        $('input[id^="billingAddress\\."]').prop('disabled', true);
        $('select[id^="billingAddress\\."]').prop('disabled', true);
    },

    enableAddressForm: function () {
        $('input[id^="billingAddress\\."]').prop('disabled', false);
        $('select[id^="billingAddress\\."]').prop('disabled', false);
    },

    clearAddressForm: function () {
        $('input[id^="billingAddress\\."]').val("");
        $('select[id^="billingAddress\\."]').val("");
    },

    useDeliveryAddressSelected: function () {
        const wpUseDeliveryAddress = $("#wpUseDeliveryAddress");
        if (wpUseDeliveryAddress.is(":checked")) {
            $('select[id^="billingAddress\\.country"]').val(wpUseDeliveryAddress.data('countryisocode'));
            ACC.worldpay.disableAddressForm();
        } else {
            ACC.worldpay.clearAddressForm();
            ACC.worldpay.enableAddressForm();
        }
    },

    bindCreditCardAddressForm: function () {
        $("#wpBillingCountrySelector").find(":input").on("change", function () {
            const countrySelection = $(this).val();
            const options = {
                'countryIsoCode': countrySelection,
                'useDeliveryAddress': false
            };
            ACC.worldpay.displayCreditCardAddressForm(options);
        });
    },

    displayCreditCardAddressForm: function (options, callback) {
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/multi/worldpay/billingaddressform',
            data: options,
            dataType: "html",
            beforeSend: function () {
                $("#wpBillingAddress").html(ACC.worldpay.spinner);
            }
        }).done(function (data) {
            $("#wpBillingAddress").html($(data).html());
            if (typeof callback === 'function') {
                callback();
            }
        });
    },

    populateDeclineCodeTimeout: function () {
        if (ACC.paymentStatus === "REFUSED") {
            const waitTimer = ACC.worldpayDeclineMessageWaitTimerSeconds * 1000;
            setTimeout(function () {
                populateDeclineCode();
            }, waitTimer);

            function populateDeclineCode() {
                $.ajax({
                    url: ACC.config.encodedContextPath + "/checkout/multi/worldpay/choose-payment-method/getDeclineMessage",
                    type: "GET",
                    success: function (data) {
                        if (data) {
                            const row = $('#hop');
                            const container = row.parent();

                            const declineCodeHTMLContent = "<div class='global-alerts'>" +
                                "<div class='alert alert-danger alert-dismissable'>" +
                                "<button class='close' aria-hidden='true' data-dismiss='alert' type='button'>×</button>"
                                + data +
                                "</div>";

                            const globalAlerts = container.find(".global-alerts");
                            if (globalAlerts) {
                                globalAlerts.last().append(declineCodeHTMLContent);
                            } else {
                                container.prepend(declineCodeHTMLContent);
                            }
                        }
                    },
                    error: function (err) {
                        console.log(err);
                    }
                });
            }
        }
    },

    /**
     * Show the saved payment checkbox when we are on CSE
     *  - google pay
     *  - paypal
     * Show the saved payment checkbox when we are not on CSE
     *  - credit card
     *  - paypal
     *  - google pay
     *  - ONLINE (no APM's configured)
     *
     * @returns {boolean}
     */
    shouldDisplaySavePaymentDetails: function () {
        if ($("#paymentButtons").hasClass("cse")) {
            return $("#paymentMethod_PAYPAL-EXPRESS").is(":checked") ||
                $("#paymentMethod_googlePay").is(":checked");
        }
        return $("#paymentMethod_CC").is(":checked") ||
            $("#paymentMethod_PAYPAL-EXPRESS").is(":checked") ||
            $("#paymentMethod_googlePay").is(":checked") ||
            $("#paymentMethod_ONLINE").val() === "ONLINE";
    },

    hideOrShowSaveDetails: function () {
        $(".cms-payment-button").on("change", function () {
            var savedPaymentDetails$ = $(".save_payment_details");
            if (ACC.worldpay.shouldDisplaySavePaymentDetails()) {
                savedPaymentDetails$.removeClass("hidden");
            } else if (!$(".cms-payment-button").length) {
                savedPaymentDetails$.removeClass("hidden");
            } else {
                $("#SaveDetails").prop('checked', false);
                savedPaymentDetails$.addClass("hidden");
            }
        });

        if ($("#paymentButtons").length > 0) {
            var savedPaymentDetails$ = $(".save_payment_details");
            if (ACC.worldpay.shouldDisplaySavePaymentDetails()) {
                savedPaymentDetails$.removeClass("hidden");
            } else {
                savedPaymentDetails$.addClass("hidden");
            }
        }
    },

    populateBankListByAPM: function (selectedAPM, callback) {
        $.get(ACC.config.encodedContextPath + "/worldpay/" + selectedAPM + "/banks", function (response) {
            const placeHolderMessage = ACC.addons.worldpayaddon["worldpayaddon.checkout.paymentMethod.shopperbankcode.default"];
            $("#shopperBankCode").empty().append($("<option></option>")
                .attr("selected", true).attr("disabled", true).text(placeHolderMessage));

            $.each(response, function (key, value) {
                $("#shopperBankCode").append($("<option></option>")
                    .attr("value", value.bankCode).text(value.bankName));
            });
            const $bankElement = $("#bankElement");
            $bankElement.removeClass("hidden");
            $bankElement.find('select').prop('disabled', false);
            if (typeof callback === 'function') {
                callback();
            }
        });
    },

    checkPreviouslySelectedBank: function () {
        const selectedAPM = $("[name='paymentMethod']:checked");
        if (selectedAPM.data("isbank")) {
            ACC.worldpay.populateBankListByAPM(selectedAPM.val(), function () {
                const shopperBankCodeSelectElem = $("#shopperBankCode");
                const selectedBankCode = shopperBankCodeSelectElem.data("bankcode");
                if (selectedBankCode !== undefined) {
                    shopperBankCodeSelectElem.val(selectedBankCode);
                }
            });
        } else if (selectedAPM.val() === "ACH_DIRECT_DEBIT-SSL") {
            const $achElement = $("#achForm");
            $achElement.removeClass("hidden");
            $achElement.find('select').prop('disabled', false);
        }
    },

    checkPreviouslySelectedPaymentMethod: function () {
        const paymentMethodCode = $("#paymentButtons").data("paymentmethod");
        $('#paymentMethod_' + paymentMethodCode).prop('checked', true);
        ACC.worldpay.checkPreviouslySelectedBank();
    },

    bindBanks: function () {
        $(".cms-payment-button").on("change", function () {
            const selectedAPM = $("[name='paymentMethod']:checked");
            if (selectedAPM.data("isbank")) {
                ACC.worldpay.populateBankListByAPM(selectedAPM.val());
            } else {
                const $bankElement = $("#bankElement");
                $bankElement.addClass("hidden");
                $bankElement.find('select').prop('disabled', true);
            }
        });
    },

    bindACH: function () {
        $(".cms-payment-button").on("change", function () {
            const selectedAPM = $("[name='paymentMethod']:checked");
            const $achElement = $("#achForm");
            if (selectedAPM.val()==="ACH_DIRECT_DEBIT-SSL") {
                $achElement.removeClass("hidden");
                $achElement.find('select').prop('disabled', false);
            } else {
                $achElement.addClass("hidden");
                $achElement.find('select').prop('disabled', true);
            }
        });
    },

    bindDOBInput: function () {
        $('.cms-payment-button').on('change', function () {
            var selectedCard = $('[name="paymentMethod"]:checked');
            if (selectedCard.attr('id') === 'paymentMethod_CC') {
                $('#dobElement').removeClass('hidden');
                $('#dobRequired').val(true);
            } else {
                $('#dobElement').addClass('hidden');
                $('#dobRequired').val(false);
            }
        });

        if ($('[id="paymentMethod_CC"]:checked').length > 0) {
            $('#dobElement').removeClass('hidden');
            $('#dobRequired').val(ACC.isFSEnabled);
        } else {
            $('#dobElement').addClass('hidden');
            $('#dobRequired').val(false);
        }
    },

    onPaymentMethodChange: function () {
        const checkboxes = $('[name="paymentMethod"]');
        checkboxes.on('change', function (event) {
            switch (event.target.value) {
                case 'APPLEPAY-SSL':
                    ACC.applePay.enableApplePayFlow();
                    break;
                case 'PAYWITHGOOGLE-SSL':
                    ACC.googlePay.enableGooglePayFlow();
                    break;
                default:
                    this.resetPaymentFlow();
                    break;
            }

            $('.main__inner-wrapper > .global-alerts .alert.alert-danger').remove();
        }.bind(this));
    },

    resetPaymentFlow: function () {
        $('#applepay-button').hide();
        $('#googlepay-button').hide();
        $('.checkout-next')
            .not('#applepay-button')
            .not('#googlepay-button')
            .show();

        $('#wpBillingCountrySelector')
            .show()
            .prev().show();

        $('#wpBillingAddress').show();
    },

    requireTermsAndConditions: function () {
        const terms = $('#termsAndConditions');
        $('<div class="help-block"><span>' +
            ACC.addons.worldpayaddon["worldpayaddon.checkout.error.terms.not.accepted"] +
            '</span></div>').appendTo(terms);
        terms.addClass('has-error');

        terms.on('change', function (event) {
            if ($(this).find('input[type=checkbox]').is(':checked')) {
                terms.find('.help-block').hide();
                terms.removeClass('has-error');
            } else {
                terms.find('.help-block').show();
                terms.addClass('has-error');
            }
        });
    },

    redirectToConfirmationPage: function (order) {
        const orderId = order.guestCustomer ? order.guid : order.code;

        window.location.href = ACC.config.encodedContextPath + '/checkout/orderConfirmation/' + orderId;
    }
};
