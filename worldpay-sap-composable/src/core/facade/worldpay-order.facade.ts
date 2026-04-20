import { Injectable, ViewContainerRef } from '@angular/core';
import { Params } from '@angular/router';
import { facadeFactory, PaymentDetails } from '@spartacus/core';
import { Order, OrderFacade, ScheduleReplenishmentForm } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { ACHPaymentForm, BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo, WorldpayChallengeResponse } from '../interfaces';
import { WORLDPAY_ORDER_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayOrderFacade =>
    facadeFactory({
      facade: WorldpayOrderFacade,
      feature: WORLDPAY_ORDER_FEATURE,
      methods: [
        'getOrderDetails',
        'clearPlacedOrder',
        'setPlacedOrder',
        'placeOrder',
        'placePaymentAuthorizedOrder',
        'getPickupEntries',
        'getDeliveryEntries',
        'initialPaymentRequest',
        'executeDDC3dsJwtCommand',
        'startLoading',
        'clearLoading',
        'challengeAccepted',
        'challengeFailed',
        'placeRedirectOrder',
        'placeBankTransferRedirectOrder',
        'placeACHOrder',
        'placedOrderSuccess',
        'listenTriggerDDC3dsJwtSetEvent',
      ],
    }),
})
export abstract class WorldpayOrderFacade extends OrderFacade {
  /**
   * Abstract method used to initiate payment request
   * @since 6.4.0
   * @param unsafePaymentDetails
   * @param dfReferenceId
   * @param cseToken
   * @param acceptedTermsAndConditions
   * @param deviceSession
   * @param browserInfo
   * @param scheduleReplenishmentFormData
   */
  abstract initialPaymentRequest(
    unsafePaymentDetails: PaymentDetails,
    dfReferenceId: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
    browserInfo: BrowserInfo,
    scheduleReplenishmentFormData?: ScheduleReplenishmentForm
  ): Observable<PlaceOrderResponse>;

  /**
   * Abstract method used to execute DDC3 dsJwt Command
   * @since 6.4.0
   */
  abstract executeDDC3dsJwtCommand(): Observable<ThreeDsDDCInfo>;

  /**
   * Abstract method used to start loading
   * @since 6.4.0
   * @param vcr ViewContainerRef
   */
  abstract startLoading(vcr: ViewContainerRef): void;

  /**
   * Abstract method used to clear loading
   * @since 6.4.0
   */
  abstract clearLoading(): void;

  /**
   * Abstract method used to accept challenge
   * @since 6.4.0
   * @param worldpayChallengeResponse
   */
  abstract challengeAccepted(worldpayChallengeResponse: WorldpayChallengeResponse): void;

  /**
   * Abstract method used to fail challenge
   *  @since 6.4.0
   *  @param key
   */
  abstract challengeFailed(key: string): void;

  /**
   * Method used to place redirect order
   * @since 4.3.6
   */
  abstract placeRedirectOrder(params: Params): Observable<boolean>;

  /**
   *  Method used to place Bank transfer redirect order
   *  @since 4.3.6
   * @param orderId
   */
  abstract placeBankTransferRedirectOrder(orderId: string): Observable<boolean>;

  /**
   * Method used to place ACH order
   * @since 6.4.2
   * @param achPaymentForm
   */
  abstract placeACHOrder(achPaymentForm: ACHPaymentForm): Observable<Order>;

  /**
   * Handles the successful placement of an order.
   *
   * This method sets the placed order, dispatches the OrderPlacedEvent,
   * and clears the Worldpay payment details. It also dispatches events
   * to indicate that the saved credit card and save as default credit card
   * options are not selected.
   *
   * @param userId - The ID of the user
   * @param cartId - The ID of the cart
   * @param order - The placed order
   * @since 6.4.0
   */
  abstract placedOrderSuccess(userId: string, cartId: string, order: Order): void;

  /**
   * Listens for the DDC3dsJwtSetEvent and handles the event when triggered.
   *
   * This method sets up a listener for the DDC3dsJwtSetEvent and dispatches the
   * ClearInitialPaymentRequestEvent with null values for 3DS challenge and DDC information
   * when the ClearWorldpayPaymentDetailsEvent is received.
   *
   * @since 6.4.0
   */
  abstract listenTriggerDDC3dsJwtSetEvent(): void;
}
