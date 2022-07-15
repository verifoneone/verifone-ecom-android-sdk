package com.verifone.mobile.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.verifone.mobile.R
import kotlinx.android.synthetic.main.error_screen.view.*

class ErrorDisplayDialog: DialogFragment() {
    companion object {

        const val TAG = "ErrorDialog"

        private const val KEY_ERROR2 = "KEY_ERROR_CONTENT2"
        private const val KEY_ERROR = "KEY_ERROR_CONTENT"

        fun newInstance(error1: String, error2: String): ErrorDisplayDialog {
            val args = Bundle()
            args.putString(KEY_ERROR2, error2)
            args.putString(KEY_ERROR, error1)
            val fragment = ErrorDisplayDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.error_screen, container, false)
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
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun setupView(view: View) {
        view.tvError.text = arguments?.getString(KEY_ERROR)
        view.tvError2.text = arguments?.getString(KEY_ERROR2)
    }


}