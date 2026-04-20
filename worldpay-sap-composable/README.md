# Worldpay Connector for the SAP Composable Storefront

The Worldpay Connector for SAP Composable Storefront is an Angular library that integrates Worldpay payment solutions into the SAP Composable Storefront (formerly Spartacus) for SAP Commerce Cloud.

It provides reusable Angular modules and components to enable secure payment processing, 3D Secure, fraud detection, guaranteed payments, and multiple Alternative Payment Methods (APMs).

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

Before updating Composable storefront to version 2211.43, you first need to make sure your Angular libraries are up to date. Composable storefront 2211.43 requires Angular 19.

- If you are working with Composable storefront 2211, see the 2211 Angular development environment requirements on
  the [SAP Help Portal](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/cfcf687ce2544bba9799aa6c8314ecd0/bf31098d779f4bdebb7a2d0591917363.html?locale=en-US&version=2211).
- **[Angular CLI:](https://angular.io/)** Version 19.2.0 is the minimum required. The most recent 19.x version is strongly recommended.
- **[npm:](https://www.npmjs.com/)** Version 10.9.2 or newer.
- **[Node.js](https://nodejs.org/)**: Version 22.14.0 is the minimum required. The most recent 22.x version is strongly recommended.


**Note:** Some Spartacus features require API endpoints that are only available in newer versions of SAP Commerce Cloud. For more information, see Feature Compatibility.

## Compatibility

The Connector is compatible with the Composable storefront Release 2211.43.0

## Installation & Usage

### Development

Run the command `./install.sh` to do a clean install and run the example-storefront

### Installing Composable storefront

- Install `@worldpay2020/sap-composable` to your Composable project - final name pending

    - #### Using npm
    ```bash
     npm install @worldpay2020/sap-composable
    ```
    - #### Using npm   (specific version)
   ```bash
     npm install @worldpay2020/sap-composable@2211.43.0
    ```

    - #### Using yarn
    ```bash
    yarn install @worldpay2020/sap-composable
    ```
    - #### Using yarn   (specific version)
    ```bash
    yarn install @worldpay2020/sap-composable@2211.43.0
    ```

## Configuring SAP Composable Storefront

1. Include Worldpay Module the following in your Composable storefront's `app.module.ts` file.

````typescript
import { WorldpayModule } from '@worldpay2020/sap-composable';

@NgModule({
  imports: [
    ...
      WorldpayModule,
  ],
})
````

### WorldpayModule Included Components

**WorldpayModule** is the main module that includes core payment functionality and optional feature modules. It imports:

#### Core Feature Module
- **WorldpayFeatureModule** - Contains:
  - OCC API adapters and connectors for Worldpay backend integration
  - Checkout payment method components and forms
  - Delivery address management
  - 3D Secure (3DS) Device Data Collection (DDC) iframe
  - 3DS Challenge iframe for 3D Secure authentication
  - Place order orchestration and processing
  - Payment review and confirmation components
  - Cart and order confirmation management
  - i18n translations for all supported languages
  - Payment method icons and assets

#### Included Modules in WorldpayModule

The `WorldpayModule` automatically imports:

```typescript
@NgModule({
  imports: [
    WorldpayFeatureModule,                      // Core payment functionality
    WorldpayGuaranteedPaymentsFeatureModule     // Guaranteed Payments (optional feature)
  ],
})
export class WorldpayModule {
}
```

#### Optional Feature Modules (included by default)
- **WorldpayGuaranteedPaymentsFeatureModule** - Payment guarantee features (can be disabled if not needed)

**Important:** The FraudSight Risk service is **automatically included** in WorldpayFeatureModule. It is used internally by the place-order functionality to collect fraud risk data during payment processing. The `<worldpay-fraudsight-risk>` **component** is optional and only needed if you want to display fraud risk information to users.

**Note:** Optional feature modules are included by default in WorldpayModule. See [Optional Modules](#optional-modules) section below for information on Guaranteed Payments configuration.

### Include Required Scripts

Include the following scripts in your `index.html` file to enable payment processing:

**Required:** Worldpay CSE (Client-Side Encryption) script for secure payment processing:

```html
<html>
  <head>
    <!-- ... other head content ... -->
  </head>
  <body>
    <app-root></app-root>
    <!-- Required: Worldpay CSE script -->
    <script src="https://secure.worldpay.com/resources/cse/js/worldpay-cse-1.latest.min.js"></script>
  </body>
</html>
```

**Optional:** FraudSight ThreatMetrix script (only if displaying the FraudSight Risk component):

```html
<head>
  <!-- ... other head content ... -->
  <!-- Optional: Include only if using <worldpay-fraudsight-risk> component -->
  <script src="./public/worldpay.threatmetrix.js" type="text/javascript"></script>
</head>
```

### Customizing iconConfig

If you want to customize Spartacus icons, you can override `iconConfig` in your storefront `app.module.ts`.
When overriding, keep the Worldpay icon sprite resource so Worldpay payment icons continue to render correctly.

```typescript
import { IconConfig, IconResourceType } from '@spartacus/storefront';
import { provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig({
      icon: {
        // Add or override symbols used by your storefront
        symbols: {
          // example: CART: 'shopping-cart'
        },
        // Keep the Worldpay icon sprite mapping
        resources: [
          {
            type: IconResourceType.SVG,
            url: 'assets/worldpay/worldpay-icons.svg',
            types: Object.values(WORLDPAY_ICONS),
          },
        ],
      },
    } as IconConfig),
  ],
})
export class AppModule {}
```

Official Spartacus icon library documentation:
https://sap.github.io/spartacus-docs/3.x/icon-library/

## B2B Configuration & Functionality

The Worldpay Connector supports B2B-specific payment configurations in addition to standard B2C checkout flows. B2B features are enabled through the `WorldpayB2BModule` which extends the core `WorldpayModule` with B2B-specific components and services.

### Prerequisites

Before enabling B2B functionality, ensure your SAP Commerce backend is properly configured:

1. **SAP Commerce Cloud Configuration**
   - B2B Units and organizational hierarchy configured
   - B2B User accounts created and assigned to units
   - B2B feature modules activated in your SAP Commerce installation

2. **SAP Composable Storefront**
   - B2B accelerator features from SAP enabled
   - B2B routing and configuration in place

### B2B Payment Features

The `WorldpayB2BModule` automatically imports:

```typescript
@NgModule({
  imports: [
    WorldpayB2bFeatureModule,                   // B2B Core payment functionality
    WorldpayGuaranteedPaymentsFeatureModule     // Guaranteed Payments (optional feature)
  ],
})
export class WorldpayB2BModule {
}
```

The B2B module provides:

- **Standard Worldpay CSE Payment Integration** - Client-side encryption for secure card payments
- **3D Secure Authentication** (optional) - Enhanced security for high-value transactions
- **Company Information** - Company name and organizational details in billing information
- **Purchase Order Management** (optional) - PO number tracking and validation
- **Scheduled Replenishment** - Automated recurring order scheduling for B2B accounts

No additional connector configuration is required beyond the base Worldpay configuration.

### Implementation

**Step 1: Import WorldpayB2BModule**

Update your `app.module.ts` to conditionally import the B2B or B2C module based on your environment:

```typescript
import { WorldpayModule, WorldpayB2BModule } from '@worldpay2020/sap-composable';

// Dynamically select module based on environment configuration
const WorldpayMainModule: typeof WorldpayB2BModule = environment.b2b ? WorldpayB2BModule : WorldpayModule;

@NgModule({
  imports: [
    // ... other imports
    WorldpayMainModule,
  ],
})
export class AppModule {
}
```

The `WorldpayB2BModule` automatically imports and configures WorldpayB2BFeatureModule which includes all necessary components and services for B2B payment processing:

```typescript

@NgModule({
  imports: [
    WorldpayModule,                                   // Core payment functionality
    OccWorldpayB2bModule,                             // B2B OCC API adapters
    WorldpayB2BCheckoutReviewSubmitModule,            // B2B order review and submission
    WorldpayB2bCheckoutPaymentMethodModule,           // B2B-specific payment method selection
    WorldpayCheckoutScheduledReplenishmentModule      // Automated order scheduling
  ]
})
export class WorldpayB2BFeatureModule {
}
```

**Module Descriptions':**
- **OccWorldpayB2bModule** - Handles B2B-specific OCC API communication with backend
- **WorldpayB2BCheckoutReviewSubmitModule** - B2B-specific checkout review workflow with company/PO information
- **WorldpayB2bCheckoutPaymentMethodModule** - Enhanced payment method selection with B2B-specific options
- **WorldpayCheckoutScheduledReplenishmentModule** - Manages automated recurring order scheduling for B2B accounts

## Assets Configuration

Add the following entry to the `assets` array in your `angular.json` under the `build` architect options:
```json
{
  "glob": "**/*",
  "input": "node_modules/@worldpay2020/sap-composable/src/assets",
  "output": "assets/worldpay"
}
```

This makes the Worldpay assets (images, icons, etc.) available at `/assets/worldpay/` in your application.

### Example
```json
"assets": [
  "src/favicon.ico",
  "src/assets",
  {
    "glob": "**/*",
    "input": "node_modules/@worldpay2020/sap-composable/src/assets",
    "output": "assets/worldpay"
  }
]
```

## Styles Configuration

Add the Worldpay stylesheet to the `styles` array in your `angular.json`:
```json
"styles": [
  "src/styles.scss",
  "node_modules/@worldpay2020/sap-composable/src/assets/styles/styles.scss"
]
```


# Optional Modules

## Overview

WorldpayModule provides complete payment processing functionality. Some features are required, while others are truly optional:

- **Required Components** - Automatically included in WorldpayModule
  - Core payment checkout and processing
  - FraudSight Risk service (internal, no UI)
  - 3D Secure authentication
  - Order confirmation and tracking

- **Optional Components** - Include only if needed
  - Guaranteed Payments feature module
  - FraudSight Risk UI component (for displaying fraud scores to users)

### When to Use Optional Features

| Feature                          | Required?      | When to Include               | When to Skip                     |
|----------------------------------|----------------|-------------------------------|----------------------------------|
| **FraudSight Risk Service**      | Yes (built-in) | Always available              | N/A                              |
| **FraudSight Risk UI Component** | No             | Display fraud scores to users | Just use internal service        |
| **Guaranteed Payments Module**   | No             | Offer payment guarantees      | Standard payment processing only |

### How Optional Features Work

When you **don't** import an optional feature:
-  The service/component is not loaded
-  No bundle size impact
-  Payment processing continues normally
-  No configuration needed

When you **do** import an optional feature:
-  Additional capabilities become available
-  Component is registered in Angular
-  Services are injected in dependency container
-  Follow the setup instructions in relevant section

### Key Points

- **FraudSight Risk service is automatic** - No extra steps needed. It's built into the core payment flow.
- **FraudSight Risk component is optional** - Only add if you want to display fraud information to users.
- **Guaranteed Payments is fully optional** - Only import if your business requires payment guarantees.

## Enable Guaranteed Payments (Optional)

Guaranteed Payments is an optional feature that provides payment guarantee capabilities. If you don't need this feature, you can skip this section.

### Implementation

1. **Import the module** in your `app.module.ts` file:

```typescript
import { WorldpayModule, WorldpayGuaranteedPaymentsFeatureModule } from '@worldpay2020/sap-composable';

@NgModule({
  imports: [
    // ... other imports
    WorldpayModule,
    WorldpayGuaranteedPaymentsFeatureModule  // Optional: Only if you need payment guarantee features
  ],
})
export class AppModule {
}
```

2. **Add the component** to your `app.component.html` to display the guaranteed payments UI:

```html
<cx-storefront></cx-storefront>
<!-- Optional: Add this component if you want to display guaranteed payments -->
<worldpay-guaranteed-payments></worldpay-guaranteed-payments>
```

## Display FraudSight Risk Information (Optional)

The FraudSight Risk service is **automatically included** in WorldpayModule and provides fraud risk assessment during payment processing.

The `<worldpay-fraudsight-risk>` **component** is optional and only needed if you want to **display** fraud risk information to users. Most merchants can skip this section and just use the automatic service.

### When to Add the FraudSight Risk Component

Add this component only if:
-  You have FraudSight Risk configured with Worldpay
-  Your UX requires displaying fraud assessment results

### Implementation

**Step 1: Add the ThreatMetrix script** to your `index.html` before the closing `</body>` tag:

```html
<body>
  <!-- ... your app content ... -->
  
  <!-- Required for FraudSight component display -->
  <script src="./public/worldpay.threatmetrix.js" type="text/javascript"></script>
</body>
```

**Note:** Ensure the `worldpay.threatmetrix.js` file exists in your `public` folder.

**Step 2: Configure environment variables** in your `environment.ts` file:

```typescript
export const environment = {
  // ... other config ...
  fraudSight: {
    organisationId: 'YOUR_ORGANISATION_ID',
    profilingDomain: 'PROFILING_DOMAIN',
    pageId: 'PAGE_ID',
  },
};
```

**Step 3: Add the component** to your `app.component.html` to display the fraud risk UI:

```html
<cx-storefront></cx-storefront>

<!-- Optional: Display fraud risk information to users -->
<worldpay-fraudsight-risk
  threatMetrix="wprofile"
  [randomIdLength]="128"
  [organisationId]="fraudsightConfig.organisationId"
  [pageId]="fraudsightConfig.pageId"
  [profilingDomain]="fraudsightConfig.profilingDomain"
></worldpay-fraudsight-risk>
```

**Note:** If you skip this entire section, the FraudSight service will still work internally to assess fraud risk during payment processing. This component is purely for displaying results to users.

## License

Copyright (c) 2026 Worldpay Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

### Release 2211.43.0
* Compatible with SAP Commerce Cloud 2211.43
* Removed deprecated APM's:
* Supported APM’s for B2B recipe:

| Payment               | Enabled Country               | Currency        | Requires User Data | Additional User Data                                                                        |
|-----------------------|-------------------------------|-----------------|--------------------|---------------------------------------------------------------------------------------------|
| ACH Direct Debit      | US                            | USD             | Yes                | Account type, Account Number, Routing Number, Check Number, Company Name, Custom Identifier |
| Sepa                  | AT,BE,FR,DE,IE,IT,NL,ES       | EUR             |                    |                                                                                             |
| Open Banking          | AT,BE,FR,DE,IE,IT,NL,ES       | EUR             |                    |                                                                                             |


Merchants can now offer ACH SEPA Open Banking Direct Debit as a payment method to their customers in the United States and Europe, providing a convenient and secure way for customers to make payments directly from their bank accounts.


### Release 2211.43

* Compatible with SAP Commerce Cloud 2211.43
* Removed deprecated APM's:
  * GiroPay
  * Postepay
  * Sofort
* Supported APM’s:

| Payment               | Enabled Country               | Currency        | Requires User Data | Additional User Data                                                                        |
|-----------------------|-------------------------------|-----------------|--------------------|---------------------------------------------------------------------------------------------|
| ACH Direct Debit      | US                            | USD             | Yes                | Account type, Account Number, Routing Number, Check Number, Company Name, Custom Identifier |
| AliPay                | CN                            | USD, EUR        |                    |                                                                                             |
| ApplePay              | All                           | All             | Yes                |                                                                                             |
| Bancontact MisterCash | BE                            | EUR             |                    |                                                                                             |
| China Union Pay       | CN                            | GBP,EUR,USD,SGD |                    |                                                                                             |
| GooglePay             | All                           | All             | Yes                |                                                                                             |
| iDeal                 | NL                            | EUR             | Yes                | Bank Code                                                                                   |
| Klarna                | AT,FI,DE,NL,NO,SE,GB          | EUR,GBP         |                    |                                                                                             |
| Klarna Pay Later      | SE,NO,FI,DE,NL,AT,CH,GB,DK,US | EUR,GBP         |                    |                                                                                             |
| Klarna Pay Now        | SE,DE,NL,AT                   | EUR,GBP         |                    |                                                                                             |
| Klarna Slice It       | SE,NO,FI,DE,AT,GB,DK,US       | EUR,GBP         |                    |                                                                                             |
| PayPal SSL            | All                           | All             |                    |                                                                                             |
| POLi                  | NZ                            | NZ              |                    |                                                                                             |
| Sepa                  | AT,BE,FR,DE,IE,IT,NL,ES       | EUR             |                    |                                                                                             |

### Release 2211.28.0

* Compatible with SAP Commerce Cloud 2211.27.0
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


