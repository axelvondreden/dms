package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.data.history.TagHistory;
import com.dude.dms.backend.service.TagHistoryService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.standard.DmsColorPicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_TAGS, layout = MainView.class)
@PageTitle(Const.TITLE_TAGS)
public class TagsView extends HistoricalCrudView<Tag, TagHistory> {

    @Autowired
    public TagsView(TagService tagService, TagHistoryService historyService) {
        super(Tag.class, tagService, historyService);
    }

    @Override
    protected void defineProperties() {
        addProperty("Name", new TextField(), Tag::getName, Tag::setName, s -> !s.isEmpty(), "Name can not be empty!");
        addProperty("Color", new DmsColorPicker(), Tag::getColor, Tag::setColor, s -> s != null && !s.isEmpty(), "Color can not be empty!");
    }
}