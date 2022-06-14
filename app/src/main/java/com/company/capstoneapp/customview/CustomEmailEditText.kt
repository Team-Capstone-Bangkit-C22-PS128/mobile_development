package com.company.capstoneapp.customview

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.company.capstoneapp.R

class CustomEmailEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable

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
    ) {
        init()
    }

    private fun init() {
        clearButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
        setOnTouchListener(this)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        inputType = InputType.TYPE_CLASS_TEXT
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) {
            setError(if (!isEmailValid(text)) {
                "Please input the right format of email"
            } else {
                null
            }
            , null)
        }
        background = if (error != null){
            resources.getDrawable(R.drawable.custom_edit_text_error)
        }else{
            resources.getDrawable(R.drawable.custom_edit_text)
        }

    }

    private fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}