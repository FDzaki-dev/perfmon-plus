package xzr.perfmon

import android.content.Context

object Tools {
    @JvmStatic
    fun format_ify_add_blank(text: String): String =
        "  ".repeat((5 - text.length).coerceAtLeast(0))

    @JvmStatic
    fun bool2text(bool: Boolean, context: Context): String =
        context.resources.getString(if (bool) R.string.yes else R.string.no)
}
