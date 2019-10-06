package com.dude.dms.ui.components.regex;

import com.dude.dms.ui.components.ValueProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexDialog extends Dialog {

    private final TextField callBack;
    private final TextField regexField;
    private final TextArea textArea;
    private final Grid<String> grid;
    private final List<String> matches;

    public RegexDialog(TextField callBack) {
        this.callBack = callBack;
        regexField = new TextField("Regex", callBack.getValue(), "");
        regexField.setWidthFull();
        regexField.addValueChangeListener(event -> refreshResults());
        regexField.setValueChangeMode(ValueChangeMode.EAGER);

        textArea = new TextArea("Test area");
        textArea.setWidthFull();
        textArea.setHeight("50%");
        textArea.addValueChangeListener(event -> refreshResults());
        textArea.setValueChangeMode(ValueChangeMode.EAGER);

        grid = new Grid<>();
        grid.addColumn(s -> s).setHeader("Matches");
        matches = new ArrayList<>();
        grid.setItems(matches);
        grid.setHeight("30%");

        HorizontalLayout horizontalLayout = new HorizontalLayout(regexField, new Button("OK", e -> save()));
        horizontalLayout.setAlignItems(FlexComponent.Alignment.END);

        SplitLayout splitLayout = new SplitLayout(textArea, grid);
        splitLayout.setWidthFull();
        splitLayout.setHeight("80%");
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);

        setWidth("60vw");
        setHeight("60vh");
        add(horizontalLayout, splitLayout);
    }

    public RegexDialog(TextField callBack, ValueProvider<String> valueProvider) {
        this(callBack);
        textArea.setValue(valueProvider.getValue());
        refreshResults();
    }

    private void refreshResults() {
        matches.clear();
        if (!regexField.isEmpty() && !textArea.isEmpty()) {
            regexField.setErrorMessage("");
            try {
                Pattern pattern = Pattern.compile(regexField.getValue());
                for (String line : textArea.getValue().split("\n")) {
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String result = matcher.group();
                        if (!result.isEmpty()) {
                            matches.add(result);
                        }
                    }
                }
            } catch (PatternSyntaxException e) {
                regexField.setErrorMessage("Invalid regex!");
            }
        }
        grid.getDataProvider().refreshAll();
    }

    private void save() {
        callBack.setValue(regexField.getValue());
        close();
    }
}