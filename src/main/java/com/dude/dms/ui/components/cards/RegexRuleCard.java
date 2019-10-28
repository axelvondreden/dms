package com.dude.dms.ui.components.cards;

import com.dude.dms.ui.components.tags.TagContainer;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.label.PrimaryLabel;

public class RegexRuleCard extends RippleClickableCard {

    public RegexRuleCard(String title, TagContainer tagContainer) {
        setWidthFull();

        add(new PrimaryLabel(title));
        tagContainer.setPadding(true);
        add(tagContainer);
    }
}