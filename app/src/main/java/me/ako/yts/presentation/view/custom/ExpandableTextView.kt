package me.ako.yts.presentation.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import com.google.android.material.textview.MaterialTextView
import me.ako.yts.R

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var originalText: CharSequence? = null
    private var trimmedText: CharSequence? = null
    private var trimLength = 100
    private var maxLength = 100
    private var seeMoreText = "See more"
    private var isTrimmed = false

    init {
        setOnClickListener {
            if(originalText!!.length > maxLength) {
                isTrimmed = !isTrimmed
                super.setText(getDisplayableText(), BufferType.SPANNABLE)
            }
        }
    }

    private fun getDisplayableText(): CharSequence? {
        return if (isTrimmed) {
            SpannableStringBuilder()
                .append(trimmedText)
                .append("... ")
                .append(
                    seeMoreText,
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary)),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
        } else {
            originalText
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val textViewWidth = measuredWidth
        val charWidth = paint.measureText("x")
        maxLength = (textViewWidth / charWidth).toInt() * 3
        trimLength = maxLength - 4 - seeMoreText.length
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        originalText = text
        trimmedText = getTrimmedText(text)
        super.setText(getDisplayableText(), type)
    }

    private fun getTrimmedText(text: CharSequence?): CharSequence? {
        return if (text != null && text.length > maxLength) {
            isTrimmed = true
            text.subSequence(0, trimLength)
        } else {
            isTrimmed = false
            text
        }
    }
}