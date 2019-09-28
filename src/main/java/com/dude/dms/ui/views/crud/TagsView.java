package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.data.entity.TagHistory;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_TAGS, layout = MainView.class)
@PageTitle(Const.TITLE_TAGS)
public class TagsView extends HistoricalCrudView<Tag, TagHistory> {

    @Autowired
    private TagService tagService;

    public TagsView() {

    }

    protected void setColumns() {
        grid.addColumn(Tag::getName).setHeader("Name");
    }

    protected void fillGrid() {
        grid.setItems(tagService.findAll());
    }

    @Override
    protected void attachBinder() {
        crudForm.addFormField("Name", new TextField(), Tag::getName, Tag::setName);
    }
}