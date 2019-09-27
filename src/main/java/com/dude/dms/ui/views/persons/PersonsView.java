package com.dude.dms.ui.views.persons;

import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.service.PersonService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.dude.dms.ui.views.HistoricalCrudView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("persons-view")
@JsModule("./src/views/persons/persons-view.js")
@Route(value = Const.PAGE_PERSONS, layout = MainView.class)
@PageTitle(Const.TITLE_PERSONS)
public class PersonsView extends HistoricalCrudView implements AfterNavigationObserver {

    @Autowired
    private PersonService personService;

    @Id("grid")
    private Grid<Person> grid;
    @Id("first_name")
    private TextField first_name;
    @Id("last_name")
    private TextField last_name;
    @Id("dateOfBirth")
    private DatePicker dateOfBirth;
    @Id("cancel")
    private Button cancel;
    @Id("save")
    private Button save;

    private final Binder<Person> binder;

    public PersonsView() {
        grid.addColumn(Person::getFirstName).setHeader("First name");
        grid.addColumn(Person::getLastName).setHeader("Last name");
        grid.addColumn(Person::getDateOfBirth).setHeader("Date of birth");

        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        binder = new Binder<>(Person.class);
        binder.bindInstanceFields(this);
        cancel.addClickListener(e -> grid.asSingleSelect().clear());
        save.addClickListener(e -> Notification.show("Not implemented"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(personService.findAll());
    }

    private void populateForm(Person value) {
        binder.readBean(value);
    }
}