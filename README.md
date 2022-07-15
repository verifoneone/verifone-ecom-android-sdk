# Verifone iOS SDK

Verifone SDKs provide the ability to encrypt and validate card payments, handles 3D Secure verification and interacts with alternative payment methods.  

### Requirements

- Android Studio Arctic Fox or above
- Kotlin 1.4.30 or higher

### Suppported Payment Methods

- Credit Cards (with 3D Secure support)
- Paypal
- Google Pay

### Installation

The SDK can be built using the assemble gradle task or fetched from the release page in github.


###### Dependencies

Along with the SDK aar file, add the following dependencies to your app's gradle build script **build.gradle(:app)**

```kotlin
dependencies {
    ...

    implementation 'com.google.android.gms:play-services-wallet:19.1.0'
    implementation group: 'org.bouncycastle', name: 'bcpg-jdk15on', version: '1.57'
    implementation 'org.jfrog.cardinalcommerce.gradle:cardinalmobilesdk:2.2.5-4'
    ...
}
```


The cardinal mobile sdk is optional and only required if you re using the SDK's 3DS features. In order To fetch the cardinal mobile sdk you need to add the following lines in your root gradle build file: build.gradle(Project: ...root)

```kotlin
maven {
    url  "https://cardinalcommerceprod.jfrog.io/artifactory/android"
    credentials {
        username '...'
        password '...'
    }
}

To get the credentials for maven please contact cardinal directly

```


## Usage


Set the payment methods you want to offer and display the list to the customer.

```kotlin
val payOptionsList = ArrayList<String>(2)
payOptionsList.add(VerifonePaymentOptions.paymentOptionCard)
payOptionsList.add(VerifonePaymentOptions.paymentOptionPayPal)
payOptionsList.add(VerifonePaymentOptions.paymentOptionGooglePay)


val paymentOptionsSheet = VerifonePaymentOptions(this,payOptionsList,::onPayMethodSelected)
paymentOptionsSheet.showPaymentOptionList()
```

###### Transaction flow without 3DS.

A simple implementation for each payment method is provided below.

If the customer selected credit card, the SDK will return the encrypted card data to perform transaction request to the merchant server.

If the customer selected PayPal, you will have to do a create transaction API call, and then pass the `approvalUrl` to the `PayPalConfirmationForm` to initialize the PayPal webview.  The SDK will display the confirmation PayPal screen and provide you with the details necessary to perform the confirmation API call.

If the customer selected Google Pay, you can initialize Google Pay using the `VerifoneGooglePay` helper. This helper provides a simple wrapper for you to define your configuration and trnsaction properties, present the native Google Pay UI and get the payment token. You can then use the payment token as the `wallet_payload` when making a wallet transaction API call.

```kotlin
    private fun onPayMethodSelected(paymentMethod: String) {
        when (paymentMethod) {

            VerifonePaymentOptions.paymentOptionPayPal -> {
                // make the paypal transaction API call to get the redirect URL
                paypalScreen = PayPalConfirmationForm(this, "PAYPAL_REDIRECT_URL, ::paypalConfirmation)
                paypalScreen.displayPaypalConfirmation()
            }

            VerifonePaymentOptions.paymentOptionCard -> {
                val formConfigData = PaymentConfigurationData(
                    ctx = this,
                    publicKey = "YOUR_PUBLIC_ENCRYPTION_KEY",
                    price = "100",
                    showStoredCard = false,
                    displayUICustomization = FormUICustomizationData()
                )

                val mVerifonePaymentForm = VerifonePaymentForm(formConfigData, ::onCreditCardInput)
                mVerifonePaymentForm.displayPaymentForm()
            }

            VerifonePaymentOptions.paymentOptionGooglePay -> {
                mVerifoneGooglePay = VerifoneGooglePay(::showGooglePayButton, this, googlePayRequestCode, VerifoneGooglePay.testEnvironment)
                mVerifoneGooglePay.showGooglePayIfPossible()

                val googlePayConfig = GooglePayConfiguration()
                googlePayConfig.transactionAmount = 100.00
                googlePayConfig.shippingCost = 0
                googlePayConfig.countryCode = "US"
                googlePayConfig.currencyCode = "USD"
                googlePayConfig.phoneNumberRequired = false
                googlePayConfig.gatewayMerchantID = "YOUR_WALLET_CONTRACT"
                googlePayConfig.supportedCountries = JSONArray(listOf("US", "GB"))
                googlePayConfig.merchantName = "YOUR_GOOGLE_MERCHANT_NAME"
                googlePayConfig.merchantID = "YOUR_GOOGLE_MERCHANT_ID"

                mVerifoneGooglePay.requestPaymentGoogle(googlePayConfig)
            }
        }
    }

    private fun onCreditCardInput(creditCardInputResult: CreditCardInputResult) {
        Log.d("payment_method", creditCardInputResult.encryptedCardData);
        Log.d("payment_method", creditCardInputResult.cardProperties.cardBrand);
        Log.d("payment_method", creditCardInputResult.payerName);
        Log.d("payment_method", creditCardInputResult.storeCard.toString());
    }

    private fun paypalConfirmation(queryParams:MutableMap<String, String>) {
        // this will be called when your paypal cancel or success urls are hit
        if (queryParams.isNotEmpty()) {
            // paypalScreen.dismissPaypalScreen()
            // Check the queryParams returned by paypal and authorize or capture the payment
        }
    }

    private fun showGooglePayButton(googlePayPossible:Boolean) {
        // called to let you know if google pay is possible on this device
        val isGooglePayPossible = googlePayPossible
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            googlePayRequestCode -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            var googlePayResult = mVerifoneGooglePay.parseGooglePayload(intent)
                            // Pass google pay result as the wallet payload on a wallet transaction request
                        }

                    RESULT_CANCELED -> {
                        // Nothing to do here normally - the user simply cancelled without selecting a payment method.
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
```

