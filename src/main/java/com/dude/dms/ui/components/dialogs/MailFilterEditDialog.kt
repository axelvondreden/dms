package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.t
import com.dude.dms.ui.components.misc.ConfirmDialog
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import javax.mail.Folder
import javax.mail.MessagingException

class MailFilterEditDialog(
        private val mailFilter: MailFilter,
        private val mailFilterService: MailFilterService,
        mailManager: MailManager
) : DmsDialog("", "70vw", "70vh") {

    private val folderGrid = TreeGrid<Folder>().apply { setWidthFull() }

    init {
        try {
            mailManager.testConnection()
        } catch (e: MessagingException) {
            LOGGER.showError(t("mail.imap.error", e), UI.getCurrent())
            close()
        }

        val folders = mailManager.getRootFolders(true)
        folderGrid.setItems(folders) { mailManager.getSubFolders(it, true) }
        folderGrid.dataProvider.refreshAll()

        folderGrid.addHierarchyColumn { it.fullName }.setHeader(t("folder"))
        folderGrid.addColumn { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.messageCount else "-" }.setHeader(t("count"))
        folderGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        folderGrid.asSingleSelect().value = folders.find { it.fullName == mailFilter.folder }
        folderGrid.height = "90%"

        val saveButton = Button(t("save"), VaadinIcon.PLUS.create()) { save() }.apply { setWidthFull() }
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(folderGrid, HorizontalLayout(saveButton, deleteButton).apply { setWidthFull() })
    }

    private fun delete() {
        ConfirmDialog(t("delete.sure"), t("delete"), VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ComponentEventListener {
            mailFilterService.delete(mailFilter)
            close()
        }).open()
    }

    private fun save() {
        if (folderGrid.asSingleSelect().isEmpty) return
        mailFilter.folder = folderGrid.asSingleSelect().value.fullName
        mailFilterService.save(mailFilter)
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(MailFilterEditDialog::class.java)
    }
}