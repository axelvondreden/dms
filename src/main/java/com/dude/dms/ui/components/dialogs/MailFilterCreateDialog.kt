package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.t
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.treeGrid
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.treegrid.TreeGrid
import javax.mail.Folder
import javax.mail.MessagingException

class MailFilterCreateDialog(private val mailFilterService: MailFilterService, mailManager: MailManager) : DmsDialog("", 70, 70) {

    private var folderGrid: TreeGrid<Folder>

    init {
        try {
            mailManager.testConnection()
        } catch (e: MessagingException) {
            LOGGER.showError(t("mail.imap.error", e), UI.getCurrent())
            close()
        }

        folderGrid = treeGrid {
            setWidthFull()
            height = "90%"
            setItems(mailManager.getRootFolders(true)) { mailManager.getSubFolders(it, true) }
            addHierarchyColumn { it.fullName }.setHeader(t("folder"))
            addColumn { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.messageCount else "-" }.setHeader(t("count"))
            setSelectionMode(Grid.SelectionMode.SINGLE)
        }

        button(t("create"), VaadinIcon.PLUS.create()) {
            onLeftClick { create() }
            setWidthFull()
        }
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