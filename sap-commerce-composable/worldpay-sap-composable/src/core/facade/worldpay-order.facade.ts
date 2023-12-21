import { Injectable, ViewContainerRef } from '@angular/core';
import { facadeFactory, HttpErrorModel } from '@spartacus/core';
import { Observable } from 'rxjs';
import { PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';
import { ORDER_CORE_FEATURE, OrderFacade } from '@spartacus/order/root';
import { PaymentDetails } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayOrderFacade,
      feature: ORDER_CORE_FEATURE,
      methods: [
        'initialPaymentRequest',
        'executeDDC3dsJwtCommand',
        'challengeAccepted',
        'challengeFailed',
        'showErrorMessage',
        'placeRedirectOrder',
        'placeBankTransferRedirectOrder',
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
   */
  abstract initialPaymentRequest(
    unsafePaymentDetails: PaymentDetails,
    dfReferenceId: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
  ): Observable<PlaceOrderResponse>;

  /**
   * Abstract method used to execute DDC3 dsJwt Command
   * @since 6.4.0
   */
  abstract executeDDC3dsJwtCommand(): Observable<ThreeDsDDCInfo>;

  /**
   * Abstract method used to start loading
   * @since 6.4.0
   */
  abstract startLoading(vcr: ViewContainerRef): void;

  /**
   * Abstract method used to get loading state
   * @since 6.4.0
   */
  abstract clearLoading(): void;

  /**
   * Abstract method used to accept challenge
   * @since 6.4.0
   * @param code
   */
  abstract challengeAccepted(code: string): void;

  /**
   * Abstract method used to fail challenge
   *  @since 6.4.0
   */
  abstract challengeFailed(): void;

  /**
   * Abstract method used to get APM redirect url
   * @since 6.4.0
   * @param error
   */
  abstract showErrorMessage(error: HttpErrorModel): void;

  /**
   * Method used to place redirect order
   * @since 4.3.6
   */
  abstract placeRedirectOrder(): Observable<boolean>;

  /**
   *  Method used to place Bank transfer redirect order
   *  @since 4.3.6
   * @param orderId
   */
  abstract placeBankTransferRedirectOrder(orderId: string): Observable<boolean>;
}
