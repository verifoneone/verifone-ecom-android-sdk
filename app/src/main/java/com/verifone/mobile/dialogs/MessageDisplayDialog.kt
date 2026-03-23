package com.verifone.mobile.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.verifone.mobile.R

class MessageDisplayDialog: DialogFragment() {

    companion object {

        const val TAG = "MessageDialog"
        private const val KEY_MESSAGE2 = "KEY_MESSAGE_CONTENT2"
        private const val KEY_MESSAGE = "KEY_MESSAGE_CONTENT"

        fun newInstance(message1: String, message2: String): MessageDisplayDialog {
            val args = Bundle()
            args.putString(KEY_MESSAGE2, message1)
            args.putString(KEY_MESSAGE, message2)
            val fragment = MessageDisplayDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.message_dialog_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        //setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupView(view: View) {
        view.findViewById<AppCompatTextView>(R.id.tvMessage).text = arguments?.getString(KEY_MESSAGE)
        view.findViewById<AppCompatTextView>(R.id.tvMessage2).text = arguments?.getString(KEY_MESSAGE2)
        view.findViewById<AppCompatButton>(R.id.close_btn).setOnClickListener {
            dismiss()
        }
    }

}