# Verifone Android SDK

Verifone SDKs provide the ability to encrypt and validate card payments, handles 3D Secure verification and interacts with alternative payment methods.  

### Requirements

- Android Studio Arctic Fox or above
- Kotlin 1.4.30 or higher

### Supported Payment Methods

- Credit Cards (with 3D Secure support)
- Paypal
- Google Pay

### Installation

The SDK can be built using the assemble gradle task or fetched from the release page in github and added to your build.gradle.

```kotlin
implementation files('/path/to/VerifoneAndroidLIB-release-1.1.3.aar')
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

### Proguard
If you are allowing minification/shrinking for your release, you should also add these lines to your proguard-rules.pro:

```kotlin
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }
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

###### Encrypt a credit card without using the UI componet
```kotlin
val card = VerifonePaymentForm.encryptCardData(12,2025,"4111111111111111","123","YOUR_PUBLIC_KEY")
```

###### Encrypt a gift card without using the UI componet**
```kotlin
val giftCard = VerifonePaymentForm.encryptGiftCardData("41111111111111111111", "1234", "YOUR_PUBLIC_KEY")
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

###### 3DS Setup
If 3DS is required we provide a wrapper around the cardinal commerce SDK to simplify the the 3ds flow, example below:

**Step 1: Setup JWT token**

```kotlin
//setting up jwt token creation
createJWTControl = JwtCreateController(this,::getJWTCallDone)
val threedContractID = "merchant contract id"
createJWTControl.startCreateJWTRequest(threedContractID)

//starting jwt token create sample code
fun startCreateJWTRequest(threeds_contract_id:String) {
val mRetrofitClient =
    TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_JWT,authInterceptor)
val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

val requestObject = RequestJwtObject(threeds_contract_id)

val requestObs = inputApi.createJWT(requestObject)

requestObs.subscribeOn(Schedulers.newThread())
    .observeOn(AndroidSchedulers.mainThread())
    .map { result->result }
    .subscribe(resultHandler)
}

//getting the jwt from API call response
//callback function example for
private fun getJWTCallDone(response: JWTResponse) {
    if (response.api_fail_message.isEmpty()) {
        
        val jwt_token = response.jwt
        
        initThreeDSecure(jwt_token)
        } else {
           //api call failed display error message
        }

    }
```

**Step 2: init Verifone 3DS SDK**

```kotlin
 private fun initThreeDSecure(jwt: String) {
        val configurationThreeds = ThreedsConfigurationData(this, jwt)
        mVerifone3DSecureManager = Verifone3DSecureManager(
            configurationThreeds,
            ::onThreedsSetupDone,
            ::onThreeDValidationResult
        )
        mVerifone3DSecureManager.cardinalValidateTS()//starts the actual 3ds SDK validation
 }
 val configurationThreeds = ThreedsConfigurationData(this, jwt)//contains the config data for 3ds SDK
 ::onThreedsSetupDone,//callback method for setup complete , in this method the next step is starting the lookup request
 ::onThreeDValidationResult//callback method for validating transaction , called when validation is complete
 
 //sample callback function that 3ds SDK will call when setup completes
 private fun onThreedsSetupDone(deviceID: String) {
    startLookupRequest(deviceID, encryptedPayerCard, receivedKeyAlias) // starting the lookup request with device id returned by the 3ds SDK and the ecnrypted card
 }
```


**Step 3: lookup request**

```kotlin
    //setting up parameters for lookup API call(main API request json object creation)
     private fun initLookupObject(deviceID:String, encryptedCard:String, keyAlias:String,amount:Double): ThreedLookupObject {
        val requestObject = ThreedLookupObject()
            requestObject.billing_first_name = "john"
            requestObject.billing_last_name = "smith"
            requestObject.billing_phone = "5551232134"
            requestObject.billing_address_1 = "input payer address"
            requestObject.billing_city = "billing city"
            requestObject.billing_postal_code = "postal code"
            requestObject.billing_state = "billing state"
            requestObject.amount = (amount).toInt()
            requestObject.currency_code = "currency code"
            requestObject.card = encryptedCard
            requestObject.publicKeyAlias = keyAlias
            requestObject.threeds_contract_id = "merchant contract id (string value)"
            requestObject.merchant_reference = "merchant reference (string value)"
            requestObject.device_info_id = deviceID
            requestObject.email = "john@gmail.com"
            requestObject.token = "3134324"
            return requestObject
        }
        
        
    //in this sample we use retrofit for the lookup api call
    @POST("v2/lookup")
    fun threedsLookup(@Header("Content-Type") contentType:String,@Body dataParams: ThreedLookupObject):Observable<LookupResponse>
            
            
    //starting the lookup API call  
    fun startLookupRequest(deviceID:String, card:String, receivedKeyAlias:String,amount:Double) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_JWT,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)


        val lookupObject = initLookupObject(deviceID,card,receivedKeyAlias,amount)
        val requestObs = inputApi.threedsLookup("application/json",lookupObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(orderResultHandler)
    }
```

**Step 4: 3DS identity validation**

```kotlin
     //after the lookup request we use cardinal with the response payload and transaction id to validate user identity and obtain 3ds payload from the the SDK
     mThreeDSVersion = response.threeds_version
        mVerifone3DSecureManager.continueThreedsValidation(
            response.transaction_id,
            response.payload,
            this
        )//this function continues the 3ds validation flow to step 3
```

**Step 5: final card payment api call**
3ds SDK validation is done and the final card payments API call to our backend services can begin
After the validation is complete we will get a "ThreedsValidationData" parameter that is the 3ds payload required for card payments api call
```kotlin
 private fun onThreeDValidationResult(authData: ThreedsValidationData) {
        if (authData.validationStatus == "fail") {
            runOnUiThread {
               //display error message 3ds validation failed
            }
            return
        }
        val mCardAuth3D = ThreedsValidationData(
            authData.eciFlag,
            authData.enrolled,
            authData.cavv,
            authData.paresStatus,
            authData.signatureVerification,
            authData.dsTransactionId,
            authData.xid
        )//here we create a 3ds payload in JSON format for the card payments final api call
        
        val currencyParam = "USD"
        val merchantReferenceParam = "merchant_ref"
      
        ........
      
        mCardPayController.startCardPaymentDataRequest(encryptedCard,payment_provider_contract,amount,auth_type,capture_now,merchant_reference,card_brand,currency_code,dynamic_descriptor,keyAlias,threedData)
        
    ........
    
    //sample function for final card payments api call
    
   fun startCardPaymentDataRequest(encryptedCard:String,payment_provider_contract:String, amount:Double,auth_type:String,capture_now:Boolean ,merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String,keyAlias:String,threedValidation: ThreedsValidationData? = null) {
        val contentType = "application/json"
        val mRetrofitClient = TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = CardPaymentObject(encryptedCard,payment_provider_contract,amount,auth_type,capture_now,
            merchant_reference,
            card_brand,
            currency_code,
            dynamic_descriptor,
            threedValidation,//threeds validation object created with 3ds SDK
            keyAlias
        )

        val requestObs: Observable<CardPaymentResponse> = inputApi.sendCardPaymentData(contentType,requestObject)
        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
    
    .......
    
    @POST("v2/transactions/card")
    fun sendCardPaymentData(@Header("Content-Type") contentType:String,  @Body params: CardPaymentObject): Observable<CardPaymentResponse>
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

