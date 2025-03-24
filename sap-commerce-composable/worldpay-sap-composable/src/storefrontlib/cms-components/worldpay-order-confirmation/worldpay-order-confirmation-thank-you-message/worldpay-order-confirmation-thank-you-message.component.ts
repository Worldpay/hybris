import { Component } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { GlobalMessageService, TranslationService } from '@spartacus/core';
import { OrderConfirmationThankYouMessageComponent } from '@spartacus/order/components';
import { Order, OrderFacade } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { filter, withLatestFrom } from 'rxjs/operators';

@Component({
  selector: 'y-worldpay-order-confirmation-thank-you-message-component',
  templateUrl: './worldpay-order-confirmation-thank-you-message.component.html',
})
export class WorldpayOrderConfirmationThankYouMessageComponent extends OrderConfirmationThankYouMessageComponent {

  orderConfirmationMessage: string = 'checkoutOrderConfirmation.thankYou';
  invoiceSentMessage: string = 'checkoutOrderConfirmation.invoiceHasBeenSentByEmail';

  /**
   * Constructor
   * @since 6.4.0
   * @param orderFacade OrderFacade
   * @param globalMessageService GlobalMessageService
   * @param translationService TranslationService
   * @param route ActivatedRoute
   */
  constructor(
    protected override orderFacade: OrderFacade,
    protected override globalMessageService: GlobalMessageService,
    protected override translationService: TranslationService,
    private route: ActivatedRoute,
  ) {
    super(
      orderFacade,
      globalMessageService,
      translationService,
    );
  }

  /**
   * Get Thank You Assistive Message
   * @since 6.4.0
   */
  protected override getThankYouAssistiveMessage(): Observable<[Order | undefined, string, string, string]> {
    const params: Params = this.route.snapshot.queryParams;
    // eslint-disable-next-line no-prototype-builtins
    if (params.hasOwnProperty('pending') && params.pending.toString() === 'true') {
      this.orderConfirmationMessage = 'checkoutOrderConfirmation.pending.thankYouForOrder';
      this.invoiceSentMessage = 'checkoutOrderConfirmation.pending.invoiceHasBeenSentByEmail';
    }

    const confirmationOfOrderMessage$: Observable<string> = this.translationService.translate('checkoutOrderConfirmation.confirmationOfOrder');
    const thankYouMessage$: Observable<string> = this.translationService.translate(this.orderConfirmationMessage);
    const invoiceHasBeenSentByEmailMessage$: Observable<string> = this.translationService.translate(this.invoiceSentMessage);

    return this.order$.pipe(
      filter((order: Order) => !!order),
      withLatestFrom(
        confirmationOfOrderMessage$,
        thankYouMessage$,
        invoiceHasBeenSentByEmailMessage$
      )
    );
  }
}
