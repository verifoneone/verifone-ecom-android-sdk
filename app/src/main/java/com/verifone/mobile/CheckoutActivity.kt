package com.verifone.mobile


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.verifone.connectors.datapack.configuration.FormUICustomizationData
import com.verifone.connectors.datapack.configuration.PaymentConfigurationData
import com.verifone.connectors.googlepay.GooglePayConfiguration
import com.verifone.connectors.googlepay.VerifoneGooglePay
import com.verifone.connectors.googlepay.WalletPayloadObject
import com.verifone.connectors.mobilepay.MobilepayManager
import com.verifone.connectors.klarna.KlarnaPaymentForm
import com.verifone.connectors.screens.PayPalConfirmationForm
import com.verifone.connectors.screens.VerifonePaymentForm
import com.verifone.connectors.screens.VerifonePaymentOptions
import com.verifone.connectors.swish.SwishPaymentsManager
import com.verifone.connectors.threeds.Verifone3DSecureManager
import com.verifone.connectors.threeds.dataobjects.EncodedThreedsData
import com.verifone.connectors.threeds.dataobjects.ThreedsConfigurationData
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData
import com.verifone.connectors.util.CreditCardInputResult
import com.verifone.connectors.vipps.VippsPaymentsManager
import com.verifone.mobile.controllers.*
import com.verifone.mobile.dataobjects.googlepayments.GooglePayResponse
import com.verifone.mobile.dataobjects.klarna.KlarnaFinalValidationResponse
import com.verifone.mobile.dataobjects.mobilepay.InitMobilePayResponse
import com.verifone.mobile.dataobjects.paypal.PayPalRequestObject
import com.verifone.mobile.dataobjects.paypal.PurchasedItem
import com.verifone.mobile.dataobjects.paypal.UnitValueData
import com.verifone.mobile.dataobjects.threeds.DecodedThreedsData
import com.verifone.mobile.dialogs.CurrencySelectorDialog
import com.verifone.mobile.dialogs.ErrorDisplayDialog
import com.verifone.mobile.dialogs.MessageDisplayDialog
import com.verifone.mobile.interfaces.LookupRequestDone
import com.verifone.mobile.responses.ECOMTransactionConfirmResponse
import com.verifone.mobile.responses.JWTResponse
import com.verifone.mobile.responses.TestLookupResponse
import com.verifone.mobile.responses.cardpayments.CardPaymentResponse
import com.verifone.mobile.responses.paypal.PayPalConfirmationResponse
import com.verifone.mobile.responses.paypal.PayPalResponse
import com.verifone.mobile.responses.vipps.VippsInitResponse
import com.verifone.mobile.screens.CustomizationSettings

import com.verifone.mobile.screens.PaymentFlowDone
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.card_payments_config_data.*
import kotlinx.android.synthetic.main.mobilepay_input_data.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt


/**
 * Checkout implementation for the app
 */
open class CheckoutActivity : AppCompatActivity(), LookupRequestDone {

    companion object {
        var currencyTV = "EUR"
    }


    private val swishResultReceiver = createSwishReceiver()
    private val googlePayReceiver = createGooglePayReceiver()
    private val mobilePayReceiver = createMobilePayReceiver()
    private val vippsPayReceiver = createVippsPayReceiver()

    private val klarnaPaymentReceiver =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { klarnaResult ->
            when (klarnaResult.resultCode) {
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(
                        CheckoutActivity@ this,
                        getString(R.string.klarna_cancel),
                        Toast.LENGTH_LONG
                    ).show()
                }

                KlarnaPaymentForm.klarnaAuthorizationSuccess -> {
                    val authToken =
                        klarnaResult.data?.getStringExtra(KlarnaPaymentForm.keyAuthToken) ?: ""
                    val validationCtrl = KlarnaValidationController(
                        this,
                        authToken,
                        klarnaCustomerID,
                        klarnaTransactionID,
                        ::onKlarnaTransactionSuccess,
                        ::onKlarnaTransactionFailed
                    )
                    showLoadingSpinner()
                    validationCtrl.startKlarnaTokenRequest()

                }

                KlarnaPaymentForm.klarnaAuthorizationCancel -> {
                    Toast.makeText(
                        CheckoutActivity@ this,
                        getString(R.string.klarna_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    private val swishReturnCode = 1009
    var mobilePayTransactionID = ""
    var vippsPayTransactionID = ""
    var swishTransactionID = ""
    private val googlePayRequestCode = 7419
    private var amount: Double = 0.0
    private var isGooglePayPossible = false
    private lateinit var mGooglePayload: WalletPayloadObject
    private lateinit var cardBrand: String
    private lateinit var currencyDialog: CurrencySelectorDialog
    var creditCardToken: String = ""
    var giftCardToken: String = ""
    var customer: String = ""
    var threedsAuthID = ""
    var dsTransactionID = ""
    var mReportingDlg: ErrorDisplayDialog? = null

    /**
     * A client for interacting with the Google Pay API.
     *
     * @see [PaymentsClient](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient)
     */

    private val shippingCost = (90 * 1000000).toLong()

    private lateinit var garmentList: JSONArray
    private lateinit var selectedGarment: JSONObject

    private lateinit var createJWTControl: JwtCreateController
    private lateinit var mExampleCardPay: ExampleCardPayment
    private lateinit var mThreeDSVersion: String
    private var startedGooglePay = true
    private var startedCardPay = true
    private lateinit var settingsButton: ImageView

    @Volatile
    private lateinit var progressDialog: ProgressDialog
    private var mVerifone3DSecureManager: Verifone3DSecureManager? = null
    private lateinit var mPaymentBtn: AppCompatButton
    private lateinit var mGooglePay: VerifoneGooglePay
    private lateinit var mCreditCardBtnNoThreeds: AppCompatButton
    private lateinit var mVerifonePaymentForm: VerifonePaymentForm
    private lateinit var paypalScreen: PayPalConfirmationForm
    private var paypalTransactionID: String = ""
    private val selectCurrencyButton by lazy { findViewById<AppCompatButton>(R.id.selectCurrencyBtn) }

    /**
     * Arbitrarily-picked constant integer you define to track a request for payment data activity.
     *
     * @value #LOAD_PAYMENT_DATA_REQUEST_CODE
     */
    private var encryptedPayerCard: String = ""
    private lateinit var receivedKeyAlias: String
    private var showRecurrent: Boolean = false

    private var threedsSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        progressDialog = ProgressDialog(this)
        mPaymentBtn = findViewById(R.id.payment_options_btn)
        currencyDialog = CurrencySelectorDialog(::onCurrencyInput)
        mCreditCardBtnNoThreeds = findViewById(R.id.card_payment_btn_no_threeds)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = View.inflate(this, R.layout.custom_action_bar, null)
        // Set up the mock information for our item in the UI.
        settingsButton = findViewById(R.id.settings_btn)
        setupCurrency()
        selectedGarment = fetchRandomGarment()
        displayGarment(selectedGarment)

        mGooglePay = VerifoneGooglePay(this, VerifoneGooglePay.testEnvironment)
        mGooglePay.showGooglePayIfPossible()

        mPaymentBtn.setOnClickListener {
            showPaymentOptions()
        }

        mCreditCardBtnNoThreeds.setOnClickListener {

        }

        selectCurrencyButton.setOnClickListener {

            currencyDialog.show(supportFragmentManager, "currency_select")
        }

        settingsButton.setOnClickListener {
            val settingsScreen = Intent(this, CustomizationSettings::class.java)
            startActivity(settingsScreen)
        }
        threedsSelected = CustomizationSettings.getThreedsUserOption(this)
    }

    private fun setLocation(setEnglish: Boolean) {
        var langSelected = "EN"
        if (!setEnglish) {
            try {
                langSelected = CustomizationSettings.getStoredLanguage(this).substring(0, 2)
            } catch (e: StringIndexOutOfBoundsException) {
                langSelected = "EN"
            }
        }

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
        startJWTCreate()
    }

    private fun cardPaymentNoThreeDS(cardBrand: String, ppc: String) {
        showLoadingSpinner()
        startedCardPay = true
        startedGooglePay = false
        receivedKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)

        val authType = "FINAL_AUTH"
        val captureNow = true
        val dynamicDescriptor = "M.reference"
        val merchantReference = "test reference"
        val currencyCode = currencyTV
        var cardBrandParam = ""
        val reuseTokenKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        cardBrandParam = if (cardBrand.isEmpty()) {
            "VISA"
        } else {
            cardBrand
        }

        mExampleCardPay = ExampleCardPayment(this, ::onCardPayFlowDone)
        mExampleCardPay.flowCardPaymentNoThreeds(
            encryptedPayerCard,
            ppc,
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

    private fun tokenPaymentNoThreeDS(cardBrand: String, cardToken: String) {
        showLoadingSpinner()
        startedCardPay = true
        startedGooglePay = false
        receivedKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)

        val paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(this)

        val authType = "FINAL_AUTH"
        val captureNow = true
        val dynamicDescriptor = "M.reference"
        val merchantReference = "test reference"
        val currencyCode = currencyTV
        var cardBrandParam = ""
        val reuseTokenKeyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        if (cardBrand.isEmpty()) {
            cardBrandParam = "VISA"
        } else {
            cardBrandParam = cardBrand
        }
        mExampleCardPay = ExampleCardPayment(this, ::onCardPayFlowDone)
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

    private fun completeReuseTokenPaymentFlow(
        cardBrand: String,
        threedParam: ThreedsValidationData,
        cardToken: String
    ) {

        val paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(this)
        val authType = "FINAL_AUTH"
        val captureNow = true
        val dynamicDescriptor = "M.reference"
        val merchantReference = "test reference"
        val currencyCode = currencyTV
        val keyAlias = CustomizationSettings.getPublicEncryptionKeyAlias(this)
        mExampleCardPay = ExampleCardPayment(this, ::onCardPayFlowDone)
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


    private fun completeCardPaymentFlow(
        cardBrand: String,
        merchantReference: String,
        currencyCode: String,
        threedParam: ThreedsValidationData
    ) {

        val paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(this)
        val authType = "FINAL_AUTH"
        val captureNow = true
        val dynamicDescriptor = "M.reference"
        mExampleCardPay = ExampleCardPayment(this, ::onCardPayFlowDone)
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

    private fun createGooglePayConfigObject(): GooglePayConfiguration {
        val temp = GooglePayConfiguration()
        temp.transactionAmount = 10.0
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

    private fun onPayMethodSelected(payMethod: String) {
        threedsSelected = CustomizationSettings.getThreedsUserOption(this)
        if (payMethod == VerifonePaymentOptions.paymentOptionPayPal) {
            startPayPalFlow()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionCard) {
            creditCardPay()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionGiftCard) {
            giftCardPay()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionGooglePay) {
            mGooglePay.requestGooglePayment(
                createGooglePayConfigObject(),
                googlePayRequestCode,
                googlePayReceiver
            )
        } else if (payMethod == VerifonePaymentOptions.paymentOptionKlarna) {
            val uuid = UUID.randomUUID()
            val klarnaPayCtrl =
                KlarnaPaymentsController(this, uuid.toString(), ::onClientTokenKlarna)
            klarnaPayCtrl.startKlarnaTokenRequest(currencyTV)
            showLoadingSpinner()
        } else if (payMethod == VerifonePaymentOptions.paymentOptionSwish) {
            if (currencyTV == "SEK") {
                showLoadingSpinner()
                val swishTokenController = GetSwishTokenController(this, ::onSwishTokenResponse)
                swishTokenController.startGetSwishTokenRequest(currencyTV)
            } else {
                Toast.makeText(this, R.string.invalid_currency_str, Toast.LENGTH_SHORT).show()
            }
        } else if (payMethod == VerifonePaymentOptions.paymentOptionMobilePay) {
            showLoadingSpinner()
            val initMobilePayController = MobilePayInitController(this, ::onMobilePayStarted)
            initMobilePayController.startMobilePayRequest(currencyTV)
        } else if (payMethod == VerifonePaymentOptions.paymentOptionVipps) {
            val initVippsController = VippsInitController(this, ::onVippsInit)
            showLoadingSpinner()
            initVippsController.startVippsInitRequest(currencyTV)
        }
    }

    lateinit var klarnaCustomerID: String
    lateinit var klarnaTransactionID: String
    private fun onClientTokenKlarna(
        token: String,
        paramCustomerID: String,
        paramTransactionID: String
    ) {
        progressDialog.dismiss()
        if (token.isEmpty()) {
            ErrorDisplayDialog.newInstance("Klarna transaction failed", "Get token failed").show(
                supportFragmentManager,
                "error"
            )
            return
        }
        klarnaCustomerID = paramCustomerID
        klarnaTransactionID = paramTransactionID

        val returnURL = "http://connectors.dos.net/"
        val klarnaPaymentForm = KlarnaPaymentForm(
            this,
            token,
            klarnaCustomerID,
            klarnaTransactionID,
            returnURL,
            klarnaPaymentReceiver
        )
        klarnaPaymentForm.displayPaymentForm()
    }

    private fun onKlarnaTransactionSuccess(response: KlarnaFinalValidationResponse) {
        progressDialog.dismiss()
        if (response.status == "AUTHORIZED") {
            gotoPaymentDoneScreen(
                response.inStoreReference,
                PaymentFlowDone.TransactionType.typeKlarna.name,
                "" + response.amount,
                CustomizationSettings.getPaymentCustomerID(this),
                "SEK"
            )
        } else {
            Toast.makeText(
                CheckoutActivity@ this,
                getString(R.string.klarna_failed),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onKlarnaTransactionFailed(e: Throwable) {
        progressDialog.dismiss()
        Toast.makeText(CheckoutActivity@ this, getString(R.string.klarna_failed), Toast.LENGTH_LONG)
            .show()
    }

    private fun showPaymentOptions() {
        val payOptionsList = ArrayList<String>(2)
        if (getShowCard()) payOptionsList.add(VerifonePaymentOptions.paymentOptionCard)
        if (getShowGiftCard()) payOptionsList.add(VerifonePaymentOptions.paymentOptionGiftCard)
        if (getShowPaypal()) payOptionsList.add(VerifonePaymentOptions.paymentOptionPayPal)
        if (getShowGooglePay()) payOptionsList.add(VerifonePaymentOptions.paymentOptionGooglePay)
        if (getShowKlarna()) payOptionsList.add(VerifonePaymentOptions.paymentOptionKlarna)
        if (getShowSwish()) payOptionsList.add(VerifonePaymentOptions.paymentOptionSwish)
        if (getShowMobilePay()) payOptionsList.add(VerifonePaymentOptions.paymentOptionMobilePay)
        if (getShowVipps()) payOptionsList.add(VerifonePaymentOptions.paymentOptionVipps)
        if (payOptionsList.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_payment_options), Toast.LENGTH_LONG).show()
            return
        }

        val paymentOptionsSheet =
            VerifonePaymentOptions(this, payOptionsList, ::onPayMethodSelected)
        paymentOptionsSheet.showPaymentOptionList()
    }


    private fun loadFormPreferences(): FormUICustomizationData {
        val sharedPref = getSharedPreferences("customization", Context.MODE_PRIVATE)
        val temp = FormUICustomizationData()
        temp.paymentFormBackground = sharedPref.getString(
            CustomizationSettings.keySaveBackgroundColor,
            ""
        ) ?: ""
        temp.formTextFieldsBackground = sharedPref.getString(
            CustomizationSettings.keySaveTextFieldsColor,
            ""
        ) ?: ""
        temp.formInputTextColor = sharedPref.getString(
            CustomizationSettings.keySaveInputTextColor,
            ""
        ) ?: ""
        temp.hintTextColor = sharedPref.getString(CustomizationSettings.keySaveHintColor, "") ?: ""
        temp.payButtonColor =
            sharedPref.getString(CustomizationSettings.keySavePayBtnColor, "") ?: ""
        temp.formTitleTextColor =
            sharedPref.getString(CustomizationSettings.keySaveTitleColor, "") ?: ""
        showRecurrent = sharedPref.getBoolean(CustomizationSettings.keySaveShowRecurrent, false)
        return temp
    }

    private fun gotoCardFormPayment(encryptionPublicKey: String) {
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

    private fun gotoGiftCardFormPayment(encryptionPublicKey: String) { // TODO!!!!
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
            ::onGiftCardInput
        )
        mVerifonePaymentForm.displayGiftCardPaymentForm()
    }

    private fun onCreditCardInput(
        cardInputResult: CreditCardInputResult
    ) {
        //this is a callback interface method that is used for getting the payer encrypted card data
        setLocation(true)
        //currencyTV = CustomizationSettings.getLookupCurrencyCode(this)
        customer = cardInputResult.payerName
        if (cardInputResult.encryptedCardData.isNotEmpty()) {
            encryptedPayerCard = cardInputResult.encryptedCardData
            cardBrand = cardInputResult.cardProperties.cardBrand
            if (cardInputResult.storeCard) {
                getCardReuseToken(cardInputResult.encryptedCardData, Keys.keyRecurrent)
            }

            if (threedsSelected) {
                startCardPaymentFlow()
            } else {
                cardPaymentNoThreeDS(
                    cardBrand,
                    CustomizationSettings.getPaymentsProviderContract(this)
                )
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

    private fun onGiftCardInput(
        cardInputResult: CreditCardInputResult
    ) {
        setLocation(true)
        customer = cardInputResult.payerName
        cardBrand = "GIFT_CARD"
        if (cardInputResult.encryptedCardData.isNotEmpty()) {
            encryptedPayerCard = cardInputResult.encryptedCardData
            if (cardInputResult.storeCard) {
                getCardReuseToken(cardInputResult.encryptedCardData, Keys.giftCardRecurrentKey)
            }
            cardPaymentNoThreeDS(
                cardBrand,
                CustomizationSettings.getGiftCardPaymentsProviderContract(this)
            )
        } else {
            progressDialog.dismiss()
            val errorFull = "Card encryption failed. Check the public key parameter in settings."
            ErrorDisplayDialog.newInstance("Transaction failed", errorFull).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun getReuseTokenDone(api_message: String) {
        if (api_message != "ACTIVE") {
            ErrorDisplayDialog.newInstance("Reuse token creation failed", api_message).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun getCardReuseToken(encryptedCard: String, reuseTokenKey: String) {
        val recurrentPaymentsCtrl =
            RecurrentPayController(this, cardBrand, reuseTokenKey, ::getReuseTokenDone)
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
        createJWTControl = JwtCreateController(this, ::getJWTCallDone)
        val threedContractID2 = CustomizationSettings.getPaymentsContractID(this)
        createJWTControl.startCreateJWTRequest(threedContractID2)
        showLoadingSpinner()
    }

    private fun getJWTCallDone(response: JWTResponse) {
        if (response.api_fail_message.isEmpty()) {
            initThreeDSecure(response.jwt)
        } else {
            val errorFull =
                "JWT_Request_Failed_message:${response.api_fail_message}  \n\n cause:${response.api_fail_cause}"
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

    private fun fetchRandomGarment(): JSONObject {
        if (!::garmentList.isInitialized) {
            garmentList = Json.readFromResources(this, R.raw.tshirts)
        }

        val randomIndex: Int = (Math.random() * (garmentList.length() - 1)).roundToInt()
        return garmentList.getJSONObject(randomIndex)
    }

    private fun displayGarment(garment: JSONObject) {
        detailTitle.text = garment.getString("title")

        val price = 10.0
        detailPrice.text = "$price $currencyTV"
        amount = price.toString().toDouble()

        val escapedHtmlText: String = Html.fromHtml(garment.getString("description")).toString()
        detailDescription.text = Html.fromHtml(escapedHtmlText)

        val imageUri = "@drawable/${garment.getString("image")}"
        val imageResource = resources.getIdentifier(imageUri, null, packageName)
        detailImage.setImageResource(imageResource)
    }

    private fun onThreedsSetupDone(deviceID: String) {
        startLookupRequest(deviceID, encryptedPayerCard, receivedKeyAlias)
    }

    private fun onThreedsDecodeResult(decodedThreedsData: DecodedThreedsData) {
        if (decodedThreedsData.validationResult.cavv.isEmpty()) {
            runOnUiThread {
                progressDialog.dismiss()
                MessageDisplayDialog.newInstance(
                    "Threeds validation failed",
                    "Transaction Status: "
                ).show(supportFragmentManager, "payment_done")
            }
            return
        }
        val cavv = decodedThreedsData.validationResult.cavv
        val enrolled = decodedThreedsData.validationResult.enrolled
        val eciFlag = decodedThreedsData.validationResult.eciFlag
        val paresStatus = decodedThreedsData.validationResult.paresStatus
        val sigVerification = decodedThreedsData.validationResult.signatureVerification
        val dsTransactionID = dsTransactionID
        val xid = decodedThreedsData.validationResult.xid

        val threedAuth = ThreedsValidationData(
            eciFlag,
            enrolled,
            cavv,
            paresStatus,
            sigVerification,
            dsTransactionID,
            xid
        )

        val currencyParam = currencyTV
        val merchantReferenceParam = "test reference"
        if (creditCardToken.isNotEmpty()) {
            completeReuseTokenPaymentFlow(cardBrand, threedAuth, creditCardToken)
        } else {
            completeCardPaymentFlow(cardBrand, merchantReferenceParam, currencyParam, threedAuth)
        }
    }

    private fun onThreeDValidationResult(authData: EncodedThreedsData) {
        if (authData.validationStatus == "fail") {
            runOnUiThread {
                progressDialog.dismiss()
                MessageDisplayDialog.newInstance(
                    "Threeds validation failed",
                    "Transaction Status: "
                ).show(supportFragmentManager, "payment_done")
            }
            return
        } else if (authData.validationStatus == "CANCEL") {
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
        val mThreedsDecode = ThreedsDecodeController(this, ::onThreedsDecodeResult)
        val tempThreedsID = CustomizationSettings.getPaymentsContractID(this)
        mThreedsDecode.startDecodeThreedsRequest(
            tempThreedsID,
            authData.encodedThreedsString,
            threedsAuthID
        )
    }

    private fun initThreeDSecure(jwt: String) {
        val configurationThreeds = ThreedsConfigurationData(this, jwt)
        mVerifone3DSecureManager = Verifone3DSecureManager.createInstance(
            configurationThreeds,
            ::onThreedsSetupDone,
            ::onThreeDValidationResult,
            Verifone3DSecureManager.environmentStaging
        )
        if (mVerifone3DSecureManager != null) {
            mVerifone3DSecureManager!!.validateTS()
        } else {
            Toast.makeText(this, "3DS not available", Toast.LENGTH_LONG).show()
            progressDialog.dismiss()
        }
    }

    override fun lookupRequestSuccess(response: TestLookupResponse) {
        if (mReportingDlg != null && mReportingDlg!!.isVisible) {
            mReportingDlg!!.dismiss()
        }
        threedsAuthID = response.authentication_id
        mThreeDSVersion = response.threeds_version
        dsTransactionID = response.ds_transaction_id
        mVerifone3DSecureManager?.continueTSValidation(
            response.transaction_id,
            response.payload,
            this
        )
    }

    override fun lookupRequestFailed(t: Throwable) {
        progressDialog.dismiss()
        ErrorDisplayDialog.newInstance(
            "Transaction failed, cause: Lookup request failed",
            t.localizedMessage
        ).show(
            supportFragmentManager,
            "error"
        )
    }

    private fun gotoPaymentDoneScreen(
        reference: String,
        transactionType: String,
        amount: String,
        customerParam: String,
        currencyParam: String
    ) {
        val paymentDone = Intent(this, PaymentFlowDone::class.java)
        paymentDone.putExtra(PaymentFlowDone.keyPayerName, customerParam)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionReference, reference)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionAmount, amount)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionCurrency, currencyParam)
        paymentDone.putExtra(PaymentFlowDone.keyTransactionType, transactionType)
        startActivity(paymentDone)
    }

    private fun onCardPayFlowDone(response: CardPaymentResponse) {
        if (response.api_fail_message.isNotEmpty()) {
            runOnUiThread {
                progressDialog.dismiss()
                ErrorDisplayDialog.newInstance(
                    "Transaction failed, cause: Card payments request failed",
                    response.api_fail_cause + "\n"
                            + response.api_fail_message
                ).show(
                    supportFragmentManager,
                    "error"
                )
            }
        } else {
            runOnUiThread {
                progressDialog.dismiss()
                gotoPaymentDoneScreen(
                    response.id,
                    PaymentFlowDone.TransactionType.typeCreditCard.name,
                    "" + response.amount,
                    customer,
                    currencyTV
                )
            }
        }
    }

    lateinit var firstPayPalRequestObject: PayPalRequestObject

    private fun startPayPalFlow() {
        showLoadingSpinner()
        val payPallCtrl =
            PayPalSimulateController(this, ::payPalSuccessResponse, ::paypalErrorResponse)
        val itemJson1 = garmentList.get(0) as JSONObject
        val itemPrice = UnitValueData(currencyTV, 1000)
        val item1 = PurchasedItem(
            itemJson1.get("name").toString(),
            1.toString(),
            itemJson1.get("description").toString().substring(0, 15),
            123.toString(),
            "PHYSICAL_GOODS",
            itemPrice
        )
        val itemsList = ArrayList<PurchasedItem>()
        itemsList.add(item1)
        payPallCtrl.startPayPalApi(itemsList)
        firstPayPalRequestObject = payPallCtrl.requestObject
    }

    private fun creditCardPay() {
        //show the verifone card form input screen
        creditCardToken = getStoredPaymentDetails(Keys.keyRecurrent)

        if (creditCardToken.isNotEmpty()) {
            tokenPaymentNoThreeDS(getStoredCardBrand(), creditCardToken)
            return
        }
        var publicKeyParam = ""

        publicKeyParam = CustomizationSettings.getCardEncryptionKey(this)

        gotoCardFormPayment(publicKeyParam)
    }

    private fun giftCardPay() {
//        show the verifone gift card form input screen
        giftCardToken = getStoredPaymentDetails(Keys.giftCardRecurrentKey)

        if (giftCardToken.isNotEmpty()) {
            tokenPaymentNoThreeDS(getStoredCardBrand(), giftCardToken)
            return
        }
        var publicKeyParam = ""

        publicKeyParam = CustomizationSettings.getCardEncryptionKey(this)

        gotoGiftCardFormPayment(publicKeyParam)
    }

    private fun payPalSuccessResponse(response: PayPalResponse) {
        val authUrl = response.approvalURL
        paypalTransactionID = response.id
        paypalScreen = PayPalConfirmationForm(this, authUrl, ::paypalConfirmationDone)
        paypalScreen.displayPaypalConfirmation()
    }

    private fun paypalErrorResponse(error: String) {
        progressDialog.dismiss()
        ErrorDisplayDialog.newInstance("PayPal failed", error).show(
            supportFragmentManager,
            "error"
        )
    }


    private fun paypalConfirmationDone(queryParams: MutableMap<String, String>) {

        paypalScreen.dismissPaypalScreen()
        if (queryParams.isEmpty()) {
            progressDialog.dismiss()
        }
        if (paypalTransactionID.isNotEmpty() && queryParams.isNotEmpty() && queryParams.containsKey(
                "PayerID"
            )
        ) {
            val confirmationCtrl = PayPalConfirmationController(this, ::paypalTransactionDone)
            confirmationCtrl.startPayPalConfirmation(paypalTransactionID)
        } else {
            progressDialog.dismiss()
        }
    }

    private fun paypalTransactionDone(response: PayPalConfirmationResponse) {
        progressDialog.dismiss()
        if (response.isError) {
            ErrorDisplayDialog.newInstance("PayPal failed", response.errorMessage).show(
                supportFragmentManager,
                "error"
            )
            return
        }
        val payPalCustomer =
            response.payer.name.firstName + " " + response.payer.name.lastName + " (paypal user)"
        gotoPaymentDoneScreen(
            response.instoreReference,
            PaymentFlowDone.TransactionType.typePayPal.name,
            "" + firstPayPalRequestObject.amountData.itemValue,
            payPalCustomer,
            firstPayPalRequestObject.amountData.currencyCode
        )
    }

    private fun getStoredPaymentDetails(key: String): String {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getString(key, "") ?: ""
    }

    fun getStoredCardBrand(): String {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getString(Keys.keyCardBrand, "") ?: ""
    }

    override fun onResume() {
        super.onResume()
        setLocation(true)
        setupRegion()
    }

    private fun parseFont(): Typeface? {
        val fontName = CustomizationSettings.getStoredFont(this)
        when (fontName) {
            "Oswald" -> return ResourcesCompat.getFont(this, R.font.oswald_demibold)
            "Pacifico" -> return ResourcesCompat.getFont(this, R.font.pacifico)
            "OpenSans" -> return ResourcesCompat.getFont(this, R.font.open_sans_semibold_italic)
            "QuickSand" -> return ResourcesCompat.getFont(this, R.font.quicksand_italic)
            "Roboto" -> return ResourcesCompat.getFont(this, R.font.roboto_condensed_bold)
            else -> {
                return null
            }
        }
    }

    private fun parseFontResource(): Int {
        val fontName = CustomizationSettings.getStoredFont(this)
        when (fontName) {
            "Oswald" -> return R.font.oswald_demibold
            "Pacifico" -> return R.font.pacifico
            "OpenSans" -> return R.font.open_sans_semibold_italic
            "QuickSand" -> return R.font.quicksand_italic
            "Roboto" -> return R.font.roboto_condensed_bold
            else -> {
                return 0
            }
        }
    }

    private fun getShowPaypal(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_paypal", true)
    }

    private fun getShowGooglePay(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_google_pay", true)
    }

    private fun getShowKlarna(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_klarna", true)
    }

    private fun getShowSwish(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_swish", true)
    }

    private fun getShowMobilePay(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_mobile_pay", true)
    }

    private fun getShowVipps(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_vipps", true)
    }

    private fun getShowCard(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_card", true)
    }

    private fun getShowGiftCard(): Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_gift_card", true)
    }

    private fun startGooglePay() {

        val mGooglePayController =
            GooglePayController(this, ::onGooglePaySuccess, ::onGooglePayFailed)

        mGooglePayController.startPaymentDataRequest(
            CustomizationSettings.getPaymentsProviderContract(this),
            1000,
            "FINAL_AUTH",
            true,
            "127.0.0.1",
            "TEST-ECOM123",
            "VISA",
            "ECOMMERCE",
            currencyTV,
            "abc123",
            "GOOGLE_PAY",
            mGooglePayload
        )
    }

    private fun onGooglePaySuccess(googlePayResponse: GooglePayResponse) {
        progressDialog.dismiss()
        val status = googlePayResponse.status
        if (status.compareTo("DECLINED") == 0) {
            Toast.makeText(this, R.string.google_pay_fail, Toast.LENGTH_LONG).show()
        } else {
            gotoPaymentDoneScreen(
                googlePayResponse.merchant_reference,
                PaymentFlowDone.TransactionType.typeGooglePay.name,
                "" + googlePayResponse.amount,
                "GooglePay User",
                currencyTV
            )
        }
    }

    private fun onGooglePayFailed(error: String) {
        progressDialog.dismiss()
        runOnUiThread {
            MessageDisplayDialog.newInstance("Google Pay failed", error).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun showGooglePayButton(googlePayPossible: Boolean) {
        isGooglePayPossible = googlePayPossible
    }

    private fun onGooglePayloadReceived(
        googlePayWalletPayloadObject: WalletPayloadObject,
        googlePaySessionID: Int
    ) {
        showLoadingSpinner()
        mGooglePayload = googlePayWalletPayloadObject
        startGooglePay()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {

        super.onConfigurationChanged(newConfig)
        //recreate()
    }

    private fun setupRegion() {
        val region = CustomizationSettings.getStoredRegion(this)
        if (region.isEmpty()) return
        when (region) {
            "CST" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS =
                    TestRetrofitClientInstance.CST_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 =
                    TestRetrofitClientInstance.CST_REGION2
            }

            "US CST" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS =
                    TestRetrofitClientInstance.US_CST_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 =
                    TestRetrofitClientInstance.US_CST_REGION
            }

            "EMEA PROD" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS =
                    TestRetrofitClientInstance.EMEA_PROD_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 =
                    TestRetrofitClientInstance.EMEA_PROD_REGION
            }

            "US PROD" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS =
                    TestRetrofitClientInstance.US_PROD_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 =
                    TestRetrofitClientInstance.US_PROD_REGION
            }

            "NZ PROD" -> {
                TestRetrofitClientInstance.BASE_URL_CONNECTORS =
                    TestRetrofitClientInstance.NZ_PROD_REGION
                TestRetrofitClientInstance.BASE_URL_CONNECTORS2 =
                    TestRetrofitClientInstance.NZ_PROD_REGION
            }
        }
    }

    private fun onSwishTokenResponse(transactionID: String, swishToken: String) {
        progressDialog.dismiss()
        swishTransactionID = transactionID
        val swishManager = SwishPaymentsManager(
            this,
            swishResultReceiver,
            SwishPaymentsManager.swishEnvironmentSandbox
        )
        val startedSwish = swishManager.startSwishPaymentFlow(swishToken, swishReturnCode)
        if (!startedSwish) {
            ErrorDisplayDialog.newInstance("Swish transaction failed", "Swish token empty").show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun onMobilePayStarted(responseObject: InitMobilePayResponse) {
        mobilePayTransactionID = responseObject.id
        progressDialog.dismiss()
        if (responseObject.redirectUrl.isNotEmpty()) MobilepayManager.openMobilePay(
            this,
            mobilePayReceiver,
            responseObject.redirectUrl
        )
        else {
            ErrorDisplayDialog.newInstance("Mobile pay transaction failed", "Get deeplink failed")
                .show(
                    supportFragmentManager,
                    "error"
                )
        }
    }

    private fun onVippsInit(responseVipps: VippsInitResponse) {
        vippsPayTransactionID = responseVipps.id
        if (vippsPayTransactionID.isEmpty()) {
            ErrorDisplayDialog.newInstance("Vipps transaction failed", "Init Request Failed").show(
                supportFragmentManager,
                "error"
            )
        }
        VippsPaymentsManager.showVippsAuthorizeScreen(
            this,
            vippsPayReceiver,
            responseVipps.redirectURL
        )
    }

    private fun createSwishReceiver(): ActivityResultLauncher<Intent> {
        val actResLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == swishReturnCode) {
                    val mpVerifyTransaction =
                        VerifyECOMTransactionController(this, ::onSwishTransactionConfirm)
                    val checkStatus: Runnable = object : Runnable {
                        override fun run() {
                            mpVerifyTransaction.checkTransactionStatus(swishTransactionID)
                        }
                    }
                    val handler1 = Handler(Looper.myLooper()!!)
                    showLoadingSpinner()
                    handler1.postDelayed(checkStatus, 5000)
                } else {
                    Toast.makeText(this, "Swish transaction failed", Toast.LENGTH_LONG).show()
                }
            }
        return actResLauncher
    }

    private fun createGooglePayReceiver(): ActivityResultLauncher<Intent> {
        val actResLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == googlePayRequestCode) {
                    if (result.data != null) {
                        result.data?.let {
                            mGooglePayload = VerifoneGooglePay.parseGooglePayload(it)
                            showLoadingSpinner()
                            startGooglePay()
                        }
                    } else {
                        Toast.makeText(this, "Google pay transaction failed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        return actResLauncher
    }

    private fun onMobilePayConfirm(responseObject: ECOMTransactionConfirmResponse) {
        if (responseObject.transactionStatus.contains("AUTHORISED")) {
            gotoPaymentDoneScreen(
                responseObject.id,
                PaymentFlowDone.TransactionType.typeMobilePay.name,
                responseObject.amount,
                responseObject.customer,
                responseObject.currencyCode
            )
        } else {
            MessageDisplayDialog.newInstance(
                "MobilePay transaction status",
                responseObject.transactionStatus
            ).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun onVippsPayConfirm(responseObject: ECOMTransactionConfirmResponse) {
        progressDialog.dismiss()
        if (responseObject.transactionStatus.contains("AUTHORISED")) {
            gotoPaymentDoneScreen(
                responseObject.id,
                PaymentFlowDone.TransactionType.typeVipps.name,
                responseObject.amount,
                responseObject.customer,
                responseObject.currencyCode
            )
        } else {
            MessageDisplayDialog.newInstance(
                responseObject.transactionStatus,
                "VippsPay transaction status"
            ).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun onSwishTransactionConfirm(responseObject: ECOMTransactionConfirmResponse) {
        progressDialog.dismiss()
        if (responseObject.transactionStatus.contains("SETTLED")) {
            gotoPaymentDoneScreen(
                responseObject.id,
                PaymentFlowDone.TransactionType.typeSwish.name,
                responseObject.amount,
                responseObject.customer,
                responseObject.currencyCode
            )
        } else {
            MessageDisplayDialog.newInstance(
                responseObject.transactionStatus,
                "MobilePay transaction status"
            ).show(
                supportFragmentManager,
                "error"
            )
        }
    }

    private fun createMobilePayReceiver(): ActivityResultLauncher<Intent> {
        val mobilePayContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                progressDialog.dismiss()

                when (it.resultCode) {
                    MobilepayManager.transactionSuccess -> {
                        MessageDisplayDialog.newInstance(
                            "Status: Success",
                            "Mobile pay transaction"
                        ).show(supportFragmentManager, "mb payment_done")
                    }

                    MobilepayManager.transactionFlowDone -> {
                        val mpVerifyTransaction =
                            VerifyECOMTransactionController(this, ::onMobilePayConfirm)
                        mpVerifyTransaction.checkTransactionStatus(mobilePayTransactionID)
                    }

                    MobilepayManager.transactionFailed -> {
                        MessageDisplayDialog.newInstance(
                            "Mobile pay transaction",
                            "Status: Failed"
                        ).show(supportFragmentManager, "mp payment_done")
                    }

                    MobilepayManager.resultUnknown -> {
                        MessageDisplayDialog.newInstance(
                            "Mobile pay transaction",
                            "Status: Unknown error"
                        ).show(supportFragmentManager, "mb payment_done")
                    }
                }
            }
        return mobilePayContract
    }

    private fun createVippsPayReceiver(): ActivityResultLauncher<Intent> {
        val mobilePayContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                progressDialog.dismiss()
                when (it.resultCode) {
                    VippsPaymentsManager.vippsTransactionFlowDone -> {
                        val mpVerifyTransaction =
                            VerifyECOMTransactionController(this, ::onVippsPayConfirm)
                        mpVerifyTransaction.checkTransactionStatus(vippsPayTransactionID)
                    }

                    VippsPaymentsManager.vippsTransactionNotStarted -> {
                        Toast.makeText(this, R.string.vipps_message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        return mobilePayContract
    }

    private fun onCurrencyInput(newCurrency: String) {
        if (newCurrency.isNotEmpty() && newCurrency.length == 3) {
            currencyTV = newCurrency
            displayGarment(selectedGarment)
            saveCurrencyPrefs(currencyTV)
        }
    }

    private fun saveCurrencyPrefs(currencyStr: String) {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        sharedPref.edit().putString("selected_currency", currencyStr).apply()
    }

    private fun getCurrencyPrefs(): String {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getString("selected_currency", "") ?: ""
    }

    private fun setupCurrency() {
        val temp = getCurrencyPrefs()
        currencyTV = "EUR"
        if (temp.isNotEmpty()) {
            currencyTV = temp
        }
    }
}