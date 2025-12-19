import { CommonModule } from '@angular/common';
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
  MockFormRequiredLegendComponent, MockWorldpayAPMACHComponent, MockWorldpayApmSubmitButtonsComponent, MockWorldpayBillingAddressComponent
} from './components';
import { MockAtMessageDirective, MockCxFeatureDirective, MockCxFeatureLevelDirective, MockCxOutletContextDirective, MockCxOutletDirective } from './directives';
import { MockUrlPipe } from './pipes';

@NgModule({
  declarations: [
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
    // Directives
    MockAtMessageDirective,
    MockCxOutletDirective,
    MockCxOutletContextDirective,
    MockCxFeatureLevelDirective,
    MockCxFeatureDirective,
    // Pipes
    MockUrlPipe
  ],
  exports: [
    MockCxOutletContextDirective
  ],
  imports: [
    CommonModule
  ]
})
export class WorldpayMockModule {
}
