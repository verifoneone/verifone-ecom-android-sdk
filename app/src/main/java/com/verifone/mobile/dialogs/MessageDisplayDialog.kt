package com.verifone.mobile.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.verifone.mobile.R
import kotlinx.android.synthetic.main.error_screen.view.*
import kotlinx.android.synthetic.main.message_dialog_layout.view.*

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
        view.tvMessage.text = arguments?.getString(KEY_MESSAGE)
        view.tvMessage2.text = arguments?.getString(KEY_MESSAGE2)
        view.close_btn.setOnClickListener {
            dismiss()
        }
    }

}