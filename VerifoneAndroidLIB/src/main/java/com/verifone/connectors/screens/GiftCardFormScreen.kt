package com.verifone.connectors.screens

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
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

internal class GiftCardFormScreen() : DialogFragment() {

    companion object {
        fun encryptCardDetails(
            cardNumberParam: String,
            pinNumberParam: String,
            publicKey: String
        ): String {
            val mCardEncryption = CardEncryption(
                cardNumberParam = cardNumberParam,
                pinNumberParam = pinNumberParam,
                publicKey = publicKey
            )
            println(mCardEncryption.toString())
            return mCardEncryption.encryptionCard()
        }
    }

    class CallbackActionKeeper(var onCardInput: (inputResult: CreditCardInputResult) -> Unit) :
        Serializable

    constructor(
        ctx: Context,
        customizationParam: FormUICustomizationData,
        showRecurrent: Boolean,
        displayPrice: String,
        payButtonLabel: String,
        encryptionKey: String,
        onCardInputComplete: (cardInputResult: CreditCardInputResult) -> Unit
    ) : this() {
        publicKey = encryptionKey
        mCtx = ctx
        price = displayPrice
        showRecurrentOption = showRecurrent
        customizationData = customizationParam
        onCardInputDone = onCardInputComplete
        callbackKeeper = CallbackActionKeeper(onCardInputDone)
        payButtonText = payButtonLabel
    }

    private var colorCodeTitle: Int = -1
    private var colorCodePayBtn: Int = -1
    private var colorCodeHint: Int = Color.BLACK
    private var colorCodeInputText: Int = -1
    private var colorCodeTextInputField: Int = -1
    private var colorCodeContainer: Int = -1
    var callbackKeeper: CallbackActionKeeper? = null
    private var showFieldErrors: Boolean = false
    private var payButtonText = ""
    lateinit var cardNRHintTV: AppCompatTextView
    lateinit var expiryDateHintTV: AppCompatTextView
    lateinit var pinNRHintTV: AppCompatTextView

    private lateinit var cardNumberInputLayout: TextInputLayout
    lateinit var pinNumberInputLayout: TextInputLayout

    lateinit var cardNumberInput: TextInputEditText
    lateinit var pinNumberInput: TextInputEditText
    var secureCodeLength = 4
    var secureCodeMaxLength = 8
    lateinit var separatorView: View
    lateinit var closeBtn: AppCompatImageView
    var foundCard: VerifoneCardValidator.Cards = VerifoneCardValidator.Cards.NOCARD
    lateinit var formTitle: AppCompatTextView

    lateinit var showRecurrentTV: AppCompatTextView
    lateinit var showRecurrentSwitch: SwitchCompat
    lateinit var textLogoTV: AppCompatTextView
    private lateinit var mEncryption: CardEncryption
    private var publicKey = ""
    lateinit var mainContainer: ConstraintLayout
    private var price = "0"
    var isCardValid = false
    private lateinit var mCtx: Context
    private var showRecurrentOption: Boolean = false
    private var customizationData = FormUICustomizationData()

    private lateinit var onCardInputDone: (inputResult: CreditCardInputResult) -> Unit
    lateinit var payButton: AppCompatButton
    lateinit var cardFormView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val formView = inflater.inflate(R.layout.gift_card_form_layout, null)
        cardFormView = formView

        cardNRHintTV = formView.findViewById(R.id.card_number_hint_tv)
        pinNRHintTV = formView.findViewById(R.id.pin_hint_tv)

        cardNumberInputLayout = formView.findViewById(R.id.gift_card_number_input_layout)
        pinNumberInputLayout = formView.findViewById(R.id.gift_card_pin_input_layout)

        textLogoTV = formView.findViewById(R.id.logo_text)
        payButton = formView.findViewById(R.id.gift_card_pay_button)
        cardNumberInput = formView.findViewById(R.id.card_edit_text)
        pinNumberInput = formView.findViewById(R.id.gift_card_pin_nr_edit)

        showRecurrentTV = formView.findViewById(R.id.store_payment_details)
        showRecurrentSwitch = formView.findViewById(R.id.gift_card_save_card_checkbox)
        separatorView = formView.findViewById(R.id.separator_top)
        formTitle = formView.findViewById(R.id.gift_card_form_title)

        closeBtn = formView.findViewById(R.id.gift_card_close_btn)
        mainContainer = formView.findViewById(R.id.gift_card_input_container)

        setupLogoText()
        var showPrice = ""
        if (price.isNotEmpty()) {
            showPrice = getString(R.string.submitPay) + " " + price
            payButton.text = showPrice
        } else {
            payButton.text = payButtonText
        }
        customizeForm()

        payButton.setOnClickListener {
            val cardNr = cardNumberInput.text.toString()
            hideKeyboard(cardFormView)
            showFieldErrors = true
            validateCardNumber(cardNr)
            var creditCardValid = false
            if (cardNr.length >= 14 && isCardValid) {
                creditCardValid = true
            }

            var pinValid = false

            val pinInput = pinNumberInput.text.toString()
            if (pinInput.length >= secureCodeLength) {
                pinValid = true
            }
            if (!pinValid) {
                pinNumberInputLayout.isErrorEnabled = true
                pinNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                pinNumberInputLayout.error = getString(R.string.giftCardFormPinError)
            } else {
                pinNumberInputLayout.isErrorEnabled = false
                pinNRHintTV.setTextColor(colorCodeHint)
            }

            if (creditCardValid && pinValid) {
                val pinNumber = pinNumberInput.text.toString()
                var encryptedCard = ""
                if (publicKey.isNotEmpty()) {
                    mEncryption = CardEncryption(
                        cardNumberParam = cardNr,
                        pinNumberParam = pinNumber,
                        publicKey = publicKey
                    )
                    if (mEncryption.isPublicKeyValid) {
                        encryptedCard = mEncryption.encryptionCard()
                    }
                }

                val cardData = PayerCardData()
                cardData.cardBrand = foundCard.cardBrandName

                val cardInputResultData = CreditCardInputResult()
                cardInputResultData.encryptedCardData = encryptedCard
                cardInputResultData.payerName = ""
                cardInputResultData.cardProperties = cardData
                cardInputResultData.storeCard = showRecurrentSwitch.isChecked
                onCardInputDone(
                    cardInputResultData
                )
                dismiss()

            } else if (!creditCardValid){
                cardNumberInputLayout.isErrorEnabled = true
                cardNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                cardNumberInputLayout.error = getString(R.string.transactionIncorrectCardInformation)
            }
        }

        showRecurrentSwitch.thumbTintList = ColorStateList.valueOf(Color.parseColor("#F8F9F9"))
        showRecurrentSwitch.trackTintList = ColorStateList.valueOf(Color.parseColor("#A6ACAF"))
        showRecurrentSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showRecurrentSwitch.thumbTintList =
                    ColorStateList.valueOf(Color.parseColor("#2ECC71"))
                showRecurrentSwitch.trackTintList =
                    ColorStateList.valueOf(Color.parseColor("#A6ACAF"))
            } else {
                showRecurrentSwitch.thumbTintList =
                    ColorStateList.valueOf(Color.parseColor("#F8F9F9"))
                showRecurrentSwitch.trackTintList =
                    ColorStateList.valueOf(Color.parseColor("#A6ACAF"))
            }
        }

        closeBtn.setOnClickListener {
            dismiss()
        }

        pinNumberInput.addTextChangedListener(pinWatcher)

        cardNumberInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                validateCardNumber(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, removed: Int, added: Int) {

            }
        })

        if (showRecurrentOption) {
            showRecurrentTV.visibility = View.VISIBLE
            showRecurrentSwitch.visibility = View.VISIBLE
        } else {
            showRecurrentTV.visibility = View.INVISIBLE
            showRecurrentSwitch.visibility = View.INVISIBLE
        }
        customizeForm()
        setupUserFontResource()
        return formView
    }

    fun validateCardNumber(cardNr: String) {
        val currentCardNr = cardNr
        if (currentCardNr.length >= 13) {
            cardNumberInputLayout.isErrorEnabled = false
            cardNRHintTV.setTextColor(colorCodeHint)
            isCardValid = VerifoneCardValidator.validateCardNumber(currentCardNr)
        } else {
            if (showFieldErrors) {
                cardNumberInputLayout.isErrorEnabled = true
                cardNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                cardNumberInputLayout.error = getString(R.string.lengthNotValid)
            }
            isCardValid = false
        }
    }

    private val pinWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val pinString = s.toString()
            if (!showFieldErrors) return
            if (pinString.length in secureCodeLength..secureCodeMaxLength) {
                pinNumberInputLayout.isErrorEnabled = false
                pinNRHintTV.setTextColor(colorCodeHint)
            } else {
                pinNRHintTV.setTextColor(resources.getColor(R.color.error_red))
                pinNumberInputLayout.isErrorEnabled = true
                pinNumberInputLayout.error = getString(R.string.giftCardFormPinError)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
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
        if (callbackKeeper == null) dismiss()
    }

    private fun customizeForm() {
        try {
            colorCodeContainer = Color.parseColor(customizationData.paymentFormBackground)
            mainContainer.backgroundTintList = ColorStateList.valueOf(colorCodeContainer)

        } catch (e: Exception) { }

        try {
            colorCodeTextInputField = Color.parseColor(customizationData.formTextFieldsBackground)
            cardNumberInputLayout.setBoxBackgroundColorStateList(
                ColorStateList.valueOf(
                    colorCodeTextInputField
                )
            )

            pinNumberInputLayout.setBoxBackgroundColorStateList(
                ColorStateList.valueOf(
                    colorCodeTextInputField
                )
            )
            showRecurrentTV.backgroundTintList = ColorStateList.valueOf(colorCodeTextInputField)

        } catch (e: java.lang.Exception) {
        }

        try {
            colorCodeInputText = Color.parseColor(customizationData.formInputTextColor)
            cardNumberInput.setTextColor(ColorStateList.valueOf(colorCodeInputText))
            pinNumberInput.setTextColor(ColorStateList.valueOf(colorCodeInputText))

        } catch (e: java.lang.Exception) {
        }

        try {
            colorCodeHint = Color.parseColor(customizationData.hintTextColor)
            expiryDateHintTV.setTextColor(ColorStateList.valueOf(colorCodeHint))
            cardNRHintTV.setTextColor(ColorStateList.valueOf(colorCodeHint))
            pinNRHintTV.setTextColor(ColorStateList.valueOf(colorCodeHint))
            showRecurrentTV.setTextColor(colorCodeHint)
        } catch (e: java.lang.Exception) {
        }

        try {
            val colorCodeSeparator: Int = Color.parseColor("#A6ACAF")
            separatorView.backgroundTintList = ColorStateList.valueOf(colorCodeSeparator)
        } catch (e: java.lang.Exception) {
        }

        try {
            colorCodePayBtn = Color.parseColor(customizationData.payButtonColor)
            payButton.backgroundTintList = ColorStateList.valueOf(colorCodePayBtn)
        } catch (e: java.lang.Exception) {
        }

        try {
            colorCodeTitle = Color.parseColor(customizationData.formTitleTextColor)
            formTitle.setTextColor(ColorStateList.valueOf(colorCodeTitle))
        } catch (e: java.lang.Exception) {
        }
    }

    private fun hideKeyboard(v: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun setupUserFontResource() {
        if (customizationData.userTextFontRes > 0) {
            cardNumberInputLayout.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            cardNRHintTV.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            cardNumberInput.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            formTitle.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            pinNumberInputLayout.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            pinNRHintTV.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            pinNumberInput.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)

            showRecurrentTV.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
            payButton.typeface =
                ResourcesCompat.getFont(requireContext(), customizationData.userTextFontRes)
        }
    }

    private fun setupLogoText() {

        val temp = requireContext().getString(R.string.footerText).replace("Verifone", "")
        textLogoTV.text = temp
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(VerifonePaymentForm.keyTextFontID, customizationData.userTextFontRes)
        outState.putString(
            VerifonePaymentForm.keyFormBackground,
            customizationData.paymentFormBackground
        )
        outState.putString(
            VerifonePaymentForm.keyTextFieldBackground,
            customizationData.formTextFieldsBackground
        )
        outState.putString(VerifonePaymentForm.keyPayButtonColor, customizationData.payButtonColor)
        outState.putString(VerifonePaymentForm.keyHintColor, customizationData.hintTextColor)
        outState.putString(
            VerifonePaymentForm.keyInputTextColor,
            customizationData.formInputTextColor
        )
        outState.putString(
            VerifonePaymentForm.keyTitleTextColor,
            customizationData.formTitleTextColor
        )
        outState.putString(VerifonePaymentForm.keyDisplayPrice, price)
        outState.putString(VerifonePaymentForm.keyMerchantPublicKey, publicKey)
        outState.putBoolean(VerifonePaymentForm.keyShowRecurrentPaymentsOption, showRecurrentOption)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val temp = savedInstanceState?.getInt(VerifonePaymentForm.keyTextFontID) ?: -1
        if (temp > 0) customizationData.userTextFontRes = temp

        var tempValue =
            savedInstanceState?.getString(VerifonePaymentForm.keyFormBackground, "") ?: ""
        if (tempValue.isNotEmpty()) {
            customizationData.paymentFormBackground = tempValue
        }

        tempValue =
            savedInstanceState?.getString(VerifonePaymentForm.keyTextFieldBackground, "") ?: ""
        if (tempValue.isNotEmpty()) {
            customizationData.formTextFieldsBackground = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyPayButtonColor, "") ?: ""
        if (tempValue.isNotEmpty()) {
            customizationData.payButtonColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyHintColor, "") ?: ""
        if (tempValue.isNotEmpty()) {
            customizationData.hintTextColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyInputTextColor, "") ?: ""
        if (tempValue.isNotEmpty()) {
            customizationData.formInputTextColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyTitleTextColor, "") ?: ""
        if (tempValue.isNotEmpty()) {
            customizationData.formTitleTextColor = tempValue
        }

        tempValue = savedInstanceState?.getString(VerifonePaymentForm.keyDisplayPrice, "") ?: ""
        if (tempValue.isNotEmpty()) {
            price = tempValue
            val showPrice = getString(R.string.submitPay) + " " + price
            payButton.text = showPrice
        }

        tempValue =
            savedInstanceState?.getString(VerifonePaymentForm.keyMerchantPublicKey, "") ?: ""
        if (tempValue.isNotEmpty()) {
            publicKey = tempValue
        }

        val showRecurrentSaved =
            savedInstanceState?.containsKey(VerifonePaymentForm.keyShowRecurrentPaymentsOption)
                ?: false
        if (showRecurrentSaved) {
            showRecurrentOption = savedInstanceState?.getBoolean(
                VerifonePaymentForm.keyShowRecurrentPaymentsOption,
                false
            ) ?: false
            if (showRecurrentOption) {
                showRecurrentTV.visibility = View.VISIBLE
                showRecurrentSwitch.visibility = View.VISIBLE
            } else {
                showRecurrentTV.visibility = View.INVISIBLE
                showRecurrentSwitch.visibility = View.INVISIBLE
            }
        }

        setupUserFontResource()
        customizeForm()
    }
}