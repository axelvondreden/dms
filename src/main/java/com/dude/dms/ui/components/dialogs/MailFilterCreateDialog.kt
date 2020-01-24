package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.EmailManager
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.treegrid.TreeGrid
import javax.mail.Folder
import javax.mail.MessagingException

class MailFilterCreateDialog(private val mailFilterService: MailFilterService, emailManager: EmailManager) : EventDialog<MailFilter>() {

    private val folderGrid = TreeGrid<Folder>().apply { setWidthFull() }

    init {
        width = "70vw"
        height = "70vh"

        try {
            emailManager.testConnection()
        } catch (e: MessagingException) {
            LOGGER.showError("IMAP Connection Failed: ${e.message}")
            close()
        }

        folderGrid.setItems(emailManager.getRootFolders(true)) { emailManager.getSubFolders(it, true) }
        folderGrid.dataProvider.refreshAll()

        folderGrid.addHierarchyColumn { it.fullName }.setHeader("Folder")
        folderGrid.addColumn { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.messageCount else "-" }.setHeader("Count")
        folderGrid.setSelectionMode(Grid.SelectionMode.SINGLE)

        val button = Button("Create", VaadinIcon.PLUS.create()) { create() }.apply { setWidthFull() }
        add(folderGrid, button)
    }

    private fun create() {
        if (folderGrid.asSingleSelect().isEmpty) {
            LOGGER.showError("No Folder selected!")
            return
        }
        val mailFilter = MailFilter(folderGrid.asSingleSelect().value.fullName)
        mailFilterService.save(mailFilter)
        LOGGER.showInfo("Created new mail-filter!")
        triggerCreateEvent(mailFilter)
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(MailFilterCreateDialog::class.java)
    }
}