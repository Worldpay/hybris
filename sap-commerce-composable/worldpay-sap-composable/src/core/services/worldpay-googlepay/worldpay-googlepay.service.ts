import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';
import { Address, Command, CommandService, CommandStrategy, EventService, GlobalMessageService, LoggerService, OCC_USER_ID_ANONYMOUS, UserIdService } from '@spartacus/core';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { WorldpayGooglePayConnector } from '../../connectors/worldpay-googlepay/worldpay-googlepay.connector';
import { ClearWorldpayPaymentStateEvent } from '../../events';
import { GooglePayMerchantConfigurationSetEvent } from '../../events/googlepay.events';
import { GooglePayMerchantConfiguration, GooglepayPaymentRequest, GooglePayPaymentRequest, PlaceOrderResponse } from '../../interfaces';
import { WorldpayOrderService } from '../worldpay-order/worldpay-order.service';

@Injectable({
  providedIn: 'root'
})
export class WorldpayGooglepayService {

  protected googlePayMerchantConfiguration$: BehaviorSubject<GooglePayMerchantConfiguration> = new BehaviorSubject<GooglePayMerchantConfiguration>(null);
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected logger: LoggerService = inject(LoggerService);

  protected requestMerchantConfigurationCommand: Command<void, GooglePayMerchantConfiguration> = this.commandService.create<void>(
    (): Observable<GooglePayMerchantConfiguration> => this.checkoutPreconditions().pipe(
      switchMap(([userId, cartId]: [string, string]): Observable<GooglePayMerchantConfiguration> =>
        this.worldpayGooglePayConnector.getGooglePayMerchantConfiguration(userId, cartId).pipe(
          tap((response: GooglePayMerchantConfiguration): void => {
            this.eventService.dispatch({
              googlePayMerchantConfiguration: response
            }, GooglePayMerchantConfigurationSetEvent);
          })
        )
      )
    ), { strategy: CommandStrategy.Queue }
  );
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  protected authoriseOrderCommand: Command<{ paymentRequest: GooglePayPaymentRequest; savePaymentMethod: boolean }, PlaceOrderResponse | any> =
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.commandService.create<{ paymentRequest: GooglePayPaymentRequest; savePaymentMethod: boolean }, PlaceOrderResponse | any>(
      ({
        paymentRequest,
        savePaymentMethod
      }: { paymentRequest: GooglePayPaymentRequest; savePaymentMethod: boolean }): Observable<PlaceOrderResponse> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<PlaceOrderResponse> => {
          const billingAddress: Address = paymentRequest.paymentMethodData.info.billingAddress;
          // eslint-disable-next-line  @typescript-eslint/no-explicit-any
          const token: any = JSON.parse(paymentRequest.paymentMethodData.tokenizationData.token);
          return this.worldpayGooglePayConnector.authoriseGooglePayPayment(userId, cartId, token, billingAddress, savePaymentMethod).pipe(
            tap((response: PlaceOrderResponse): void => {
              this.eventService.dispatch({}, ClearWorldpayPaymentStateEvent);
              this.eventService.dispatch(
                {
                  userId,
                  cartId,
                  /**
                   * As we know the cart is not anonymous (precondition checked),
                   * we can safely use the cartId, which is actually the cart.code.
                   */
                  cartCode: cartId,
                  order: response.order,
                },
                OrderPlacedEvent
              );
            })
          );
        })
      ), { strategy: CommandStrategy.CancelPrevious }
    );

  /**
   * Constructor
   * @since 6.4.0
   * @param userIdService - UserIdService
   * @param activeCartFacade - ActiveCartFacade
   * @param worldpayOrderService - WorldpayOrderService
   * @param commandService - CommandService
   * @param eventService - EventService
   * @param globalMessageService - GlobalMessageService
   * @param worldpayGooglePayConnector - WorldpayGooglePayConnector
   */
  constructor(
    protected userIdService: UserIdService,
    protected activeCartFacade: ActiveCartFacade,
    protected worldpayOrderService: WorldpayOrderService,
    protected commandService: CommandService,
    protected eventService: EventService,
    protected globalMessageService: GlobalMessageService,
    protected worldpayGooglePayConnector: WorldpayGooglePayConnector
  ) {
    this.onGooglePayMerchantConfigurationSetEvent();
    this.onClearWorldpayStateEvent();
  }

  /**
   * Set the google pay merchant configuration in state
   * @since 6.4.0
   * @param googlePayMerchantConfiguration
   */
  setGooglepayMerchantConfiguration(googlePayMerchantConfiguration: GooglePayMerchantConfiguration): void {
    this.googlePayMerchantConfiguration$.next(googlePayMerchantConfiguration);
  }

  /**
   * Retrieve the google pay merchant configuration from state
   * @since 6.4.0
   */
  getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration> {
    return this.googlePayMerchantConfiguration$.asObservable();
  }

  /**
   * Observable Dispatch call to request merchant information from OCC
   * @since 6.4.0
   */
  public executeRequestMerchantConfiguration(): Observable<GooglePayMerchantConfiguration> {
    return this.requestMerchantConfigurationCommand.execute();
  }

  /**
   * Dispatch call to request merchant information from OCC
   * @since 6.4.0
   */
  public requestMerchantConfiguration(): void {
    this.executeRequestMerchantConfiguration().pipe(takeUntilDestroyed(this.destroyRef)).subscribe();
  }

  /**
   * Authorise Order
   * @since 6.4.0
   * @param paymentRequest - GooglePayPaymentRequest
   * @param savePaymentMethod - boolean 'true' to save payment data.
   */
  authoriseOrder(
    paymentRequest: GooglePayPaymentRequest,
    savePaymentMethod: boolean
  ): void {
    this.executeAuthoriseOrderCommand(paymentRequest, savePaymentMethod).pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (response: PlaceOrderResponse): void => {
        this.worldpayOrderService.setPlacedOrder(response.order);
      },
      error: (error: unknown): void => {
        this.logger.log(error);
      }
    });
  }

  /**
   * Create the initial payment request
   * @since 4.3.6
   * @param merchantConfiguration - GooglePayMerchantConfiguration
   * @returns GooglepayPaymentRequest - GooglepayPaymentRequest
   */
  createInitialPaymentRequest(merchantConfiguration: GooglePayMerchantConfiguration): GooglepayPaymentRequest {
    return {
      apiVersion: 2,
      apiVersionMinor: 0,
      allowedPaymentMethods: [
        {
          type: 'CARD',
          parameters: {
            allowedAuthMethods: merchantConfiguration.allowedAuthMethods,
            allowedCardNetworks: merchantConfiguration.allowedCardNetworks,
            billingAddressRequired: true,
            billingAddressParameters: {
              format: 'FULL'
            }
          }
        }
      ]
    };
  }

  /**
   * Create the full payment request
   * @since 4.3.6
   * @param merchantConfiguration - GooglePayMerchantConfiguration
   * @param cart - Cart
   * @returns GooglepayPaymentRequest - GooglepayPaymentRequest
   */
  createFullPaymentRequest(
    merchantConfiguration: GooglePayMerchantConfiguration,
    cart: Cart
  ): GooglepayPaymentRequest {
    return {
      apiVersion: 2,
      apiVersionMinor: 0,
      allowedPaymentMethods: [
        {
          type: merchantConfiguration.cardType,
          parameters: {
            allowedAuthMethods: merchantConfiguration.allowedAuthMethods,
            allowedCardNetworks: merchantConfiguration.allowedCardNetworks,
            billingAddressRequired: true,
            billingAddressParameters: {
              format: 'FULL'
            }
          },
          tokenizationSpecification: {
            type: 'PAYMENT_GATEWAY',
            parameters: {
              gateway: 'worldpay',
              gatewayMerchantId: merchantConfiguration.gatewayMerchantId
            }
          }
        }
      ],
      merchantInfo: {
        merchantName: merchantConfiguration.merchantName || '',
        merchantId: merchantConfiguration.merchantId || ''
      },
      transactionInfo: {
        currencyCode: cart.totalPrice.currencyIso,
        totalPrice: `${cart.totalPrice.value}`,
        totalPriceStatus: 'FINAL'
      }
    };
  }

  /**
   * Get current user id and cart id
   * @since 6.4.0
   * @returns Observable<[string, string]> - Observable with user id and cart id
   */
  checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]: [string, string, boolean]): [string, string] => {
        if (
          !userId ||
          !cartId ||
          (userId === OCC_USER_ID_ANONYMOUS && !isGuestCart)
        ) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  /**
   * Listen to GooglePayMerchantConfigurationSetEvent
   * @since 6.4.0
   */
  protected onGooglePayMerchantConfigurationSetEvent(): void {
    this.eventService.get(GooglePayMerchantConfigurationSetEvent).subscribe({
      next: (event: GooglePayMerchantConfigurationSetEvent): void => {
        this.setGooglepayMerchantConfiguration(event.googlePayMerchantConfiguration);
      }
    });
  }

  /**
   * Listen to ClearWorldpayStateEvent
   * @since 6.4.0
   */
  protected onClearWorldpayStateEvent(): void {
    this.eventService.get(ClearWorldpayPaymentStateEvent).subscribe({
      next: (): void => {
        this.setGooglepayMerchantConfiguration(null);
      }
    });
  }

  /**
   * Execute Authorise Order Command
   * @since 6.4.0
   * @param paymentRequest - GooglePayPaymentRequest
   * @param savePaymentMethod - boolean 'true' to save payment data.
   * @returns Observable<PlaceOrderResponse> - PlaceOrderResponse as Observable
   */
  protected executeAuthoriseOrderCommand(
    paymentRequest: GooglePayPaymentRequest,
    savePaymentMethod: boolean
  ): Observable<PlaceOrderResponse> {
    return this.authoriseOrderCommand.execute({
      paymentRequest,
      savePaymentMethod
    });
  }
}
