# Worldpay Connector for the SAP Commerce Cloud

The Worldpay Connector for SAP Commerce Cloud is a seamless extension to the Commerce storefront, enabling retailers to implement their global payment strategy through a single integration in a secure, compliant and unified approach. The extensive WorldPay extension enables retailers to gain access to: Global and regional payment methods, a variety of integration options, customisable hosted payment pages, 3D Secure and market leading fraud screening (RiskGuardian™).
The Connector is SAP Premium Certified.
About Worldpay: Worldpay (formerly RBS WorldPay) is a payment processing company. The company provides payment services for mail order and Internet retailers, as well as point of sale transactions. Customers are a mix of multinational, multichannel retailers, with the majority being small business merchants. It also provides loans to small businesses.

## Introduction
## SAP Commerce Cloud
The extension is crafted for SAP Commerce Cloud as well previous versions of what was formerly called Hybris.

## Release Information
This release is tailored for SAP Commerce Cloud 1905. Functionalities of fixed of newer release (2005) may not be backported into this release. It is advised to use the latest release available in Github to get the benefits of newest development made to this extension.

# Installation and Usage

## Installing the Plugin using the provided recipes

The AddOn provides three gradle recipes to be used with the hybris installer.

1. wp_b2c_acc with fulfilment functionality for both accelerator storefront and OCC.

2. wp_b2c_acc_oms with OMS functionality for both accelerator storefront and OCC.

3. wp_b2b_acc with fulfilment functionality for b2b accelerator storefront.

4. wp_b2c_acc_occ with fulfilment functionality for b2c accelerator storefront and OCC with specific properties for Spartacus.


The recipes are based on the b2c_acc, b2c_acc_oms and b2b_acc recipes provided by hybris.

The recipes can be found under the installer folder.

To use the recipes on a clean hybris installation, copy the folder hybris to your ${HYBRIS_BIN_DIR}

Since the recipe generates the local.properties file with the properties defined in the recipe, optionally you can add your local.properties to the customconfig folder.

In order to install the AddOn using one of the recipes, run the following commands:
- This will run setup, build, initialize and start
HYBRIS_HOME/installer$ ./install.sh -r [RECIPE_NAME] perform

## RELEASE NOTES
- OCC new endpoints to support 3DS Flex, Apple Pay and Google Pay under Spartacus frontend

- Tokenisation for Google Pay and Paypal

- Support for Klarna PayNow, Klarna PayLater, Klarna SliceIt (https://developer.worldpay.com/docs/wpg/apms/klarnakp)

- RiskData information added for credit card payments under redirect implementation

- New recipe wp_b2c_acc_occ to support the Spartacus storefront application