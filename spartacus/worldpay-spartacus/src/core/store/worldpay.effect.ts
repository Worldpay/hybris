import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { CartActions, GlobalMessage, GlobalMessageActions, GlobalMessageType, OCC_USER_ID_ANONYMOUS, Order, PaymentDetails, UserActions } from '@spartacus/core';

import * as WorldpayActions from './worldpay.action';
import { WorldpayConnector } from '../connectors/worldpay.connector';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse, ThreeDsDDCInfo } from '../connectors/worldpay.adapter';
import { ApmData, APMRedirectResponse, GetOrderPayload, GooglePayMerchantConfiguration, InitialPaymentRequestPayload } from '../interfaces';
import { CheckoutActions } from '@spartacus/checkout/core';

@Injectable()
export class WorldpayEffects {
  constructor(
    private actions$: Actions,
    private worldpayConnector: WorldpayConnector
  ) {
  }

  @Effect()
  createPaymentDetails$: Observable<| CheckoutActions.CreatePaymentDetailsSuccess
    | GlobalMessageActions.AddMessage
    | WorldpayActions.CreateWorldpayPaymentDetailsSuccess
    | WorldpayActions.CreateWorldpayPaymentDetailsFail> = this.actions$.pipe(
      ofType(WorldpayActions.CREATE_WORLDPAY_PAYMENT_DETAILS),
      map((action: any) => action.payload),
      mergeMap(
        (payload: {
        paymentDetails: PaymentDetails;
        cseToken: string;
        userId: string;
      }) => {
          if (!payload.cseToken) {
            return [
              createGlobalMessageAction('checkoutReview.tokenizationFailed'),
              new WorldpayActions.CreateWorldpayPaymentDetailsFail()
            ];
          }

          return [
            new CheckoutActions.CreatePaymentDetailsSuccess(
              payload.paymentDetails
            ),
            new WorldpayActions.CreateWorldpayPaymentDetailsSuccess(
              payload.paymentDetails
            )
          ];
        }
      )
    );

  @Effect()
  useExistingPaymentDetails$: Observable<| CheckoutActions.SetPaymentDetailsSuccess
    | CheckoutActions.CreatePaymentDetailsSuccess
    | UserActions.LoadUserPaymentMethods
    | WorldpayActions.UseExistingWorldpayPaymentDetailsSuccess
    | WorldpayActions.UseExistingWorldpayPaymentDetailsFail> = this.actions$.pipe(
      ofType(WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector
        .useExistingPaymentDetails(
          payload.userId,
          payload.cartId,
          payload.paymentDetails
        )
        .pipe(
          mergeMap(() => {
            const paymentDetails = payload.paymentDetails;
            const userId = payload.userId;

            if (userId === OCC_USER_ID_ANONYMOUS) {
              return [
                new CheckoutActions.SetPaymentDetailsSuccess(paymentDetails)
              ];
            } else {
              return [
                new UserActions.LoadUserPaymentMethods(userId),
                new CheckoutActions.CreatePaymentDetailsSuccess(paymentDetails),
                new WorldpayActions.UseExistingWorldpayPaymentDetailsSuccess(
                  paymentDetails
                )
              ];
            }
          }),
          catchError(error =>
            of(new WorldpayActions.UseExistingWorldpayPaymentDetailsFail(error))
          )
        ))
    );

  @Effect()
  setPaymentAddress$: Observable<| WorldpayActions.SetPaymentAddressSuccess
    | WorldpayActions.SetPaymentAddressFail> = this.actions$.pipe(
      ofType(WorldpayActions.SET_PAYMENT_ADDRESS),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector
        .setPaymentAddress(payload.userId, payload.cartId, payload.address)
        .pipe(
          switchMap(() => [
            new WorldpayActions.SetPaymentAddressSuccess(payload.address)
          ]),
          catchError(error =>
            of(new WorldpayActions.SetPaymentAddressFail(error))
          )
        ))
    );

  @Effect()
  getPublicKey$: Observable<| WorldpayActions.GetWorldpayPublicKeySuccess
    | WorldpayActions.GetWorldpayPublicKeyFail
    | GlobalMessageActions.AddMessage> = this.actions$.pipe(
      ofType(WorldpayActions.GET_WORLDPAY_PUBLIC_KEY),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector.getPublicKey().pipe(
        switchMap(data => [
          new WorldpayActions.GetWorldpayPublicKeySuccess(data)
        ]),
        catchError(error => [
          new WorldpayActions.GetWorldpayPublicKeyFail(error),
          createGlobalMessageAction('paymentForm.publicKey.requestFailed'),
        ]
        )
      ))
    );

  @Effect()
  getDDCJwt: Observable<| WorldpayActions.GetWorldpayDDCJwtSuccess
    | WorldpayActions.GetWorldpayDDCJwtFail
    | GlobalMessageActions.AddMessage> = this.actions$.pipe(
      ofType(WorldpayActions.GET_WORLDPAY_DDC_JWT),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector.getDDC3dsJwt().pipe(
        switchMap((ddcInfo: ThreeDsDDCInfo) => [
          new WorldpayActions.GetWorldpayDDCJwtSuccess(ddcInfo)
        ]),
        catchError(error => [
          createGlobalMessageAction(),
          new WorldpayActions.GetWorldpayDDCJwtFail(error)
        ])
      ))
    );

  @Effect()
  initialPaymentRequest: Observable<| GlobalMessageActions.AddMessage
    | WorldpayActions.InitialPaymentRequestFail
    | CheckoutActions.PlaceOrderSuccess
    | WorldpayActions.InitialPaymentRequestChallengeRequired
    | CartActions.RemoveCart> = this.actions$.pipe(
      ofType(WorldpayActions.INITIAL_PAYMENT_REQUEST),
      map((action: any) => action.payload),
      mergeMap((payload: InitialPaymentRequestPayload) => this.worldpayConnector
        .initialPaymentRequest(
          payload.userId,
          payload.cartId,
          payload.paymentDetails,
          payload.dfReferenceId,
          payload.challengeWindowSize,
          payload.cseToken,
          payload.acceptedTermsAndConditions,
          payload.deviceSession,
        )
        .pipe(
          switchMap((response: PlaceOrderResponse) => {
            if (response.threeDSecureNeeded) {
              return [
                new WorldpayActions.InitialPaymentRequestChallengeRequired(
                  response.threeDSecureInfo
                )
              ];
            } else if (response.transactionStatus === 'AUTHORISED') {
              return [
                new CartActions.RemoveCart({ cartId: payload.cartId }),
                new CheckoutActions.PlaceOrderSuccess(response.order)
              ];
            } else {
              return [
                createGlobalMessageAction(),
                new WorldpayActions.InitialPaymentRequestFail(
                  response.transactionStatus
                )
              ];
            }
          }),
          catchError(error => [
            createGlobalMessageAction(),
            new WorldpayActions.InitialPaymentRequestFail(error)
          ])
        ))
    );

  @Effect()
  getOrderDetails: Observable<| WorldpayActions.ChallengeAcceptedFail
    | WorldpayActions.ChallengeAcceptedSuccess
    | CheckoutActions.PlaceOrderSuccess
    | GlobalMessageActions.AddMessage> = this.actions$.pipe(
      ofType(WorldpayActions.CHALLENGE_ACCEPTED),
      map((action: any) => action.payload),
      mergeMap((payload: GetOrderPayload) => this.worldpayConnector
        .getOrder(payload.userId, payload.code)
        .pipe(
          switchMap((order: Order) => [
            new CheckoutActions.PlaceOrderSuccess(order),
            new WorldpayActions.ChallengeAcceptedSuccess(order)
          ]),
          catchError(error => [
            createGlobalMessageAction('checkoutReview.challengeFailed'),
            new WorldpayActions.ChallengeAcceptedFail(error)
          ])
        ))
    );

  @Effect()
  applePayPaymentRequest: Observable<| WorldpayActions.RequestApplePayPaymentRequestSuccess
    | WorldpayActions.RequestApplePayPaymentRequestFail> = this.actions$.pipe(
      ofType(WorldpayActions.REQUEST_APPLE_PAY_PAYMENT_REQUEST),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector
        .requestApplePayPaymentRequest(payload.userId, payload.cartId)
        .pipe(
          switchMap((applePayPaymentRequest: ApplePayPaymentRequest) => [
            new WorldpayActions.RequestApplePayPaymentRequestSuccess(
              applePayPaymentRequest
            )
          ]),
          catchError(error => [
            new WorldpayActions.RequestApplePayPaymentRequestFail(error)
          ])
        ))
    );
  @Effect()
  applePayValidateMerchant: Observable<| WorldpayActions.ValidateApplePayMerchantSuccess
    | WorldpayActions.ValidateApplePayMerchantFail
    | GlobalMessageActions.AddMessage> = this.actions$.pipe(
      ofType(WorldpayActions.VALIDATE_APPLE_PAY_MERCHANT),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector
        .validateApplePayMerchant(
          payload.userId,
          payload.cartId,
          payload.validationURL
        )
        .pipe(
          switchMap((response: any) => [
            new WorldpayActions.ValidateApplePayMerchantSuccess(response)
          ]),
          catchError(error => [
            createGlobalMessageAction(
              'paymentForm.applePay.merchantValidationFailed'
            ),
            new WorldpayActions.ValidateApplePayMerchantFail(error)
          ])
        ))
    );
  @Effect()
  applePayAuthorisePayment: Observable<| WorldpayActions.AuthoriseApplePayPaymentSuccess
    | WorldpayActions.AuthoriseApplePayPaymentFail
    | GlobalMessageActions.AddMessage
    | CheckoutActions.PlaceOrderSuccess
    | CartActions.RemoveCart
    | WorldpayActions.ClearPaymentDetails> = this.actions$.pipe(
      ofType(WorldpayActions.AUTHORISE_APPLE_PAY_PAYMENT),
      map((action: any) => action.payload),
      mergeMap(payload => this.worldpayConnector
        .authorizeApplePayPayment(
          payload.userId,
          payload.cartId,
          payload.payment
        )
        .pipe(
          switchMap((response: ApplePayAuthorization) => [
            new CheckoutActions.PlaceOrderSuccess(response.order),
            new CartActions.RemoveCart({ cartId: payload.cartId }),
            new WorldpayActions.AuthoriseApplePayPaymentSuccess(response),
            new WorldpayActions.ClearPaymentDetails()
          ]),
          catchError(error => [
            createGlobalMessageAction('paymentForm.applePay.authorisationFailed'),
            new WorldpayActions.AuthoriseApplePayPaymentFail(error)
          ])
        ))
    );

  @Effect()
  googlePayGetMerchantConfiguration: Observable<| WorldpayActions.GetGooglePayMerchantConfigurationSuccess
    | WorldpayActions.GetGooglePayMerchantConfigurationFail> = this.actions$.pipe(
      ofType(WorldpayActions.GET_CONFIG_GOOGLE_PAY),
      map((action: any) => action.payload),
      mergeMap(({
        userId,
        cartId
      }) => this.worldpayConnector
        .getGooglePayMerchantConfiguration(userId, cartId)
        .pipe(
          switchMap((merchantConfiguration: GooglePayMerchantConfiguration) => [
            new WorldpayActions.GetGooglePayMerchantConfigurationSuccess(
              merchantConfiguration
            )
          ]),
          catchError(error => [
            new WorldpayActions.GetGooglePayMerchantConfigurationFail(error)
          ])
        ))
    );

  @Effect()
  googlePayAuthorisePayment: Observable<| CheckoutActions.PlaceOrderSuccess
    | CartActions.RemoveCart
    | GlobalMessageActions.AddMessage
    | WorldpayActions.AuthoriseGooglePayPaymentSuccess
    | WorldpayActions.AuthoriseGooglePayPaymentFail
    | WorldpayActions.ClearPaymentDetails> = this.actions$.pipe(
      ofType(WorldpayActions.AUTHORISE_GOOGLE_PAY_PAYMENT),
      map((action: any) => action.payload),
      mergeMap(({
        userId,
        cartId,
        token,
        billingAddress,
        savePaymentMethod
      }) => this.worldpayConnector
        .authoriseGooglePayPayment(
          userId,
          cartId,
          token,
          billingAddress,
          savePaymentMethod
        )
        .pipe(
          switchMap((placeOrderResponse: PlaceOrderResponse) => [
            new CheckoutActions.PlaceOrderSuccess(placeOrderResponse.order),
            new CartActions.RemoveCart({ cartId }),
            new WorldpayActions.AuthoriseGooglePayPaymentSuccess(
              placeOrderResponse
            ),
            new WorldpayActions.ClearPaymentDetails(),
          ]),
          catchError(error => [
            createGlobalMessageAction(
              'paymentForm.googlepay.authorisationFailed'
            ),
            new WorldpayActions.AuthoriseGooglePayPaymentFail(error)
          ])
        ))
    );

  @Effect()
  apmRedirectAuthorise: Observable<| WorldpayActions.GetAPMRedirectUrlSuccess
    | WorldpayActions.ClearPaymentDetails
    | GlobalMessageActions.AddMessage
    | WorldpayActions.GetAPMRedirectUrlFail> = this.actions$.pipe(
      ofType(WorldpayActions.GET_APM_REDIRECT_URL),
      map((action: any) => action.payload),
      mergeMap(({
        userId,
        cartId,
        apm,
        save
      }) => this.worldpayConnector
        .authoriseApmRedirect(userId, cartId, apm, save)
        .pipe(
          switchMap((payload: APMRedirectResponse) => [
            new WorldpayActions.GetAPMRedirectUrlSuccess(payload),
            new WorldpayActions.ClearPaymentDetails(),
          ]),
          catchError(error => [
            createGlobalMessageAction(),
            new WorldpayActions.GetAPMRedirectUrlFail(error)
          ])
        ))
    );

  @Effect()
  getAvailableApms: Observable<| WorldpayActions.GetAvailableApmsSuccess
    | WorldpayActions.GetAvailableApmsFail
    | GlobalMessageActions.AddMessage> = this.actions$.pipe(
      ofType(WorldpayActions.GET_AVAILABLE_APMS),
      map((action: any) => action.payload),
      mergeMap(({
        userId,
        cartId
      }) => this.worldpayConnector
        .getAvailableApms(userId, cartId)
        .pipe(
          switchMap((payload: ApmData[]) => [
            new WorldpayActions.GetAvailableApmsSuccess(payload)
          ]),
          catchError(error => [
            createGlobalMessageAction(),
            new WorldpayActions.GetAvailableApmsFail(error)
          ])
        ))
    );

  @Effect()
  isFraudSightEnabled: Observable<WorldpayActions.GetFraudSightEnabledSuccess> = this.actions$.pipe(
    ofType(WorldpayActions.IS_FRAUD_SIGHT_ENABLED),
    map((action: any) => action.payload),
    mergeMap(() => this.worldpayConnector
      .isFraudSightEnabled()
      .pipe(
        switchMap((payload: boolean) => [
          new WorldpayActions.GetFraudSightEnabledSuccess(payload)
        ]),
        catchError(() => [
          new WorldpayActions.GetFraudSightEnabledSuccess(false)
        ])
      ))
  );

  @Effect()
  placeRedirectOrder: Observable<| WorldpayActions.PlaceOrderRedirectFail
    | WorldpayActions.PlaceOrderRedirectSuccess
    | CheckoutActions.PlaceOrderSuccess
    | CartActions.RemoveCart
    | GlobalMessageActions.AddMessage> = this.actions$.pipe(
      ofType(WorldpayActions.PLACE_ORDER_REDIRECT),
      map((action: any) => action.payload),
      mergeMap(({
        userId,
        cartId
      }) => this.worldpayConnector
        .placeOrderRedirect(userId, cartId)
        .pipe(
          switchMap((payload: Order) => [
            new CheckoutActions.PlaceOrderSuccess(payload),
            new CartActions.RemoveCart({ cartId }),
            new WorldpayActions.PlaceOrderRedirectSuccess(payload),
          ]),
          catchError(error => [
            createGlobalMessageAction(),
            new WorldpayActions.PlaceOrderRedirectFail(error)
          ])
        ))
    );

}

const createGlobalMessageAction = (
  key: string = 'checkoutReview.initialPaymentRequestFailed'
): GlobalMessageActions.AddMessage => {
  const failMessage: GlobalMessage = {
    text: {
      key
    },
    type: GlobalMessageType.MSG_TYPE_ERROR
  };
  return new GlobalMessageActions.AddMessage(failMessage);
};

export const effects: any[] = [WorldpayEffects];
