ACC.worldpayCSE = {

  _autoload: [
    'bindSubmitBillingAddressForm',
    'populateErrorCodeMap',
    'bindSubmitCseForm',
    'bindPaymentButtons',
    'fillResolutionForWindowChallenge',
    'bindMessageEventListener',
    'bindPlaceOrderForm',
    ['bindCollectionFormToCseDetails', $('.submit_cseDetails').length > 0],
    ['performJsc', $('#worldpayCsePaymentForm').length > 0 && ACC.isFSEnabled === 'true'],
  ],

  errorCodeMap: {},
  fraudSightIdPromise: null,

  encryptCardDetails: function () {
    ACC.worldpayCSE.clearCSEErrorFields();
    const data = {
      cvc: $('#cvc').val(),
      cardHolderName: $('#nameOnCard').val(),
      cardNumber: $('#number').val(),
      expiryMonth: $('#exp-month').val(),
      expiryYear: $('#exp-year').val(),
    };
    const encryptedData = Worldpay.encrypt(data, ACC.worldpayCSE.errorHandler);
    if (encryptedData) {
      $('#encryptedData').val(encryptedData);
      return true;
    } else {
      return false;
    }
  },

  bindPaymentButtons: function () {
    let paymentButtonsSlot = $('.cms-payment-button');
    if (paymentButtonsSlot.length && paymentButtonsSlot.children().length > 0) {
      paymentButtonsSlot.on('change', function () {
        if ($('#paymentMethod_CC').is(':checked') || $('#paymentMethod_ONLINE').val() == 'ONLINE') {
          $('.terms').addClass('hidden');
        } else {
          $('.terms').removeClass('hidden');
        }
      });
    } else if (paymentButtonsSlot.length) {
      $('.terms').addClass('hidden');
    }
  },

  errorHandler: function (errorCodes) {
    for (let index in errorCodes) {
      const errorCode = errorCodes[index].toString();
      ACC.worldpayCSE.showError($('#' + ACC.worldpayCSE.errorCodeMap[errorCode]), errorCode);
    }
  },

  showError: function (errorMessageField, errorCode) {
    errorMessageField.html(ACC.addons.worldpayb2baddon['worldpayaddon.CSE.validation.error.' + errorCode]);
    errorMessageField.parent().removeClass('hidden');
    errorMessageField.closest('.form-group').addClass('has-error');

  },

  hideError: function (errorMessageField) {
    errorMessageField.parent().addClass('hidden');
    errorMessageField.closest('.form-group').removeClass('has-error');
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
    $('#windowSizePreference').val('fullPage');
  },

  bindSubmitBillingAddressForm: function () {
    $('.submit_worldpayCSEForm').click(
      function (event) {
        event.preventDefault();
        ACC.common.blockFormAndShowProcessingMessage($(this));
        $('.wpBillingAddress').filter(':hidden').remove();
        ACC.worldpay.enableAddressForm();
        $('#worldpayBillingAddressForm').submit();
      },
    );
  },

  bindSubmitCseForm: function () {
    $('.submit_cseDetails').click(
      function (event) {
        event.preventDefault();
        var container = $('.checkout-headline').parent();
        container.find('.global-alerts').remove();

        var submit = ACC.worldpayCSE.encryptCardDetails();
        if (submit) {
          $('#worldpayCsePaymentForm').submit();
        }
      },
    );
  },

  bindMessageEventListener: function () {
    window.addEventListener('message', function (event) {
      if (event.origin === ACC.worldpayCSE.originEventDomain3DSFlex) {
        var data = event.data;

        if (typeof data === 'string') {
          try {
            data = JSON.parse(data);
          } catch (e) {
            console.warn('Unable to parse 3DS Flex message:', event.data);
            return;
          }
        }

        if (data === undefined || data === null || !data.Status || !data.SessionId) {
          return;
        }

        // Extract the ReferenceId and store it in your data to submit back to Worldpay.
        $('#threeDSReferenceId').val(data.SessionId);
        ACC.worldpayCSE.submitCSEForm();
      }
    }, false);
  },

  submitCSEForm: function () {
    const container = $('.checkout-headline').parent();
    container.find('.global-alerts').remove();

    const submit = ACC.worldpayCSE.encryptCardDetails();
    if (!$('#Terms1').is(':checked')) {
      container.prepend(
        '<div class=\'global-alerts\'>' +
        '<div class=\'alert alert-danger alert-dismissable\'>' +
        '<button class=\'close\' aria-hidden=\'true\' data-dismiss=\'alert\' type=\'button\'>&times;</button>' +
        ACC.addons.worldpayaddon['worldpayaddon.checkout.error.terms.not.accepted'] +
        '</div></div>',
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

  appendFieldToForm(form) {
    const browserFields = {
      'browserInfo.JavaEnabled': navigator.javaEnabled(),
      'browserInfo.Language': navigator.language || navigator.userLanguage,
      'browserInfo.ColorDepth': screen.colorDepth,
      'browserInfo.ScreenHeight': screen.height,
      'browserInfo.ScreenWidth': screen.width,
      'browserInfo.TimeZone': new Date().getTimezoneOffset().toString(),
      'browserInfo.UserAgent': navigator.userAgent,
      'browserInfo.JavascriptEnabled': true,
    }

    Object.entries(browserFields).forEach(([key, value]) => {
      $('<input>').attr({
        type: 'hidden',
        name: key,
        value: value,
      }).appendTo(form);
    });
  },

  submitCSEFormAjax: function (selector) {
    if (ACC.worldpayCSE.fraudSightIdPromise) {
      const pending = ACC.worldpayCSE.fraudSightIdPromise;
      ACC.worldpayCSE.fraudSightIdPromise = null;
      const resume = () => ACC.worldpayCSE.submitCSEFormAjax(selector);
      pending.then(resume, resume);
      return;
    }
    var $form = $(selector);
    var xhr = new XMLHttpRequest();
    if ($('.js-credit-card-type').length > 0) {
      ACC.worldpayCSE.appendFieldToForm($form[0]);
    }

    $.ajax({
      type: 'POST',
      url: $form.attr('action'),
      data: $form.serialize(),
      xhr: function () {
        return xhr;
      },
      success: function (res) {
        if (xhr.getResponseHeader('3D-Secure-Flow') === 'false' || !xhr.getResponseHeader('3D-Secure-Flow')) {
          $('body').html(res);
          window.scrollTo(0, 0);
        } else {
          ACC.colorbox.open('', {
            html: res,
            onComplete: function () {
              if (xhr.getResponseHeader('3D-Secure-Flex-Flow') === 'false' || !xhr.getResponseHeader('3D-Secure-Flex-Flow')) {
                submitLegacy3dForm();
              }
            },
          });
        }
      },
      error: function (err) {
        $('body').html(err.responseText);
        window.scrollTo(0, 0);
      },
    });
  },

  performJsc: function () {
    const publishableKey = ACC.fraudSightPublishableKey;
    if (!publishableKey || typeof Ravelin === 'undefined') {
      return;
    }

    const ravelin = new Ravelin({ key: publishableKey });
    ACC.worldpayCSE.ravelin = ravelin;

    const $form = $('#worldpayCsePaymentForm');
    if ($form.find('#deviceSession').length === 0) {
      $form.append('<input type="hidden" id="deviceSession" name="deviceSession" />');
    }

    ravelin.track?.load?.();
    ravelin.track?.paste?.();
    if (ACC.userId) {
      ravelin.core?.setCustomerId?.(ACC.userId);
    }

    ACC.worldpayCSE.fraudSightIdPromise = ravelin.core.id().then((deviceId) => {
      $('#deviceSession').val(deviceId);
      return deviceId;
    });
  },

  populateErrorCodeMap: function () {
    ACC.worldpayCSE.errorCodeMap['101'] = 'error-number';
    ACC.worldpayCSE.errorCodeMap['102'] = 'error-number';
    ACC.worldpayCSE.errorCodeMap['103'] = 'error-number';
    ACC.worldpayCSE.errorCodeMap['201'] = 'error-cvc';
    ACC.worldpayCSE.errorCodeMap['301'] = 'error-exp-date';
    ACC.worldpayCSE.errorCodeMap['302'] = 'error-exp-date';
    ACC.worldpayCSE.errorCodeMap['303'] = 'error-exp-date';
    ACC.worldpayCSE.errorCodeMap['304'] = 'error-exp-date';
    ACC.worldpayCSE.errorCodeMap['305'] = 'error-exp-date';
    ACC.worldpayCSE.errorCodeMap['306'] = 'error-exp-date';
    ACC.worldpayCSE.errorCodeMap['401'] = 'error-nameOnCard';
    ACC.worldpayCSE.errorCodeMap['402'] = 'error-nameOnCard';
  },

  /**
   * Sends the DDC information to worldpay on the checkout page (card select page)
   * Fills information in the iframe form and sumbits it
   */
  bindCollectionFormToCseDetails: function () {
    $('.submit_cseDetails').off('click.worldpayDdc').on('click.worldpayDdc', function (event) {
      event.preventDefault();

      const encrypted = ACC.worldpayCSE.encryptCardDetails();

      if (encrypted) {
        const ddcIframe = document.getElementById('DDCIframe');
        const cardNumber = String($('#number').val() || '');

        if (
          ddcIframe &&
          ddcIframe.contentWindow &&
          typeof ddcIframe.contentWindow.submitWorldpayDdcCollectionForm === 'function'
        ) {
          ddcIframe.contentWindow.submitWorldpayDdcCollectionForm(cardNumber);
        }
      }
    });
  },
};

