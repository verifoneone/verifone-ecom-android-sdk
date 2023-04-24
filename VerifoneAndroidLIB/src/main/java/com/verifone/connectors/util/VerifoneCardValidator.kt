package com.verifone.connectors.util

import java.util.*
import java.util.regex.Pattern

internal class VerifoneCardValidator {

    enum class Cards(
        brandName: String,
        pattern: String,
        format: String,
        cardLength: IntArray,
        cvvLength: IntArray,
        luhn: Boolean,
        supported: Boolean
    ) {
        MAESTRO(
            "maestro", "^(5[06-9]|6[37])[0-9]{10,17}$", DEFAULT_CARD_FORMAT, intArrayOf(
                12,
                13,
                14,
                15,
                16,
                17,
                18,
                19
            ), intArrayOf(3), true, true
        ),
        MASTERCARD(
            "mastercard", "^5[0-5][0-9]{14}$", DEFAULT_CARD_FORMAT, intArrayOf(16, 17),
            intArrayOf(3), true, true
        ),
        VISA(
            "visa", "^4[0-9]{12}(?:[0-9]{3})?$", DEFAULT_CARD_FORMAT, intArrayOf(13, 16),
            intArrayOf(3), true, true
        ),
        JCB("jcb","^(?:2131|1800|35[0-9]{3})[0-9]{11}$", DEFAULT_CARD_FORMAT, intArrayOf(16),
            intArrayOf(3),true,true
        ),
        DINERSCLUB("dinersclub","^3(?:0[0-5]|[68][0-9])?[0-9]{11}$","(\\d{1,4})(\\d{1,6})?(\\d{1,4})?",
            intArrayOf(14), intArrayOf(3),true,true
        ),
        DISCOVER(
            "discover", "^6(?:011|5[0-9]{2})[0-9]{12}$", DEFAULT_CARD_FORMAT, intArrayOf(16),
            intArrayOf(3), true, true
        ),NOCARD("", "", DEFAULT_CARD_FORMAT, intArrayOf(16),
            intArrayOf(3), true, true
        ),AMEX("amex", "^3[47][0-9]{13}$", "^(\\d{1,4})(\\d{1,6})?(\\d{1,5})?$", intArrayOf(15),
            intArrayOf(4),true,true);

        var cardBrandName: String = ""
        var cardNRPattern: String = ""
        var cardFormat: String = ""
        var cardNRLength: IntArray = intArrayOf()
        var cvvNRLength: IntArray = intArrayOf()
        var luhnAlgorithm = false
        var isSupported = false

        init {
            cardBrandName = name
            cardNRPattern = pattern
            cardFormat = format
            cardNRLength = cardLength
            cvvNRLength = cvvLength
            luhnAlgorithm = luhn
            isSupported = supported
        }

    }

    companion object {
        const val DEFAULT_CARD_FORMAT = "(\\d{1,4})"

        fun getCardType(cardNumber: String):Cards {
            val temp = sanitizeEntry(cardNumber, true)
            if (Pattern.matches("^(54)", temp) && temp.length > 16) {
                return Cards.MAESTRO
            }

            val cards = Cards.values()
            for (i in cards.indices) {
                if (Pattern.matches(cards[i].cardNRPattern, temp)) {
                    return cards[i]
                }
            }
            return Cards.MAESTRO
        }

        fun validateCardNumber(number: String):Boolean {
            if (number == "") return false
            val cardNR = sanitizeEntry(number, true)
            if (Pattern.matches("^\\d+$", cardNR)) {
                val c = getCardType(cardNR)
                if (c != null) {
                    var len = false
                    for (i in c.cardNRLength.indices) {
                        if (c.cardNRLength[i] == cardNR.length) {
                            len = true
                            break
                        }
                    }
                    return len && (!c.luhnAlgorithm || validateLuhnNumber(cardNR))
                }
            }
            return false
        }

        private fun sanitizeEntry(entry: String, isNumber: Boolean): String {
            return if (isNumber) entry.replace(
                "\\D".toRegex(),
                ""
            ) else entry.replace("\\s+|-".toRegex(), "")
        }

        private fun validateLuhnNumber(num: String): Boolean {
            var num = num
            if (num == "") return false
            var nCheck = 0
            var nDigit = 0
            var bEven = false
            num = sanitizeEntry(num, true)
            for (i in num.length - 1 downTo 0) {
                nDigit = num[i].toString().toInt()
                if (bEven) {
                    if (2.let { nDigit *= it; nDigit } > 9) nDigit -= 9
                }
                nCheck += nDigit
                bEven = !bEven
            }
            return nCheck % 10 == 0
        }

        fun validateExpiryDate(month: Int, year: Int): Boolean {
            if (month < 1 || year < 1) return false
            val cal = Calendar.getInstance()
            val currentMonth = cal[Calendar.MONTH] + 1
            val currentYear = cal[Calendar.YEAR]

            if (month > 12) return false
            if (year>2099) return false
            return if (currentYear == year) {
                currentMonth <= month
            } else {
                currentYear < year
            }
        }

        fun validateCVV(cvv: String, card: Cards?): Boolean {
            if (cvv == "" || card == null) return false
            for (i in card.cvvNRLength.indices) {
                if (card.cvvNRLength[i] == cvv.length) return true
            }
            return false
        }

    }
}