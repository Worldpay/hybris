import { CxEvent } from '@spartacus/core';
import { ApmData, APMRedirectResponse } from '../interfaces';

/**
 * RequestAvailableApmsEvent
 * @since 6.4.0
 */
export class RequestAvailableApmsEvent extends CxEvent {
  static override readonly type: string = 'RequestAvailableApmsEvent';
  apmList: ApmData[];
  loading: boolean;
  error: boolean | Error;
}

/**
 * SelectWorldpayAPMEvent
 * @since 6.4.0
 */
export class SelectWorldpayAPMEvent extends CxEvent {
  static override readonly type: string = 'SelectWorldpayAPMEvent';
  apm: ApmData;
}

/**
 * SetWorldpayAPMRedirectResponseEvent
 * @since 6.4.0
 */
export class SetWorldpayAPMRedirectResponseEvent extends CxEvent {
  static override readonly type: string = 'SetWorldpayAPMRedirectResponseEvent';
  apmRedirectUrl: APMRedirectResponse;
}
