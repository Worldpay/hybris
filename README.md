# Worldpay Connector for the SAP Commerce Cloud

The Worldpay Connector for SAP Commerce Cloud is a seamless extension to the Commerce storefront, enabling retailers to implement their global payment strategy through a single integration in a secure, compliant and unified approach. The extensive WorldPay extension enables retailers to gain access to: Global and regional payment methods, a variety of integration options, customisable hosted payment pages, 3D Secure and market leading fraud screening (RiskGuardian™).
The Connector is SAP Premium Certified.
About Worldpay: Worldpay (formerly RBS WorldPay) is a payment processing company. The company provides payment services for mail order and Internet retailers, as well as point of sale transactions. Customers are a mix of multinational, multichannel retailers, with the majority being small business merchants. It also provides loans to small businesses.

## Introduction
## SAP Commerce Cloud
The extension is crafted for SAP Commerce Cloud as well previous versions of what was formerly called Hybris.

## Release Information
This release is tailored for SAP Commerce Cloud 2005. It is advised to use the latest release available in Github to get the benefits of newest development made to this extension.

# Installation and Usage

## Installing the Plugin using the provided recipes

The AddOn provides three gradle recipes to be used with the hybris installer.

1. wp_b2c_acc with fulfilment functionality for both accelerator storefront and OCC.

2. wp_b2c_acc_oms with OMS functionality for both accelerator storefront and OCC.

3. wp_b2b_acc with fulfilment functionality for b2b accelerator storefront.

4. wp_b2c_acc_occ with fulfilment functionality for b2c accelerator storefront and extra porperties for Spartacus.

The recipes are based on the b2c_acc, b2c_acc_oms and b2b_acc recipes provided by hybris.

The recipes can be found under the installer folder.

To use the recipes on a clean hybris installation, copy the folder hybris to your ${HYBRIS_BIN_DIR}

Since the recipe generates the local.properties file with the properties defined in the recipe, optionally you can add your local.properties to the customconfig folder.

In order to install the AddOn using one of the recipes, run the following commands:
- This will run setup, build, initialize and start
HYBRIS_HOME/installer$ ./install.sh -r [RECIPE_NAME] perform

## RELEASE NOTES
- A read/write socket connection timeout was configured in the library
  
-  ypay-lib was updated to the latest DTD version
  
-  When upgrading to version 2011 redundant address converters were removed after ootb hybris populated the email
  
-  A bug was raised to fix hybris ootb causing an order process failure. Unnecessary user rights were removed and warehouse users were imported just for OMS. User rights were also moved to the common impex 
  
-  Added obfuscated cart number, card expiry month and year in the order confirmation page and payment info for googlepay with spartacus
  
-  Fixed a bug related to saved cards for the b2b accelerator. The issue was caused by order placed with replenishment and card tokenized and saved in the step. The billing address was not saved in this case and it was causing the error when reusing the saved card.
