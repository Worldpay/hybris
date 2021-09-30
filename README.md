# Worldpay Connector for the SAP Commerce Cloud

The Worldpay Connector for SAP Commerce Cloud is a seamless extension to the Commerce storefront, enabling retailers to implement their global payment strategy through a single integration in a secure, compliant and unified approach. The extensive WorldPay extension enables retailers to gain access to: Global and regional payment methods, a variety of integration options, customisable hosted payment pages, 3D Secure and market leading fraud screening (RiskGuardian™).
The Connector is SAP Premium Certified.
About Worldpay: Worldpay (formerly RBS WorldPay) is a payment processing company. The company provides payment services for mail order and Internet retailers, as well as point of sale transactions. Customers are a mix of multinational, multichannel retailers, with the majority being small business merchants. It also provides loans to small businesses.

## Introduction
## SAP Commerce Cloud
The extension is crafted for SAP Commerce Cloud as well previous versions of what was formerly called Hybris.

## Release Information
This release is tailored for SAP Commerce Cloud 2105. It is advised to use the latest release available in Github to get the benefits of newest development made to this extension.

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
##Features:
- SEPA Direct Debit has been integrated as an APM
- Prime routing
- Level2/3 Data
- Request and response payloads are now saved into the Order
- Fraud Sight Integration
- Unprocessed order tickets are now linked to their order

##Breaking changes:
- Several POJO objects are now created as beans defined in a beans.xml file. 
- transformToInternalModel has been removed. New Converters/Populators  per each one of the types that we have has been created and are used instead
- Java Classes from the Worldpay's DTD are now generated with JAXB's xjc
- The merchant configuration has been migrated to the data model:
	1. Every merchant.xml file has been deleted from the repository and sensitive data like passwords, macSecret are not visible anymore.
	2. New Types have been added into the DB definition: WorldpayMerchantConfiguration, WorldpayApplePayConfiguration, WorldpayGooglePayConfiguration, WorldpayThreeDS2JsonWebTokenConfiguration.
	3. New enumerations have been added into the DB definition: EnvironmentType, ApplePaySupportedNetworks, ApplePayMerchantCapabilities, GooglePayCardNetworks, GooglePayCardAuthMethods, ChallengePreference
	4. The Merchant Configuration can be found in the backoffice following the path: Worldpay → Merchant Configuration
	5. The Merchant Configuration is related to the Site. Every site will have now a new tab called Worldpay. Inside this type there is the section MERCHANT CONFIGURATION DETAILS in which you can find the new 3 attributes:
		- Web Merchant Configuration
		- ASM Merchant Configuration
		- Replenishment Merchant Configuration
- All of them are of the same type WorldpayMerchantConfiguration. This new configuration represents the old xml configuration of the merchant.xml file.

##Bugs Fixed: 
- Fixed an issue related with date generation that was causing unprocessed order tickets not being created.
- Fixed an issue that was causing customers being redirected to an incorrect HOP URL when using express checkout.
- Fixed a NPE thrown on the summary step of the checkout when a paymentInfo has no bin number set.
- Fixed an issue with Ideal that was making the successURL not being encrypted.
- The profiletagaddon has been added to the project to fix a JSP file not found exception.
- Fixed an issue related to the tax configuration that was causing an error in the Electronics site when paying with a saved card.
- Fixed an issue related to the client side encryption that was causing an error when accessing to the payment details page.
- Fixed an issue with Klarna where an invalid shopper locale was being set.

