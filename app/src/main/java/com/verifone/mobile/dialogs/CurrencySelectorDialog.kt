package com.verifone.mobile.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.verifone.mobile.R
import com.verifone.mobile.dataobjects.CurrencyAdapterItem

class CurrencySelectorDialog(onCurrencyFunction: (currencyStr:String) -> Unit): DialogFragment() {
    private lateinit var currencyList: ArrayList<CurrencyAdapterItem>
    private lateinit var currencySpinner: MaterialAutoCompleteTextView
    private lateinit var mOkButton:AppCompatButton
    private lateinit var mCancelButton:AppCompatButton
    val onCurrencyInput = onCurrencyFunction
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fillCountryList()
        val vv = inflater.inflate(R.layout.currency_selector_layout, container, false)
        currencySpinner = vv.findViewById(R.id.currencyAutoCompleteView)
        mOkButton = vv.findViewById(R.id.selectCurrencyOK)
        mCancelButton = vv.findViewById(R.id.selectCurrencyCancel)
        initAutocompleteView()


        mOkButton.setOnClickListener {
            if (validateInputCurrency(currencySpinner.text.toString())) {
                onCurrencyInput(currencySpinner.text.toString())
            } else {
                Toast.makeText(activity,R.string.invalid_currency_str,Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
        mCancelButton.setOnClickListener {
            dismiss()
        }
        return vv

    }

    override fun onResume() {
        super.onResume()
        currencySpinner.text.clear()
    }

    private fun validateInputCurrency(inputCurrency:String):Boolean {
        for (itm in currencyList) {
            if (inputCurrency == itm.getCurrencyName()){
                return true
            }
        }
        return false
    }

    private fun initAutocompleteView(){
        context?.let { ctx ->
            val cAdapter =
                CurrencySelectorAdapter(ctx, R.layout.currency_item_layout, currencyList)
            currencySpinner.setAdapter(cAdapter)
            currencySpinner.setOnItemClickListener { parent, _, position, _ ->
                val currencyItem = cAdapter.getItem(position) as CurrencyAdapterItem?
                currencySpinner.setText(currencyItem?.getCurrencyName())
            }
        }
    }

    private fun fillCountryList() {
        currencyList = ArrayList()
        currencyList.add(CurrencyAdapterItem("AED"))
        currencyList.add(CurrencyAdapterItem("AFN"))
        currencyList.add(CurrencyAdapterItem("ALL"))
        currencyList.add(CurrencyAdapterItem("AMD"))
        currencyList.add(CurrencyAdapterItem("ANG"))
        currencyList.add(CurrencyAdapterItem("AOA"))
        currencyList.add(CurrencyAdapterItem("ARS"))
        currencyList.add(CurrencyAdapterItem("AUD"))
        currencyList.add(CurrencyAdapterItem("AWG"))
        currencyList.add(CurrencyAdapterItem("AZN"))
        currencyList.add(CurrencyAdapterItem("BAM"))
        currencyList.add(CurrencyAdapterItem("BBD"))
        currencyList.add(CurrencyAdapterItem("BDT"))
        currencyList.add(CurrencyAdapterItem("BGN"))
        currencyList.add(CurrencyAdapterItem("BHD"))
        currencyList.add(CurrencyAdapterItem("BIF"))
        currencyList.add(CurrencyAdapterItem("BMD"))
        currencyList.add(CurrencyAdapterItem("BND"))
        currencyList.add(CurrencyAdapterItem("BOB"))
        currencyList.add(CurrencyAdapterItem("BOV"))
        currencyList.add(CurrencyAdapterItem("BRL"))
        currencyList.add(CurrencyAdapterItem("BSD"))
        currencyList.add(CurrencyAdapterItem("BTN"))
        currencyList.add(CurrencyAdapterItem("BWP"))
        currencyList.add(CurrencyAdapterItem("BYR"))
        currencyList.add(CurrencyAdapterItem("BZD"))
        currencyList.add(CurrencyAdapterItem("CAD"))
        currencyList.add(CurrencyAdapterItem("CDF"))
        currencyList.add(CurrencyAdapterItem("CHE"))
        currencyList.add(CurrencyAdapterItem("CHF"))
        currencyList.add(CurrencyAdapterItem("CHW"))
        currencyList.add(CurrencyAdapterItem("CLF"))
        currencyList.add(CurrencyAdapterItem("CLP"))
        currencyList.add(CurrencyAdapterItem("CNY"))
        currencyList.add(CurrencyAdapterItem("COP"))
        currencyList.add(CurrencyAdapterItem("COU"))
        currencyList.add(CurrencyAdapterItem("CRC"))
        currencyList.add(CurrencyAdapterItem("CUC"))
        currencyList.add(CurrencyAdapterItem("CVE"))
        currencyList.add(CurrencyAdapterItem("CZK"))
        currencyList.add(CurrencyAdapterItem("DJF"))
        currencyList.add(CurrencyAdapterItem("DKK"))
        currencyList.add(CurrencyAdapterItem("DOP"))
        currencyList.add(CurrencyAdapterItem("DZD"))
        currencyList.add(CurrencyAdapterItem("EGP"))
        currencyList.add(CurrencyAdapterItem("ERN"))
        currencyList.add(CurrencyAdapterItem("ETB"))
        currencyList.add(CurrencyAdapterItem("EUR"))
        currencyList.add(CurrencyAdapterItem("FJD"))
        currencyList.add(CurrencyAdapterItem("FKP"))
        currencyList.add(CurrencyAdapterItem("GBP"))
        currencyList.add(CurrencyAdapterItem("GEL"))
        currencyList.add(CurrencyAdapterItem("GHS"))
        currencyList.add(CurrencyAdapterItem("GIP"))
        currencyList.add(CurrencyAdapterItem("GMD"))
        currencyList.add(CurrencyAdapterItem("GNF"))
        currencyList.add(CurrencyAdapterItem("GTQ"))
        currencyList.add(CurrencyAdapterItem("GYD"))
        currencyList.add(CurrencyAdapterItem("HKD"))
        currencyList.add(CurrencyAdapterItem("HNL"))
        currencyList.add(CurrencyAdapterItem("HRK"))
        currencyList.add(CurrencyAdapterItem("HTG"))
        currencyList.add(CurrencyAdapterItem("HUF"))
        currencyList.add(CurrencyAdapterItem("IDR"))
        currencyList.add(CurrencyAdapterItem("ILS"))
        currencyList.add(CurrencyAdapterItem("INR"))
        currencyList.add(CurrencyAdapterItem("IQD"))
        currencyList.add(CurrencyAdapterItem("IRR"))
        currencyList.add(CurrencyAdapterItem("ISK"))
        currencyList.add(CurrencyAdapterItem("JMD"))
        currencyList.add(CurrencyAdapterItem("JOD"))
        currencyList.add(CurrencyAdapterItem("JPY"))
        currencyList.add(CurrencyAdapterItem("KES"))
        currencyList.add(CurrencyAdapterItem("KGS"))
        currencyList.add(CurrencyAdapterItem("KHR"))
        currencyList.add(CurrencyAdapterItem("KMF"))
        currencyList.add(CurrencyAdapterItem("KPW"))
        currencyList.add(CurrencyAdapterItem("KRW"))
        currencyList.add(CurrencyAdapterItem("KWD"))
        currencyList.add(CurrencyAdapterItem("KYD"))
        currencyList.add(CurrencyAdapterItem("KZT"))
        currencyList.add(CurrencyAdapterItem("LAK"))
        currencyList.add(CurrencyAdapterItem("LBP"))
        currencyList.add(CurrencyAdapterItem("LKR"))
        currencyList.add(CurrencyAdapterItem("LRD"))
        currencyList.add(CurrencyAdapterItem("LSL"))
        currencyList.add(CurrencyAdapterItem("LTL"))
        currencyList.add(CurrencyAdapterItem("LVL"))
        currencyList.add(CurrencyAdapterItem("LYD"))
        currencyList.add(CurrencyAdapterItem("MAD"))
        currencyList.add(CurrencyAdapterItem("MDL"))
        currencyList.add(CurrencyAdapterItem("MGA"))
        currencyList.add(CurrencyAdapterItem("MKD"))
        currencyList.add(CurrencyAdapterItem("MMK"))
        currencyList.add(CurrencyAdapterItem("MNT"))
        currencyList.add(CurrencyAdapterItem("MOP"))
        currencyList.add(CurrencyAdapterItem("MRO"))
        currencyList.add(CurrencyAdapterItem("MUR"))
        currencyList.add(CurrencyAdapterItem("MVR"))
        currencyList.add(CurrencyAdapterItem("MWK"))
        currencyList.add(CurrencyAdapterItem("MXN"))
        currencyList.add(CurrencyAdapterItem("MXV"))
        currencyList.add(CurrencyAdapterItem("MYR"))
        currencyList.add(CurrencyAdapterItem("MZN"))
        currencyList.add(CurrencyAdapterItem("NAD"))
        currencyList.add(CurrencyAdapterItem("NGN"))
        currencyList.add(CurrencyAdapterItem("NIO"))
        currencyList.add(CurrencyAdapterItem("NOK"))
        currencyList.add(CurrencyAdapterItem("NPR"))
        currencyList.add(CurrencyAdapterItem("NZD"))
        currencyList.add(CurrencyAdapterItem("OMR"))
        currencyList.add(CurrencyAdapterItem("PAB"))
        currencyList.add(CurrencyAdapterItem("PEN"))
        currencyList.add(CurrencyAdapterItem("PGK"))
        currencyList.add(CurrencyAdapterItem("PHP"))
        currencyList.add(CurrencyAdapterItem("PKR"))
        currencyList.add(CurrencyAdapterItem("PLN"))
        currencyList.add(CurrencyAdapterItem("PYG"))
        currencyList.add(CurrencyAdapterItem("QAR"))
        currencyList.add(CurrencyAdapterItem("RON"))
        currencyList.add(CurrencyAdapterItem("RSD"))
        currencyList.add(CurrencyAdapterItem("RUB"))
        currencyList.add(CurrencyAdapterItem("RWF"))
        currencyList.add(CurrencyAdapterItem("SAR"))
        currencyList.add(CurrencyAdapterItem("SBD"))
        currencyList.add(CurrencyAdapterItem("SCR"))
        currencyList.add(CurrencyAdapterItem("SDG"))
        currencyList.add(CurrencyAdapterItem("SEK"))
        currencyList.add(CurrencyAdapterItem("SGD"))
        currencyList.add(CurrencyAdapterItem("SHP"))
        currencyList.add(CurrencyAdapterItem("SLL"))
        currencyList.add(CurrencyAdapterItem("SOS"))
        currencyList.add(CurrencyAdapterItem("SRD"))
        currencyList.add(CurrencyAdapterItem("SSP"))
        currencyList.add(CurrencyAdapterItem("STD"))
        currencyList.add(CurrencyAdapterItem("SVC"))
        currencyList.add(CurrencyAdapterItem("SYP"))
        currencyList.add(CurrencyAdapterItem("SZL"))
        currencyList.add(CurrencyAdapterItem("THB"))
        currencyList.add(CurrencyAdapterItem("TJS"))
        currencyList.add(CurrencyAdapterItem("TMT"))
        currencyList.add(CurrencyAdapterItem("TND"))
        currencyList.add(CurrencyAdapterItem("TOP"))
        currencyList.add(CurrencyAdapterItem("TRY"))
        currencyList.add(CurrencyAdapterItem("TTD"))
        currencyList.add(CurrencyAdapterItem("TWD"))
        currencyList.add(CurrencyAdapterItem("TZS"))
        currencyList.add(CurrencyAdapterItem("UAH"))
        currencyList.add(CurrencyAdapterItem("UGX"))
        currencyList.add(CurrencyAdapterItem("USD"))
        currencyList.add(CurrencyAdapterItem("USN"))
        currencyList.add(CurrencyAdapterItem("USS"))
        currencyList.add(CurrencyAdapterItem("UYI"))
        currencyList.add(CurrencyAdapterItem("UYU"))
        currencyList.add(CurrencyAdapterItem("UZS"))
        currencyList.add(CurrencyAdapterItem("VEF"))
        currencyList.add(CurrencyAdapterItem("VND"))
        currencyList.add(CurrencyAdapterItem("VUV"))
        currencyList.add(CurrencyAdapterItem("WST"))
        currencyList.add(CurrencyAdapterItem("XAF"))
        currencyList.add(CurrencyAdapterItem("XAG"))
        currencyList.add(CurrencyAdapterItem("XAU"))
        currencyList.add(CurrencyAdapterItem("XBA"))
        currencyList.add(CurrencyAdapterItem("XBB"))
        currencyList.add(CurrencyAdapterItem("XBC"))
        currencyList.add(CurrencyAdapterItem("XBD"))
        currencyList.add(CurrencyAdapterItem("XCD"))
        currencyList.add(CurrencyAdapterItem("XDR"))
        currencyList.add(CurrencyAdapterItem("XOF"))
        currencyList.add(CurrencyAdapterItem("XPD"))
        currencyList.add(CurrencyAdapterItem("XPF"))
        currencyList.add(CurrencyAdapterItem("XPT"))
        currencyList.add(CurrencyAdapterItem("XTS"))
        currencyList.add(CurrencyAdapterItem("XXX"))
        currencyList.add(CurrencyAdapterItem("YER"))
        currencyList.add(CurrencyAdapterItem("ZAR"))
        currencyList.add(CurrencyAdapterItem("ZMK"))
        currencyList.add(CurrencyAdapterItem("ZMW"))
        currencyList.add(CurrencyAdapterItem("BTC"))
    }
}