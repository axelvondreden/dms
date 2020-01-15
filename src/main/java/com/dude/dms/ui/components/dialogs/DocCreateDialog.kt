package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.brain.OptionKey
import com.dude.dms.backend.brain.polling.DocPollingService
import com.dude.dms.backend.brain.polling.PollingService
import com.dude.dms.backend.data.docs.Doc
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DocCreateDialog(pollingService: PollingService) : EventDialog<Doc>() {

    init {
        width = "40vw"
        height = "40vh"

        val buffer = MultiFileMemoryBuffer()
            val upload = Upload(buffer).apply {
            setAcceptedFileTypes(".pdf")
            maxFileSize = OptionKey.MAX_UPLOAD_FILE_SIZE.int * 1024 * 1024
            height = "80%"
            addFinishedListener {
                for (file in buffer.files) {
                    try {
                        buffer.getInputStream(file).use { inputStream ->
                            val bytes = ByteArray(inputStream.available())
                            inputStream.read(bytes)
                            val targetFile = File(OptionKey.DOC_POLL_PATH.string, file)
                            LOGGER.info("Writing file {}...", file)
                            FileOutputStream(targetFile).use { outStream -> outStream.write(bytes) }
                        }
                    } catch (ex: IOException) {
                        ex.message?.let { it1 -> LOGGER.error(it1, ex) }
                    }
                }
                close()
                pollingService.poll()
            }
        }
        add(upload)
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocCreateDialog::class.java)
    }
}
