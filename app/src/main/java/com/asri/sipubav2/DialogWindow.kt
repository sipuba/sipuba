package com.asri.sipubav2
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DialogWindow : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())

        builder.setMessage("AWAS !Terdeteksi Ketinggian Air Melewati Batas")
            .setPositiveButton("OK") { dialog, id ->
            }
            .setNegativeButton("Cancel") { dialog, id ->
            }

        return builder.create()
    }
}