package com.example.wellfish.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.wellfish.R
import java.util.regex.Pattern

class InputUsername @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    init {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isValidUsername(s.toString())) {
                    error = context.getString(R.string.invalid_username)
                } else {
                    error = null
                }
            }
        })
    }

    private fun isValidUsername(username: String): Boolean {
        // Username pattern: letters, numbers, underscores, and dashes
        val usernamePattern = "^[a-zA-Z0-9_-]*$"
        val pattern = Pattern.compile(usernamePattern)
        return pattern.matcher(username).matches() && username.isNotEmpty()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}
