import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { LoadWorldpayGuaranteedPayments } from '../../store/worldpay-guaranteed-payments/actions/worldpay-guaranteed-payments-session-id.actions';
import { getWorldpayGuaranteedPaymentsSessionIdValue } from '../../store/worldpay-guaranteed-payments/selectors/worldpay-guaranteed-payments-session-id.selectors';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { LoadScriptService } from '../../utils/load-script.service';
import { WindowRef } from '@spartacus/core';
import { IsWorldpayGuaranteedPaymentsEnabled } from '../../store/worldpay-guaranteed-payments/actions/worldpay-guaranteed-payments-enabled.actions';
import { getWorldpayGuaranteedPaymentsEnabledValue } from '../../store/worldpay-guaranteed-payments/selectors/worldpay-guaranteed-payments-enabled.selectors';

@Injectable({
  providedIn: 'root'
})
export class WorldpayGuaranteedPaymentsService {

  window = this.winRef.nativeWindow as any;
  document = this.winRef.document as any;
  idScript = 'sig-api';
  idScriptTag = 'script-tag-tmx';
  sessionId = '';
  attributeId = 'data-order-session-id';
  userId: string;
  cartId: string;
  drop = new Subject();

  constructor(
    protected store: Store<StateWithWorldpay>,
    protected loadScriptService: LoadScriptService,
    protected winRef: WindowRef
  ) {
  }

  isGuaranteedPaymentsEnabled(): void {
    this.store.dispatch(new IsWorldpayGuaranteedPaymentsEnabled());
  }

  isGuaranteedPaymentsEnabledFromState(): Observable<boolean> {
    return this.store.pipe(select(getWorldpayGuaranteedPaymentsEnabledValue));
  }

  getSessionId(): Observable<string> {
    return this.store.pipe(select(getWorldpayGuaranteedPaymentsSessionIdValue));
  }

  setSessionId(sessionId): void {
    this.store.dispatch(new LoadWorldpayGuaranteedPayments(sessionId));
  }

  generateScript(sessionId): void {
    this.window['tmx_profiling_started'] = false;
    this.sessionId = sessionId;
    const attributes = {
      [this.attributeId]: sessionId
    };
    const node = this.document.querySelector(`script#${this.idScript}`);

    if (node) {

      this.removeScript();
      this.loadScriptService.updateScript(node, attributes);
      this.window?.SIGNIFYD_GLOBAL?.init();

    } else if (sessionId) {

      this.loadScriptService.loadScript({
        idScript: this.idScript,
        src: 'https://cdn-scripts.signifyd.com/api/script-tag.js',
        defer: true,
        attributes,
      });

    }

  }

  removeScript(): void {
    this.loadScriptService.removeScript(this.idScriptTag);
  }
}
