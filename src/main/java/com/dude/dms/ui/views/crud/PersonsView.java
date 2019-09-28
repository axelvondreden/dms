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
    public PersonsView(PersonService personService) {
        super(Person.class, personService);
    }

    @Override
    protected void defineProperties() {
        addProperty("First name", new TextField(), Person::getFirstName, Person::setFirstName, s -> !s.isEmpty(), "First name can not be empty!");
        addProperty("Last name", new TextField(), Person::getLastName, Person::setLastName, s -> !s.isEmpty(), "Last name can not be empty!");
        addProperty("Date of birth", new DatePicker(), Person::getDateOfBirth, Person::setDateOfBirth);
    }
}