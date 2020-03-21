package com.dude.dms.brain.polling

import com.dude.dms.brain.options.Options
import com.dude.dms.ui.Const
import org.springframework.stereotype.Component
import java.io.File

@Component
class DocPollingService {

    fun poll() = emptyList<File>()
            .plus(File(Options.get().doc.pollingPath).listFiles { _, name -> name.endsWith(".pdf") } ?: emptyArray<File>())
            .plus(File(Options.get().doc.pollingPath).listFiles { _, name -> Const.IMAGE_FORMATS.any { name.endsWith(it) } } ?: emptyArray<File>())
            .toList()
}