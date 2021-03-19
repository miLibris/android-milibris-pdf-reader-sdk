package com.milibris.pdfreader.sampleapp.sharing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.text.HtmlCompat
import com.milibris.lib.pdfreader.PdfReader

object SharingUtils {

    fun shareArticleUrl(
        activity: Activity,
        issueMid: String,
        kioskURL: String,
        article: PdfReader.Article
    ) {

        val articleUrl = "$kioskURL/share/article/$issueMid/${article.mid}"
        val intentBuilder = IntentBuilder.from(activity)
            .setType("text/plain")
            .setText(articleUrl)
        intentBuilder.startChooser()
    }

    fun shareArticleViaMail(context: Context, article: PdfReader.Article) {


        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject(article))

        val contentBuilder =
            StringBuilder()
                .append("I thought you might be interested in this article:<br/><br/>")
        article.title?.let {
            contentBuilder.append("$it<br/><br/>")
        }
        val content = article.content
        val startHead = content.indexOf("<head>")
        val endHead = content.indexOf("</head>") + 7
        val contentNoHead = String.format(
            "%s%s",
            content.substring(0, startHead),
            content.substring(endHead)
        )
        contentBuilder.append(contentNoHead)


        intent.putExtra(
            Intent.EXTRA_TEXT, HtmlCompat.fromHtml(
                contentBuilder.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    private fun mailSubject(article: PdfReader.Article): String {
        // article_title (edition_title, edition_subtitle)
        return when {
            article.title.isNullOrEmpty() -> ""
            article.editionSubtitle.isNullOrEmpty() -> {
                "${article.title} (${article.editionTitle})"
            }
            else -> {
                "${article.title} (${article.editionTitle}, ${article.editionSubtitle})"
            }
        }
    }
}