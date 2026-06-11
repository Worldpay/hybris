import { NgModule } from '@angular/core';
import {
  MockCxAddToHomeScreenBannerComponent,
  MockCxCardComponent,
  MockCxConsignmentTrackingComponent,
  MockCxGuestRegisterFormComponent,
  MockCxIconComponent,
  MockCxMediaComponent,
  MockCxSpinnerComponent,
  MockFormRequiredAsterisksComponent,
  MockFormRequiredLegendComponent,
  MockWorldpayAPMACHComponent,
  MockWorldpayApmSubmitButtonsComponent,
  MockWorldpayBillingAddressComponent
} from './components';
import { MockWorldpayApmComponent } from './components/worldpay-apm-component.mock';
import { MockWorldpayApmSepaComponent, MockWorldpayB2BApmSepaComponent } from './components/worldpay-apm-sepa.component.mock';
import { MockAtMessageDirective, MockCxFeatureDirective, MockCxFeatureLevelDirective, MockCxOutletContextDirective, MockCxOutletDirective } from './directives';
import { MockUrlPipe } from './pipes';

// eslint-disable-next-line @typescript-eslint/typedef
const config = [
  // Components
  MockCxAddToHomeScreenBannerComponent,
  MockCxCardComponent,
  MockCxConsignmentTrackingComponent,
  MockCxGuestRegisterFormComponent,
  MockCxIconComponent,
  MockCxMediaComponent,
  MockCxSpinnerComponent,
  MockFormRequiredAsterisksComponent,
  MockFormRequiredLegendComponent,
  MockWorldpayAPMACHComponent,
  MockWorldpayApmSubmitButtonsComponent,
  MockWorldpayBillingAddressComponent,
  MockWorldpayApmComponent,
  MockWorldpayApmSepaComponent,
  MockWorldpayB2BApmSepaComponent,
  // Directives
  MockAtMessageDirective,
  MockCxOutletDirective,
  MockCxOutletContextDirective,
  MockCxFeatureLevelDirective,
  MockCxFeatureDirective,
  // Pipes
  MockUrlPipe
];

@NgModule({
  imports: config,
  exports: config,
})
export class WorldpayMockModule {
}
