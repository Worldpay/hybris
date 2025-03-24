import { ComponentRef, DestroyRef, inject, Injectable, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import {
  Command,
  CommandService,
  CommandStrategy,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  LoggerService,
  PaymentDetails,
  RoutingService,
  UserIdService
} from '@spartacus/core';
import { OrderConnector, OrderService } from '@spartacus/order/core';
import { Order, OrderPlacedEvent } from '@spartacus/order/root';
import { LAUNCH_CALLER, LaunchDialogService } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { WorldpayApmConnector } from '../../connectors';
import { WorldpayACHConnector } from '../../connectors/worldpay-ach/worldpay-ach.connector';
import { WorldpayConnector } from '../../connectors/worldpay.connector';
import { ClearInitialPaymentRequestEvent, DDC3dsJwtSetEvent, InitialPaymentRequestSetEvent } from '../../events/checkout-payment.events';
import {
  ClearWorldpayACHPaymentFormEvent,
  ClearWorldpayPaymentDetailsEvent,
  SetWorldpaySaveAsDefaultCreditCardEvent,
  SetWorldpaySavedCreditCardEvent
} from '../../events/worldpay.events';
import { ACHPaymentForm, BrowserInfo, CSEPaymentForm, PlaceOrderResponse, ThreeDsDDCInfo, WorldpayChallengeResponse } from '../../interfaces';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';

@Injectable({
  providedIn: 'root'
})
export class WorldpayOrderService extends OrderService {

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  placedOrder: void | Observable<ComponentRef<any> | undefined>;
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected logger: LoggerService = inject(LoggerService);
  /**
   * Command used to initial payment request
   *
   * This command handles the initial payment request by executing the necessary
   * preconditions and then making a request to the Worldpay connector. It processes
   * the response to handle 3D Secure, authorised transactions, and failed transactions.
   *
   * @param {CSEPaymentForm} paymentDetails - The payment details
   * @param {string} dfReferenceId - The device fingerprint reference ID
   * @param {string} challengeWindowSize - The size of the challenge window
   * @param {string} cseToken - The client-side encryption token
   * @param {boolean} acceptedTermsAndConditions - Whether the terms and conditions are accepted
   * @param {string} deviceSession - The device session ID
   * @param {BrowserInfo} browserInfo - The browser information
   * @returns {Observable<PlaceOrderResponse>} - Observable that emits the place order response
   * @since 6.4.0
   */
  protected initialPaymentRequestCommand: Command<CSEPaymentForm, PlaceOrderResponse> = this.commandService.create<CSEPaymentForm, PlaceOrderResponse>(
    ({
      paymentDetails,
      dfReferenceId,
      challengeWindowSize,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
      browserInfo
    }: CSEPaymentForm): Observable<PlaceOrderResponse> => this.checkoutPreconditions().pipe(
      switchMap(([userId, cartId]: [string, string]): Observable<PlaceOrderResponse> => this.worldpayConnector.initialPaymentRequest(
        userId,
        cartId,
        paymentDetails,
        dfReferenceId,
        challengeWindowSize,
        cseToken,
        acceptedTermsAndConditions,
        deviceSession,
        browserInfo
      ).pipe(
        tap((response: PlaceOrderResponse): void => {
          if (response.threeDSecureNeeded === true) {
            const values: { [key: string]: string } = {};
            // eslint-disable-next-line @typescript-eslint/typedef
            response.threeDSecureInfo.threeDSFlexData.entry.map(kv => {
              values[kv.key] = kv.value;
            });

            Object.assign(response.threeDSecureInfo.threeDSFlexData, values);

            this.eventService.dispatch(response, InitialPaymentRequestSetEvent);
            this.clearLoading();
          } else if (response.transactionStatus === 'AUTHORISED') {
            this.eventService.dispatch(response, InitialPaymentRequestSetEvent);
            this.placedOrderSuccess(userId, cartId, response.order);
          } else {
            this.clearLoading();
            this.globalMessageService.add({ key: 'checkoutReview.initialPaymentRequestFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
          }
        })
      ))
    )
  );

  /**
   * Command used to get DDC3dsJwt
   *
   * This command retrieves the DDC3dsJwt by making a request to the Worldpay connector.
   * It then dispatches the DDC3dsJwtSetEvent with the retrieved DDC information.
   *
   * @returns {Observable<ThreeDsDDCInfo>} - Observable that emits the ThreeDsDDCInfo
   * @since 6.4.0
   */
  protected getDDC3dsJwtCommand: Command<undefined, ThreeDsDDCInfo> = this.commandService.create<undefined, ThreeDsDDCInfo>(
    () => this.worldpayConnector.getDDC3dsJwt().pipe(
      tap((ddcInfo: ThreeDsDDCInfo) => this.eventService.dispatch({
        ddcInfo
      }, DDC3dsJwtSetEvent))
    ),
    { strategy: CommandStrategy.CancelPrevious }
  );

  /**
   * Command used to handle challenge accepted
   *
   * This command processes the response from a challenge acceptance by retrieving the order
   * from the Worldpay connector and dispatching the necessary events.
   *
   * @param {WorldpayChallengeResponse} response - The response from the challenge acceptance
   * @returns {Observable<Order>} - Observable that emits the order
   * @since 6.4.0
   */
  protected challengeAcceptedCommand: Command<WorldpayChallengeResponse, Order> = this.commandService.create<WorldpayChallengeResponse, Order>(
    (response: WorldpayChallengeResponse): Observable<Order> => this.checkoutPreconditions().pipe(
      switchMap(([userId, cartId]: [string, string]): Observable<Order> =>
        this.worldpayConnector.getOrder(response.customerID || userId, response.orderCode, response.guestCustomer).pipe(
          tap((order: Order): void => {
            this.eventService.dispatch({
              userId,
              cartId,
              order,
              cartCode: cartId
            }, OrderPlacedEvent);
            this.eventService.dispatch({}, ClearWorldpayPaymentDetailsEvent);
          })
        )
      )
    ),
    { strategy: CommandStrategy.CancelPrevious }
  );

  /**
   * Command used to place redirect order
   *
   * This command handles the placement of a redirect order by executing the necessary
   * preconditions and then making a request to the Worldpay APM connector. It processes
   * the response to handle the successful placement of the order.
   *
   * @returns {Observable<void>} - Observable that emits void
   * @since 6.4.0
   */
  protected placeRedirectOrderCommand: Command<void> =
    this.commandService.create<void>(
      () => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<Order> =>
          this.worldpayApmConnector.placeOrderRedirect(userId, cartId).pipe(
            tap((order: Order): void => {
              this.placedOrderSuccess(userId, cartId, order);
            })
          )
        )
      ),
      { strategy: CommandStrategy.CancelPrevious, }
    );

  /**
   * Command used to place bank transfer redirect order
   *
   * This command handles the placement of a bank transfer redirect order by executing the necessary
   * preconditions and then making a request to the Worldpay APM connector. It processes
   * the response to handle the successful placement of the order.
   *
   * @param {string} orderId - The ID of the order to be placed
   * @returns {Observable<void>} - Observable that emits void
   * @since 6.4.0
   */
  protected placeBankTransferRedirectOrderCommand: Command<string> =
    this.commandService.create<string>(
      (orderId: string) => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<Order> =>
          this.worldpayApmConnector.placeBankTransferOrderRedirect(userId, cartId, orderId).pipe(
            tap((order: Order): void => {
              this.placedOrderSuccess(userId, cartId, order);
            })
          )
        )
      ),
      { strategy: CommandStrategy.CancelPrevious, }
    );

  /**
   * Command used to place ACH order
   *
   * This command handles the placement of an ACH order by executing the necessary
   * preconditions and then making a request to the Worldpay ACH connector. It processes
   * the response to handle the successful placement of the order.
   *
   * @param {ACHPaymentForm} achPaymentForm - The form data for the ACH order
   * @returns {Observable<Order>} - Observable that emits the placed order
   * @since 6.4.2
   */
  protected placeACHOrderCommand$: Command<ACHPaymentForm, Order> =
    this.commandService.create<ACHPaymentForm, Order>(
      (achPaymentForm: ACHPaymentForm): Observable<Order> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<Order> =>
          this.worldpayACHConnector.placeACHOrder(
            userId,
            cartId,
            achPaymentForm
          ).pipe(
            tap((response: Order): void => {
              this.placedOrderSuccess(userId, cartId, response);
              this.eventService.dispatch({}, ClearWorldpayACHPaymentFormEvent);
            })
          )
        )
      )
    );

  /**
   * Constructor for the WorldpayOrderService
   *
   * @param {ActiveCartFacade} activeCartFacade - The active cart facade
   * @param {UserIdService} userIdService - The user ID service
   * @param {CommandService} commandService - The command service
   * @param {OrderConnector} orderConnector - The order connector
   * @param {EventService} eventService - The event service
   * @param {GlobalMessageService} globalMessageService - The global message service
   * @param {LaunchDialogService} launchDialogService - The launch dialog service
   * @param {RoutingService} routingService - The routing service
   * @param {WorldpayCheckoutPaymentService} worldpayCheckoutPaymentService - The Worldpay checkout payment service
   * @param {WorldpayConnector} worldpayConnector - The Worldpay connector
   * @param {WorldpayApmConnector} worldpayApmConnector - The Worldpay APM connector
   * @param {WorldpayACHConnector} worldpayACHConnector - The Worldpay ACH connector
   */
  constructor(
    protected override activeCartFacade: ActiveCartFacade,
    protected override userIdService: UserIdService,
    protected override commandService: CommandService,
    protected override orderConnector: OrderConnector,
    protected override eventService: EventService,
    protected globalMessageService: GlobalMessageService,
    protected launchDialogService: LaunchDialogService,
    protected routingService: RoutingService,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
    protected worldpayConnector: WorldpayConnector,
    protected worldpayApmConnector: WorldpayApmConnector,
    protected worldpayACHConnector: WorldpayACHConnector
  ) {
    super(activeCartFacade, userIdService, commandService, orderConnector, eventService);
    this.listenTriggerDDC3dsJwtSetEvent();
  }

  /**
   * Listens for the DDC3dsJwtSetEvent and handles the event when triggered.
   *
   * This method sets up a listener for the DDC3dsJwtSetEvent and dispatches the
   * ClearInitialPaymentRequestEvent with null values for 3DS challenge and DDC information
   * when the ClearWorldpayPaymentDetailsEvent is received.
   *
   * @since 6.4.0
   */
  listenTriggerDDC3dsJwtSetEvent(): void {
    this.worldpayCheckoutPaymentService.listenSetThreeDsDDCInfoEvent();

    this.eventService.get(ClearWorldpayPaymentDetailsEvent)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (): void => {
          this.eventService.dispatch({
            threeDsChallengeInfo: null,
            threeDsChallengeIframeUrl: null,
            threeDsDDCIframeUrl: null,
            threeDsDDCInfo: null
          }, ClearInitialPaymentRequestEvent);
        }
      });
  }

  /**
   * Executes the DDC3dsJwtCommand to retrieve the DDC3dsJwt.
   *
   * This method executes the command to get the DDC3dsJwt by making a request to the Worldpay connector.
   * It returns an Observable that emits the ThreeDsDDCInfo.
   *
   * @returns {Observable<ThreeDsDDCInfo>} - Observable that emits the ThreeDsDDCInfo
   * @since 6.4.0
   */
  executeDDC3dsJwtCommand(): Observable<ThreeDsDDCInfo> {
    return this.getDDC3dsJwtCommand.execute(undefined);
  }

  /**
   * Initiates the initial payment request.
   *
   * This method handles the initial payment request by sanitizing the payment details,
   * determining the challenge window size, and dispatching the ClearInitialPaymentRequestEvent.
   * It then executes the initialPaymentRequestCommand with the provided parameters.
   *
   * @param {PaymentDetails} unsafePaymentDetails - The payment details, including sensitive information
   * @param {string} dfReferenceId - The device fingerprint reference ID
   * @param {string} cseToken - The client-side encryption token
   * @param {boolean} acceptedTermsAndConditions - Whether the terms and conditions are accepted
   * @param {string} deviceSession - The device session ID
   * @param {BrowserInfo} browserInfo - The browser information
   * @returns {Observable<PlaceOrderResponse>} - Observable that emits the place order response
   * @since 6.4.0
   */
  initialPaymentRequest(
    unsafePaymentDetails: PaymentDetails,
    dfReferenceId: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
    browserInfo: BrowserInfo
  ): Observable<PlaceOrderResponse> {

    const paymentDetails: PaymentDetails = { ...unsafePaymentDetails };
    delete paymentDetails.cardNumber;
    const challengeWindowSize: string = window.innerWidth >= 620 ? '600x400' : '390x400';

    this.eventService.dispatch({
      threeDsChallengeInfo: null,
      threeDsChallengeIframeUrl: null,
      threeDsDDCIframeUrl: null,
      threeDsDDCInfo: null
    }, ClearInitialPaymentRequestEvent);

    return this.initialPaymentRequestCommand.execute({
      paymentDetails,
      cseToken,
      dfReferenceId,
      challengeWindowSize,
      acceptedTermsAndConditions,
      deviceSession,
      browserInfo
    });
  }

  /**
   * Handles the acceptance of a 3DS challenge.
   *
   * This method processes the response from a 3DS challenge acceptance by executing the challengeAcceptedCommand.
   * It subscribes to the command's observable to handle the order placement and navigation to the order confirmation page.
   * In case of an error, it logs the error and displays a global error message.
   *
   * @param {WorldpayChallengeResponse} worldpayChallengeResponse - The response from the 3DS challenge acceptance
   * @since 6.4.0
   */
  challengeAccepted(worldpayChallengeResponse: WorldpayChallengeResponse): void {
    this.challengeAcceptedCommand.execute(worldpayChallengeResponse).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (order: Order): void => {
          this.setPlacedOrder(order);
          this.routingService.go({ cxRoute: 'orderConfirmation' });
        },
        error: (error: unknown): void => {
          this.logger.error('Challenge Failed:', error);
          this.globalMessageService.add({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
        }
      });
  }

  /**
   * Handles the failure of a 3DS challenge.
   *
   * This method adds a global error message indicating the challenge failure
   * and dispatches the ClearWorldpayPaymentDetailsEvent to clear the payment details.
   *
   * @since 6.4.0
   */
  challengeFailed(): void {
    this.globalMessageService.add({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
    this.eventService.dispatch({}, ClearWorldpayPaymentDetailsEvent);
  }

  /**
   * Places a redirect order.
   *
   * This method executes the placeRedirectOrderCommand and then retrieves the order details.
   * It returns an Observable that emits true if the order details are not empty, otherwise false.
   *
   * @returns {Observable<boolean>} - Observable that emits a boolean indicating the success of the order placement
   * @since 6.4.0
   */
  placeRedirectOrder(): Observable<boolean> {
    return this.placeRedirectOrderCommand.execute().pipe(
      switchMap(() =>
        this.getOrderDetails().pipe(
          map((orderDetails: Order): boolean => !!orderDetails && Object.keys(orderDetails).length !== 0
          )
        )
      )
    );
  }

  /**
   * Places a bank transfer redirect order.
   *
   * This method executes the placeBankTransferRedirectOrderCommand and then retrieves the order details.
   * It returns an Observable that emits true if the order details are not empty, otherwise false.
   *
   * @param {string} orderId - The ID of the order to be placed
   * @returns {Observable<boolean>} - Observable that emits a boolean indicating the success of the order placement
   * @since 6.4.0
   */
  placeBankTransferRedirectOrder(orderId: string): Observable<boolean> {
    return this.placeBankTransferRedirectOrderCommand.execute(orderId).pipe(
      switchMap(() =>
        this.getOrderDetails().pipe(
          map((orderDetails: Order): boolean => !!orderDetails && Object.keys(orderDetails).length !== 0
          )
        )
      )
    );
  }

  /**
   * Executes the ACH order placement command.
   *
   * This method handles the placement of an ACH order by executing the placeACHOrderCommand.
   * It returns an Observable that emits the placed order.
   *
   * @param {ACHPaymentForm} achPaymentForm - The form data for the ACH order
   * @returns {Observable<Order>} - Observable that emits the placed order
   * @since 6.4.2
   */
  placeACHOrder(achPaymentForm: ACHPaymentForm): Observable<Order> {
    return this.placeACHOrderCommand$.execute(achPaymentForm);
  }

  /**
   * Handles the successful placement of an order.
   *
   * This method sets the placed order, dispatches the OrderPlacedEvent,
   * and clears the Worldpay payment details. It also dispatches events
   * to indicate that the saved credit card and save as default credit card
   * options are not selected.
   *
   * @param {string} userId - The ID of the user
   * @param {string} cartId - The ID of the cart
   * @param {Order} order - The placed order
   * @since 6.4.0
   */
  placedOrderSuccess(userId: string, cartId: string, order: Order): void {
    this.setPlacedOrder(order);
    this.eventService.dispatch(
      {
        userId,
        cartId,
        cartCode: cartId,
        order,
      },
      OrderPlacedEvent
    );
    this.eventService.dispatch({}, ClearWorldpayPaymentDetailsEvent);
    this.eventService.dispatch({ saved: false }, SetWorldpaySavedCreditCardEvent);
    this.eventService.dispatch({ saved: false }, SetWorldpaySaveAsDefaultCreditCardEvent);
  }

  /**
   * Start loading spinner.
   *
   * This method launches a loading spinner dialog using the provided ViewContainerRef.
   * It sets the placedOrder property to the result of the launchDialogService.launch method.
   *
   * @param {ViewContainerRef} vcr - The ViewContainerRef to attach the loading spinner dialog to
   * @since 6.4.0
   */
  startLoading(vcr: ViewContainerRef): void {
    this.placedOrder = this.launchDialogService.launch(
      LAUNCH_CALLER.PLACE_ORDER_SPINNER,
      vcr
    );
  }

  /**
   * Clear loading spinner.
   *
   * This method clears the loading spinner dialog if the placedOrder property is defined.
   * It subscribes to the placedOrder observable and calls the clear method of the launchDialogService.
   * If a component is provided, it destroys the component.
   *
   * @since 6.4.0
   */
  clearLoading(): void {
    if (!this.placedOrder) {
      return;
    }

    this.placedOrder.subscribe({
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      next: (component: ComponentRef<any>): void => {
        this.launchDialogService.clear(LAUNCH_CALLER.PLACE_ORDER_SPINNER);
        if (component) {
          component.destroy();
        }
      }
    }).unsubscribe();
  }
}
