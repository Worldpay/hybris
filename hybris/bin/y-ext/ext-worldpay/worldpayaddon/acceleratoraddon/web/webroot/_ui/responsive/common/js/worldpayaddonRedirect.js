ACC.worldpayRedirect = {

    _autoload: [
        "bindSubmitBillingAddressForm"
    ],

    bindSubmitBillingAddressForm: function () {
        $(".submit_worldpayHopForm").click(
            function (event) {
                event.preventDefault();
                ACC.common.blockFormAndShowProcessingMessage($(this));
                $(".wpBillingAddress").filter(":hidden").remove();
                ACC.worldpay.enableAddressForm();
                $("#worldpayBillingAddressForm").submit();
            }
        );
    }
};
