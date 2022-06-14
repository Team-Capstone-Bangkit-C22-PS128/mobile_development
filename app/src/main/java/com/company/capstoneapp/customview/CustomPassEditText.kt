package com.company.capstoneapp.customview

import android.content.Context
import android.graphics.Rect
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.company.capstoneapp.R


class CustomPassEditText : AppCompatEditText, View.OnTouchListener {

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
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
            setError(if (text.toString().trim().length < 6) {
                "Password must contain at least 6 character"
            } else {
                null
            }, null)
        background = if (error != null){
            resources.getDrawable(R.drawable.custom_edit_text_error)
        }else{
            resources.getDrawable(R.drawable.custom_edit_text)
        }

    }

}