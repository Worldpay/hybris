$('#number').on('input', function () {
    validateCardNumber();
});

function loadBinRange(input) {
    $.ajax(ACC.config.encodedContextPath + '/checkout/multi/worldpay/cse/cardDetails',
        {
            data: {prefix: input}
        }
    ).done(function (data) {
        if (data !== undefined && data !== "") {
            $("#imageField").attr("src", ACC.config.contextPath.concat("/_ui/addons/worldpayb2cdemo").concat(data.imageLink));
            $("#creditCardDetail").html("&nbsp;".concat(data.cardName + data.cardNotes));
        }
    });
}

function validateCardNumber() {
    var cardImageElement = $("#imageField");
    var cardNumberInput = $("#number");
    var cardNumber = cardNumberInput.val();
    var creditCardDetails = $("#creditCardDetail");

    if (cardImageElement.length === 0) {
        cardNumberInput.parent().append($('<img id="imageField" />'));
    }
    if (creditCardDetails.length === 0) {
        cardNumberInput.parent().append($('<label id="creditCardDetail" />'));
    }

    if (cardNumber.length >= 4) {
        loadBinRange(cardNumber);
    } else {
        cardImageElement.attr("src", "");
        creditCardDetails.html("");
    }
}
