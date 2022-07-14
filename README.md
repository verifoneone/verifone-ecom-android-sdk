# verifone-android-sdk

**Building the library using Android Studio**

    1) Checkout Verifone Android SDK project using git
    2) Open the project in Android Studio
    3) on the top right side in Android Studio we have the tab called Gradle which brings up a gradle build tasks list
        (if the gradle build tasks list does not show then the option will need to be turned on
        go to: File->Settings->Experimental(tab), 
        in the Experimental tab there is the option: "Do not build Gradle task list during Gradle sync".
        Uncheck this option and then click on: File -> Sync project with Gradle files (this will generate the Gradle task list)
    4) in the gradle build task list we have the module VerifoneAndroidLIB
    Here select the option: VerifoneAndroidLIB->build->assemble (this will build our library debug+release versions)

    The build will be created in outputs directory of the VerifoneAndroidLIB module : "\VerifoneAndroidLIB\build\outputs\aar" 




**Show the available card payment options(sample code) :**

    private fun showPaymentOptions() {
        val payOptionsList = ArrayList<String>(2)
        payOptionsList.add(VerifonePaymentOptions.paymentOptionCard)
        payOptionsList.add(VerifonePaymentOptions.paymentOptionPayPal)
        payOptionsList.add(VerifonePaymentOptions.paymentOptionGooglePay)
       
       
        val paymentOptionsSheet = VerifonePaymentOptions(this,payOptionsList,::onPayMethodSelected)
        paymentOptionsSheet.showPaymentOptionList()
    }
    
    **parameters for VerifonePaymentOptions class:**
    
    this // activty context
    payOptionList // ArrayList that contains the payment options you want to display to the user
    ::onPayMethodSelected // callback method to receive the payment option selected by the user
    

 **Example callback method for getting the selected payment option:**
 
 
    private fun onPayMethodSelected(payMethod:String) {
        if (payMethod == VerifonePaymentOptions.paymentOptionPayPal) {
            startPayPalFlow()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionCard) {
            creditCardPay(true)
        } else if (payMethod == VerifonePaymentOptions.paymentOptionGooglePay) {
            mGooglePay.requestPaymentGoogle(createGooglePayConfigObject())
        }
    }    
   
    

**Show the Verifone card input screen:**

    val formConfigData = PaymentConfigurationData(
            this,
            fullPrice, //item price to be displayed on the verifone card input form including currency code
            publicKey, //public key for card encryption
            publicKeyReuseToken, //to be removed
            showRecurrent//show the save card details checkbox on the verifone card input form
        )
        
        mVerifonePaymentForm = VerifonePaymentForm(
            formConfigData, // card input form configuration object defined above
            ::onCreditCardInput // callback method used to return the encrypted card and save card details checkbox status
        )
        mVerifonePaymentForm.displayPaymentForm() // display the card input form
        

 **sample callback method for receiving credit card data (::onCreditCardInput)**
        
         private fun onCreditCardInput(
                encryptedCard: String,//encrypted card data
                encryptedCardReuseToken: String,
                customerName:String,
                cardData: PayerCardData,//unencrypted non-sensitive card data that is returned
                recurrentPaymentsActive: Boolean
            ) {
                //this is a callback interface method that is used for getting the payer encrypted card data
                
                
                if (encryptedCard.isNotEmpty()) {
                    //start the card payment flow with encrypted card parameter
                    //startCardPaymentFlow()
                
                } else {
                    progressDialog.dismiss()
                    val errorFull = "Card encryption failed. Check the public key parameter in settings."
                    //show user error
                }
            }
            
        
        
 **code sample to encrypt the card without using the card input form:**
    
        val encryptedCard = VerifonePaymentForm.encryptCardData(/*expiryMonth*/8, /*expiryYear*/2024, /*card number*/"41111111111111111",/*cvcNumber*/ "417", "merchant public key")
            
            
 **sample 3ds flow step 1: creating jwt token**
 
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
            
            
 **sample 3ds flow step 2: init Verifone 3DS SDK**
     
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
   
             
 **sample 3ds flow step 3: lookup request**
 
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
    
 ** sample 3ds flow step 4: 3DS identity validation**
 
     //after the lookup request we use cardinal with the response payload and transaction id to validate user identity and obtain 3ds payload from the the SDK
     mThreeDSVersion = response.threeds_version
        mVerifone3DSecureManager.continueThreedsValidation(
            response.transaction_id,
            response.payload,
            this
        )//this function continues the 3ds validation flow to step 3
        
 ** sample 3ds flow step 5: final card payment api call**
 
         3ds SDK validation is done and the final card payments API call to our backend services can begin
         After the validation is complete we will get a "ThreedsValidationData" parameter that is the 3ds payload required for card payments api call
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
    