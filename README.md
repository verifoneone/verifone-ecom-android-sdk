# Verifone Android SDK

Verifone SDKs provide the ability to encrypt and validate card payments, handles 3D Secure verification and interacts with alternative payment methods.  

### Requirements

- Android Studio Arctic Fox or above
- Kotlin 1.4.30 or higher

### Suppported Payment Methods

- Credit Cards (with 3D Secure support)
- Paypal
- Google Pay

### Installation

The SDK can be built using the assemble gradle task or fetched from the release page in github and added to your build.gradle.

```kotlin
implementation files('/path/to/VerifoneAndroidLIB-release-1.1.2.aar')
```

###### Dependencies

Along with the SDK aar file, add the following dependencies to your app's gradle build script **build.gradle(:app)**

```kotlin
dependencies {
    ...

    implementation 'com.google.android.gms:play-services-wallet:19.1.0'
    implementation group: 'org.bouncycastle', name: 'bcpg-jdk15on', version: '1.57'
    implementation 'org.jfrog.cardinalcommerce.gradle:cardinalmobilesdk:2.2.7-2'
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

###### Encrypt a card without using the UI componet
```kotlin
val card = VerifonePaymentForm.encryptCardData(12,2025,"4111111111111111","123","YOUR_PUBLIC_KEY")
```

###### Transaction flow with the UI componet.

A simple implementation for each payment method is provided below.

If the customer selected credit card, the SDK will return the encrypted card data to perform transaction request to the merchant server.

If the customer selected PayPal, you will have to do a create transaction API call, and then pass the `approvalUrl` to the `PayPalConfirmationForm` to initialize the PayPal webview.  The SDK will display the confirmation PayPal screen and provide you with the details necessary to perform the confirmation API call.

If the customer selected Google Pay, you can initialize Google Pay using the `VerifoneGooglePay` helper. This helper provides a simple wrapper for you to define your configuration and trnsaction properties, present the native Google Pay UI and get the payment token. You can then use the payment token as the `wallet_payload` when making a wallet transaction API call.

```kotlin

private val googlePayReceiver = createGooglePayReceiver()
private val googlePayRequestCode = 7419
private val vippsPayReceiver = createVippsPayReceiver()

...

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
                mVerifoneGooglePay = VerifoneGooglePay(this, VerifoneGooglePay.testEnvironment)
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

                mVerifoneGooglePay.requestPaymentGoogle(googlePayConfig, googlePayRequestCode, googlePayReceiver)
            }

            VerifonePaymentOptions.paymentOptionVipps -> {
                // make ecommerce API call to initiate a vipps transaction and use the redirect URL to initiate the vipps authorization
                // https://verifone.cloud/api-catalog/verifone-ecommerce-api#tag/Ecom-Payments/operation/vippsTransaction

                VippsPaymentsManager.showVippsAuthorizeScreen(this, vippsPayReceiver, transactionResponse.redirectURL)
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

    private fun createGooglePayReceiver():ActivityResultLauncher<Intent> {
        val actResLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(StartActivityForResult()){
                    result:ActivityResult->
                if (result.resultCode == googlePayRequestCode ) {
                    if (result.data!=null) {
                        result.data?.let {
                            mGooglePayload = VerifoneGooglePay.parseGooglePayload(it)
                        }
                    } else {
                        Toast.makeText(this,"Google pay transaction failed",Toast.LENGTH_LONG).show()
                    }
                }
            }
        return actResLauncher
    }


    private fun createVippsPayReceiver():ActivityResultLauncher<Intent> {
        val vippsContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                VippsPaymentsManager.vippsTransactionFlowDone -> {
                    // fetch the transaction with the ecommerce API to validate the status
                    //https://verifone.cloud/api-catalog/verifone-ecommerce-api#tag/Transaction/operation/readTransaction
                }
                VippsPaymentsManager.vippsTransactionNotStarted->{
                    Toast.makeText(this,"transaction failed",Toast.LENGTH_SHORT).show()
                }

            }
        }
        return vippsContract
    }
```


##### Customize the card form

Configure default theme properties for a credit card form.

```kotlin
val customization = FormUICustomizationData()
customization.formInputTextColor = "#F08080"
```

List of properties for customizing the credit card form.

N | PROPERTY NAME | DESCRIPTION 
| --- | --- | --- |  
1 | formBackground | Card form view background color |
2 | textFieldBackground | Background color for any text fields in a card form |
3 | inputTextColor | Text color for any text fields in a card form |
4 | hintColor | Text color for any hints in a card form |
5 | titleTextColor | Text color for any titles in a card form |
6 | buttonColor | button background color |
7 | payButtonDisabledBackgroundColor | Pay button background color for disabled state |
8 | textFont | Typeface |
9 | textFontID | Int |

