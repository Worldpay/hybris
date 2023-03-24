import { Injectable } from '@angular/core';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { select, Store } from '@ngrx/store';
import { combineLatest, Observable } from 'rxjs';
import { getWorldpayGooglePayMerchantConfiguration } from '../../store/worldpay.selectors';
import { AuthoriseGooglePayPayment, GetGooglePayMerchantConfiguration, AuthoriseGooglePayPaymentFail } from '../../store/worldpay.action';
import { ActiveCartService, Cart, UserIdService } from '@spartacus/core';
import { filter, take } from 'rxjs/operators';
import { GooglePayMerchantConfiguration, GooglePayPaymentRequest } from '../../interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorldpayGooglepayService {
  constructor(
    protected userIdService: UserIdService,
    protected activeCartService: ActiveCartService,
    protected worldpayStore: Store<StateWithWorldpay>
  ) {
  }

  /**
   * Retrieve the google pay merchant configuration from state
   */
  getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration> {
    return this.worldpayStore.pipe(
      select(getWorldpayGooglePayMerchantConfiguration)
    );
  }

  /**
   * Dispatch call to request merchant information from OCC
   */
  public requestMerchantConfiguration(): void {
    const userId = this.getUserId();
    const cartId = this.getCartId();

    this.worldpayStore.dispatch(
      new GetGooglePayMerchantConfiguration({
        userId,
        cartId
      })
    );
  }

  authoriseOrder(
    paymentRequest: GooglePayPaymentRequest,
    savePaymentMethod: boolean
  ): void {
    combineLatest([
      this.userIdService.getUserId(),
      this.activeCartService.getActiveCartId(),
    ]).pipe(
      filter(([userId, cartId]) => !!userId && !!cartId),
      take(1),
    ).subscribe(([userId, cartId]) => {
      const billingAddress = paymentRequest.paymentMethodData.info.billingAddress;
      const token = JSON.parse(
        paymentRequest.paymentMethodData.tokenizationData.token
      );

      this.worldpayStore.dispatch(
        new AuthoriseGooglePayPayment({
          userId,
          cartId,
          token,
          billingAddress,
          savePaymentMethod
        })
      );
    });
  }

  private getCartId(): string {
    let cartId: string;
    this.activeCartService
      .getActiveCartId()
      .pipe(take(1))
      .subscribe((res: string) => (cartId = res))
      .unsubscribe();
    return cartId;
  }

  private getUserId(): string {
    let userId: string;
    this.userIdService
      .getUserId()
      .pipe(take(1))
      .subscribe((res: string) => (userId = res))
      .unsubscribe();
    return userId;
  }

  createInitialPaymentRequest(
    merchantConfiguration: GooglePayMerchantConfiguration
  ): { apiVersionMinor: number; apiVersion: number; allowedPaymentMethods: { type: string; parameters: { allowedAuthMethods: string[]; billingAddressRequired: boolean; billingAddressParameters: { format: string }; allowedCardNetworks: string[] } }[] } {
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

  createFullPaymentRequest(
    merchantConfiguration: GooglePayMerchantConfiguration,
    cart: Cart
  ): any {
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

  canceledPaymentRequest(error): void {
    this.worldpayStore.dispatch(new AuthoriseGooglePayPaymentFail(error));
  }
}
