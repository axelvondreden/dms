package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.extensions.mailFilterService
import com.dude.dms.extensions.mailManager
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.treeGrid
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.treegrid.TreeGrid
import javax.mail.Folder
import javax.mail.MessagingException

class MailFilterEditDialog(private val mailFilter: MailFilter) : DmsDialog("", 70, 70) {

    private var folderGrid: TreeGrid<Folder>

    init {
        try {
            mailManager.testConnection()
        } catch (e: MessagingException) {
            LOGGER.showError(t("mail.imap.error", e), UI.getCurrent())
            close()
        }

        val folders = mailManager.getRootFolders(true)

        folderGrid = treeGrid {
            setWidthFull()
            height = "90%"
            setItems(folders) { mailManager.getSubFolders(it, true) }
            addHierarchyColumn { it.fullName }.setHeader(t("folder"))
            addColumn { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.messageCount else "-" }.setHeader(t("count"))
            setSelectionMode(Grid.SelectionMode.SINGLE)
            asSingleSelect().value = folders.find { it.fullName == mailFilter.folder }
        }
        horizontalLayout {
            setWidthFull()

            button(t("save"), VaadinIcon.PLUS.create()) {
                onLeftClick { save() }
                setWidthFull()
            }
            button(t("delete"), VaadinIcon.TRASH.create()) {
                onLeftClick { delete() }
                setWidthFull()
            }
        }
    }

    private fun delete() {
        ConfirmDialog(t("delete.sure"), t("delete"), VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR) {
            mailFilterService.delete(mailFilter)
            close()
        }.open()
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