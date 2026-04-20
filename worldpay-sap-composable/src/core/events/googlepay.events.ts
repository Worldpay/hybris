import { CxEvent } from '@spartacus/core';
import { GooglePayMerchantConfiguration } from '../interfaces';

/**
 * GooglePayMerchantConfigurationSetEvent is triggered when setGooglePayMerchantConfiguration is called
 * @since 6.4.0
 */
export class GooglePayMerchantConfigurationSetEvent extends CxEvent {
  static override readonly type: string = 'GooglePayMerchantConfigurationSetEvent';
  googlePayMerchantConfiguration: GooglePayMerchantConfiguration;
}

/**
 * GooglePayMerchantConfigurationSetEvent is triggered when onOrderPlacedEvent is called
 * @since 6.4.0
 */
export class ClearGooglepayEvent extends CxEvent {
  static override readonly type: string = 'ClearGooglepayEvent';
}

