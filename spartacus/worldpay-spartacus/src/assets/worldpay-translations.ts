export const worldpayTranslations = {
  en: {
    payment: {
      paymentForm: {
        payment: 'Credit card details',
        setAsSaved: 'Save payment details',
        applePay: {
          title: 'Apple Pay',
          introduction:
            'Pay directly with Apple Pay and skip filling the payment details.',
          merchantValidationFailed:
            'Your Apple Pay Payment could not be processed. Please contact customer service at 555....',
          authorisationFailed:
            'Your payment could be authorized. Please try another time.',
          cancelled: 'Your payment was cancelled by Apple Pay'
        },
        googlepay: {
          authorisationFailed: 'Your GooglePay payment could not be processed',
          help:
            'When you press the Google Pay button you can pay your order in an instance.'
        },
        publicKey: {
          requestFailed: 'Please contact the webmaster, configuration publicKey is missing.'
        },

        idealForm: {
          bank: {
            label: 'Bank',
            placeholder: 'Select your bank'
          }
        },

        apmChanged: 'The previously selected payment option is not available, please select another one.',

        dateOfBirth: {
          label: 'Date of birth',
          placeholder: 'Select your date of birth',
        }
      },

      paymentCard: {
        apm: 'Payment with {{ apm }}'
      },
    },
    checkout: {
      checkoutReview: {
        threeDsChallengeFailed: 'Failed to verify your payment details',
        tokenizationFailed: 'Please check your payment details',
        initialPaymentRequestFailed: 'Failed to process your payment',
        challengeFailed:
          'Your payment was refused. Please try again with a different card',
        redirectPaymentRejected: 'Your payment was rejected. Please try again',
        redirectPaymentFailed: 'Your payment was refused. Please try again',
        redirectPaymentCancelled: 'Your payment was cancelled',
        placingOrder: 'Your order is being processed, please wait.'
      }
    }
  }
};
