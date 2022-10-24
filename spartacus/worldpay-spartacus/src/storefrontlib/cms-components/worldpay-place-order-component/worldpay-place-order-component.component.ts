import { ChangeDetectionStrategy, Component, NgZone, OnDestroy, OnInit, Renderer2, ViewContainerRef, ViewEncapsulation } from '@angular/core';
import { GlobalMessageService, GlobalMessageType, PaymentDetails, RoutingService, WindowRef } from '@spartacus/core';
import { LaunchDialogService, } from '@spartacus/storefront';
import { WorldpayCheckoutService } from '../../../core/services/worldpay-checkout/worldpay-checkout.service';
import { BehaviorSubject, combineLatest, Observable, Subject, Subscription } from 'rxjs';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { filter, take, takeUntil } from 'rxjs/operators';
import { FormBuilder, Validators } from '@angular/forms';
import { SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { APMRedirectResponse } from '../../../core/interfaces';
import { CheckoutService } from '@spartacus/checkout/core';
import { CheckoutReplenishmentFormService, CheckoutStepService, PlaceOrderComponent } from '@spartacus/checkout/components';

@Component({
  selector: 'y-worldpay-place-order',
  templateUrl: './worldpay-place-order-component.component.html',
  styleUrls: ['worldpay-place-order-component.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayPlaceOrderComponent extends PlaceOrderComponent implements OnInit, OnDestroy {

  paymentDetails: PaymentDetails;
  cseToken: string;

  placeOrderSubscription: Subscription;
  checkoutSubmitForm = this.fb.group({
    termsAndConditions: [false, Validators.requiredTrue]
  });

  paymentAuthorized = false;

  ddcIframeUrl$: Observable<SafeResourceUrl>;
  challengeIframeUrl$: Observable<SafeResourceUrl>;
  challengeIframeHeight: number;
  challengeIframeWidth: number;

  redirectData$: BehaviorSubject<APMRedirectResponse> = new BehaviorSubject<APMRedirectResponse>(null);
  isLoading$: Observable<boolean> = this.worldpayCheckoutService.getLoading();
  nativeWindow = this.winRef.nativeWindow;
  listenerFn: () => void;

  private ddcInfoFromState$: Subscription;
  private challengeInfo$: Subscription;
  private ddcHandler: any;
  private challengeHandler: any;
  private drop = new Subject<void>();
  private fraudSightId: string;

  constructor(
    protected checkoutService: CheckoutService,
    protected checkoutReplenishmentFormService: CheckoutReplenishmentFormService,
    protected routingService: RoutingService,
    protected launchDialogService: LaunchDialogService,
    protected fb: FormBuilder,
    protected vcr: ViewContainerRef,
    protected worldpayCheckoutService: WorldpayCheckoutService,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
    protected checkoutPaymentService: WorldpayCheckoutPaymentService,
    protected globalMessageService: GlobalMessageService,
    protected checkoutStepService: CheckoutStepService,
    protected activatedRoute: ActivatedRoute,
    protected worldpayApmService: WorldpayApmService,
    protected ngZone: NgZone,
    protected worldpayFraudsightService: WorldpayFraudsightService,
    protected winRef: WindowRef,
    private renderer: Renderer2
  ) {
    super(
      checkoutService,
      routingService,
      fb,
      checkoutReplenishmentFormService,
      launchDialogService,
      vcr
    );
  }

  submitForm(): void {
    if (this.checkoutSubmitForm.valid) {
      this.ddcIframeHandler();
      this.challengeIframeHandler();

      this.globalMessageService.add({
        key: 'checkoutReview.placingOrder'
      }, GlobalMessageType.MSG_TYPE_INFO);

      this.worldpayCheckoutService.startLoading();
      this.worldpayCheckoutService.placeOrder();
    } else {
      this.checkoutSubmitForm.markAllAsTouched();
    }
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.checkoutPaymentService
      .getPaymentDetails()
      .pipe(
        filter(
          paymentDetails => paymentDetails && Object.keys(paymentDetails).length > 0
        ),
        takeUntil(this.drop)
      )
      .subscribe((paymentDetails: PaymentDetails) => {
        this.paymentDetails = paymentDetails;
      })
      .unsubscribe();

    this.worldpayCheckoutPaymentService
      .getCseTokenFromState()
      .pipe(
        take(1),
        takeUntil(this.drop)
      )
      .subscribe(cseToken => {
        this.cseToken = cseToken;
      })
      .unsubscribe();

    this.placeOrderSubscription = this.checkoutService
      .getOrderDetails()
      .pipe(
        filter(order => Object.keys(order).length !== 0),
        takeUntil(this.drop)
      )
      .subscribe(() => {
        this.routingService.go({ cxRoute: 'orderConfirmation' });
      });

    if (!this.paymentDetails || Object.keys(this.paymentDetails).length === 0) {
      this.routingService.go(
        this.checkoutStepService.getPreviousCheckoutStepUrl(this.activatedRoute)
      );
    }

    this.ddcIframeUrl$ = this.worldpayCheckoutPaymentService.getThreeDsDDCIframeUrlFromState();
    this.challengeIframeUrl$ = this.worldpayCheckoutPaymentService.getThreeDsChallengeIframeUrlFromState();

    combineLatest([
      this.checkoutSubmitForm.controls.termsAndConditions.valueChanges,
      this.checkoutPaymentService.getPaymentDetails(),
      this.worldpayCheckoutPaymentService.getPaymentDetails(),
      this.worldpayCheckoutPaymentService.getCseTokenFromState()
    ]).pipe(
        takeUntil(this.drop)
      )
      .subscribe(([termsAndConditions, paymentDetails, pdw, cseToken]) => {
        this.paymentAuthorized =
          termsAndConditions &&
          paymentDetails &&
          Object.keys(paymentDetails).length > 0;
      });

    this.worldpayApmService
      .getWorldpayAPMRedirectUrl()
      .pipe(
        filter((r) => !!r),
        takeUntil(this.drop)
      )
      .subscribe(redirectData => {
        this.redirectData$.next(redirectData);
        this.ngZone.run(() => {
          setTimeout(() => {
            const form: HTMLFormElement = this.winRef.document.querySelector('#redirect-form');
            if (form) {
              form.submit();
            }
          }, 250);
        });
      });

    this.worldpayFraudsightService.getFraudSightIdFromState()
      .pipe(
        take(1),
        takeUntil(this.drop)
      ).subscribe((res) => this.fraudSightId = res);
  }

  ngOnDestroy(): void {
    if (this.ddcHandler) {
      this.nativeWindow.removeEventListener('message', this.ddcHandler, true);
    }
    if (this.challengeHandler) {
      this.nativeWindow.removeEventListener('message', this.challengeHandler, true);
    }

    if (this.listenerFn) {
      this.listenerFn();
    }

    this.drop.next();
    this.drop.complete();
  }

  /**
   * Populate this.ddcIframeUrl based on the available JWT for Device Data Collection
   */
  private ddcIframeHandler(): void {
    if (!this.ddcHandler) {
      this.ddcHandler = this.processDdc.bind(this);
      this.listenerFn = this.renderer.listen(this.nativeWindow, 'message', this.ddcHandler);
    }

    this.ddcInfoFromState$ = this.worldpayCheckoutPaymentService
      .getDDCInfoFromState()
      .pipe(
        filter(
          threeDsDDCInfo => !!threeDsDDCInfo && threeDsDDCInfo.jwt.length !== 0
        ),
        takeUntil(this.drop)
      )
      .subscribe(({
        ddcUrl,
        jwt
      }) => {
        this.worldpayCheckoutPaymentService.setThreeDsDDCIframeUrl(
          ddcUrl,
          this.paymentDetails.cardNumber,
          jwt
        );
      });
  }

  /**
   * Event handler for the Device Data Collection
   *
   * @param event event from the DDT iframe
   */
  private processDdc(event: MessageEvent): void {
    if (event.origin.includes('https://centinelapistag.cardinalcommerce.com')) {
      try {
        const deviceData: { SessionId: string; Status: boolean } = JSON.parse(
          event.data
        );
        if (deviceData && deviceData.Status) {
          this.worldpayCheckoutService.initialPaymentRequest(
            this.paymentDetails,
            deviceData.SessionId,
            this.cseToken,
            this.checkoutSubmitForm.get('termsAndConditions').value,
            this.fraudSightId
          );
          return;
        }
      } catch (err) {
        console.log('failed to process ddc response', err);
      }

      this.globalMessageService.add(
        {
          key: 'checkoutReview.threeDsChallengeFailed'
        },
        GlobalMessageType.MSG_TYPE_ERROR
      );
    }
  }

  /**
   * Show the 3ds Challenge when challenge is given during the initial payment request.
   * Initial payment request is kicked of after DDC - see this.processDdc
   */
  private challengeIframeHandler(): void {
    this.challengeInfo$ = this.worldpayCheckoutPaymentService
      .getThreeDsChallengeInfoFromState()
      .pipe(
        filter(challengeInfo => !!challengeInfo),
        takeUntil(this.drop)
      )
      .subscribe(({
        threeDSFlexData,
        merchantData
      }) => {
        if (!threeDSFlexData || !threeDSFlexData.jwt) {
          console.log('3ds flow not implemented');
          return;
        }
        this.challengeIframeHeight = 400;
        this.challengeIframeWidth = this.nativeWindow.innerWidth >= 620 ? 600 : 390;

        this.worldpayCheckoutPaymentService.setThreeDsChallengeIframeUrl(
          threeDSFlexData.challengeUrl,
          threeDSFlexData.jwt,
          merchantData
        );

        // unregister message handler for device detection (previous step in process)
        if (this.ddcHandler) {
          this.nativeWindow.removeEventListener('message', this.ddcHandler, true);
        }

        if (!this.challengeHandler) {
          this.challengeHandler = this.processChallenge.bind(this);
          this.listenerFn = this.renderer.listen(this.nativeWindow, 'message', this.challengeHandler);
        }
      });
  }

  private processChallenge(event: MessageEvent): void {
    if (this.challengeHandler) {
      this.nativeWindow.removeEventListener('message', this.challengeHandler, true);
    }

    try {
      const response: { accepted: boolean; orderCode: string } = event.data;
      if (!response.hasOwnProperty('accepted')) {
        // don't trigger on other message events
        return;
      }

      if (response.accepted) {
        this.worldpayCheckoutService.challengeAccepted(response.orderCode);
        return;
      }
    } catch (err) {
      console.log('Failed to process challenge event data', err);
    }
    this.worldpayCheckoutService.challengeFailed();
  }
}
