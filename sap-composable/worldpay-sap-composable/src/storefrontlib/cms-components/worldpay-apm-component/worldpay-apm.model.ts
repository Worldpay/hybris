import { Media } from '@spartacus/storefront';
import { CmsComponent } from '@spartacus/core';

export interface WorldpayApmConfiguration {
  code: string;
  name: string;
  description?: string;
}

export interface WorldpayApmModel extends CmsComponent {
  media?: Media;
  apmConfiguration: WorldpayApmConfiguration;
}

export interface WorldpayCCModel extends CmsComponent {
  media?: Media;
}

export interface WorldpayGooglePayModel extends CmsComponent {
  media?: Media;
}
