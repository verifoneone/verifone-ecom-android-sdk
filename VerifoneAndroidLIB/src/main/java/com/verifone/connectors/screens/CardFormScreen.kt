package com.verifone.connectors.screens

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.*
import android.view.*
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.verifone.connectors.R
import com.verifone.connectors.datapack.configuration.FormUICustomizationData
import com.verifone.connectors.datapack.configuration.PayerCardData
import com.verifone.connectors.util.CardEncryption
import com.verifone.connectors.util.CreditCardInputResult
import com.verifone.connectors.util.VerifoneCardValidator
import java.io.Serializable

internal class CardFormScreen():DialogFragment() {

    companion object {
        val keyCardEditInput = "cardEditInput"
        val keyCVCEditInput = "cvcEditInput"
        val keyExpiryEditInput = "expiryEditInput"
        val keyPayerNameEditInput = "payerNameEditInput"
        fun encryptCardDetails(expiryMonthParam: Int,
                               expiryYearParam: Int,
                               cardNumberParam: String,
                               cvvNumberParam: String,
                               publicKey: String):String {
            val mCardEncryption = CardEncryption(expiryMonthParam,expiryYearParam,cardNumberParam,cvvNumberParam,publicKey)
            return mCardEncryption.encryptionCard()
        }
    }

    class CallbackActionKeeper(var onCardInput: (inputResult:CreditCardInputResult) -> Unit?):Serializable
    constructor(ctx: Context,
                customizationParam: FormUICustomizationData,
                showRecurrent: Boolean,
                displayPrice: String,
                payButtonLabel:String,
                encryptionKey: String,
                onCardInputComplete: (cardInputResult:CreditCardInputResult) -> Unit
    ) : this() {
        publicKey = encryptionKey
        mCtx = ctx
        price = displayPrice
        showRecurrentOption = showRecurrent
        customizationData = customizationParam
        onCardInputDone = onCardInputComplete
        payButtonText = payButtonLabel
    }


    private var colorCodeTitle: Int = -1
    private var colorCodePayBtn: Int = -1
    private var colorCodeHint: Int = Color.BLACK
    private var colorCodeInputText:Int =-1
    private var colorCodeTextInputField: Int = -1
    private var colorCodeContainer: Int = -1

    private var expValid: Boolean = false
    private var validResult: Boolean = false
    private var showFieldErrors:Boolean = false
    private var currentCardExpiryString = ""
    private var payButtonText = ""
    lateinit var cardNRHintTV:AppCompatTextView
    lateinit var expiryDateHintTV:AppCompatTextView
    lateinit var cvvNRHintTV:AppCompatTextView
    lateinit var buyerNameHintTV:AppCompatTextView

    private lateinit var cardNumberInputLayout:TextInputLayout
    lateinit var expiryDateInputLayout:TextInputLayout
    lateinit var cvcNumberInputLayout:TextInputLayout
    lateinit var ownerNameInputLayout:TextInputLayout
    lateinit var expiryDateEdit:AppCompatEditText
    lateinit var cardNumberInput:TextInputEditText
    lateinit var cvcNumberInput:TextInputEditText
    lateinit var secureCodeHintIM:AppCompatImageView
    var secureCodeLength = 3
    lateinit var ownerNameInput:TextInputEditText
    lateinit var separatorView:View
    lateinit var closeBtn:AppCompatImageView
    var foundCard:VerifoneCardValidator.Cards = VerifoneCardValidator.Cards.NOCARD
    lateinit var formTitle:AppCompatTextView
    lateinit var visaCardTypeIM:AppCompatImageView
    lateinit var mastercardTypeIM:AppCompatImageView
    lateinit var amexCardTypeIM:AppCompatImageView
    lateinit var maestroCardTypeIM:AppCompatImageView
    lateinit var discoverCardTypeIM:AppCompatImageView
    lateinit var jcbCardTypeIM:AppCompatImageView
    lateinit var dinersCardTypeIM:AppCompatImageView

    lateinit var showRecurrentTV:AppCompatTextView
    lateinit var showRecurrentSwitch: SwitchCompat
    lateinit var textLogoTV:AppCompatTextView
    private lateinit var mEncryption:CardEncryption
    private var publicKey = ""
    lateinit var mainContainer:ConstraintLayout
    private var price = "0"
    private val yearConst = 2000
    var isCardValid = false
    var isNameValid = false
    private lateinit var mCtx:Context
    var selectedYear:Int = 0
    var selectedMonth:Int = 0
    private var showRecurrentOption:Boolean = false
    private var customizationData = FormUICustomizationData() //= customizationParam

    private lateinit var onCardInputDone: (inputResult:CreditCardInputResult) -> Unit //= onCardInputComplete
    lateinit var payButton:AppCompatButton
    lateinit var cardFormView:View
    var isExpiryDateInputValid:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val formView = inflater.inflate(R.layout.card_form_layout, null)
        cardFormView = formView

        cardNRHintTV = formView.findViewById(R.id.card_number_hint_tv)
        expiryDateHintTV = formView.findViewById(R.id.expiry_hint_tv)
        cvvNRHintTV = formView.findViewById(R.id.cvc_hint_tv)
        buyerNameHintTV = formView.findViewById(R.id.payer_name_hint_tv)

        cardNumberInputLayout = formView.findViewById(R.id.card_number_input_layout)
        expiryDateInputLayout = formView.findViewById(R.id.card_expiry_input_layout)
        cvcNumberInputLayout = formView.findViewById(R.id.cvc_input_layout)
        ownerNameInputLayout = formView.findViewById(R.id.payer_name_input_layout)
        textLogoTV = formView.findViewById(R.id.logo_text)
        expiryDateEdit = formView.findViewById(R.id.card_expiry_edit)
        payButton = formView.findViewById(R.id.pay_button)
        cardNumberInput = formView.findViewById(R.id.card_edit_text)
        secureCodeHintIM = formView.findViewById(R.id.secure_code_hint_im)
        cvcNumberInput = formView.findViewById(R.id.cvc_nr_edit)
        ownerNameInput = formView.findViewById(R.id.payer_name_edit_text)
        showRecurrentTV = formView.findViewById(R.id.store_payment_details)
        showRecurrentSwitch = formView.findViewById(R.id.save_card_checkbox)
        separatorView = formView.findViewById(R.id.separator_top)
        formTitle = formView.findViewById(R.id.card_form_title)
        visaCardTypeIM = formView.findViewById(R.id.visa_card_type)
        mastercardTypeIM = formView.findViewById(R.id.mastercard_card_type)
        amexCardTypeIM = formView.findViewById(R.id.amex_card_type)
        maestroCardTypeIM = formView.findViewById(R.id.maestro_card_type)
        discoverCardTypeIM = formView.findViewById(R.id.discover_card_type)
        jcbCardTypeIM = formView.findViewById(R.id.jcb_card_type)
        dinersCardTypeIM = formView.findViewById(R.id.diners_card_type)

        closeBtn = formView.findViewById(R.id.close_btn)
        mainContainer = formView.findViewById(R.id.card_input_container)
        expiryDateEdit.isEnabled = true
        expiryDateEdit.setTextIsSelectable(true)
        visaCardTypeIM.visibility = GONE
        mastercardTypeIM.visibility = GONE
        amexCardTypeIM.visibility = GONE
        maestroCardTypeIM.visibility = GONE
        discoverCardTypeIM.visibility = GONE
        jcbCardTypeIM.visibility = GONE
        setupLogoText()
        var showPrice = ""
        if (price.isNotEmpty()){
            showPrice = getString(R.string.submitPay)+" "+ price
            payButton.text = showPrice
        } else {
            payButton.text = payButtonText
        }

        expiryDateEdit.addTextChangedListener(expiryDateWatcher)
        expiryDateEdit.setOnClickListener {
            expiryDateEdit.text?.let { it1 -> expiryDateEdit.setSelection(it1.length) }
        }

        payButton.setOnClickListener {
            val cardNr = cardNumberInput.text.toString()
            hideKeyboard(cardFormView)
            showFieldErrors = true
            validateCardNumber(cardNr)
            validateExpiryInput(currentCardExpiryString, 1)
            var creditCardValid = false
            if (cardNr.length>=15 && isCardValid) {
                creditCardValid = true
            }
            val expiryValid = VerifoneCardValidator.validateExpiryDate(
                selectedMonth,
                selectedYear
            )
            var cvcValid = false

            val cvcInput = cvcNumberInput.text.toString()
            if (cvcInput.length == secureCodeLength) {
                cvcValid = true
            }

            if (isCardValid) {
                cvcValid = VerifoneCardValidator.validateCVV(
                    cvcNumberInput.text.toString(),
                    foundCard
                )
            }

            val name = ownerNameInput.text.toString()
            if (name.isNullOrEmpty()) {
                isNameValid = false
                ownerNameInputLayout.isErrorEnabled = true
                buyerNameHintTV.setTextColor(resources.getColor(R.color.error_red))
                ownerNameInputLayout.error = getString(R.string.customerText)
            } else {
                isNameValid = true
                buyerNameHintTV.setTextColor(colorCodeHint)
                ownerNameInputLayout.isErrorEnabled = false
            }

            if (!cvcValid) {
                cvcNumberInputLayout.isErrorEnabled = true
                cvvNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                if (isCardValid && foundCard.name== "AMEX"){
                    cvcNumberInputLayout.error = getString(R.string.cvv4NotValid)
                } else {
                    cvcNumberInputLayout.error = getString(R.string.cvv3NotValid)
                }

            } else {
                cvcNumberInputLayout.isErrorEnabled = false
                cvvNRHintTV.setTextColor(colorCodeHint)
            }

            if (creditCardValid && cvcValid && expiryValid && isExpiryDateInputValid && isNameValid) {

                val expMonth = selectedMonth
                val expYear = selectedYear
                val cvcNumber = cvcNumberInput.text.toString()
                var encryptedCard = ""
                var encryptedCardReuse = ""
                if (publicKey.isNotEmpty()) {
                    mEncryption = CardEncryption(
                        expMonth,
                        expYear,
                        cardNr,
                        cvcNumber,
                        publicKey
                    )
                    if (mEncryption.isPublicKeyValid) {
                        encryptedCard = mEncryption.encryptionCard()
                    }
                }

                val cardData = PayerCardData()
                cardData.cardBrand = foundCard.cardBrandName

                val cardInputResultData = CreditCardInputResult()
                cardInputResultData.encryptedCardData = encryptedCard
                cardInputResultData.payerName = name
                cardInputResultData.cardProperties = cardData
                cardInputResultData.storeCard = showRecurrentSwitch.isChecked
                onCardInputDone(
                   cardInputResultData
                )
                dismiss()
            }
        }

        showRecurrentSwitch.thumbTintList = ColorStateList.valueOf(Color.parseColor("#F8F9F9"))
        showRecurrentSwitch.trackTintList = ColorStateList.valueOf(Color.parseColor("#A6ACAF"))
        showRecurrentSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                showRecurrentSwitch.thumbTintList = ColorStateList.valueOf(Color.parseColor("#2ECC71"))
                showRecurrentSwitch.trackTintList = ColorStateList.valueOf(Color.parseColor("#A6ACAF"))
            } else {
                showRecurrentSwitch.thumbTintList = ColorStateList.valueOf(Color.parseColor("#F8F9F9"))
                showRecurrentSwitch.trackTintList = ColorStateList.valueOf(Color.parseColor("#A6ACAF"))
            }
        }

        closeBtn.setOnClickListener {
            //val emptyCard = PayerCardData()
            //onCardInputDone("", "", emptyCard, false)
            dismiss()
        }

        cvcNumberInput.addTextChangedListener(cvcWatcher)

        cardNumberInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                validateCardNumber(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, removed: Int, added: Int) {

            }
        })

        ownerNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty() && showFieldErrors) {
                    isNameValid = false
                    ownerNameInputLayout.isErrorEnabled = true
                    buyerNameHintTV.setTextColor(resources.getColor(R.color.error_red))
                    ownerNameInputLayout.error = getString(R.string.customerText)
                } else {
                    isNameValid = true
                    buyerNameHintTV.setTextColor(colorCodeHint)
                    ownerNameInputLayout.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, removed: Int, added: Int) {

            }
        })

        ownerNameInput.setFilters(arrayOf<InputFilter>(object : InputFilter {
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence {
                if(source == "") { // for backspace
                    return source
                }
                if(source.toString().contains("[0-9]+".toRegex())) {
                    return source.replace("[0-9]+".toRegex(),"")
                }
                return source
            }
        }))

        if (showRecurrentOption) {
            showRecurrentTV.visibility = VISIBLE
            showRecurrentSwitch.visibility = VISIBLE
        } else {
            showRecurrentTV.visibility = INVISIBLE
            showRecurrentSwitch.visibility = INVISIBLE
        }
        customizeForm()
        setupUserFontResource()
        return formView
    }



    fun validateCardNumber(cardNr: String){
        val currentCardNr = cardNr
        if (currentCardNr.length >= 15) {
            cardNumberInputLayout.isErrorEnabled = false
            cardNRHintTV.setTextColor(colorCodeHint)
            isCardValid = VerifoneCardValidator.validateCardNumber(currentCardNr)
            if (isCardValid) {
                cardNumberInputLayout.isErrorEnabled = false
                cardNRHintTV.setTextColor(colorCodeHint)
                foundCard = VerifoneCardValidator.getCardType(currentCardNr)
                secureCodeLength = if (foundCard.name == "AMEX"){
                    4
                } else {
                    3
                }

                if (foundCard.name=="AMEX"){

                    secureCodeHintIM.setImageResource(R.drawable.secure_hint_amex)
                } else {
                    secureCodeHintIM.setImageResource(R.drawable.secure_hint_other)
                }

                when (foundCard.name) {
                    "VISA" -> {
                        visaCardTypeIM.setImageResource(R.drawable.sv_visa_logo)
                        visaCardTypeIM.visibility = VISIBLE
                    }
                    "MASTERCARD" -> {
                        mastercardTypeIM.setImageResource(R.drawable.sv_mastercard_logo)
                        mastercardTypeIM.visibility = VISIBLE
                    }
                    "AMEX" -> {
                        amexCardTypeIM.setImageResource(R.drawable.sv_amex_logo)
                        amexCardTypeIM.visibility = VISIBLE
                    }
                    "MAESTRO" -> {
                        maestroCardTypeIM.setImageResource(R.drawable.sv_maestro_logo)
                        maestroCardTypeIM.visibility = VISIBLE
                    }
                    "DISCOVER" -> {
                        discoverCardTypeIM.setImageResource(R.drawable.sv_discover_logo)
                        discoverCardTypeIM.visibility = VISIBLE
                    }
                    "JCB" -> {
                        jcbCardTypeIM.setImageResource(R.drawable.jcb_card_logo)
                        jcbCardTypeIM.visibility = VISIBLE
                    }
                    "DINERSCLUB" -> {
                        dinersCardTypeIM.setImageResource(R.drawable.diners_club)
                        dinersCardTypeIM.visibility = VISIBLE
                    }
                }
            } else {
                if (showFieldErrors) {
                    cardNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                    cardNumberInputLayout.isErrorEnabled = true
                    cardNumberInputLayout.error = getString(R.string.identityCardNumberNotValid)
                }
                visaCardTypeIM.visibility = GONE
            }
        } else {
            if (showFieldErrors) {
                cardNumberInputLayout.isErrorEnabled = true
                cardNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                cardNumberInputLayout.error = getString(R.string.identityCardNumberLengthNotValid)
            }
            visaCardTypeIM.visibility = GONE
            isCardValid = false
        }
    }

    private fun updateIsExpiredView(expiryValid: Boolean){
        if (!showFieldErrors) return
        if (!expiryValid){
            expiryDateInputLayout.isErrorEnabled = true
            expiryDateHintTV.setTextColor(resources.getColor(R.color.error_red))
            expiryDateInputLayout.error = getString(R.string.cardExpired)
        } else {
            expiryDateInputLayout.isErrorEnabled = false
            expiryDateHintTV.setTextColor(colorCodeHint)
        }
    }

    private fun updateExpiryDateValidityView(isFormatValid: Boolean) {
        if (!showFieldErrors) return
        if (isFormatValid) {
            expiryDateInputLayout.isErrorEnabled = false
            expiryDateHintTV.setTextColor(colorCodeHint)


        } else {
            expiryDateInputLayout.isErrorEnabled = true
            expiryDateInputLayout.error = getString(R.string.cardExpiryDateFormat)
            expiryDateHintTV.setTextColor(resources.getColor(R.color.error_red))
        }
    }

    private fun validateExpiryDate(expiryDate: String):Boolean {
        val currentLength = expiryDate.length
        if (currentLength < 5) return false
        if (currentLength >= 2) {
            val firstChar = expiryDate[0]
            val secondChar = expiryDate[1]
            if (firstChar=='0') {
                var secondDigit = 0
                try {
                    secondDigit = Integer.parseInt("" + secondChar)
                } catch (e: NumberFormatException){}
                if (secondDigit in 1..9) {
                    isExpiryDateInputValid = true
                }
                return isExpiryDateInputValid

            } else if (firstChar == '1') {
                return if (secondChar=='2' || secondChar=='1'|| secondChar=='0'){
                    isExpiryDateInputValid = true
                    true
                } else {
                    isExpiryDateInputValid = false
                    false
                }
            } else {
                isExpiryDateInputValid = false
                return false
            }
        } else {
            isExpiryDateInputValid = false
            return false
        }
    }

    fun validateExpiryInput(s: CharSequence?, before: Int){
        var current = ""
        var applySeparator = true
        current = s.toString()
        applySeparator = before <= 0

        val len = current.length-1

        if (len == -1) return
        val ch = s?.get(len)
        if (ch == '\\') {
            return
        }
        val currentLength = s?.length?:0
        if (currentLength>=2) {
            val mon = s?.substring(0, 2)?.toInt()?:0
            selectedMonth = mon

        } else if (currentLength<2) {
            selectedMonth = -1
        }

        if (currentLength == 5) {
            val year = s?.substring(3, 5)?.toInt()?:0
            selectedYear = yearConst + year

            expValid = VerifoneCardValidator.validateExpiryDate(
                selectedMonth,
                selectedYear
            )

        } else if (currentLength<5) {
            selectedYear = -1
        }
        if (currentLength==2 && applySeparator) {
            val pad = s.toString()+"/"
            expiryDateEdit.setText(pad)
            expiryDateEdit.setSelection(pad.length)
        } else if (currentLength==3 && s?.last() !='/'&& applySeparator) {
            val lastChar = s?.last()
            val temp = s?.subSequence(0, 2).toString()+"/"+lastChar
            expiryDateEdit.setText(temp)
            expiryDateEdit.setSelection(temp.length)
        }
        validResult = validateExpiryDate(current)
        updateExpiryDateValidityView(validResult)
        if (validResult) updateIsExpiredView(expValid)
    }

    private val expiryDateWatcher = object: TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
           currentCardExpiryString = s.toString()
           validateExpiryInput(s, before)
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private val cvcWatcher = object: TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val cvcString = s.toString()
            if (!showFieldErrors) return
            if (cvcString.length == secureCodeLength) {
                cvcNumberInputLayout.isErrorEnabled = false
                cvvNRHintTV.setTextColor(colorCodeHint)
            } else {
                cvvNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                cvcNumberInputLayout.isErrorEnabled = true
                if ( foundCard.name == "AMEX")
                    cvcNumberInputLayout.error = getString(R.string.cvv4NotValid)
                 else cvcNumberInputLayout.error = getString(R.string.cvv3NotValid)
            }

        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun customizeForm() {
        try {
            colorCodeContainer = Color.parseColor(customizationData.paymentFormBackground)
            mainContainer.backgroundTintList = ColorStateList.valueOf(colorCodeContainer)

        } catch (e: Exception) {
        }

        try {
            colorCodeTextInputField = Color.parseColor(customizationData.formTextFieldsBackground)
            ownerNameInputLayout.setBoxBackgroundColorStateList(ColorStateList.valueOf(colorCodeTextInputField))
            cardNumberInputLayout.setBoxBackgroundColorStateList(ColorStateList.valueOf(colorCodeTextInputField))

            cvcNumberInputLayout.setBoxBackgroundColorStateList(ColorStateList.valueOf(colorCodeTextInputField))
            expiryDateInputLayout.setBoxBackgroundColorStateList(ColorStateList.valueOf(colorCodeTextInputField))
            showRecurrentTV.backgroundTintList = ColorStateList.valueOf(colorCodeTextInputField)

        } catch (e: java.lang.Exception){
        }

        try {
            colorCodeInputText = Color.parseColor(customizationData.formInputTextColor)
            ownerNameInput.setTextColor(ColorStateList.valueOf(colorCodeInputText))
            cardNumberInput.setTextColor(ColorStateList.valueOf(colorCodeInputText))
            cvcNumberInput.setTextColor(ColorStateList.valueOf(colorCodeInputText))
            expiryDateEdit.setTextColor(ColorStateList.valueOf(colorCodeInputText))

        } catch (e: java.lang.Exception){
        }

        try {
            colorCodeHint = Color.parseColor(customizationData.hintTextColor)
            expiryDateInputLayout.defaultHintTextColor = ColorStateList.valueOf(colorCodeHint)
            cardNumberInputLayout.defaultHintTextColor = ColorStateList.valueOf(colorCodeHint)
            ownerNameInputLayout.defaultHintTextColor = ColorStateList.valueOf(colorCodeHint)
            cvcNumberInputLayout.defaultHintTextColor = ColorStateList.valueOf(colorCodeHint)
            showRecurrentTV.setTextColor(colorCodeHint)
        } catch (e: java.lang.Exception){

        }

        try {
            val colorCodeSeparator: Int = Color.parseColor("#A6ACAF")
            separatorView.backgroundTintList = ColorStateList.valueOf(colorCodeSeparator)
        } catch (e: java.lang.Exception){
        }

        try {
            colorCodePayBtn = Color.parseColor(customizationData.payButtonColor)
            payButton.backgroundTintList = ColorStateList.valueOf(colorCodePayBtn)
        } catch (e: java.lang.Exception){
        }

        try {
            colorCodeTitle = Color.parseColor(customizationData.formTitleTextColor)
            formTitle.setTextColor(ColorStateList.valueOf(colorCodeTitle))
        } catch (e: java.lang.Exception){
        }
    }

    private fun hideKeyboard(v: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun setupUserFontResource() {
        if (customizationData.userTextFontRes>0) {
            cardNumberInputLayout.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            cardNRHintTV.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            cardNumberInput.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            formTitle.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            expiryDateInputLayout.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            expiryDateHintTV.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            expiryDateEdit.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            cvcNumberInputLayout.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            cvvNRHintTV.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            cvcNumberInput.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            ownerNameInputLayout.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            buyerNameHintTV.typeface =ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            ownerNameInput.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            showRecurrentTV.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
            payButton.typeface = ResourcesCompat.getFont(requireContext(),customizationData.userTextFontRes)
        }
    }

    private fun setupLogoText() {

        val temp = requireContext().getString(R.string.footerText).replace("Verifone","")
        textLogoTV.text = temp
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(VerifonePaymentForm.keyTextFontID,customizationData.userTextFontRes)
        outState.putSerializable(VerifonePaymentForm.keyCallbackCardInputResult,CallbackActionKeeper(onCardInputDone))
        outState.putString(VerifonePaymentForm.keyFormBackground,customizationData.paymentFormBackground)
        outState.putString(VerifonePaymentForm.keyTextFieldBackground,customizationData.formTextFieldsBackground)
        outState.putString(VerifonePaymentForm.keyPayButtonColor,customizationData.payButtonColor)
        outState.putString(VerifonePaymentForm.keyHintColor,customizationData.hintTextColor)
        outState.putString(VerifonePaymentForm.keyInputTextColor,customizationData.formInputTextColor)
        outState.putString(VerifonePaymentForm.keyTitleTextColor,customizationData.formTitleTextColor)
        outState.putString(VerifonePaymentForm.keyDisplayPrice,price)
        outState.putString(VerifonePaymentForm.keyMerchantPublicKey,publicKey)
        outState.putBoolean(VerifonePaymentForm.keyShowRecurrentPaymentsOption,showRecurrentOption)
        //outState.putString(keyCardEditInput,cardNumberInput.text.toString())
        //outState.putString(keyCVCEditInput,cvcNumberInput.text.toString())
        //outState.putString(keyExpiryEditInput,expiryDateEdit.text.toString())
        //outState.putString(keyCardEditInput,ownerNameInput.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val temp = savedInstanceState?.getInt(VerifonePaymentForm.keyTextFontID)?:-1
        val container  = savedInstanceState?.getSerializable(VerifonePaymentForm.keyCallbackCardInputResult) as CallbackActionKeeper?
        if (container != null) {
            onCardInputDone = container.onCardInput as (CreditCardInputResult) -> Unit
        }
        if (temp>0) customizationData.userTextFontRes = temp

        var tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyFormBackground,"")?:""
        if (tempValue.isNotEmpty()){
            customizationData.paymentFormBackground = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyTextFieldBackground,"")?:""
        if (tempValue.isNotEmpty()) {
            customizationData.formTextFieldsBackground = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyPayButtonColor,"")?:""
        if (tempValue.isNotEmpty()) {
            customizationData.payButtonColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyHintColor,"")?:""
        if (tempValue.isNotEmpty()) {
            customizationData.hintTextColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyInputTextColor,"")?:""
        if (tempValue.isNotEmpty()) {
            customizationData.formInputTextColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyTitleTextColor,"")?:""
        if (tempValue.isNotEmpty()) {
            customizationData.formTitleTextColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyDisplayPrice,"")?:""
        if (tempValue.isNotEmpty()) {
            price = tempValue
            val showPrice = getString(R.string.submitPay)+" "+ price
            payButton.text = showPrice
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyMerchantPublicKey,"")?:""
        if (tempValue.isNotEmpty()) {
            publicKey = tempValue
        }

        val showRecurrentSaved = savedInstanceState?.containsKey(VerifonePaymentForm.keyShowRecurrentPaymentsOption)?:false
        if (showRecurrentSaved) {
            showRecurrentOption = savedInstanceState?.getBoolean(VerifonePaymentForm.keyShowRecurrentPaymentsOption,false)?:false
            if (showRecurrentOption) {
                showRecurrentTV.visibility = VISIBLE
                showRecurrentSwitch.visibility = VISIBLE
            } else {
                showRecurrentTV.visibility = INVISIBLE
                showRecurrentSwitch.visibility = INVISIBLE
            }
        }

        setupUserFontResource()
        customizeForm()
    }
}