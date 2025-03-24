# Worldpay Connector for the SAP Spartacus Storefront

The Worldpay Connector for SAP Spartacus Storefront is an Angular Library that provides the components to integrate WorldPay payment solutions with the SAP Commerce Cloud Spartacus
storefront.

## About Worldpay from FIS

Worldpay from FIS is one of the world's leading global eCommerce and payment technology companies. FIS is a leading provider of technology solutions for merchants, banks and
capital markets firms globally.

## About SAP Composable Storefront

Starting with version 2211.19, composable storefront has aligned its versioning with SAP Commerce Cloud. The previous release of composable storefront was version 6.8. For more
information,
see [Changes to Release Numbering and Update Policies for Composable Storefront Starting in February 2024](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/6c7b98dbe68f4a508cac17a207182f4c/5fea969613a341308e2519c5f2827331.html?locale=en-US&version=2211).

Composable storefront is based off the Spartacus open source code, and is included in the SAP Commerce Cloud license at no extra cost. Composable storefront has a roll-forward
update policy.
Spartacus documentation: [https://sap.github.io/spartacus-docs/](https://sap.github.io/spartacus-docs/).
Release information: [https://sap.github.io/spartacus-docs/release-information/](https://sap.github.io/spartacus-docs/release-information/).

## Requirements

- If you are working with Spartacus 2211, see the 2211 Angular development environment requirements on
  the [SAP Help Portal](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/cfcf687ce2544bba9799aa6c8314ecd0/bf31098d779f4bdebb7a2d0591917363.html?locale=en-US&version=2211).
- **[Angular CLI:](https://angular.io/)** Version 17.0.0 is the minimum required. The most recent 17.x version is strongly recommended.
- **[npm:](https://www.npmjs.com/)** Version 10.2.4 or newer.
- **[Node.js](https://nodejs.org/)**: Version 20.9.0 is the minimum required. The most recent 20.x version is strongly recommended.

**Note:** Some Spartacus features require API endpoints that are only available in newer versions of SAP Commerce Cloud. For more information, see Feature Compatibility.

## Compatibility

The Connector is compatible with the Spartacus Release 2211.32.1

## Installation & Usage

### Development

Run the command `./install.sh` to do a clean install and run the example-storefront

### Installing Spartacus

- Install `@worldpay2020/sap-composable` to your Spartacus project - final name pending

    - #### Using npm
    ```bash
     npm install @worldpay2020/sap-composable
    ```
    - #### Using npm   (specific version)
   ```bash
     npm install @worldpay2020/sap-composable@2211.32.1
    ```

    - #### Using yarn
    ```bash
    yarn install @worldpay2020/sap-composable
    ```
    - #### Using yarn   (specific version)
    ```bash
    yarn install @worldpay2020/sap-composable@2211.32.1
    ```

## Configuring SAP Composable Storefront

1. Include Worldpay Module the following in your Spartacus storefront's `app.module.ts` file.

````typescript
import { WorldpayModule } from '@worldpay2020/sap-composable';

@NgModule({
  imports: [
    ...
      WorldpayModule,
  ],
})
````

**Note:** WorldpayModule will include the following modules:

```typescript 

@NgModule({
  imports: [
  ...
    CommonModule,
    OccWorldpayModule,
    WorldpayCheckoutPaymentMethodModule,
    WorldpayCheckoutDeliveryAddressModule,
    WorldpayDdcIframeModule,
    WorldpayDdcIframeRoutingModule,
    Worldpay3dsChallengeIframeModule,
    WorldpayCheckoutPlaceOrderModule,
    WorldpayCheckoutReviewPaymentModule,
    WorldpayCartSharedModule,
    WorldpayOrderConfirmationModule,
    WorldpayOrderDetailsModule,
  ],
  providers: [
    provideConfig({
      i18n: {
        resources: worldpayTranslations
      },
    } as I18nConfig),
    {
      provide: APP_BASE_HREF,
      useFactory: getBaseHref,
      deps: [PlatformLocation]
    },
    ...worldpayFacadeProviders,

    provideFeatureTogglesFactory(() => {
      const appFeatureToggles: any = {
        a11yCheckoutDeliveryFocus: true,
        useExtractedBillingAddressComponent: true,
      };
      return appFeatureToggles;
    })
  ],
  ...
})
export class WorldpayModule {
}
```

2. Include Worldpay script before closing the body tag inside the index.html file

```
<html>
...
<app-root></app-root>
...
<script src="https://secure.worldpay.com/resources/cse/js/worldpay-cse-1.latest.min.js"></script>
</body>
</html>
```

## Enable Guaranteed Payments

To enable Guaranteed Payments, you must:

1. add the `WorldpayGuaranteedPaymentsModule` to your `app.module.ts` file:

```typescript
@NgModule({
  imports: [
    ...
      WorldpayModule,
    WorldpayGuaranteedPaymentsModule
  ],
})
````

2. Then update your `app.component.html` file using the `<worldpay-guaranteed-payments></worldpay-guaranteed-payments>` tag

```
<cx-storefront></cx-storefront>
<worldpay-guaranteed-payments></worldpay-guaranteed-payments>
```

## Enable FraudSight Risk Module

1. To enable Guaranteed Payments, you must add the ***WorldpayFraudsightRiskModule*** to your `app.module.ts` file:

```typescript
@NgModule({
  imports: [
    ...
      WorldpayModule,
    WorldpayFraudsightRiskModule
  ],
})
````

2. Update your environment.ts file with the following configuration:

```typescript
export const environment = {
  fraudSight: {
    organisationId: 'YOUR_ORGANISATION_ID',
    profilingDomain: 'PROFILING_DOMAIN',
    pageId: 'PAGE_ID',
  },
};
```

3. Then update your `app.component.html` file using the ***<worldpay-fraudsight-risk></worldpay-fraudsight-risk>*** tag

```
<cx-storefront></cx-storefront>
<worldpay-fraudsight-risk
  threatMetrix="wprofile"
  [randomIdLength]="128"
  [organisationId]="fraudsightConfig.organisationId"
  [pageId]="fraudsightConfig.pageId"
  [profilingDomain]="fraudsightConfig.profilingDomain"
></worldpay-fraudsight-risk>
```

## License

Copyright (c) 2022 Worldpay Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

### Release 2211.2X

* Compatible with SAP Commerce Cloud 2211.2X
* Supported APM’s:

| Payment               | Enabled Country               | Currency        | Requires User Data | Additional User Data                                                                        |
|-----------------------|-------------------------------|-----------------|--------------------|---------------------------------------------------------------------------------------------|
| ACH Direct Debit      | US                            | USD             | Yes                | Account type, Account Number, Routing Number, Check Number, Company Name, Custom Identifier |
| AliPay                | CN                            | USD, EUR        |                    |                                                                                             |
| ApplePay              | All                           | All             | Yes                |                                                                                             |
| Bancontact MisterCash | BE                            | EUR             |                    |                                                                                             |
| China Union Pay       | CN                            | GBP,EUR,USD,SGD |                    |                                                                                             |
| GiroPay               | GE                            | EUR             |                    |                                                                                             |
| GooglePay             | All                           | All             | Yes                |                                                                                             |
| iDeal                 | NL                            | EUR             | Yes                | Bank Code                                                                                   |
| Klarna                | AT,FI,DE,NL,NO,SE,GB          | EUR,GBP         |                    |                                                                                             |
| Klarna Pay Later      | SE,NO,FI,DE,NL,AT,CH,GB,DK,US | EUR,GBP         |                    |                                                                                             |
| Klarna Pay Now        | SE,DE,NL,AT                   | EUR,GBP         |                    |                                                                                             |
| Klarna Slice It       | SE,NO,FI,DE,AT,GB,DK,US       | EUR,GBP         |                    |                                                                                             |
| PayPal SSL            | All                           | All             |                    |                                                                                             |
| POLi                  | NZ                            | NZ              |                    |                                                                                             |
| Postepay              | IT                            | EUR             |                    |                                                                                             |
| Sepa                  | AT,BE,FR,DE,IE,IT,NL,ES       | EUR             |                    |                                                                                             |
| Sofort                | AT,BE,FR,DE,CH                | EUR,CHF         |                    |                                                                                             |

### Release 6.4.2

* Included ACH payment method for United States
* Supported APM’s:

| Payment               | Enabled Country               | Currency        | Requires User Data | Additional User Data                                                                        |
|-----------------------|-------------------------------|-----------------|--------------------|---------------------------------------------------------------------------------------------|
| ACH Direct Debit      | US                            | USD             | Yes                | Account type, Account Number, Routing Number, Check Number, Company Name, Custom Identifier |
| AliPay                | CN                            | USD, EUR        |                    |                                                                                             |
| ApplePay              | All                           | All             | Yes                |                                                                                             |
| Bancontact MisterCash | BE                            | EUR             |                    |                                                                                             |
| China Union Pay       | CN                            | GBP,EUR,USD,SGD |                    |                                                                                             |
| GiroPay               | GE                            | EUR             |                    |                                                                                             |
| GooglePay             | All                           | All             | Yes                |                                                                                             |
| iDeal                 | NL                            | EUR             | Yes                | Bank Code                                                                                   |
| Klarna                | AT,FI,DE,NL,NO,SE,GB          | EUR,GBP         |                    |                                                                                             |
| Klarna Pay Later      | SE,NO,FI,DE,NL,AT,CH,GB,DK,US | EUR,GBP         |                    |                                                                                             |
| Klarna Pay Now        | SE,DE,NL,AT                   | EUR,GBP         |                    |                                                                                             |
| Klarna Slice It       | SE,NO,FI,DE,AT,GB,DK,US       | EUR,GBP         |                    |                                                                                             |
| PayPal SSL            | All                           | All             |                    |                                                                                             |
| POLi                  | NZ                            | NZ              |                    |                                                                                             |
| Postepay              | IT                            | EUR             |                    |                                                                                             |
| Sepa                  | AT,BE,FR,DE,IE,IT,NL,ES       | EUR             |                    |                                                                                             |
| Sofort                | AT,BE,FR,DE,CH                | EUR,CHF         |                    |                                                                                             |

### Release 6.4.1

* Updated iDeal 2.0
* Implemented PayPal SSL

### Release 6.4.0

* Credit card form
* Supports Guest checkout
* 3d Secure
* Fraud Sight
* Supported APM’s:

| Payment               | Enabled Country               | Currency        | Requires User Data | Additional User Data |
|-----------------------|-------------------------------|-----------------|--------------------|----------------------|
| AliPay                | CN                            | USD             |                    |                      |
| ApplePay              | All                           | All             | Yes                |                      |
| Bancontact MisterCash | BE                            | EUR             |                    |                      |
| China Union Pay       | CN                            | GBP,EUR,USD,SGD |                    |                      |
| GiroPay               | DE                            | EUR             |                    |                      |
| GooglePay             | All                           | All             | Yes                |                      |
| iDeal                 | NL                            | EUR             | Yes                | Bank Code            |
| Klarna                | AT,FI,DE,NL,NO,SE,GB          | EUR,GBP         |                    |                      |
| Klarna Pay Later      | SE,NO,FI,DE,NL,AT,CH,GB,DK,US | EUR,GBP         |                    |                      |
| Klarna Pay Now        | SE,DE,NL,AT                   | EUR,GBP         |                    |                      |
| Klarna Slice It       | SE,NO,FI,DE,AT,GB,DK,US       | EUR,GBP         |                    |                      |
| PayPal                | All                           | All             |                    |                      |
| POLi                  | NZ                            | NZ              |                    |                      |
| Postepay              | IT                            | EUR             |                    |                      |
| Sepa                  | AT,BE,FR,DE,IE,IT,NL,ES       | EUR             |                    |                      |
| Sofort                | AT,BE,FR,DE,CH                | EUR,CHF         |                    |                      |


