package com.verifone.mobile.dialogs


import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.verifone.mobile.R
import com.verifone.mobile.dataobjects.CurrencyAdapterItem

class CurrencySelectorAdapter(ctx:Context, itemResourceLayout:Int, currencyObjects:List<CurrencyAdapterItem>): ArrayAdapter<CurrencyAdapterItem>(ctx,itemResourceLayout,currencyObjects) {
    private val mContext = ctx
    private val mLayoutResourceId = itemResourceLayout
    private val currenciesSubSet: MutableList<CurrencyAdapterItem> = ArrayList(currencyObjects)
    private var allCurrencies: List<CurrencyAdapterItem> = ArrayList(currencyObjects)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var elementView = convertView
        if (elementView == null) {
            val inflater = (mContext as Activity).layoutInflater
            elementView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val currencyItm = getItem(position)
            val currencyAutoCompleteView = elementView!!.findViewById<View>(R.id.currency_view_name) as TextView
            currencyAutoCompleteView.text = currencyItm?.getCurrencyName()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return elementView!!

    }

    override fun getCount(): Int {
        return currenciesSubSet.size
    }

    override fun getItem(position: Int): CurrencyAdapterItem? {
        return currenciesSubSet[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any) :String {
                return (resultValue as CurrencyAdapterItem).getCurrencyName()
            }
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val currencySuggestion: MutableList<CurrencyAdapterItem> = ArrayList()
                    for (currency in allCurrencies) {
                        if (currency.getCurrencyName().lowercase().startsWith(constraint.toString().lowercase())
                        ) {
                            currencySuggestion.add(currency)
                        }
                    }
                    filterResults.values = currencySuggestion
                    filterResults.count = currencySuggestion.size
                }
                return filterResults
            }
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                currenciesSubSet.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is CurrencyAdapterItem) {
                            currenciesSubSet.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    currenciesSubSet.addAll(allCurrencies)
                    notifyDataSetInvalidated()
                }
            }
        }
    }

}