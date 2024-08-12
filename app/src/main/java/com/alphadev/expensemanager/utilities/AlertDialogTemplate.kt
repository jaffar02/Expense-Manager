package com.alphadev.expensemanager.utilities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AlertDialogTemplate(context: Context) {
    inner class CreateLottieDialog{
        private var context: Context
        private lateinit var dialog: Dialog
        private lateinit var view: View
        private lateinit var childView: View
        private var inflater: LayoutInflater
        private var isCancelAble = false
        private var alertDialog: AlertDialog.Builder

        public constructor(context: Context){
            this.context = context
            alertDialog = AlertDialog.Builder(context)
            inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        public fun setLayout(layout: Int){
            view = inflater.inflate(layout, null)
            alertDialog.setView(view)
            dialog = alertDialog.create()
            dialog.setCancelable(isCancelAble)
        }

        public fun showDialog(){
            dialog.show()
        }

        public fun getDialogObject(): Dialog {
            return dialog
        }

        public fun setCancelAble(isCancelAble: Boolean) {
            this.isCancelAble = isCancelAble
        }

        public fun addChildView(resourceId: Int) : View{
            childView = view.findViewById(resourceId)
            return childView
        }

        public fun addChildMaterialCardView(resourceId: Int) : MaterialCardView? {
            childView = view.findViewById<MaterialCardView>(resourceId)
            return childView as MaterialCardView?
        }

        public fun addChildTextView(resourceId: Int): TextView {
            val tv = view.findViewById<TextView>(resourceId)
            return tv
        }

        public fun addChildEditText(resourceId: Int): EditText {
            val tv = view.findViewById<EditText>(resourceId)
            return tv
        }

        public fun addChildImageView(resourceId: Int): ImageView {
            val tv = view.findViewById<ImageView>(resourceId)
            return tv
        }

        public fun addChildMaterialButton(resourceId: Int): MaterialButton {
            val tv = view.findViewById<MaterialButton>(resourceId)
            return tv
        }

        public fun dismissDialog(){
            dialog.dismiss()
        }
    }

}