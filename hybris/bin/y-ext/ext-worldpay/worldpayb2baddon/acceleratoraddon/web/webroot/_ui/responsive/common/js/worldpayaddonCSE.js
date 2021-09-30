ACC.worldpayCSE = {

    _autoload: [
        "bindSubmitBillingAddressForm",
        "populateErrorCodeMap",
        "bindSubmitCseForm",
        "bindPaymentButtons",
        "fillResolutionForWindowChallenge",
        "bindMessageEventListener",
        "bindPlaceOrderForm",
        ["performJsc", $("#worldpayCsePaymentForm").length > 0 && ACC.isFSEnabled === 'true']
    ],

    errorCodeMap: {},

    encryptCardDetails: function () {
        ACC.worldpayCSE.clearCSEErrorFields();
        const data = {
            cvc: $("#cvc").val(),
            cardHolderName: $("#nameOnCard").val(),
            cardNumber: $("#number").val(),
            expiryMonth: $("#exp-month").val(),
            expiryYear: $("#exp-year").val()
        };
        const encryptedData = Worldpay.encrypt(data, ACC.worldpayCSE.errorHandler);
        if (encryptedData) {
            $("#encryptedData").val(encryptedData);
            return true;
        } else {
            return false;
        }
    },

    bindPaymentButtons: function () {
        let paymentButtonsSlot = $(".cms-payment-button");
        if (paymentButtonsSlot.length && paymentButtonsSlot.children().length > 0) {
            paymentButtonsSlot.on("change", function () {
                if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() == "ONLINE") {
                    $(".terms").addClass("hidden");
                } else {
                    $(".terms").removeClass("hidden");
                }
            });
        } else if (paymentButtonsSlot.length) {
            $(".terms").addClass("hidden");
        }
    },

    errorHandler: function (errorCodes) {
        for (let index in errorCodes) {
            const errorCode = errorCodes[index].toString();
            ACC.worldpayCSE.showError($("#" + ACC.worldpayCSE.errorCodeMap[errorCode]), errorCode);
        }
    },

    showError: function (errorMessageField, errorCode) {
        errorMessageField.html(ACC.addons.worldpayb2baddon["worldpayaddon.CSE.validation.error." + errorCode]);
        errorMessageField.parent().removeClass("hidden");
        errorMessageField.closest(".form-group").addClass("has-error");

    },

    hideError: function (errorMessageField) {
        errorMessageField.parent().addClass("hidden");
        errorMessageField.closest(".form-group").removeClass("has-error");
    },

    clearCSEErrorFields: function () {
        ACC.worldpayCSE.hideError($('span[id^="error-"]'));
    },

    fillResolutionForWindowChallenge: function () {
        /* Empty value uses as resolution 390x400
           When the 3Ds Version is 1, the unique possibility is 390x400
           When the 3Ds Version is 2, the possibilities are:
           250x400, 390x400, 500x600, 600x400, fullPage
           https://beta.developer.worldpay.com/docs/wpg/directintegration/3ds2
         */
        $("#windowSizePreference").val("fullPage");
    },

    bindSubmitBillingAddressForm: function () {
        $(".submit_worldpayCSEForm").click(
            function (event) {
                event.preventDefault();
                ACC.common.blockFormAndShowProcessingMessage($(this));
                $(".wpBillingAddress").filter(":hidden").remove();
                ACC.worldpay.enableAddressForm();
                $("#worldpayBillingAddressForm").submit();
            }
        );
    },

    bindSubmitCseForm: function () {
        $(".submit_cseDetails").click(
            function (event) {
                event.preventDefault();
                var container = $(".checkout-headline").parent();
                container.find(".global-alerts").remove();

                var submit = ACC.worldpayCSE.encryptCardDetails();
                if (submit) {
                    $("#worldpayCsePaymentForm").submit();
                }
            }
        );
    },

    bindMessageEventListener: function () {
        window.addEventListener('message', function (event) {
            console.log(event);
            if (event.origin === ACC.worldpayCSE.originEventDomain3DSFlex) {
                var data = JSON.parse(event.data);
                //TODO Remove this warn console log line whenever you go PROD
                console.warn('Merchant received a message:', data);
                if (data !== undefined && data.Status) {
                    // Extract the ReferenceId and store it in your data to submit back to Worldpay.
                    $('#threeDSReferenceId').val(data.SessionId);
                }
                ACC.worldpayCSE.submitCSEForm();
            }
        }, false);
    },

    submitCSEForm: function () {
        const container = $(".checkout-headline").parent();
        container.find(".global-alerts").remove();

        const submit = ACC.worldpayCSE.encryptCardDetails();
        if (!$("#Terms1").is(':checked')) {
            container.prepend(
                "<div class='global-alerts'>" +
                "<div class='alert alert-danger alert-dismissable'>" +
                "<button class='close' aria-hidden='true' data-dismiss='alert' type='button'>&times;</button>" +
                ACC.addons.worldpayaddon["worldpayaddon.checkout.error.terms.not.accepted"] +
                "</div></div>"
            );
        } else if (submit) {
            ACC.worldpayCSE.submitCSEFormAjax('#placeOrderForm1');
        }
    },

    bindPlaceOrderForm: function () {
        $('#placeOrderForm1').on('submit', function (e) {
            e.preventDefault();
            ACC.worldpayCSE.submitCSEFormAjax('#placeOrderForm1');
        })
    },

    submitCSEFormAjax: function (selector) {
        var $form = $(selector);
        var xhr = new XMLHttpRequest();
        $.ajax({
            type: 'POST',
            url: $form.attr('action'),
            data: $form.serialize(),
            xhr: function () {
                return xhr;
            },
            success: function (res) {
                if (xhr.getResponseHeader('3D-Secure-Flow') === "false" || !xhr.getResponseHeader('3D-Secure-Flow')) {
                    $('body').html(res);
                    window.scrollTo(0, 0);
                } else {
                    ACC.colorbox.open("", {
                        html: res,
                        onComplete: function () {
                            if (xhr.getResponseHeader('3D-Secure-Flex-Flow') === "false" || !xhr.getResponseHeader('3D-Secure-Flex-Flow')) {
                                submitLegacy3dForm();
                            }
                        }
                    });
                }
            },
            error: function (err) {
                $('body').html(err.responseText);
                window.scrollTo(0, 0);
            }
        });
    },

    performJsc: function () {
        let ndownc = ACC.worldpayCSE.create_uuid(); //that’s the sessionId
        let div = $('#worldpayCsePaymentForm'); //suggestion how you can store sessionId for when you need it in payment request
        let input = document.createElement('input');
        input.setAttribute('type', 'hidden');
        input.setAttribute('id', 'deviceSession');
        input.setAttribute('name', 'deviceSession');
        input.setAttribute('value', ndownc);
        div.append(input);
        threatmetrix.prfl(ACC.profilingDomain, ACC.organizationId, ndownc, 1); // Jsc.prfl is the renamed function
    },

    create_uuid: function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0,
                v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    },

    populateErrorCodeMap: function () {
        ACC.worldpayCSE.errorCodeMap["101"] = "error-number";
        ACC.worldpayCSE.errorCodeMap["102"] = "error-number";
        ACC.worldpayCSE.errorCodeMap["103"] = "error-number";
        ACC.worldpayCSE.errorCodeMap["201"] = "error-cvc";
        ACC.worldpayCSE.errorCodeMap["301"] = "error-exp-date";
        ACC.worldpayCSE.errorCodeMap["302"] = "error-exp-date";
        ACC.worldpayCSE.errorCodeMap["303"] = "error-exp-date";
        ACC.worldpayCSE.errorCodeMap["304"] = "error-exp-date";
        ACC.worldpayCSE.errorCodeMap["305"] = "error-exp-date";
        ACC.worldpayCSE.errorCodeMap["306"] = "error-exp-date";
        ACC.worldpayCSE.errorCodeMap["401"] = "error-nameOnCard";
        ACC.worldpayCSE.errorCodeMap["402"] = "error-nameOnCard";
    }
};

