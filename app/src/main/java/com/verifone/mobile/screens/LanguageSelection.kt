package com.verifone.mobile.screens

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.size
import com.verifone.mobile.R
import java.util.*

class LanguageSelection:AppCompatActivity() {

    //lateinit var languageArrayList:ArrayList<String>
    //lateinit var languageRecycleView:RecyclerView
    companion object {
        val reqCode = 2314
        val resultCode = 1
        val keySelectedLand = "selected_lang"
    }
    enum class LanguageCodes{
        EN,
        FR,
        AR,
        DA,
        DE,
        ET,
        FI,
        IW,
        IS,
        IT,
        LT,
        LV,
        NO,
        PL,
        PT,
        RU,
        SV
    }

    enum class LanguageNames{
        English,
        French,
        Arabic,
        Danish,
        German,
        Estonian,
        Finish,
        Hebrew,
        Icelandic,
        Italian,
        Lithuanian,
        Latvian,
        Norwegian,
        Polish,
        Portuguese,
        Russian,
        Swedish
    }

    lateinit var radioContainerLang:RadioGroup
    lateinit var languageOption1:RadioButton
    lateinit var languageOption2:RadioButton
    lateinit var languageOption3:RadioButton
    lateinit var languageOption4:RadioButton
    lateinit var languageOption5:RadioButton
    lateinit var languageOption6:RadioButton
    lateinit var languageOption7:RadioButton
    lateinit var languageOption8:RadioButton
    lateinit var languageOption9:RadioButton
    lateinit var languageOption10:RadioButton
    lateinit var languageOption11:RadioButton
    lateinit var languageOption12:RadioButton
    lateinit var languageOption13:RadioButton
    lateinit var languageOption14:RadioButton
    lateinit var languageOption15:RadioButton
    lateinit var languageOption16:RadioButton
    lateinit var languageOption17:RadioButton
    lateinit var okButton:AppCompatButton
    lateinit var cancelButton:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocation()
        setContentView(R.layout.lang_selection_screen)

        radioContainerLang = findViewById(R.id.language_options)
        languageOption1 = findViewById(R.id.language_option1)
        languageOption2 = findViewById(R.id.language_option2)
        languageOption3 = findViewById(R.id.language_option3)
        languageOption4 = findViewById(R.id.language_option4)
        languageOption5 = findViewById(R.id.language_option5)
        languageOption6 = findViewById(R.id.language_option6)
        languageOption7 = findViewById(R.id.language_option7)
        languageOption8 = findViewById(R.id.language_option8)
        languageOption9 = findViewById(R.id.language_option9)
        languageOption10 = findViewById(R.id.language_option10)
        languageOption11 = findViewById(R.id.language_option11)
        languageOption12 = findViewById(R.id.language_option12)
        languageOption13 = findViewById(R.id.language_option13)
        languageOption14 = findViewById(R.id.language_option14)
        languageOption15 = findViewById(R.id.language_option15)
        languageOption16 = findViewById(R.id.language_option16)
        languageOption17 = findViewById(R.id.language_option17)

        languageOption1.text = LanguageCodes.EN.toString()+" ("+LanguageNames.English+")"
        languageOption2.text = LanguageCodes.AR.toString()+" ("+LanguageNames.Arabic+")"
        languageOption3.text = LanguageCodes.DA.toString()+" ("+LanguageNames.Danish+")"
        languageOption4.text = LanguageCodes.DE.toString()+" ("+LanguageNames.German+")"
        languageOption5.text = LanguageCodes.FI.toString()+" ("+LanguageNames.Finish+")"
        languageOption6.text = LanguageCodes.FR.toString()+" ("+LanguageNames.French+")"
        languageOption7.text = LanguageCodes.ET.toString()+" ("+LanguageNames.Estonian+")"
        languageOption8.text = LanguageCodes.IW.toString()+" ("+LanguageNames.Hebrew+")"
        languageOption9.text = LanguageCodes.IS.toString()+" ("+LanguageNames.Icelandic+")"
        languageOption10.text = LanguageCodes.IT.toString()+" ("+LanguageNames.Italian+")"
        languageOption11.text = LanguageCodes.LT.toString()+" ("+LanguageNames.Lithuanian+")"
        languageOption12.text = LanguageCodes.LV.toString()+" ("+LanguageNames.Latvian+")"
        languageOption13.text = LanguageCodes.NO.toString()+" ("+LanguageNames.Norwegian+")"
        languageOption14.text = LanguageCodes.PL.toString()+" ("+LanguageNames.Polish+")"
        languageOption15.text = LanguageCodes.PT.toString()+" ("+LanguageNames.Portuguese+")"
        languageOption16.text = LanguageCodes.RU.toString()+" ("+LanguageNames.Russian+")"
        languageOption17.text = LanguageCodes.SV.toString()+" ("+LanguageNames.Swedish+")"

        var selectedLang = CustomizationSettings.getStoredLanguage(this)
        if (selectedLang.isEmpty()){
            selectedLang = "EN (English)"
        }
        for (i in 0..16) {
            val vt = radioContainerLang.getChildAt(i) as RadioButton
            if (vt.text == selectedLang){
                radioContainerLang.check(vt.id)
            }
        }

        okButton = findViewById(R.id.select_ok_btn)
        cancelButton = findViewById(R.id.select_cancel_btn)

        okButton.setOnClickListener {
            val retData  = Intent()
            retData.putExtra(keySelectedLand,getLangSelection())
            setResult(resultCode,retData)
            finish()
        }
        cancelButton.setOnClickListener {
            setResult(0)
            finish()
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

    fun getLangSelection():String {
        val temp = findViewById<RadioButton>(radioContainerLang.checkedRadioButtonId)
        return temp.text.toString()
    }
}