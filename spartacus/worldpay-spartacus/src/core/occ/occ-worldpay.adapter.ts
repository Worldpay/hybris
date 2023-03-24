import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Address, CmsComponent, ConverterService, OccEndpointsService, Order, PaymentDetails } from '@spartacus/core';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse, ThreeDsDDCInfo, WorldpayAdapter } from '../connectors/worldpay.adapter';
import { map, pluck } from 'rxjs/operators';
import { APM_NORMALIZER } from './converters';
import { ApmData, ApmPaymentDetails, APMRedirectRequestBody, APMRedirectResponse, GooglePayMerchantConfiguration, WorldpayApmPaymentInfo } from '../interfaces';
import { PAYMENT_DETAILS_SERIALIZER } from '@spartacus/checkout/core';

@Injectable()
export class OccWorldpayAdapter implements WorldpayAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService
  ) {
  }

  public create(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    cseToken: string
  ): Observable<PaymentDetails> {
    let body = {
      ...paymentDetails,
      cseToken
    };
    delete body.cardNumber;
    delete body.dateOfBirth;
    body = this.converter.convert(body, PAYMENT_DETAILS_SERIALIZER);

    return this.http.post<PaymentDetails>(
      this.occEndpoints.buildUrl(
        'createWorldpayPaymentDetails',
        {
          urlParams: {
            userId,
            cartId
          }
        }
      ),
      body,
      {}
    );
  }

  public useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails> {

    const url = this.occEndpoints.buildUrl(
      'useExistingPaymentDetails',
      {
        urlParams: {
          userId,
          cartId,
        },
        queryParams: {
          paymentDetailsId: paymentDetails.id
        }
      });
    const body = { ...paymentDetails };

    return this.http.put(url, body);
  }

  public getPublicKey(): Observable<string> {
    const options = {
      responseType: 'text' as 'json'
    };
    const url = this.occEndpoints.buildUrl('getPublicKey');

    return this.http.get<string>(
      url,
      options
    );
  }

  public setPaymentAddress(
    userId: string,
    cartId: string,
    address: Address
  ): Observable<any> {

    const body = {
      ...address,
      visibleInAddressBook: false,
    };

    const url = this.occEndpoints.buildUrl('setPaymentAddress', {
      urlParams: {
        userId,
        cartId,
      },
    });

    return this.http.post(url, body, {});
  }

  public getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    const url = this.occEndpoints.buildUrl('getDDC3dsJwt');
    return this.http.get<ThreeDsDDCInfo>(url);
  }

  public initialPaymentRequest(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    dfReferenceId: string,
    challengeWindowSize: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
  ): Observable<PlaceOrderResponse> {
    const body = {
      ...paymentDetails,
      challengeWindowSize,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
    };

    const url = this.occEndpoints.buildUrl(
      'initialPaymentRequest',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );

    return this.http.post<PlaceOrderResponse>(
      url,
      body,
      {}
    );
  }

  public getOrder(userId: string, code: string): Observable<Order> {
    const url = this.occEndpoints.buildUrl(
      'getOrder',
      {
        urlParams: {
          userId,
          code
        }
      }
    );
    return this.http.get(url);
  }

  public requestApplePayPaymentRequest(
    userId: string,
    cartId: string
  ): Observable<ApplePayPaymentRequest> {
    const url = this.occEndpoints.buildUrl(
      'requestApplePayPaymentRequest',
      {
        urlParams: {
          userId,
          cartId,
        }
      });
    return this.http.get<ApplePayPaymentRequest>(url);
  }

  validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<any> {
    const body = {
      validationURL
    };
    const url = this.occEndpoints.buildUrl(
      'validateApplePayMerchant',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );

    return this.http.post<any>(
      url,
      body,
      {}
    );
  }

  authorizeApplePayPayment(
    userId: string,
    cartId: string,
    request: any
  ): Observable<ApplePayAuthorization> {
    const body = {
      ...request
    };
    const url = this.occEndpoints.buildUrl(
      'authorizeApplePayPayment',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );

    return this.http.post<ApplePayAuthorization>(
      url,
      body,
      {}
    );
  }

  getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration> {
    const url = this.occEndpoints.buildUrl(
      'getGooglePayMerchantConfiguration',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.get<GooglePayMerchantConfiguration>(
      url
    );
  }

  authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: string,
    billingAddress: any,
    saved: boolean
  ): Observable<PlaceOrderResponse> {
    const body = {
      token,
      billingAddress,
      saved
    };
    const url = this.occEndpoints.buildUrl(
      'authoriseGooglePayPayment',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<PlaceOrderResponse>(
      url,
      body,
      {}
    );
  }

  authoriseApmRedirect(
    userId: string,
    cartId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse> {
    const body: APMRedirectRequestBody = {
      paymentMethod: apm.code,
      save,
    };

    if (apm.shopperBankCode) {
      body.shopperBankCode = apm.shopperBankCode;
    }

    const url = this.occEndpoints.buildUrl(
      'authoriseApmRedirect',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<APMRedirectResponse>(
      url,
      body,
      {}
    );
  }

  getAvailableApms(userId: string, cartId: string): Observable<ApmData[]> {
    const url = this.occEndpoints.buildUrl(
      'getAvailableApms',
      {
        urlParams: {
          userId,
          cartId
        }
      });
    return this.http.get<CmsComponent[]>(
      url,
    ).pipe(
      pluck('apmComponents'),
      this.converter.pipeableMany(APM_NORMALIZER)
    );

  }

  isFraudSightEnabled(): Observable<boolean> {
    const url = this.occEndpoints.buildUrl('isFraudSightEnabled');
    const options = {
      responseType: 'text' as 'json'
    };
    return this.http.get<string>(
      url,
      options,
    ).pipe(map((str) => str === 'true'));
  }

  placeRedirectOrder(userId: string, cartId: string): Observable<Order> {
    const url = this.occEndpoints.buildUrl(
      'placeRedirectOrder',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<Order>(
      url,
      {},
    );
  }

  setAPMPaymentInfo(
    userId: string,
    cartId: string,
    apmPaymentDetails: ApmPaymentDetails
  ): Observable<any> {

    const {
      billingAddress,
      code,
      name,
      shopperBankCode
    } = apmPaymentDetails;

    const body: WorldpayApmPaymentInfo = {
      billingAddress,
      apmName: shopperBankCode || name,
      apmCode: code
    };

    const url = this.occEndpoints.buildUrl('setAPMPaymentInfo', {
      urlParams: {
        userId,
        cartId,
      },
    });

    return this.http.post<any>(
      url,
      body,
      {}
    );
  }

  isGuaranteedPaymentsEnabled(): Observable<boolean> {
    const url = this.occEndpoints.buildUrl(
      'isGuaranteedPaymentsEnabled'
    );

    return this.http.get<boolean>(
      url,
      {},
    );
  }

}
