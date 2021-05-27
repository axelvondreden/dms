package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.github.mvysny.karibudsl.v10.upload
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DocUploadDialog : DmsDialog(t("doc.upload"), 40, 70) {

    init {
        val buffer = MultiFileMemoryBuffer()
        upload(buffer).apply {
            setAcceptedFileTypes(*Const.IMAGE_FORMATS.plus("pdf").map { ".$it" }.toTypedArray())
            maxFileSize = Options.get().storage.maxUploadFileSize * 1024 * 1024
            height = "80%"
            addFinishedListener {
                for (file in buffer.files) {
                    try {
                        buffer.getInputStream(file).use { inputStream ->
                            val bytes = ByteArray(inputStream.available())
                            inputStream.read(bytes)
                            FileOutputStream(File(Options.get().doc.pollingPath, file)).use { outStream -> outStream.write(bytes) }
                        }
                    } catch (ex: IOException) {
                        ex.message?.let { it1 -> LOGGER.error(it1, ex) }
                    }
                }
            }
            addAllFinishedListener {
                close()
            }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocUploadDialog::class.java)
    }
}
