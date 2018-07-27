ACC.worldpayCSE = {

    _autoload: [
        "bindSubmitBillingAddressForm",
        "populateErrorCodeMap",
        "bindSubmitCseForm",
        "bindPaymentButtons"
    ],

    errorCodeMap: {},

    encryptCardDetails: function () {
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
        if ($(".cse").length) {
            const paymentButtonsSlot = $(".cms-payment-button");
            if (paymentButtonsSlot.length && paymentButtonsSlot.children().length > 0) {
                paymentButtonsSlot.on("change", function () {
                    if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() === "ONLINE") {
                        $(".terms").addClass("hidden");
                    }
                    else {
                        $(".terms").removeClass("hidden");
                    }
                });
            } else if (paymentButtonsSlot.length) {
                $(".terms").addClass("hidden");
            }
        }
    },

    errorHandler: function (errorCodes) {
        for (let index in errorCodes) {
            const errorCode = errorCodes[index].toString();
            ACC.worldpayCSE.showError($("#" + ACC.worldpayCSE.errorCodeMap[errorCode]), errorCode);
        }
    },

    showError: function (errorMessageField, errorCode) {
        errorMessageField.html(ACC.addons.worldpayaddon["worldpayaddon.CSE.validation.error." + errorCode]);
        errorMessageField.parent().removeClass("hidden");
        errorMessageField.closest(".form-group").addClass("has-error");

    },

    hideError: function (errorMessageField) {
        errorMessageField.addClass("hidden");
        errorMessageField.closest(".form-group").removeClass("has-error");
    },

    clearCSEErrorFields: function () {
        ACC.worldpayCSE.hideError($('div[id^="error-"]'));
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
                const container = $(".checkout-headline").parent();
                container.find(".global-alerts").remove();

                ACC.worldpayCSE.clearCSEErrorFields();

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
                    $("#worldpayCsePaymentForm").submit();
                }
            }
        );
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
