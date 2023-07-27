package com.verifone.mobile.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputEditText
import com.verifone.mobile.R
import android.net.Uri
import android.os.Build
import com.verifone.mobile.Keys
import com.verifone.mobile.dialogs.ErrorDisplayDialog
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*


class CustomizationSettings:AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        const val keySaveBackgroundColor="pref_background_color"
        const val keySaveInputTextColor="pref_input_text_color"
        const val keySaveTextFieldsColor="pref_text_fields_color"
        const val keySaveHintColor="pref_hint_color"
        const val keySavePayBtnColor="pref_pay_btn_color"
        const val keySaveTitleColor="pref_title_color"
        const val keySaveShowRecurrent="pref_show_recurrent"

        const val keyThreedsOption = "pref_threeds_check"
        const val keyPublicEncryptionKeyAlias = "public_key_alias"
        const val keyContractID = "contract_id"

        const val keyPaymentsContractProviderParameter = "payments_provider_contract"
        const val keyPaymentsContractProviderPaypalParameter = "payments_provider_contract_paypal"
        const val keyGiftCardPaymentsContractProviderParameter = "gift_card_payment_provider_contract"

        const val keyPaymentsApiUserName = "api_payments_api_user_name"
        const val keyPaymentsApiKey = "api_payments_api_key"
        const val keyPaymentsTokenScope = "payments_token_scope"
        const val keyCardEncryptionKey = "card_encryption_key"

        const val keyGooglePayInputGatewayMerchantID = "google_pay_input_gateway_merchant_id"
        const val keyGooglePayInputMerchantName = "google_pay_input_merchant_name"
        const val keyGooglePayInputMerchantID = "google_pay_input_merchant_id"

        const val keyPaymentCustomerID = "klarnaCustomerID"
        const val keyPaymentOrganizationID = "klarnaOrgID"



        fun getStoredLanguage(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
            return sharedPref.getString("key_store_lang","")?:""
        }

        fun getStoredFont(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
            return sharedPref.getString("key_store_font","")?:""
        }

        fun getStoredRegion(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
            return sharedPref.getString("key_store_region","")?:""
        }

        fun getPublicEncryptionKeyAlias(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPublicEncryptionKeyAlias,"")?:""
        }

        fun getThreedsUserOption(ctx:Context):Boolean{
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getBoolean(keyThreedsOption,true)
        }

        fun getPaymentsContractID(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyContractID,"")?:""
        }

        fun getPaymentsProviderContract(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentsContractProviderParameter,"")?:""
        }

        fun getPaymentsProviderContractPaypal(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentsContractProviderPaypalParameter,"")?:""
        }

        fun getPaymentsApiUserID(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentsApiUserName,"")?:""
        }

        fun getPaymentsApiKey(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentsApiKey,"")?:""
        }

        fun getPaymentsTokenScope(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentsTokenScope,"")?:""
        }

        fun getGiftCardPaymentsProviderContract(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyGiftCardPaymentsContractProviderParameter,"")?:""
        }

        fun getGooglePayInputGatewayMerchantID(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyGooglePayInputGatewayMerchantID,"")?:""
        }

        fun getGooglePayInputMerchantName(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyGooglePayInputMerchantName,"")?:""
        }

        fun getGooglePayInputMerchantID(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyGooglePayInputMerchantID,"")?:""
        }

        fun getCardEncryptionKey(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyCardEncryptionKey,"")?:""
        }

        fun getPaymentOrgID(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentOrganizationID,"")?:""
        }

        fun getPaymentCustomerID(ctx:Context):String {
            val sharedPref = ctx.getSharedPreferences("payment_settings_data", Context.MODE_PRIVATE)
            return sharedPref.getString(keyPaymentCustomerID,"")?:""
        }

    }

    var languageSelection:String=""
    lateinit var backgroundColorInput: TextInputEditText
    lateinit var backgroundColorSample: AppCompatTextView

    lateinit var textFieldBackgroundInput:TextInputEditText
    lateinit var textFieldBackgroundSample:AppCompatTextView

    lateinit var textInputColor:TextInputEditText
    lateinit var textInputColorSample:AppCompatTextView

    lateinit var textHintColor:TextInputEditText
    lateinit var textHintColorSample:AppCompatTextView

    lateinit var payButtonColorCode:TextInputEditText
    lateinit var payButtonColorSample:AppCompatTextView

    lateinit var cardTitleColorCode:TextInputEditText
    lateinit var cardTitleColorSample:AppCompatTextView

    lateinit var cancelButton:AppCompatButton
    lateinit var saveButton:AppCompatButton

    lateinit var showRecurrentSwitch:SwitchCompat
    lateinit var removeCardButton:AppCompatButton
    lateinit var removeGiftCardButton:AppCompatButton

    lateinit var showCardCheck:CheckBox
    lateinit var showGiftCardCheck:CheckBox
    lateinit var showPaypalCheck:CheckBox
    lateinit var showGooglePayCheck:CheckBox
    lateinit var showKlarnaCheck:CheckBox
    lateinit var showSwishCheck:CheckBox
    lateinit var showMobilePayCheck:CheckBox
    lateinit var showVippsCheck:CheckBox

    private fun inputValuesFromJson(jsonConfig:JSONObject) {

        if (jsonConfig.has("api_user_id")) cardPaymentsApiUserName.setText(jsonConfig.getString("api_user_id"))
        if (jsonConfig.has("api_key")) cardPaymentsApiKey.setText(jsonConfig.getString("api_key"))
        if (jsonConfig.has("encryption_key")) cardEncryptionKeyEd.setText(jsonConfig.getString("encryption_key"))
        if (jsonConfig.has("public_key_alias")) jwtKeyAliasInput.setText(jsonConfig.getString("public_key_alias"))
        if (jsonConfig.has("threeds_contract_id")) jwtContractIDInput.setText(jsonConfig.getString("threeds_contract_id"))
        if (jsonConfig.has("payment_provider_contract")) cardPaymentsProviderContract.setText(jsonConfig.getString("payment_provider_contract"))
        if (jsonConfig.has("paypal_provider_contract")) paypalPaymentsProviderContract.setText(jsonConfig.getString("paypal_provider_contract"))
        if (jsonConfig.has("gift_card_payment_provider_contract")) giftCardPaymentsProviderContract.setText(jsonConfig.getString("gift_card_payment_provider_contract"))
        if (jsonConfig.has("token_scope")) cardPaymentsReuseTokenScope.setText(jsonConfig.getString("token_scope"))
        if (jsonConfig.has("entity_id")) edKlarnaOrgID.setText(jsonConfig.getString("entity_id"))
        if (jsonConfig.has("customer")) edKlarnaCustomer.setText(jsonConfig.getString("customer"))
    }

    private val loadConfigBtn by lazy { findViewById<AppCompatButton>(R.id.json_config_btn) }
    private val enableThreedsCheck by lazy { findViewById<CheckBox>(R.id.checkbox_option_3ds) }

    private val jwtKeyAliasInput by lazy { findViewById<TextInputEditText>(R.id.jwt_key_alias_edit) }
    private val jwtContractIDInput by lazy { findViewById<TextInputEditText>(R.id.jwt_contract_id_edit) }
    private val cardEncryptionKeyEd by lazy { findViewById<TextInputEditText>(R.id.card_encryption_key_edit) }


    private val cardPaymentsProviderContract by lazy { findViewById<TextInputEditText>(R.id.request_card_payment_provider_contract_edit) }
    private val paypalPaymentsProviderContract by lazy { findViewById<TextInputEditText>(R.id.request_paypal_payment_provider_contract_edit) }

    private val giftCardPaymentsProviderContract by lazy { findViewById<TextInputEditText>(R.id.gift_card_input_data_edit) }

    private val cardPaymentsApiUserName by lazy { findViewById<TextInputEditText>(R.id.request_card_api_user_name_edit) }
    private val cardPaymentsApiKey by lazy { findViewById<TextInputEditText>(R.id.request_card_api_key_edit) }
    private val cardPaymentsReuseTokenScope by lazy { findViewById<TextInputEditText>(R.id.request_card_reuse_token_scope_edit) }


    private val edGooglePayInputGatewayMerchantID by lazy { findViewById<TextInputEditText>(R.id.google_pay_input_gateway_merchant_id_edit) }
    private val edGooglePayInputMerchantName by lazy { findViewById<TextInputEditText>(R.id.google_pay_input_merchant_name_edit) }
    private val edGooglePayInputMerchantID by lazy { findViewById<TextInputEditText>(R.id.google_pay_input_merchant_id_edit) }

    private val edKlarnaOrgID by lazy { findViewById<TextInputEditText>(R.id.request_klarna_org_id_edit) }
    private val edKlarnaCustomer by lazy { findViewById<TextInputEditText>(R.id.request_klarna_customer_id_edit) }

    lateinit var showSetupLanguageTV:AppCompatTextView
    var currentLanguage = "EN"
    lateinit var changeLangButton:AppCompatButton
    lateinit var fontSpinner: Spinner
    lateinit var regionsSpinner: Spinner
    private lateinit var fontOption:String
    private lateinit var regionOption:String
    private var fontOptionPos:Int = 0
    private val textWatchBackground = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.length<6) return
            val colorCode = parseColor(s.toString())
            if (colorCode!=0) backgroundColorSample.setBackgroundColor(colorCode)
        }
    }

    private val textWatchTextFields = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.length<6) return
            val colorCode = parseColor(s.toString())
            if (colorCode!=0) textFieldBackgroundSample.setBackgroundColor(colorCode)
        }
    }

    private val textWatchInputTextColor = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.length<6) return
            val colorCode = parseColor(s.toString())
            if (colorCode!=0) textInputColorSample.setBackgroundColor(colorCode)
        }
    }

    private val textWatchHintColor = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.length<6) return
            val colorCode = parseColor(s.toString())
            if (colorCode!=0) textHintColorSample.setBackgroundColor(colorCode)
        }
    }

    private val textWatchPayButtonColor = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.length<6) return
            val colorCode = parseColor(s.toString())
            if (colorCode!=0) payButtonColorSample.setBackgroundColor(colorCode)
        }
    }

    private val textWatchTitleCardColor = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.length<6) return
            val colorCode = parseColor(s.toString())
            if (colorCode!=0) cardTitleColorSample.setBackgroundColor(colorCode)
        }
    }

    private fun setLocation() {

        val langSelected = "EN"
        val locale = Locale(langSelected)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, this.resources.displayMetrics)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocation()
        setContentView(R.layout.customization_settings)
        backgroundColorInput = findViewById(R.id.form_background_input)
        backgroundColorSample = findViewById(R.id.form_background_sample)

        textFieldBackgroundInput = findViewById(R.id.text_field_background_input)
        textFieldBackgroundSample = findViewById(R.id.text_field_background_sample)

        textInputColor = findViewById(R.id.text_input_color_code)
        textInputColorSample = findViewById(R.id.text_input_color_sample)

        textHintColor = findViewById(R.id.text_hint_color_code)
        textHintColorSample = findViewById(R.id.text_hint_color_sample)

        payButtonColorCode = findViewById(R.id.pay_button_color_code)
        payButtonColorSample = findViewById(R.id.pay_button_color_sample)

        cardTitleColorCode = findViewById(R.id.card_title_color_code)
        cardTitleColorSample = findViewById(R.id.card_title_color_sample)

        saveButton = findViewById(R.id.settings_save_btn)
        cancelButton = findViewById(R.id.settings_cancel_btn)

        showRecurrentSwitch = findViewById(R.id.show_recurrent_checkbox)
        removeCardButton = findViewById(R.id.delete_card_details)
        removeGiftCardButton = findViewById(R.id.delete_card_details2)

        showSetupLanguageTV = findViewById(R.id.setup_language_display)
        changeLangButton = findViewById(R.id.change_lang_btn)

        currentLanguage = getStoredLanguage(this)

        showCardCheck = findViewById(R.id.checkbox_option_card)
        showGiftCardCheck = findViewById(R.id.checkbox_option_gift_card)
        showPaypalCheck = findViewById(R.id.checkbox_option_paypal)
        showGooglePayCheck = findViewById(R.id.checkbox_option_google_pay)
        showKlarnaCheck = findViewById(R.id.checkbox_option_klarna)
        showMobilePayCheck = findViewById(R.id.checkbox_option_mobile_pay)
        showSwishCheck = findViewById(R.id.checkbox_option_swish)
        showVippsCheck = findViewById(R.id.checkbox_option_vipps)

        changeLangButton.setOnClickListener {
            gotoLangSelectionScreen()
        }
        fontSpinner = findViewById(R.id.fonts_spinner)
        regionsSpinner = findViewById(R.id.regions_spinner)
        if (currentLanguage.isNotEmpty()){
            showSetupLanguageTV.text = currentLanguage
        } else showSetupLanguageTV.text = "EN"
        if(getStoredPaymentDetails(Keys.keyRecurrent).isEmpty()){
            removeCardButton.visibility = View.INVISIBLE
        } else {
            removeCardButton.setOnClickListener {
                removeStoredPaymentDetails(Keys.keyRecurrent)
            }
        }

        if(getStoredPaymentDetails(Keys.giftCardRecurrentKey).isEmpty()){
            removeGiftCardButton.visibility = View.INVISIBLE
        } else {
            removeGiftCardButton.setOnClickListener {
                removeStoredPaymentDetails(Keys.giftCardRecurrentKey)
            }
        }

        backgroundColorInput.addTextChangedListener(textWatchBackground)
        textFieldBackgroundInput.addTextChangedListener(textWatchTextFields)
        textInputColor.addTextChangedListener(textWatchInputTextColor)
        textHintColor.addTextChangedListener(textWatchHintColor)
        cardTitleColorCode.addTextChangedListener(textWatchTitleCardColor)
        payButtonColorCode.addTextChangedListener(textWatchPayButtonColor)

        saveButton.setOnClickListener {
            saveFormDetails()
            if (fontOption.isNotEmpty()) saveFont(fontOption)
            if (regionOption.isNotEmpty()) saveRegion(regionOption)
            saveFontPos(fontOptionPos)

            saveShowCard(showCardCheck.isChecked)
            saveShowGiftCard(showGiftCardCheck.isChecked)
            saveShowPaypal(showPaypalCheck.isChecked)
            saveShowGooglePay(showGooglePayCheck.isChecked)
            saveShowKlarna(showKlarnaCheck.isChecked)
            saveShowSwish(showSwishCheck.isChecked)
            saveShowVipps(showVippsCheck.isChecked)
            saveShowMobilePay(showMobilePayCheck.isChecked)
            savePaymentSettingsData()
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        loadConfigBtn.setOnClickListener {
            openFile()
        }

        initFontsDropDown()
        initRegionDropDown()
        loadFormDetails()
        loadPaymentData()
    }

    fun parseColor(colorCodeParam:String):Int {
        return try {
            val colorCode: Int = Color.parseColor(colorCodeParam)
            colorCode
        } catch (e: Exception) {
            0
        }
    }

    private fun saveFormDetails() {
        val sharedPref = getSharedPreferences("customization", Context.MODE_PRIVATE)
        sharedPref.edit().putString(keySaveBackgroundColor,backgroundColorInput.text.toString()).apply()
        sharedPref.edit().putString(keySaveInputTextColor,textInputColor.text.toString()).apply()
        sharedPref.edit().putString(keySaveTextFieldsColor,textFieldBackgroundInput.text.toString()).apply()
        sharedPref.edit().putString(keySaveHintColor,textHintColor.text.toString()).apply()
        sharedPref.edit().putString(keySavePayBtnColor,payButtonColorCode.text.toString()).apply()
        sharedPref.edit().putString(keySaveTitleColor,cardTitleColorCode.text.toString()).apply()
        sharedPref.edit().putBoolean(keySaveShowRecurrent,showRecurrentSwitch.isChecked).apply()

        saveLanguage(languageSelection)
    }

    private fun loadFormDetails() {
        val sharedPref = getSharedPreferences("customization", Context.MODE_PRIVATE)
        backgroundColorInput.setText(sharedPref.getString(keySaveBackgroundColor,""))
        textInputColor.setText(sharedPref.getString(keySaveInputTextColor,""))
        textFieldBackgroundInput.setText(sharedPref.getString(keySaveTextFieldsColor,""))
        textHintColor.setText(sharedPref.getString(keySaveHintColor,""))
        payButtonColorCode.setText(sharedPref.getString(keySavePayBtnColor,""))
        cardTitleColorCode.setText(sharedPref.getString(keySaveTitleColor,""))
        showRecurrentSwitch.isChecked = sharedPref.getBoolean(keySaveShowRecurrent,false)
        showCardCheck.isChecked = getShowCard()
        showGiftCardCheck.isChecked = getShowGiftCard()
        showPaypalCheck.isChecked = getShowPaypal()
        showGooglePayCheck.isChecked = getShowGooglePay()
        showKlarnaCheck.isChecked = getShowKlarna()
        showMobilePayCheck.isChecked = getShowMobilePay()
        showSwishCheck.isChecked = getShowSwish()
        showVippsCheck.isChecked = getShowVipps()
    }

    private fun loadPaymentData() {
        jwtContractIDInput.setText(getPaymentsContractID(this))
        jwtKeyAliasInput.setText(getPublicEncryptionKeyAlias(this))
        cardEncryptionKeyEd.setText(getCardEncryptionKey(this))
        enableThreedsCheck.isChecked = getThreedsUserOption(this)
        cardPaymentsProviderContract.setText(getPaymentsProviderContract(this))
        paypalPaymentsProviderContract.setText(getPaymentsProviderContractPaypal(this))
        giftCardPaymentsProviderContract.setText(getGiftCardPaymentsProviderContract(this))
        cardPaymentsApiUserName.setText(getPaymentsApiUserID(this))
        cardPaymentsApiKey.setText(getPaymentsApiKey(this))
        cardPaymentsReuseTokenScope.setText(getPaymentsTokenScope(this))

        edGooglePayInputGatewayMerchantID.setText(getGooglePayInputGatewayMerchantID(this))
        edGooglePayInputMerchantName.setText(getGooglePayInputMerchantName(this))
        edGooglePayInputMerchantID.setText(getGooglePayInputMerchantID(this))

        edKlarnaOrgID.setText(getPaymentOrgID(this))
        edKlarnaCustomer.setText(getPaymentCustomerID(this))

    }

    fun removeStoredPaymentDetails(key: String) {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        sharedPref.edit().remove(key).apply()
        finish()
    }
    fun getStoredPaymentDetails(key: String):String {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getString(key,"")?:""
    }

    fun gotoLangSelectionScreen() {
        val langScreen = Intent(this,LanguageSelection::class.java)
        startActivityForResult(langScreen,LanguageSelection.reqCode)
    }

    private fun saveLanguage(lang:String) {
        if (lang.isEmpty()) return
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putString("key_store_lang",lang)
            .apply()
    }

    private fun saveFont(fontName:String) {
        if (fontName.isEmpty()) return
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putString("key_store_font",fontName)
            .apply()
    }

    private fun saveRegion(fontName:String) {
        if (fontName.isEmpty()) return
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putString("key_store_region",fontName)
            .apply()
    }

    private fun saveFontPos(position:Int) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putInt("key_store_font_pos",position)
            .apply()
    }

    private fun saveRegionPos(position:Int) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putInt("key_store_region_pos",position)
            .apply()
    }

    private fun saveShowCard(showCard:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_card",showCard)
            .apply()
    }

    private fun getShowCard():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_card",true)
    }

    private fun saveShowGiftCard(showGiftCard:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_gift_card",showGiftCard)
            .apply()
    }

    private fun getShowGiftCard():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_gift_card",true)
    }

    private fun saveShowPaypal(showPaypal:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_paypal",showPaypal)
            .apply()
    }

    private fun getShowPaypal():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_paypal",true)
    }

    private fun saveShowGooglePay(showGooglePay:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_google_pay",showGooglePay)
            .apply()
    }

    private fun getShowGooglePay():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_google_pay",true)
    }
    private fun saveShowKlarna(showKlarna:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_klarna",showKlarna)
            .apply()
    }

    private fun saveShowSwish(showKlarna:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_swish",showKlarna)
            .apply()
    }

    private fun saveShowVipps(showVipps:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_vipps",showVipps)
            .apply()
    }

    private fun saveShowMobilePay(showMobilePay:Boolean) {
        val sp = getSharedPreferences("checkout_data",Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean("key_store_show_mobile_pay",showMobilePay)
            .apply()
    }

    private fun getShowKlarna():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_klarna",true)
    }

    private fun getShowMobilePay():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_mobile_pay",true)
    }

    private fun getShowSwish():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_swish",true)
    }

    private fun getShowVipps():Boolean {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("key_store_show_vipps",true)
    }

    private fun getStoredFontPos():Int {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getInt("key_store_font_pos",0)
    }

    private fun getStoredRegionPos():Int {
        val sharedPref = getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        return sharedPref.getInt("key_store_region_pos",0)
    }

    private fun savePaymentSettingsData() {
        val sp = getSharedPreferences("payment_settings_data",Context.MODE_PRIVATE)
        sp.edit()
            .putString(keyContractID,jwtContractIDInput.text.toString())
            .putString(keyPublicEncryptionKeyAlias,jwtKeyAliasInput.text.toString())
            .putString(keyPaymentsContractProviderParameter,cardPaymentsProviderContract.text.toString())
            .putString(keyPaymentsContractProviderPaypalParameter,paypalPaymentsProviderContract.text.toString())
            .putString(keyGiftCardPaymentsContractProviderParameter,giftCardPaymentsProviderContract.text.toString())
            .putString(keyPaymentsApiUserName,cardPaymentsApiUserName.text.toString())
            .putString(keyPaymentsApiKey,cardPaymentsApiKey.text.toString())
            .putString(keyPaymentsTokenScope, cardPaymentsReuseTokenScope.text.toString())
            .putBoolean(keyThreedsOption,enableThreedsCheck.isChecked)
            .putString(keyGooglePayInputGatewayMerchantID,edGooglePayInputGatewayMerchantID.text.toString())
            .putString(keyGooglePayInputMerchantName,edGooglePayInputMerchantName.text.toString())
            .putString(keyGooglePayInputMerchantID,edGooglePayInputMerchantID.text.toString())
            .putString(keyCardEncryptionKey,cardEncryptionKeyEd.text.toString())
            .putString(keyPaymentCustomerID,edKlarnaCustomer.text.toString())
            .putString(keyPaymentOrganizationID,edKlarnaOrgID.text.toString())
            .apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LanguageSelection.reqCode && resultCode == 1) {
            languageSelection = data?.getStringExtra(LanguageSelection.keySelectedLand)?:""
            showSetupLanguageTV.text = languageSelection

        } else if (requestCode == PICK_JSON_FILE && resultCode != 0){
            val configUri = data?.data?:Uri.parse("")
            loadJsonFileConfig(configUri)
        }
    }
    fun initFontsDropDown() {
        ArrayAdapter.createFromResource(
            this,
            R.array.fonts_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.custom_region_item)

            // Apply the adapter to the spinner
            fontSpinner.adapter = adapter
            fontSpinner.setSelection(getStoredFontPos())

        }
        fontSpinner.onItemSelectedListener = this
    }

    fun initRegionDropDown() {
        ArrayAdapter.createFromResource(
            this,
            R.array.regions_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.custom_spinner_item)

            // Apply the adapter to the spinner
            regionsSpinner.adapter = adapter
            regionsSpinner.setSelection(getStoredRegionPos())

        }
        regionsSpinner.onItemSelectedListener = object :AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                regionOption = parent?.getItemAtPosition(position) as String
                saveRegionPos(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        fontOption = parent?.getItemAtPosition(position) as String
        fontOptionPos = position
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun loadJsonFileConfig(configUri:Uri):JSONObject {

        val configInput = contentResolver.openInputStream(configUri)
        val configBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            configInput!!.readAllBytes()
        } else null
        var configJson = JSONObject()
        val configContent = configBytes?.let { String(it, StandardCharsets.UTF_8) }
        if (configContent!=null && configContent.isNotEmpty()){
            configJson = JSONObject(configContent)
            inputValuesFromJson(configJson)
        } else {
            ErrorDisplayDialog.newInstance("Upload json config", "failed to upload").show(
                supportFragmentManager,
                "error"
            )
        }
        configInput?.close()
        return  configJson
    }


    val PICK_JSON_FILE = 121

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, PICK_JSON_FILE)
    }

}