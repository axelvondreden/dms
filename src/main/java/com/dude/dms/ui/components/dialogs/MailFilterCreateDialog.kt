package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.t
import com.dude.dms.extensions.resizable
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.treegrid.TreeGrid
import javax.mail.Folder
import javax.mail.MessagingException

class MailFilterCreateDialog(private val mailFilterService: MailFilterService, mailManager: MailManager) : Dialog() {

    private val folderGrid = TreeGrid<Folder>().apply { setWidthFull() }

    init {
        resizable()
        width = "70vw"
        height = "70vh"

        try {
            mailManager.testConnection()
        } catch (e: MessagingException) {
            LOGGER.showError(t("mail.imap.error", e), UI.getCurrent())
            close()
        }

        folderGrid.setItems(mailManager.getRootFolders(true)) { mailManager.getSubFolders(it, true) }
        folderGrid.dataProvider.refreshAll()

        folderGrid.addHierarchyColumn { it.fullName }.setHeader(t("folder"))
        folderGrid.addColumn { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.messageCount else "-" }.setHeader(t("count"))
        folderGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        folderGrid.height = "90%"

        val button = Button(t("create"), VaadinIcon.PLUS.create()) { create() }.apply { setWidthFull() }
        add(folderGrid, button)
    }

    private fun create() {
        if (folderGrid.asSingleSelect().isEmpty) return
        mailFilterService.save(MailFilter(folderGrid.asSingleSelect().value.fullName))
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(MailFilterCreateDialog::class.java)
    }
}