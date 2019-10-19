package com.dude.dms.ui.components.dialogs.crud;

import com.dude.dms.backend.data.docs.Word;
import com.dude.dms.backend.service.WordService;
import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

public class WordEditDialog extends CrudEditDialog<Word> {

    private final TextField text;

    private final RadioButtonGroup<String> group;

    private Word word;

    private String originalText;

    private final WordService wordService;

    public WordEditDialog(WordService wordService) {
        this.wordService = wordService;

        text = new TextField("Text");
        text.setWidthFull();

        group = new RadioButtonGroup<>();
        group.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        Button createButton = new Button("Save", e -> save());
        createButton.setWidthFull();
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(createButton, cancelButton);
        buttonLayout.setWidthFull();
        VerticalLayout vLayout = new VerticalLayout(text, group, buttonLayout);
        vLayout.setSizeFull();
        vLayout.setPadding(false);
        vLayout.setSpacing(false);

        add(vLayout);
    }

    @Override
    protected void save() {
        if (text.isEmpty()) {
            text.setErrorMessage("Text can not be empty!");
            return;
        }
        String newText = text.getValue();
        word.setText(newText);
        wordService.save(word);
        List<Word> words = new ArrayList<>();
        if (group.getValue().startsWith("change all in doc")) {
            words = wordService.findByTextAndDoc(originalText, word.getDoc());
        } else if (group.getValue().startsWith("change all (")) {
            words = wordService.findByText(originalText);
        }
        words.forEach(word -> {
            word.setText(newText);
            wordService.save(word);
        });
        eventListener.ifPresent(EntityEventListener::onChange);
        close();
    }

    @Override
    public void open(Word item) {
        word = item;
        originalText = word.getText();
        text.setValue(originalText);
        group.setItems(
                "change this",
                "change all in document (" + wordService.countByTextAndDoc(originalText, word.getDoc()) + ")",
                "change all (" + wordService.countByext(originalText) + ")"
        );
        group.setValue("change this");
        open();
    }
}