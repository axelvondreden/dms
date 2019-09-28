package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.data.entity.PersonHistory;
import com.dude.dms.backend.service.PersonService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_PERSONS, layout = MainView.class)
@PageTitle(Const.TITLE_PERSONS)
public class PersonsView extends HistoricalCrudView<Person, PersonHistory> {

    @Autowired
    private PersonService personService;

    public PersonsView() {

    }

    protected void setColumns() {
        grid.addColumn(Person::getFirstName).setHeader("First name");
        grid.addColumn(Person::getLastName).setHeader("Last name");
        grid.addColumn(Person::getDateOfBirth).setHeader("Date of birth");
    }

    protected void fillGrid() {
        grid.setItems(personService.findAll());
    }

    @Override
    protected void attachBinder() {
        crudForm.addFormField("First name", new TextField(), Person::getFirstName, Person::setFirstName);
        crudForm.addFormField("Last name", new TextField(), Person::getLastName, Person::setLastName);
        crudForm.addFormField("Date of birth", new DatePicker(), Person::getDateOfBirth, Person::setDateOfBirth);
    }
}