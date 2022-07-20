package com.verifone.mobile


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.verifone.connectors.datapack.configuration.FormUICustomizationData
import com.verifone.connectors.datapack.configuration.PaymentConfigurationData
import com.verifone.connectors.googlepay.GooglePayConfiguration
import com.verifone.connectors.googlepay.VerifoneGooglePay
import com.verifone.connectors.googlepay.WalletPayloadObject
import com.verifone.connectors.screens.PayPalConfirmationForm
import com.verifone.connectors.screens.VerifonePaymentForm
import com.verifone.connectors.screens.VerifonePaymentOptions
import com.verifone.mobile.controllers.*
import com.verifone.mobile.dataobjects.googlepayments.GooglePayResponse
import com.verifone.mobile.dataobjects.paypal.PayPalRequestObject
import com.verifone.mobile.dataobjects.paypal.PurchasedItem
import com.verifone.mobile.dataobjects.paypal.UnitValueData
import com.verifone.mobile.dialogs.ErrorDisplayDialog
import com.verifone.mobile.dialogs.MessageDisplayDialog
import com.verifone.mobile.interfaces.LookupRequestDone
import com.verifone.mobile.responses.JWTResponse
import com.verifone.mobile.responses.TestLookupResponse
import com.verifone.mobile.responses.cardpayments.CardPaymentResponse
import com.verifone.mobile.responses.paypal.PayPalConfirmationResponse
import com.verifone.mobile.responses.paypal.PayPalResponse
import com.verifone.mobile.screens.CustomizationSettings
import com.verifone.mobile.screens.PaymentFlowDone
import com.verifone.connectors.threeds.Verifone3DSecureManager
import com.verifone.connectors.threeds.dataobjects.ThreedsConfigurationData
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData
import com.verifone.connectors.util.CreditCardInputResult
import kotlinx.android.synthetic.main.activity_checkout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt


/**
 * Checkout implementation for the app
 */
open class CheckoutActivity : AppCompatActivity(), LookupRequestDone {

    companion object{
        var currencyTV = "EUR"
    }


    private val googlePayRequestCode = 7419
    private var amount: Double = 0.0
    private var isGooglePayPossible = false
    private lateinit var mGooglePayload:WalletPayloadObject
    private lateinit var cardBrand: String
    var cardToken:String = ""
    var customer:String = ""
    val keyRecurrent = "key_store_rec"
    val keyCardBrand = "key_store_brand"
    var mReportingDlg:ErrorDisplayDialog?=null
    /**
     * A client for interacting with the Google Pay API.
     *
     * @see [PaymentsClient](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient)
     */

    private val shippingCost = (90 * 1000000).toLong()

    private lateinit var garmentList: JSONArray
    private lateinit var selectedGarment: JSONObject

    private lateinit var createJWTControl: JwtCreateController
    private lateinit var mExampleCardPay:ExampleCardPayment
    private lateinit var mThreeDSVersion:String
    private var startedGooglePay = true
    private var startedCardPay = true
    private lateinit var settingsButton:ImageView
    @Volatile private lateinit var progressDialog: ProgressDialog
    private var mVerifone3DSecureManager: Verifone3DSecureManager?=null
    private lateinit var mPaymentBtn:AppCompatButton
    private lateinit var mGooglePay:VerifoneGooglePay
    private lateinit var mCreditCardBtnNoThreeds:AppCompatButton
    private lateinit var mVerifonePaymentForm:VerifonePaymentForm
    private lateinit var paypalScreen:PayPalConfirmationForm
    private var paypalTransactionID:String = ""
    /**
     * Arbitrarily-picked constant integer you define to track a request for payment data activity.
     *
     * @value #LOAD_PAYMENT_DATA_REQUEST_CODE
     */
    private var encryptedPayerCard:String = ""
    private lateinit var receivedKeyAlias:String
    private var showRecurrent:Boolean = false

    private var threedsSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        progressDialog = ProgressDialog(this)
        mPaymentBtn = findViewById(R.id.payment_options_btn)
        mCreditCardBtnNoThreeds = findViewById(R.id.card_payment_btn_no_threeds)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = View.inflate(this, R.layout.custom_action_bar, null)
        // Set up the mock information for our item in the UI.
        settingsButton = findViewById(R.id.settings_btn)
        selectedGarment = fetchRandomGarment()
        displayGarment(selectedGarment)

        mGooglePay = VerifoneGooglePay(::showGooglePayButton,this,googlePayRequestCode,VerifoneGooglePay.testEnvironment)
        mGooglePay.showGooglePayIfPossible()

        mPaymentBtn.setOnClickListener {
            showPaymentOptions()
        }

        mCreditCardBtnNoThreeds.setOnClickListener {

        }

        settingsButton.setOnClickListener {
            val settingsScreen = Intent(this, CustomizationSettings::class.java)
            startActivity(settingsScreen)
        }
        threedsSelected = CustomizationSettings.getThreedsUserOption(this)
    }

    private fun setLocation(setEnglish:Boolean) {
        var langSelected = "EN"
        if (!setEnglish){
            try{
                langSelected = CustomizationSettings.getStoredLanguage(this).substring(0,2)
            }catch (e:StringIndexOutOfBoundsException){
                langSelected = "EN"
            }
        }
        //if (langSelected.isEmpty()) langSelected ="EN"
        val locale = Locale(langSelected)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, this.resources.displayMetrics)
    }

    //this function starts the card payment flow, please note by this point the merchant should have key alias set up
    private fun startCardPaymentFlow() {
        startedCardPay = true
        startedGooglePay = false
        receivedKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        //"K1463"
        startJWTCreate()
    }

    private fun cardPaymentNoThreeDS(cardBrand: String){
        showLoadingSpinner()
        startedCardPay = true
        startedGooglePay = false
        receivedKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)

        val paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(this)
        val authType = "FINAL_AUTH"
        val captureNow= true
        val dynamicDescriptor = "M.reference"
        val merchantReference = "test reference"
        val currencyCode = currencyTV
        var cardBrandParam = ""
        val reuseTokenKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        cardBrandParam = if (cardBrand.isEmpty()){
            "VISA"
        } else {
            cardBrand
        }

        mExampleCardPay = ExampleCardPayment(this,::onCardPayFlowDone)
        mExampleCardPay.flowCardPaymentNoThreeds(
            encryptedPayerCard,
            paymentProviderContract,
            amount,
            authType,
            captureNow,
            merchantReference,
            cardBrand,
            currencyCode,
            dynamicDescriptor,
            receivedKeyAlias,
            null
        )
    }

    private fun tokenPaymentNoThreeDS(cardBrand: String) {
        showLoadingSpinner()
        startedCardPay = true
        startedGooglePay = false
        receivedKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)

        val paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(this)

        val authType = "FINAL_AUTH"
        val captureNow= true
        val dynamicDescriptor = "M.reference"
        val merchantReference = "test reference"
        val currencyCode = currencyTV
        var cardBrandParam = ""
        val reuseTokenKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        if (cardBrand.isEmpty()){
            cardBrandParam = "VISA"
        } else {
            cardBrandParam = cardBrand
        }
        mExampleCardPay = ExampleCardPayment(this,::onCardPayFlowDone)
        if (cardToken.isNotEmpty()) {
            mExampleCardPay.flowTokenPayment(
                cardToken,
                paymentProviderContract,
                amount,
                authType,
                captureNow,
                merchantReference,
                cardBrandParam,
                currencyCode,
                dynamicDescriptor,
                reuseTokenKeyAlias,
                null
            )

        }
    }

    private fun completeReuseTokenPaymentFlow(cardBrand: String, threedParam: ThreedsValidationData) {

        val paymentProviderContract =CustomizationSettings.getPaymentsProviderContract(this)
        val authType = "FINAL_AUTH"
        val captureNow= true
        val dynamicDescriptor = "M.reference"
        val merchantReference = "test reference"
        val currencyCode = currencyTV
        val keyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        mExampleCardPay = ExampleCardPayment(this,::onCardPayFlowDone)
        mExampleCardPay.flowTokenPayment(
            cardToken,
            paymentProviderContract,
            amount,
            authType,
            captureNow,
            merchantReference,
            cardBrand,
            currencyCode,
            dynamicDescriptor,
            keyAlias,
            threedParam
        )
    }


    private fun completeCardPaymentFlow(cardBrand: String,merchantReference:String,currencyCode:String ,threedParam: ThreedsValidationData) {

        val paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(this)
        val authType = "FINAL_AUTH"
        val captureNow= true
        val dynamicDescriptor = "M.reference"
        mExampleCardPay = ExampleCardPayment(this,::onCardPayFlowDone)
        mExampleCardPay.flowCardPayment(
            encryptedPayerCard,
            paymentProviderContract,
            amount,
            authType,
            captureNow,
            merchantReference,
            cardBrand,
            currencyCode,
            dynamicDescriptor,
            receivedKeyAlias,
            threedParam
        )
    }

    private fun createGooglePayConfigObject():GooglePayConfiguration {
        val temp = GooglePayConfiguration()
        temp.transactionAmount = 10.0//selectedGarment.getDouble("price")
        temp.shippingCost = 0
        temp.countryCode = "US"
        temp.currencyCode = currencyTV
        temp.phoneNumberRequired = false
        temp.gatewayMerchantID = CustomizationSettings.getGooglePayInputGatewayMerchantID(this)
        temp.supportedCountries = JSONArray(listOf("US", "GB"))
        temp.merchantName = CustomizationSettings.getGooglePayInputMerchantName(this)
        temp.merchantID = CustomizationSettings.getGooglePayInputMerchantID(this)
        return temp
    }

    private fun onPayMethodSelected(payMethod:String) {
        threedsSelected = CustomizationSettings.getThreedsUserOption(this)
        if (payMethod == VerifonePaymentOptions.paymentOptionPayPal) {
            startPayPalFlow()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionCard) {
            creditCardPay()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionGooglePay) {
            mGooglePay.requestPaymentGoogle(createGooglePayConfigObject())
        }
    }

    private fun showPaymentOptions() {
        val payOptionsList = ArrayList<String>(2)
        if (getShowCard()) payOptionsList.add(VerifonePaymentOptions.paymentOptionCard)
        if (getShowPaypal()) payOptionsList.add(VerifonePaymentOptions.paymentOptionPayPal)
        if (isGooglePayPossible && getShowGooglePay()) payOptionsList.add(VerifonePaymentOptions.paymentOptionGooglePay)
        if (payOptionsList.isEmpty()) {
            Toast.makeText(this,getString(R.string.no_payment_options),Toast.LENGTH_LONG).show()
            return
        }
        //val paymentOptions = PaymentOptionsDialog(payOptionsList,::startPayPalFlow,::creditCardPay,this)
        //val paymentOptions = PaymentOptionsDialog(payOptionsList,::onPayMethodSelected,this)
        val paymentOptionsSheet = VerifonePaymentOptions(this,payOptionsList,::onPayMethodSelected)
        paymentOptionsSheet.showPaymentOptionList()
    }


    private fun loadFormPreferences():FormUICustomizationData {
        val sharedPref = getSharedPreferences("customization", Context.MODE_PRIVATE)
        val temp = FormUICustomizationData()
        temp.paymentFormBackground = sharedPref.getString(
            CustomizationSettings.keySaveBackgroundColor,
            ""
        )?:""
        temp.formTextFieldsBackground = sharedPref.getString(
            CustomizationSettings.keySaveTextFieldsColor,
            ""
        )?:""
        temp.formInputTextColor = sharedPref.getString(
            CustomizationSettings.keySaveInputTextColor,
            ""
        )?:""
        temp.hintTextColor =sharedPref.getString(CustomizationSettings.keySaveHintColor, "")?:""
        temp.payButtonColor = sharedPref.getString(CustomizationSettings.keySavePayBtnColor, "")?:""
        temp.formTitleTextColor = sharedPref.getString(CustomizationSettings.keySaveTitleColor, "")?:""
        showRecurrent = sharedPref.getBoolean(CustomizationSettings.keySaveShowRecurrent, false)
        return temp
    }

    private fun gotoCardFormPayment(encryptionPublicKey:String) {
        val publicKey = encryptionPublicKey
        val customizationParam = loadFormPreferences()
        customizationParam.userTextFont = parseFont()
        customizationParam.userTextFontRes = parseFontResource()
        val fullPrice = "10 $currencyTV"

        val formConfigData = PaymentConfigurationData(
            this,
            fullPrice,
            publicKey,
            showRecurrent,
            customizationParam
        )


        setLocation(false)
        mVerifonePaymentForm = VerifonePaymentForm(
            formConfigData,
            ::onCreditCardInput
        )
        mVerifonePaymentForm.displayPaymentForm()
    }

    private fun onCreditCardInput(
        cardInputResult:CreditCardInputResult
    ) {
        //this is a callback interface method that is used for getting the payer encrypted card data
        setLocation(true)
        //currencyTV = CustomizationSettings.getLookupCurrencyCode(this)
        customer = cardInputResult.payerName//customerName
        if (cardInputResult.encryptedCardData.isNotEmpty()) {
            encryptedPayerCard = cardInputResult.encryptedCardData
            cardBrand = cardInputResult.cardProperties.cardBrand
            if (cardInputResult.storeCard) {
                getCardReuseToken(cardInputResult.encryptedCardData)
            }

            if (threedsSelected) {
                startCardPaymentFlow()
            } else {
                cardPaymentNoThreeDS(cardBrand)
            }

        } else {
            progressDialog.dismiss()
            val errorFull = "Card encryption failed. Check the public key parameter in settings."
            ErrorDisplayDialog.newInstance("Transaction failed", errorFull).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun getReuseTokenDone(api_message:String) {
        if (api_message != "ACTIVE"){
            ErrorDisplayDialog.newInstance("Reuse token creation failed", api_message).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun getCardReuseToken(encryptedCard: String){
        val recurrentPaymentsCtrl = RecurrentPayController(this, cardBrand,::getReuseTokenDone)
        val tokenScope = CustomizationSettings.getPaymentsTokenScope(this)
        val publicKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        val tokenType = "REUSE"
        val tokenExpiryDate = ""
        val encryptedCardEX = encryptedCard
        recurrentPaymentsCtrl.getReuseTokenRequest(
            encryptedCardEX,
            tokenScope,
            publicKeyAlias,
            tokenType,
            tokenExpiryDate
        )
    }

    private fun showLoadingSpinner() {
        runOnUiThread {
            progressDialog.setTitle("Processing transaction...")
            progressDialog.setCancelable(true)
            progressDialog.show()
        }
    }

    private fun startJWTCreate() {
        createJWTControl = JwtCreateController(this,::getJWTCallDone)
        val threedContractID2 = CustomizationSettings.getPaymentsContractID(this)
        createJWTControl.startCreateJWTRequest(threedContractID2)
        showLoadingSpinner()
    }

    private fun getJWTCallDone(response: JWTResponse) {
        if (response.api_fail_message.isEmpty()){
            initThreeDSecure(response.jwt)
        } else {
            val errorFull = "JWT_Request_Failed_message:${response.api_fail_message}  \n\n cause:${response.api_fail_cause}"
            ErrorDisplayDialog.newInstance("Transaction failed", errorFull).show(
                supportFragmentManager,
                "error"
            )
            progressDialog.dismiss()
        }

    }

    private fun startLookupRequest(deviceID: String, encryptedCard: String, keyAlias: String) {
        val orderLookupRequestControl = LookupSimulateController(this)
        orderLookupRequestControl.startSimulateRequest(deviceID, encryptedCard, keyAlias, amount)
    }

    private fun fetchRandomGarment() : JSONObject {
        if (!::garmentList.isInitialized) {
            garmentList = Json.readFromResources(this, R.raw.tshirts)
        }

        val randomIndex:Int = (Math.random() * (garmentList.length() - 1)).roundToInt()
        return garmentList.getJSONObject(randomIndex)
    }

    private fun displayGarment(garment: JSONObject) {
        detailTitle.text = garment.getString("title")
        //detailPrice.text = "\$${garment.getString("price")}"
        
        val price = 10.0//detailPrice.text.subSequence(1,len)
        detailPrice.text = "$price $currencyTV"
        amount = price.toString().toDouble()

        val escapedHtmlText:String = Html.fromHtml(garment.getString("description")).toString()
        detailDescription.text = Html.fromHtml(escapedHtmlText)

        val imageUri = "@drawable/${garment.getString("image")}"
        val imageResource = resources.getIdentifier(imageUri, null, packageName)
        detailImage.setImageResource(imageResource)
    }

    private fun onThreedsSetupDone(deviceID: String) {
        startLookupRequest(deviceID, encryptedPayerCard, receivedKeyAlias)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {

    }

    private fun onThreeDValidationResult(authData: ThreedsValidationData) {
        if (authData.validationStatus == "fail") {
            runOnUiThread {
                progressDialog.dismiss()
                MessageDisplayDialog.newInstance(
                    "Threeds validation failed",
                    "Transaction Status: "
                ).show(supportFragmentManager, "payment_done")
            }
            return
        } else if (authData.validationStatus == "CANCEL")   {

            runOnUiThread {
                progressDialog.dismiss()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("3DS validation cancel")
                builder.setMessage("Transaction canceled")
                builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
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
        )
        val currencyParam = currencyTV
        val merchantReferenceParam = "test reference"
        if (cardToken.isNotEmpty()) {
            completeReuseTokenPaymentFlow(cardBrand, mCardAuth3D)
        } else {
            completeCardPaymentFlow(cardBrand,merchantReferenceParam,currencyParam, mCardAuth3D)
        }
    }

    private fun initThreeDSecure(jwt: String) {
        val configurationThreeds = ThreedsConfigurationData(this, jwt)
        mVerifone3DSecureManager = Verifone3DSecureManager.createInstance(
            configurationThreeds,
            ::onThreedsSetupDone,
            ::onThreeDValidationResult
        )
        if (mVerifone3DSecureManager!=null) {
            mVerifone3DSecureManager!!.validateTS()
        } else {
            Toast.makeText(this,"3DS not available",Toast.LENGTH_LONG).show()
            progressDialog.dismiss()
        }

    }

    override fun lookupRequestSuccess(response: TestLookupResponse) {
        if (mReportingDlg !=null && mReportingDlg!!.isVisible){
            mReportingDlg!!.dismiss()
        }
        mThreeDSVersion = response.threeds_version
        mVerifone3DSecureManager?.continueTSValidation(
            response.transaction_id,
            response.payload,
            this
        )
    }

    override fun lookupRequestFailed(t: Throwable) {
        progressDialog.dismiss()
        ErrorDisplayDialog.newInstance("Transaction failed, cause: Lookup request failed", t.localizedMessage).show(
            supportFragmentManager,
            "error"
        )
    }

    private fun gotoPaymentDoneScreen(reference: String, amount: String,customerParam:String,currencyParam:String,isPayPal:Boolean) {
        val paymentDone = Intent(this, PaymentFlowDone::class.java)
        paymentDone.putExtra(PaymentFlowDone.keyPayerName, customerParam)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionReference, reference)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionAmount, amount)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionCurrency,currencyParam)
        paymentDone.putExtra(PaymentFlowDone.keyIsPayPal,isPayPal)
        startActivity(paymentDone)
    }

    private fun onCardPayFlowDone(response: CardPaymentResponse) {
        if (response.api_fail_message.isNotEmpty()) {
            runOnUiThread {
                progressDialog.dismiss()
                //Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show()
                ErrorDisplayDialog.newInstance("Transaction failed, cause: Card payments request failed", response.api_fail_cause+"\n"
                        +response.api_fail_message).show(
                    supportFragmentManager,
                    "error"
                )
            }
        } else {
            runOnUiThread {
                progressDialog.dismiss()
                gotoPaymentDoneScreen(response.id, "" + response.amount,customer,currencyTV,false)
            }
        }
    }

    lateinit var firstPayPalRequestObject:PayPalRequestObject

    private fun startPayPalFlow() {
        showLoadingSpinner()
        val payPallCtrl = PayPalSimulateController(this,::payPalSuccessResponse,::paypalErrorResponse)
        val itemJson1 = garmentList.get(0) as JSONObject
        val itemPrice = UnitValueData("EUR",1000)
        val item1 = PurchasedItem(itemJson1.get("name").toString(),1.toString(),itemJson1.get("description").toString().substring(0,15),123.toString(),"PHYSICAL_GOODS",itemPrice)
        val itemsList = ArrayList<PurchasedItem>()
        itemsList.add(item1)
        payPallCtrl.startPayPalApi(itemsList)
        firstPayPalRequestObject = payPallCtrl.requestObject
    }

    private fun creditCardPay() {
        //show the verifone card form input screen
        cardToken = getStoredPaymentDetails()

        if (cardToken.isNotEmpty()) {
            tokenPaymentNoThreeDS(getStoredCardBrand())
            return
        }
        var publicKeyParam  = ""

        publicKeyParam = CustomizationSettings.getCardEncryptionKey(this)

        gotoCardFormPayment(publicKeyParam)
    }

    private fun payPalSuccessResponse(response: PayPalResponse) {
        val authUrl = response.approvalURL
        paypalTransactionID = response.id
        paypalScreen = PayPalConfirmationForm(this,authUrl,::paypalConfirmationDone)
        paypalScreen.displayPaypalConfirmation()
    }

    private fun paypalErrorResponse(error: String) {
        progressDialog.dismiss()
        ErrorDisplayDialog.newInstance("PayPal failed", error).show(
            supportFragmentManager,
            "error"
        )
    }


    private fun paypalConfirmationDone(queryParams:MutableMap<String, String>) {
        paypalScreen.dismissPaypalScreen()
        if (queryParams.isEmpty()){
            progressDialog.dismiss()
        }
        if (paypalTransactionID.isNotEmpty() && queryParams.isNotEmpty() && !queryParams.containsKey("fullCancelUrl")) {
            val confirmationCtrl = PayPalConfirmationController(this,::paypalTransactionDone)
            confirmationCtrl.startPayPalConfirmation(paypalTransactionID)
        } else {
            progressDialog.dismiss()
        }
    }

    private fun paypalTransactionDone(response:PayPalConfirmationResponse) {
        progressDialog.dismiss()
        if (response.isError) {
            ErrorDisplayDialog.newInstance("PayPal failed", response.errorMessage).show(
                supportFragmentManager,
                "error"
            )
            return
        }
        val payPalCustomer = response.payer.name.firstName+" "+response.payer.name.lastName+" (paypal user)"
        gotoPaymentDoneScreen(response.instoreReference, "" + firstPayPalRequestObject.amountData.itemValue,payPalCustomer,firstPayPalRequestObject.amountData.currencyCode,true)
    }

    private fun getStoredPaymentDetails():String {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getString(keyRecurrent, "")?:""
    }

    fun getStoredCardBrand():String {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getString(keyCardBrand, "")?:""
    }

    override fun onResume() {
        super.onResume()
        setLocation(true)
        setupRegion()
    }

    private fun parseFont():Typeface? {
        val fontName = CustomizationSettings.getStoredFont(this)
        when (fontName) {
            "Oswald"-> return ResourcesCompat.getFont(this, R.font.oswald_demibold)
            "Pacifico"-> return ResourcesCompat.getFont(this, R.font.pacifico)
            "OpenSans"-> return ResourcesCompat.getFont(this, R.font.open_sans_semibold_italic)
            "QuickSand"-> return ResourcesCompat.getFont(this, R.font.quicksand_italic)
            "Roboto"-> return ResourcesCompat.getFont(this, R.font.roboto_condensed_bold)
            else -> {
                return null
            }
        }
    }


    private fun parseFontResource():Int {
        val fontName = CustomizationSettings.getStoredFont(this)
        when (fontName) {
            "Oswald"-> return R.font.oswald_demibold
            "Pacifico"-> return R.font.pacifico
            "OpenSans"-> return R.font.open_sans_semibold_italic
            "QuickSand"-> return R.font.quicksand_italic
            "Roboto"-> return R.font.roboto_condensed_bold
            else -> {
                return 0
            }
        }
    }

    private fun getShowPaypal():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_paypal",true)
    }

    private fun getShowGooglePay():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_google_pay",true)
    }

    private fun getShowCard():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_card",true)
    }

    private fun startGooglePay() {

        val mGooglePayController = GooglePayController(this,::onGooglePaySuccess,::onGooglePayFailed)
        //mGooglePayController.startPaymentDataRequest(authorizationHeader,cookieHeader,payment_provider_contract,amount,auth_type,capture_now,customer_ip,merchant_reference,card_brand,shopper_interaction,currency_code,dynamic_descriptor,wallet_type,mGooglePayloadObject)

        mGooglePayController.startPaymentDataRequest(
            CustomizationSettings.getPaymentsProviderContract(this),
            1000,
            "FINAL_AUTH",
            true,
            "127.0.0.1",
            "TEST-ECOM123",
            "VISA",
            "ECOMMERCE",
            "EUR",
            "abc123",
            "GOOGLE_PAY",
            mGooglePayload
        )
    }

    private fun onGooglePaySuccess(googlePayResponse:GooglePayResponse) {
        progressDialog.dismiss()
        val status = googlePayResponse.status
        if (status.compareTo("DECLINED")==0) {
            Toast.makeText(this,R.string.google_pay_fail,Toast.LENGTH_LONG).show()
        } else {
            gotoPaymentDoneScreen(googlePayResponse.merchant_reference,""+googlePayResponse.amount,"GooglePay User","EUR",false)
        }
    }

    fun onGooglePayFailed(error:String) {
        progressDialog.dismiss()
        ErrorDisplayDialog.newInstance("Google Pay failed", error).show(
            supportFragmentManager,
            "error"
        )
    }

    private fun showGooglePayButton(googlePayPossible:Boolean){
        isGooglePayPossible = googlePayPossible
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            googlePayRequestCode -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            mGooglePayload = mGooglePay.parseGooglePayload(intent)
                            showLoadingSpinner()
                            startGooglePay()
                        }

                    RESULT_CANCELED -> {
                        // Nothing to do here normally - the user simply cancelled without selecting a
                        // payment method.
                    }

                    1 -> {
                        //AutoResolveHelper.getStatusFromIntent(data)?.let {
                        //handleError(1)
                        //it.statusCode)
                        //}
                    }
                }
                // Re-enables the Google Pay payment button.
                googlePayButton.isClickable = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    private fun setupRegion() {
        val region = CustomizationSettings.getStoredRegion(this)
        if (region.isEmpty()) return
        when (region) {
            "CST" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS = TestRetrofitClientInstance.CST_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 = TestRetrofitClientInstance.CST_REGION2
            }
            "US CST" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS = TestRetrofitClientInstance.US_CST_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 = TestRetrofitClientInstance.US_CST_REGION
            }
            "EMEA PROD" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS = TestRetrofitClientInstance.EMEA_PROD_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 = TestRetrofitClientInstance.EMEA_PROD_REGION
            }
            "US PROD" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS = TestRetrofitClientInstance.US_PROD_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 = TestRetrofitClientInstance.US_PROD_REGION
            }
            "NZ PROD" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS = TestRetrofitClientInstance.NZ_PROD_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 = TestRetrofitClientInstance.NZ_PROD_REGION
            }
        }
    }

}