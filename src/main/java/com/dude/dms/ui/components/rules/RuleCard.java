package com.dude.dms.ui.components.rules;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.ui.components.tags.TagContainer;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.label.PrimaryLabel;
import com.vaadin.flow.component.ComponentEventListener;

import java.util.Set;

public class RuleCard extends RippleClickableCard {

    public RuleCard(String title, Set<Tag> tags, ComponentEventListener<?> onClick) {
        super(onClick);
        setWidthFull();

        add(new PrimaryLabel(title));
        TagContainer tagContainer = new TagContainer(tags);
        tagContainer.setPadding(true);
        add(tagContainer);
    }
}