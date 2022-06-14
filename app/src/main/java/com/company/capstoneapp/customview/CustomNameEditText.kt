package com.company.capstoneapp.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.company.capstoneapp.R


class CustomNameEditText : AppCompatEditText, View.OnTouchListener {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init()
    }

    private fun init() {
        setOnTouchListener(this)
        inputType = InputType.TYPE_CLASS_TEXT
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (lengthBefore > lengthAfter || lengthAfter > lengthBefore) {
            setError(if (text.toString().trim().length < 3) {
                "Name must contain at least 3 character"
            }else if (text.toString().contains(Regex("[^A-Za-z ]"))) {
                "Name must not contain any Number or symbol"
            }
            else {
                null
            }, null)
        }
        background = if (error != null){
            resources.getDrawable(R.drawable.custom_edit_text_error)
        }else{
            resources.getDrawable(R.drawable.custom_edit_text)
        }
    }

}