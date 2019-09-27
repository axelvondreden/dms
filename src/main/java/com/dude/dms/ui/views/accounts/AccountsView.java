package com.dude.dms.ui.views.accounts;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.service.AccountService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.dude.dms.ui.views.HistoricalCrudView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
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

@Tag("accounts-view")
@JsModule("./src/views/accounts/accounts-view.js")
@Route(value = Const.PAGE_ACCOUNTS, layout = MainView.class)
@PageTitle(Const.TITLE_ACCOUNTS)
public class AccountsView extends HistoricalCrudView implements AfterNavigationObserver {

    @Autowired
    private AccountService accountService;

    @Id("grid")
    private Grid<Account> grid;
    @Id("name")
    private TextField name;
    @Id("cancel")
    private Button cancel;
    @Id("save")
    private Button save;

    private final Binder<Account> binder;

    public AccountsView() {
        grid.addColumn(Account::getName).setHeader("Name");

        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        binder = new Binder<>(Account.class);
        binder.bindInstanceFields(this);
        cancel.addClickListener(e -> grid.asSingleSelect().clear());
        save.addClickListener(e -> Notification.show("Not implemented"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(accountService.findAll());
    }

    private void populateForm(Account value) {
        binder.readBean(value);
    }
}