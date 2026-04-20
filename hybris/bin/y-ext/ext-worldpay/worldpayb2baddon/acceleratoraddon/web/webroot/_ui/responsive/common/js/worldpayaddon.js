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
            this.disableAddressForm();
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
            this.clearAddressForm();
            this.enableAddressForm();
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
                callback.call();
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

    shouldDisplaySavePaymentDetails: function () {
        if ($("#paymentButtons").hasClass("cse")) {
            return $("#paymentMethod_SEPA_DIRECT_DEBIT-SSL").is(":checked");
        }
        return $("#paymentMethod_CC").is(":checked") ||
            $("#paymentMethod_SEPA_DIRECT_DEBIT-SSL").is(":checked") ||
            $("#paymentMethod_ONLINE").val() === "ONLINE";
    },

    hideOrShowSaveDetails: function () {
      const savedPaymentDetails$ = $(".save_payment_details");
        $(".cms-payment-button").on("change", function () {

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
            $("#bankElement").removeClass("hidden");

            if (typeof callback === 'function') {
                callback.call();
            }
        });
    },

    checkPreviouslySelectedBank: function () {
        const selectedAPM = $("[name='paymentMethod']:checked");
        if (selectedAPM.data("isbank")) {
            this.populateBankListByAPM(selectedAPM.val(), function () {
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
        this.checkPreviouslySelectedBank();
    },

    bindBanks: function () {
        $(".cms-payment-button").on("change", function () {
            const selectedAPM = $("[name='paymentMethod']:checked");
            if (selectedAPM.data("isbank")) {
                ACC.worldpay.populateBankListByAPM(selectedAPM.val());
            } else {
                $("#bankElement").addClass("hidden");
            }
        });

    },

    onPaymentMethodChange: function () {
        const checkboxes = $('[name="paymentMethod"]');
        checkboxes.on('change', function (event) {
            this.resetPaymentFlow();
            $('.main__inner-wrapper > .global-alerts .alert.alert-danger').remove();
        }.bind(this));
    },

    resetPaymentFlow: function () {
        $('.checkout-next')
            .show();

        $('#wpBillingCountrySelector')
            .show()
            .prev().show();

        $('#wpBillingAddress').show();
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
            const selectedCard = $('[name="paymentMethod"]:checked');
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
    }
};
