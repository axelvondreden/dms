package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.service.TextBlockService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

public class TextBlockEditDialog extends EventDialog {

    private final TextField text;

    private final RadioButtonGroup<String> group;

    private final TextBlock textBlock;

    private final String originalText;

    private final TextBlockService textBlockService;

    public TextBlockEditDialog(TextBlock textBlock, TextBlockService textBlockService) {
        this.textBlock = textBlock;
        this.textBlockService = textBlockService;

        originalText = textBlock.getText();

        setWidth("40vw");

        text = new TextField("Text");
        text.setWidthFull();
        text.setValue(originalText);

        group = new RadioButtonGroup<>();
        group.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        group.setItems(
                "change this",
                "change all in document (" + textBlockService.countByTextAndDoc(originalText, textBlock.getDoc()) + ')',
                "change all (" + textBlockService.countByext(originalText) + ')'
        );
        group.setValue("change this");

        Button createButton = new Button("Save", e -> save());
        createButton.setWidthFull();
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(createButton, cancelButton);
        buttonLayout.setWidthFull();

        add(text, group, buttonLayout);
    }

    private void save() {
        if (text.isEmpty()) {
            text.setErrorMessage("Text can not be empty!");
            return;
        }
        String newText = text.getValue();
        textBlock.setText(newText);
        textBlockService.save(textBlock);
        List<TextBlock> textBlocks = new ArrayList<>();
        if (group.getValue().startsWith("change all in doc")) {
            textBlocks = textBlockService.findByTextAndDoc(originalText, textBlock.getDoc());
        } else if (group.getValue().startsWith("change all (")) {
            textBlocks = textBlockService.findByText(originalText);
        }
        textBlocks.forEach(block -> {
            block.setText(newText);
            textBlockService.save(block);
        });
        if (eventListener != null) {
            eventListener.onChange();
        }
        close();
    }
}