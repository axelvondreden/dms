package com.dude.dms.ui.components.dialogs.docimport

import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.mlottmann.vstepper.StepContent
import com.mlottmann.vstepper.stepEvent.AbortEvent
import com.mlottmann.vstepper.stepEvent.CompleteEvent
import com.mlottmann.vstepper.stepEvent.EnterEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon

class FileImportStep(private val stepData: DocImportDialog.StepData) : StepContent() {

    private var grid = initGrid()

    private val wrapper = Div().apply { setSizeFull() }

    init {
        add(wrapper)
        fill()
        setSizeFull()
    }

    override fun onAbort(event: AbortEvent?) { }

    override fun onComplete(event: CompleteEvent?) {
        stepData.files.forEach { it.selected = grid.selectedItems.contains(it) }
    }

    override fun isValid() = grid.selectedItems.isNotEmpty()

    override fun onEnter(event: EnterEvent?) { fill() }

    private fun initGrid(): Grid<DocImportDialog.FileContainer> {
        return Grid<DocImportDialog.FileContainer>().apply {
            setItems(stepData.files)
            setSelectionMode(Grid.SelectionMode.MULTI)
            addItemClickListener { event -> event.item?.let { asMultiSelect().select(it) } }
            asMultiSelect().addSelectionListener { stepChanged() }
            asMultiSelect().select(stepData.files.filter { it.selected })

            addColumn { it.file.name }.setHeader(t("file"))
            addComponentColumn { fileContainer ->
                ComboBox("", *Const.OCR_LANGUAGES).apply {
                    value = fileContainer.language
                    isPreventInvalidInput = true
                    isAllowCustomValue = false
                    addValueChangeListener { fileContainer.language = value }
                }
            }.setHeader(t("language"))
            addComponentColumn { fileContainer ->
                val del = Button(VaadinIcon.TRASH.create()) {
                    asMultiSelect().deselect(fileContainer)
                    stepData.files.remove(fileContainer)
                    fileContainer.file.delete()
                    fill()
                    stepChanged()
                }
                del
            }
        }
    }

    private fun fill() {
        wrapper.removeAll()
        grid = initGrid()
        wrapper.add(grid)
    }
}