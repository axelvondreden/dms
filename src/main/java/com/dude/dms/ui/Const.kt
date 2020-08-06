package com.dude.dms.ui

import java.util.*

object Const {
    const val PAGE_ADMIN = "admin"
    const val PAGE_ATTRIBUTE = "attribute"
    const val PAGE_DOCIMPORT = "import"
    const val PAGE_DOCS = "docs"
    const val PAGE_LOG = "log"
    const val PAGE_MAILS = "mails"
    const val PAGE_OPTIONS = "options"
    const val PAGE_RECYCLE = "recycle"
    const val PAGE_ROOT = ""
    const val PAGE_RULES = "rules"
    const val PAGE_TAG = "tag"

    @JvmField
    val LOCALES = arrayOf(
            Locale("de", "DE"),
            Locale("en", "US")
    )

    @JvmField
    val IMAGE_FORMATS = arrayOf("png", "jpg", "jpeg")

    @JvmField
    val OCR_LANGUAGES = arrayOf("eng", "deu")
}