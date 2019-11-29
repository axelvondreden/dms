package com.dude.dms.ui.builder.docs;

import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.service.TextBlockService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.components.dialogs.TextBlockEditDialog;

public final class TextBlockEditDialogBuilder {

    private final TextBlock textBlock;

    private final TextBlockService textBlockService;

    private EntityCreateListener eventListener;

    TextBlockEditDialogBuilder(TextBlock textBlock, TextBlockService textBlockService) {
        this.textBlock = textBlock;
        this.textBlockService = textBlockService;
    }

    public TextBlockEditDialogBuilder withEventListener(EntityCreateListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public TextBlockEditDialog build() {
        TextBlockEditDialog dialog = new TextBlockEditDialog(textBlock, textBlockService);
        dialog.setCreateListener(eventListener);
        return dialog;
    }
}
