package me.ako.yts.presentation.view.custom

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.StaticLayout
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.core.text.toSpannable
import com.google.android.material.textview.MaterialTextView

class ExpandableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {
    // width = line width * line count - expand text width
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val lineWidth = givenWidth - compoundPaddingStart - compoundPaddingEnd
        val expandText = "See more"
        val expandTextWidth = paint.measureText(expandText)

        val staticLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, lineWidth)
            .setMaxLines(maxLines)
            .setIncludePad(false)
            .setEllipsize(ellipsize)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            .build()

        val sumLineWidth = (0 until staticLayout.lineCount).sumOf {
            staticLayout.getLineWidth(it).toDouble()
        }

        val truncatedTextWidth = sumLineWidth.toFloat() - expandTextWidth
        val truncatedText = TextUtils.ellipsize(text, paint, truncatedTextWidth, TextUtils.TruncateAt.END)
        val span = "$truncatedText $expandText".toSpannable()
        val i = span.indexOf(expandText)
        span.setSpan(ForegroundColorSpan(Color.GREEN), i, i + expandText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = span

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}