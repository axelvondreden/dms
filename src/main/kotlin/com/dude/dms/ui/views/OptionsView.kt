package com.dude.dms.ui.views

import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.utils.card
import com.dude.dms.ui.Const
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.Lumo
import mslinks.ShellLink
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

@Route(value = Const.PAGE_OPTIONS, layout = MainView::class)
@PageTitle("Options")
class OptionsView(private val tagService: TagService, private val docService: DocService) : VerticalLayout() {

    private val options = Options.get()

    init {
        alignItems = FlexComponent.Alignment.CENTER
        card {
            width = "50vw"

            details(t("view")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        comboBox<Locale>(t("language")) {
                            setItems(*Const.LOCALES)
                            value = Locale.forLanguageTag(options.view.locale)
                            isAllowCustomValue = false
                            isPreventInvalidInput = true
                            addValueChangeListener {
                                if (!isEmpty) {
                                    options.view.locale = value.toLanguageTag()
                                    save()
                                }
                            }
                        }
                        textField("${t("date")} Format") {
                            value = options.view.dateFormat
                            addValueChangeListener {
                                if (it.value.isNotEmpty()) {
                                    options.view.dateFormat = it.value
                                    save()
                                }
                            }
                        }
                        checkBox(t("darkmode")) {
                            value = options.view.darkMode
                            addValueChangeListener { event ->
                                val themeList = UI.getCurrent().element.themeList
                                themeList.clear()
                                themeList.add(if (event.value) Lumo.DARK else Lumo.LIGHT)
                                options.view.darkMode = value
                                save()
                            }
                        }
                        checkBox(t("words.preview.show")) {
                            value = options.view.loadWordsInPreview
                            addValueChangeListener {
                                options.view.loadWordsInPreview = value
                                save()
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("Popups")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        comboBox<Notification.Position>("Popup Position") {
                            setItems(*Notification.Position.values())
                            value = Notification.Position.valueOf(options.view.notificationPosition)
                            isAllowCustomValue = false
                            isPreventInvalidInput = true
                            setWidthFull()
                            addValueChangeListener {
                                if (!isEmpty) {
                                    options.view.notificationPosition = value.name
                                    save()
                                }
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("docs")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        numberField("Import DPI") {
                            value = options.doc.imageParserDpi
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    try {
                                        options.doc.imageParserDpi = it.value
                                        save()
                                    } catch (ignored: NumberFormatException) {
                                    }
                                }
                            }
                        }
                        textField("${t("date")} Format") {
                            value = options.view.dateScanFormats.joinToString(",")
                            addValueChangeListener {
                                if (!it.value.isNullOrEmpty()) {
                                    options.view.dateScanFormats = it.value.split(",")
                                    save()
                                }
                            }
                        }
                        comboBox<String>("OCR ${t("language")}") {
                            setItems(*Const.OCR_LANGUAGES)
                            value = options.doc.ocrLanguage
                            isAllowCustomValue = false
                            isPreventInvalidInput = true
                            addValueChangeListener {
                                if (!isEmpty) {
                                    options.doc.ocrLanguage = value
                                    save()
                                }
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("storage")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        textField(t("doc.save.path", Paths.get("../").absolutePathString())) {
                            value = options.doc.savePath
                            addValueChangeListener {
                                if (it.value.isNotEmpty()) {
                                    val dir = File(it.value)
                                    if (dir.exists() && dir.isDirectory) {
                                        options.doc.savePath = it.value
                                        save()
                                    } else {
                                        LOGGER.showError(t("dir.missing", it.value), UI.getCurrent())
                                    }
                                }
                            }
                        }
                        integerField(t("upload.max.size")) {
                            value = options.storage.maxUploadFileSize
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    options.storage.maxUploadFileSize = it.value
                                    save()
                                }
                            }
                        }
                        textField(t("doc.offline.path", Paths.get("../").absolutePathString())) {
                            value = options.storage.offlineLinkLocation
                            addValueChangeListener {
                                if (it.value.isNotEmpty()) {
                                    val dir = File(it.value)
                                    if (dir.exists() && dir.isDirectory) {
                                        options.storage.offlineLinkLocation = it.value
                                        save()
                                    } else {
                                        LOGGER.showError(t("dir.missing", it.value), UI.getCurrent())
                                    }
                                }
                            }
                        }
                        button(t("create.now")) { onLeftClick { createOfflineBackup() } }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details(t("tags")) {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        setWidthFull()
                        multiSelectListBox<String> {
                            setWidthFull()
                            val entries = tagService.findAll().map { it.name }
                            setItems(entries)
                            addComponentAsFirst(Text(t("tags.automatic")))
                            select(options.tag.automaticTags.filter { it in entries })
                            addSelectionListener {
                                options.tag.automaticTags = it.allSelectedItems.toList()
                                save()
                            }
                        }
                    }
                }
            }
        }
        card {
            width = "50vw"

            details("Update") {
                isOpened = true
                style["width"] = "100%"
                style["padding"] = "5px"

                content {
                    formLayout {
                        integerField(t("update.interval")) {
                            value = options.update.checkInterval
                            addValueChangeListener {
                                if (it.value != null && it.value > 0) {
                                    options.update.checkInterval = it.value
                                    save()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createOfflineBackup() {
        if (options.storage.offlineLinkLocation.isNotBlank()) {
            val docs = docService.findAll()
            val root = File(options.storage.offlineLinkLocation)
            val docPath = options.doc.savePath
            for (doc in docs) {
                if (doc.tags.isNullOrEmpty()) {
                    ShellLink.createLink(Paths.get(docPath, "pdf", doc.guid + ".pdf").toString(), Paths.get(root.absolutePath, doc.guid + ".lnk").toString())
                } else {
                    val tagOrders = permute(doc.tags.toList())
                    for (order in tagOrders) {
                        var path = root
                        for (tag in order) {
                            path = File(path, tag.name)
                            path.mkdir()
                        }
                        ShellLink.createLink(Paths.get(docPath, "pdf", doc.guid + ".pdf").toString(), Paths.get(path.absolutePath, doc.guid + ".lnk").toString())
                    }
                }
            }
            LOGGER.showInfo(t("done"), UI.getCurrent())
        }
    }

    private fun <T> permute(list: List<T>): List<List<T>> {
        if (list.size == 1) return listOf(list)
        val perms = mutableListOf<List<T>>()
        val sub = list[0]
        for (perm in permute(list.drop(1)))
            for (i in 0..perm.size) {
                val newPerm = perm.toMutableList()
                newPerm.add(i, sub)
                perms.add(newPerm)
            }
        return perms
    }

    private fun save() {
        options.save()
        LOGGER.showInfo(t("settings.saved"), UI.getCurrent())
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(OptionsView::class.java)
    }
}