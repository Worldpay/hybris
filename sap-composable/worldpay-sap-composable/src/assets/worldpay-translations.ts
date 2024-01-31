export const worldpayTranslations = {
  en: {
    address: {
      addressForm: {
        jp: {
          address1: 'Further subarea number, house number',
          address2: 'Subarea',
          city: {
            label: 'City, village, city ward'
          },
          state: 'Prefecture name',
          zipCode: {
            label: 'Postal code'
          }
        }
      }
    },
    checkout: {
      checkoutReview: {
        applicationError: 'The application has encountered an error',
        challengeFailed: 'Your payment was refused. Please try again with a different card',
        encrypt: {
          [101]: 'A credit or debut card number is mandatory',
          [102]: 'Card number should contain between 12 and 20 numeric characters',
          [103]: 'The card number entered is invalid',
          [201]: 'The security code is invalid',
          [301]: 'The expiry month is not included',
          [302]: 'Expiry Month must contain exactly 2 numbers',
          [303]: 'Expiry Month should be between 01 and 12',
          [304]: 'The expiry year is not included',
          [305]: 'Expiry Year must contain exactly 4 numbers',
          [306]: 'Expiry Month and Expiry Year together must be a date in the future',
          [401]: 'Card Holder name is mandatory',
          [402]: 'Card Holder name cannot exceed thirty (30) characters'
        },
        initialPaymentRequestFailed: 'Failed to process your payment',
        placingOrder: 'Your order is being processed, please wait.',
        redirectPaymentCancelled: 'Your payment was cancelled',
        redirectPaymentFailed: 'Your payment was refused. Please try again',
        redirectPaymentRejected: 'Your payment was rejected. Please try again',
        threeDsChallengeFailed: 'Failed to verify your payment details',
        threeDsFlowNotImplemented: '3ds flow not implemented',
        tokenizationFailed: 'Please check your payment details'
      },
      checkoutOrderConfirmation: {
        pending: {
          thankYouForOrder: 'Your order is pending! Thank you for shopping with us.',
          invoiceHasBeenSentByEmail: 'A copy of your order details has been sent to {{email}}',
        }
      },
    },
    common: {
      formErrors: {
        minlength: 'Please enter at least {{minLength}} characters.'
      }
    },
    order: {
      orderDetails: {
        statusDisplay_open: 'Open',
        statusDisplay_pending: 'Pending',
      }
    },
    payment: {
      paymentCard: {
        apm: 'Payment with {{ apm }}',
        bank: 'Selected Bank: {{ bank }}',
        expires: 'Expires: {{ month }}/{{ year }}',
      },

      paymentForm: {
        apmChanged: 'The previously selected payment option is not available, please select another one.',
        applePay: {
          authorisationFailed: 'Your payment could be authorized. Please try another time.',
          cancelled: 'Your payment was cancelled by Apple Pay',
          introduction: 'Pay directly with Apple Pay and skip filling the payment details.',
          merchantValidationFailed: 'Your Apple Pay Payment could not be processed. Please contact customer service at 555....',
          title: 'Apple Pay'
        },
        dateOfBirth: {
          label: 'Date of birth',
          placeholder: 'Select your date of birth',
        },
        googlepay: {
          authorisationFailed: 'Your GooglePay payment could not be processed',
          help: 'When you press the Google Pay button you can pay your order in an instance.'
        },
        idealForm: {
          bank: {
            label: 'Bank',
            placeholder: 'Select your bank'
          }
        },
        invalid: {
          applicationError: 'The application has encountered an error',
        },
        error: {
          savePayment: 'Invalid payment details. Please try again',
        },
        payment: 'Payment details',
        paymentApm: 'Payment method',
        publicKey: {
          requestFailed: 'Please contact the webmaster, configuration publicKey is missing.'
        },
        setAsSaved: 'Save payment details'
      },
    }
  }
};
