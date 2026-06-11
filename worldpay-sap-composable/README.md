# Worldpay Connector for SAP Composable Storefront

The Worldpay Connector for SAP Composable Storefront is an Angular library that integrates Worldpay payment solutions into the SAP Composable Storefront (formerly Spartacus) for
SAP Commerce Cloud.
It provides reusable Angular modules and components to enable secure payment processing, 3D Secure, fraud detection, guaranteed payments, and multiple Alternative Payment Methods (
APMs).

> **Version:** 221121.11.0 | **Status:** ✅ Stable | **Compatible with:** SAP Composable Storefront 2211.x & Angular 21+

## Table of Contents

| What do you need?             | Go to                                                                           |
|-------------------------------|---------------------------------------------------------------------------------|
| **Worldpay**                  | [About Worldpay](#about-worldpay-from-global-payments)                          |
| **SAP Composable Storefront** | [About SAP Composable Storefront](#about-sap-composable-storefront)             |
| **Verify compatibility**      | [Requirements](#requirements)                                                   |
| **Install the package**       | [Installation](#installation)                                                   |
| **Compatibility**             | [Compatibility](#compatibility)                                                 |
| **Configure it**              | [Configuration](#configuration)                                                 |
| **Understand features**       | [Features & Modules](#features--modules)                                        |
| **See how to use it**         | [Usage Examples](#usage-examples)                                               |
| **Customize it**              | [Advanced Configuration](#advanced-configuration)                               |
| **Pre-deployment checklist**  | [Application Checklist](#application-checklist)                                 |
| **Payment methods**           | [Supported Alternative Payment Methods](#supported-alternative-payment-methods) |
| **Fix problems**              | [Troubleshooting](#troubleshooting)                                             |
| **Release Notes**             | [Release Notes](#release-notes)                                                 |

## What This Library Provides

### Standalone Components

All Worldpay components are implemented as standalone components, following Angular best practices:

- Import components directly into feature modules or other standalone components.
- No need for NgModules for component registration; add the component to your `imports` array.
- Providers for each feature are exposed via factory functions (e.g., `provideWorldpayPaymentMethods()`), which can be registered globally.

### Integration Approaches

| Approach   | Description                                                           | Recommended For                          |
|------------|-----------------------------------------------------------------------|------------------------------------------|
| Standalone | Import components directly, register providers via factory functions. | New projects, lazy loading, tree-shaking |
| NgModule   | Use Angular modules for registration.                                 | Existing projects, gradual migration     |

Both approaches are fully supported. See [Usage Examples](#usage-examples) for details.

This connector integrates Worldpay payment solutions into SAP Commerce Cloud with:

- **Secure credit card payments** - Client-side encryption (PCI compliant)
- **3D Secure authentication** - Enhanced security for high-value transactions
- **11+ Alternative Payment Methods** - Apple Pay, Google Pay, iDEAL, Klarna, and more
- **Fraud detection** - Built-in FraudSight risk assessment
- **B2B support** - Purchase orders, company information, scheduled replenishment
- **Guaranteed Payments** - Optional payment guarantee feature
- **Standalone Angular components** - Import directly, no NgModules required

## About Worldpay from Global Payments

Worldpay from FIS is one of the world's leading global eCommerce and payment technology companies. FIS is a leading provider of technology solutions for merchants, banks and
capital markets firms globally.

---

## About SAP Composable Storefront

SAP Composable Storefront is the modern JavaScript storefront for SAP Commerce Cloud. Starting with version 2211.19, composable storefront has aligned its versioning with SAP
Commerce Cloud. The previous release was version 6.8. For more information,
see [Changes to Release Numbering and Update Policies for Composable Storefront Starting in February 2024](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/6c7b98dbe68f4a508cac17a207182f4c/5fea969613a341308e2519c5f2827331.html?locale=en-US&version=2211).

Composable storefront is based off the Spartacus open source code, and is included in the SAP Commerce Cloud license at no extra cost. Composable storefront has a roll-forward
update policy.

Spartacus documentation: [https://sap.github.io/spartacus-docs/](https://sap.github.io/spartacus-docs/)
Release information: [https://sap.github.io/spartacus-docs/release-information/](https://sap.github.io/spartacus-docs/release-information/)

---

## Requirements

Before updating composable storefront to version 221121.11.0, ensure your Angular libraries are up to date. Composable storefront 221121.11.0 requires Angular 21.

| Requirement            | Version     | Notes                                                                                                                                                                             |
|------------------------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Angular**            | `^21.0.0`   | TypeScript 5.x required                                                                                                                                                           |
| **Angular CLI**        | `^21.0.0`   | Latest 21.x version recommended                                                                                                                                                   |
| **Angular Animations** | `^21.0.0`   | Latest 21.x version recommended                                                                                                                                                   |
| **Angular CDK**        | `^21.0.0`   | Latest 21.x version recommended                                                                                                                                                   |
| **Node.js**            | `>=22.14.0` | Latest 22.x version recommended                                                                                                                                                   |
| **npm**                | `>=10.9.2`  | or yarn 4.x                                                                                                                                                                       |
| **SAP Commerce Cloud** | `2211+`     | [SAP Help Portal](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/cfcf687ce2544bba9799aa6c8314ecd0/bf31098d779f4bdebb7a2d0591917363.html?locale=en-US&version=2211). |

> Some features require API endpoints only available in newer SAP Commerce Cloud versions.

---

## Compatibility

| Package                        | Version       |
|--------------------------------|---------------|
| `@worldpay2020/sap-composable` | `221121.11.0` |
| SAP Composable Storefront      | `221121.11.0` |
| Angular                        | `^21.0.0`     |
| SAP Commerce Cloud             | `2211+`       |

---

## Installation

### Using npm (Recommended)

```bash
npm install @worldpay2020/sap-composable
```

### Using yarn

```bash
yarn add @worldpay2020/sap-composable
```

### Specific Version

```bash
npm install @worldpay2020/sap-composable@221121.11.0
```

### Install dependencies:

```bash
npm install @angular/animations@^21.2.0
npm install @angular/cdk@^21.2.0
```

---

## Configuration

### Step 1: Configure in `app.config.ts`

For **B2C (Standard) Configuration:**

```typescript
import { provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideClientHydration, withEventReplay, withNoHttpTransferCache } from '@angular/platform-browser';
import { WorldpayModule } from '@worldpay2020/sap-composable';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    provideClientHydration(withEventReplay(), withNoHttpTransferCache()),
    importProvidersFrom(AppModule),
    importProvidersFrom(WorldpayModule),
    // Optional: Include additional feature modules as needed
    // importProvidersFrom(WorldpayFraudsightRiskFeatureModule)
  ],
};
```

### Step 2: Bootstrap your application with the config

In your `main.ts`:

```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent, appConfig).catch(err =>
  console.error(err)
);
```

### Step 3: Include required scripts in `index.html`

```html
<html>
<head>
  <!-- ... other head content ... -->
  <!-- Optional: FraudSight ThreatMetrix script (only if displaying the FraudSight Risk component) -->
  <script
    src="./public/worldpay.threatmetrix.js"
    type="text/javascript"
  ></script>
</head>
<body>
<app-root></app-root>
<!-- Required: Worldpay CSE script for secure payment processing -->
<script src="https://secure.worldpay.com/resources/cse/js/worldpay-cse-1.latest.min.js"></script>
</body>
</html>
```

### Step 4: Configure Assets (in `angular.json`)

Add the Worldpay assets to your `angular.json`:

```json
{
  "projects": {
    "your-app": {
      "architect": {
        "build": {
          "options": {
            "assets": [
              "src/favicon.ico",
              "src/assets",
              {
                "glob": "**/*",
                "input": "node_modules/@worldpay2020/sap-composable/src/assets",
                "output": "assets/worldpay"
              }
            ]
          }
        }
      }
    }
  }
}
```

### Step 5: Configure Styles (in `angular.json`)

Add the Worldpay stylesheet to your `angular.json`:

```json
{
  "projects": {
    "your-app": {
      "architect": {
        "build": {
          "options": {
            "styles": [
              "src/styles.scss",
              "node_modules/@worldpay2020/sap-composable/src/assets/styles/styles.scss"
            ]
          }
        }
      }
    }
  }
}
```

---

## Features & Modules

Not sure what you need? This matrix shows what's included and what's optional:

| Feature                                       | Included?   | Required?   | When to Use                      |
|-----------------------------------------------|-------------|-------------|----------------------------------|
| **Credit Card**                               | ✅ Default   | ✓ Yes       | All merchants                    |
| **3D Secure**                                 | ✅ Default   | ⚠️ Optional | High-value transactions          |
| **FraudSight Risk Service**                   | ✅ Automatic | ✅ Yes       | Always enabled (internal)        |
| **FraudSight Risk UI**                        | ❌ Separate  | ⚠️ Optional | Display fraud scores to users    |
| **APMs** (Apple Pay, Google Pay, iDEAL, etc.) | ✅ Default   | ⚠️ Optional | Accept e-wallets                 |
| **Guaranteed Payments**                       | ❌ Separate  | ⚠️ Optional | Offer payment guarantees         |
| **B2B Module**                                | ❌ Separate  | ⚠️ Optional | B2B merchants (PO numbers, etc.) |

**Legend:**

- ✅ **Default** = Already included in WorldpayModule
- ⚠️ **Optional** = Configure only if you need this feature
- ✓ **Required** = Must configure, payment won't work without it
- ❌ **Separate** = Requires additional import/configuration
- ✅ **Automatic** = Works by itself, zero configuration needed

### WorldpayModule Architecture

**WorldpayModule** is the main module that includes core payment functionality and optional feature modules.

#### Core Components

The `WorldpayModule` automatically includes:

```typescript
@NgModule({
  imports: [
    WorldpayFeatureModule,                      // Core payment functionality
    WorldpayGuaranteedPaymentsFeatureModule     // Guaranteed Payments (optional)
  ],
})
export class WorldpayModule {
}
```

#### WorldpayFeatureModule Contains

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

#### Important Notes

- The **FraudSight Risk service** is automatically included in WorldpayFeatureModule and used internally by place-order functionality
- The `<worldpay-fraudsight-risk>` **component** is optional and only needed if you want to display fraud risk information to users
- Optional feature modules (like WorldpayGuaranteedPaymentsFeatureModule) are included by default in WorldpayModule

---

## Usage Examples

Choose your use case and follow the example:

### Example 1: Standard B2C Checkout (Most Common)

For a regular ecommerce storefront with all payment methods enabled:

```typescript
// app.config.ts
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { WorldpayModule } from '@worldpay2020/sap-composable';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    importProvidersFrom(WorldpayModule), // Includes all payment methods
  ],
};
```

**What's included:**

- Credit card form
- 3D Secure (optional, user can enable)
- Apple Pay, Google Pay, iDEAL, Klarna, and all APMs
- FraudSight risk assessment (internal)

---

### Example 2: B2B with Purchase Orders

For business-to-business transactions with PO number support:

```typescript
// app.config.ts
import { WorldpayB2bModule } from '@worldpay2020/sap-composable';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    importProvidersFrom(WorldpayB2bModule), // B2B features auto-enabled
  ],
};
```

**What's included:**

- Credit card form with company information
- PO number field (required)
- 3D Secure (optional)
- Scheduled replenishment support
- All payment methods

---

### Example 3: Display Fraud Risk to Users

For merchants who want to show fraud assessment results to customers:

```typescript
// app.config.ts - Configuration
import { WorldpayModule } from '@worldpay2020/sap-composable';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    importProvidersFrom(WorldpayModule),
  ],
};
```

```html
<!-- app.component.html - Display component -->
<cx-storefront></cx-storefront>

<!-- Optional: Display fraud risk information -->
<worldpay-fraudsight-risk
  threatMetrix="wprofile"
  [randomIdLength]="128"
  [organisationId]="fraudsightConfig.organisationId"
  [pageId]="fraudsightConfig.pageId"
  [profilingDomain]="fraudsightConfig.profilingDomain"
></worldpay-fraudsight-risk>
```

```typescript
// environment.ts - Configuration
export const environment = {
  fraudSight: {
    organisationId: 'YOUR_ORGANISATION_ID',
    profilingDomain: 'PROFILING_DOMAIN',
    pageId: 'PAGE_ID',
  },
};
```

---

### Example 4: Guaranteed Payments

For merchants offering payment guarantees:

```typescript
// app.config.ts
import {
  WorldpayModule,
  WorldpayGuaranteedPaymentsFeatureModule
} from '@worldpay2020/sap-composable';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    importProvidersFrom(WorldpayModule),
    importProvidersFrom(WorldpayGuaranteedPaymentsFeatureModule),
  ],
};
```

```html
<!-- app.component.html -->
<cx-storefront></cx-storefront>
<worldpay-guaranteed-payments></worldpay-guaranteed-payments>
```

---

## Advanced Configuration

### Using NgModule Approach (Legacy)

If your project still uses NgModules:

```typescript
import { NgModule } from '@angular/core';
import { WorldpayModule } from '@worldpay2020/sap-composable';

@NgModule({
  imports: [WorldpayModule],
})
export class PaymentModule {
}
```

Both approaches (standalone and NgModule) are fully supported.

### Customizing Icons

If you want to customize Spartacus icons, you can override `iconConfig` in your storefront `app.module.ts`. When overriding, keep the Worldpay icon sprite resource so Worldpay payment icons continue to render correctly.

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
export class AppModule {
}
```

**Official Spartacus icon library documentation:**
https://sap.github.io/spartacus-docs/3.x/icon-library/

---

## Application Checklist

Before going live, verify everything works:

- [ ] `npm install` completed without errors
- [ ] Angular version is 21+ (`ng version`)
- [ ] Worldpay CSE script added to `index.html`
- [ ] Assets configured in `angular.json` (worldpay icons load correctly)
- [ ] Styles imported in `angular.json`
- [ ] `WorldpayModule` imported in `app.config.ts`
- [ ] Backend OCC endpoints enabled in SAP Commerce
- [ ] 3D Secure configured (if needed for your region)
- [ ] Mobile payment methods tested (Apple Pay, Google Pay on mobile)
- [ ] Fraud detection working (if enabled)
- [ ] B2B PO number field working (if B2B module enabled)
- [ ] Payment form renders without errors
- [ ] Test transaction completes successfully
- [ ] Error messages display correctly
- [ ] Production environment variables configured

## Supported Alternative Payment Methods

The following APMs are enabled by default. Region and currency availability varies:

| Method               | Regions     | Currencies         | Setup |
|----------------------|-------------|--------------------|-------|
| **Apple Pay**        | All         | All                | Auto  |
| **Google Pay**       | All         | All                | Auto  |
| **iDEAL**            | Netherlands | EUR                | Auto  |
| **Klarna**           | 8 countries | EUR, GBP           | Auto  |
| **Bancontact**       | Belgium     | EUR                | Auto  |
| **Przelewy24**       | Poland      | EUR                | Auto  |
| **EPS**              | Austria     | EUR                | Auto  |
| **ACH Direct Debit** | USA         | USD                | Auto  |
| **AliPay**           | China       | USD, EUR           | Auto  |
| **China Union Pay**  | China       | GBP, EUR, USD, SGD | Auto  |
| **PayPal**           | All         | All                | Auto  |
| **SEPA**             | EU          | EUR                | Auto  |

All methods are configured automatically. Just enable/disable in your backend configuration.

## Troubleshooting

### Installation Issues

#### "Module not found" error

```
Error: Cannot find module '@worldpay2020/sap-composable'
```

**Solution:**

```bash
# Clear npm cache
npm cache clean --force
 
# Remove and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### "Integrity check failed" error

```
npm ERR! Integrity check failed for @worldpay2020/sap-composable
```

**Solution:**

```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

---

### Configuration Issues

#### "WorldpayModule is not defined"

```
Error: Unexpected value 'WorldpayModule' imported by the module
```

**Solution:** Ensure import is correct in `app.config.ts`:

```typescript
import { WorldpayModule } from '@worldpay2020/sap-composable';
```

#### Payment form not showing

```
Warning: worldpay-payment-form component not recognized
```

**Solution:**

1. Check DevTools → Network → Verify CSE script loaded (https://secure.worldpay.com/resources/cse/js/worldpay-cse-1.latest.min.js)
2. Check Console for JavaScript errors
3. Verify WorldpayModule is imported
4. Clear browser cache: `Ctrl+Shift+Delete` (or `Cmd+Shift+Delete` on Mac)

---

### Runtime Issues

#### Payment always fails with "Invalid merchant key"

```
Error: Invalid or expired merchant key
```

**Solution:**

1. Verify merchant key is correct in your configuration
2. Check merchant key is active in [Worldpay dashboard](https://www.worldpay.com/en/payments)
3. Ensure key is for correct environment (test vs. production)

## License

Copyright (c) 2026 Worldpay Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Release Notes

### Release 221121.11.0 (Current)

* Compatible with SAP Commerce Cloud 221121
* Updated components to be standalone Angular 21
* Enhanced 3D Secure support
* Improved FraudSight integration
* B2B module stabilization
* Better error messages

### Release 2211.43.0

* Compatible with SAP Commerce Cloud 2211.43
* Removed deprecated APMs
* Supported APM's for B2B recipe:

| Payment          | Enabled Country         | Currency | Requires User Data | Additional User Data                                                                        |
|------------------|-------------------------|----------|--------------------|---------------------------------------------------------------------------------------------|
| ACH Direct Debit | US                      | USD      | Yes                | Account type, Account Number, Routing Number, Check Number, Company Name, Custom Identifier |
| Sepa             | AT,BE,FR,DE,IE,IT,NL,ES | EUR      |                    |                                                                                             |
| Open Banking     | AT,BE,FR,DE,IE,IT,NL,ES | EUR      |                    |                                                                                             |

Merchants can now offer ACH SEPA Open Banking Direct Debit as a payment method to their customers in the United States and Europe, providing a convenient and secure way for
customers to make payments directly from their bank accounts.

### Release 2211.43

* Compatible with SAP Commerce Cloud 2211.43
* Removed deprecated APM's:
    * GiroPay
    * Postepay
    * Sofort
* Supported APM's:

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

### Release 2211.28

* Compatible with SAP Commerce Cloud 2211.28
* Supported APM's:

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
* Supported APM's:

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
* 3D Secure
* Fraud Sight
* Supported APM's:

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