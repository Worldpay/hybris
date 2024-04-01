import { ComponentRef, Injectable, ViewContainerRef } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Command, CommandService, CommandStrategy, EventService, GlobalMessageService, GlobalMessageType, RoutingService, UserIdService } from '@spartacus/core';
import {BrowserInfo, CSEPaymentForm, PlaceOrderResponse, ThreeDsDDCInfo, WorldpayChallengeResponse } from '../../interfaces';
import { map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { ClearInitialPaymentRequestEvent, DDC3dsJwtSetEvent, InitialPaymentRequestSetEvent } from '../../events/checkout-payment.events';
import { Order, OrderPlacedEvent } from '@spartacus/order/root';
import { ActiveCartFacade, PaymentDetails } from '@spartacus/cart/base/root';
import { OrderConnector, OrderService } from '@spartacus/order/core';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayConnector } from '../../connectors/worldpay.connector';
import { ClearWorldpayPaymentDetailsEvent, SetWorldpaySaveAsDefaultCreditCardEvent, SetWorldpaySavedCreditCardEvent } from '../../events/worldpay.events';
import { LAUNCH_CALLER, LaunchDialogService } from '@spartacus/storefront';
import { WorldpayApmConnector } from '../../connectors';

@Injectable({
  providedIn: 'root'
})
export class WorldpayOrderService extends OrderService {

  protected drop: Subject<void> = new Subject<void>();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  placedOrder: void | Observable<ComponentRef<any> | undefined>;

  /**
   * Command used to initial payment request
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   * @param dfReferenceId string
   * @param challengeWindowSize string
   * @param cseToken string
   * @param acceptedTermsAndConditions boolean
   * @param deviceSession string
   * @param browserInfo BrowserInfo
   * @returns Observable<PlaceOrderResponse> - PlaceOrderResponse as Observable
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
    }: CSEPaymentForm) => this.checkoutPreconditions().pipe(
      switchMap(([userId, cartId]) => this.worldpayConnector.initialPaymentRequest(
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
   * @since 6.4.0
   * @returns Observable<ThreeDsDDCInfo> - ThreeDsDDCInfo as Observable
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
   * Command used to challenge accepted
   * @since 6.4.0
   * @param worldpayChallengeResponse WorldpayChallengeResponse
   * @returns Observable<Order> - Order as Observable
   */
  protected challengeAcceptedCommand: Command<WorldpayChallengeResponse, Order> = this.commandService.create<WorldpayChallengeResponse, Order>(
    (response: WorldpayChallengeResponse) => this.checkoutPreconditions().pipe(
      switchMap(([userId, cartId]) => this.worldpayConnector.getOrder(response.customerID || userId, response.orderCode, response.guestCustomer).pipe(
        tap((order: Order): void => {
          this.eventService.dispatch({
            userId,
            cartId,
            order,
            cartCode: cartId
          }, OrderPlacedEvent);
          this.eventService.dispatch({}, ClearWorldpayPaymentDetailsEvent);
        })
      ))
    ),
    { strategy: CommandStrategy.CancelPrevious }
  );

  /**
   * Command used to place redirect order
   * @since 6.4.0
   * @returns Observable<void> - void as Observable
   */
  protected placeRedirectOrderCommand: Command<void> =
    this.commandService.create<void>(
      () => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
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
   * @since 6.4.0
   * @returns Observable<void> - void as Observable
   */
  protected placeBankTransferRedirectOrderCommand: Command<string> =
    this.commandService.create<string>(
      (orderId: string) => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
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
   * Constructor
   * @param activeCartFacade ActiveCartFacade
   * @param userIdService UserIdService
   * @param commandService CommandService
   * @param orderConnector OrderConnector
   * @param eventService EventService
   * @param globalMessageService GlobalMessageService
   * @param launchDialogService LaunchDialogService
   * @param routingService RoutingService
   * @param worldpayCheckoutPaymentService WorldpayCheckoutPaymentService
   * @param worldpayConnector WorldpayConnector
   * @param worldpayApmConnector WorldpayApmConnector
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
  ) {
    super(activeCartFacade, userIdService, commandService, orderConnector, eventService);
    this.listenTriggerDDC3dsJwtSetEvent();
  }

  /**
   * Listen trigger DDC3dsJwtSetEvent
   * @since 6.4.0
   */
  listenTriggerDDC3dsJwtSetEvent(): void {
    this.worldpayCheckoutPaymentService.listenSetThreeDsDDCInfoEvent();

    this.eventService.get(ClearWorldpayPaymentDetailsEvent)
      .pipe(takeUntil(this.drop))
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
   * Excecute DDC3dsJwtCommand
   * @since 6.4.0
   * @returns - ThreeDsDDCInfo as Observable
   */
  executeDDC3dsJwtCommand(): Observable<ThreeDsDDCInfo> {
    return this.getDDC3dsJwtCommand.execute(undefined);
  }

  /**
   * Initial payment request
   * @param unsafePaymentDetails PaymentDetails
   * @param dfReferenceId string
   * @param cseToken string
   * @param acceptedTermsAndConditions boolean
   * @param deviceSession string
   * @param browserInfo BrowserInfo
   * @returns Observable<PlaceOrderResponse> - PlaceOrderResponse as Observable
   */
  initialPaymentRequest(
    unsafePaymentDetails: PaymentDetails,
    dfReferenceId: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
    browserInfo: BrowserInfo
  ): Observable<PlaceOrderResponse> {

    const paymentDetails = { ...unsafePaymentDetails };
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
   * Challenge accepted
   * @since 6.4.0
   * @param worldpayChallengeResponse WorldpayChallengeResponse
   */
  challengeAccepted(worldpayChallengeResponse: WorldpayChallengeResponse): void {
    this.challengeAcceptedCommand.execute(worldpayChallengeResponse).pipe(takeUntil(this.drop))
      .subscribe({
        next: (order: Order): void => {
          this.setPlacedOrder(order);
          this.routingService.go({ cxRoute: 'orderConfirmation' });
        },
        error: (error: unknown): void => {
          console.log('Challenge Failed:', error);
          this.globalMessageService.add({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
        }
      });
  }

  /**
   * Challenge failed
   * @since 6.4.0
   */
  challengeFailed(): void {
    this.globalMessageService.add({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
    this.eventService.dispatch({}, ClearWorldpayPaymentDetailsEvent);
  }

  /**
   * Place redirect order
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
   * Place bank transfer redirect order
   * @since 6.4.0
   * @param orderId string - Order ID
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
   * Placed order success
   * @since 6.4.0
   * @param userId string - User ID
   * @param cartId string - Cart ID
   * @param order Order
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
   * Start loading spinner
   * @since 6.4.0
   * @param vcr ViewContainerRef
   */
  startLoading(vcr: ViewContainerRef): void {
    this.placedOrder = this.launchDialogService.launch(
      LAUNCH_CALLER.PLACE_ORDER_SPINNER,
      vcr
    );
  }

  /**
   * Clear loading spinner
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

  /**
   * Destroy
   * @since 6.4.0
   */
  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
