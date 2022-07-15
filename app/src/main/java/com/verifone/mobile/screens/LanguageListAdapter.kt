package com.verifone.mobile.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.verifone.mobile.R

class LanguageListAdapter: RecyclerView.Adapter<LanguageListAdapter.LanguageViewHolder>() {

    class LanguageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var selectedIndicator:RadioButton = itemView.findViewById(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val itmLayout:View = LayoutInflater.from(parent.context).inflate(R.layout.language_item,null)
        return LanguageViewHolder(itmLayout)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }
}