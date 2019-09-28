package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.data.entity.AccountHistory;
import com.dude.dms.backend.service.AccountService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_ACCOUNTS, layout = MainView.class)
@PageTitle(Const.TITLE_ACCOUNTS)
public class AccountsView extends HistoricalCrudView<Account, AccountHistory> {

    @Autowired
    private AccountService accountService;

    public AccountsView() {

    }

    protected void setColumns() {
        grid.addColumn(Account::getName).setHeader("Name");
    }

    protected void fillGrid() {
        grid.setItems(accountService.findAll());
    }

    @Override
    protected void attachBinder() {
        crudForm.addFormField("Name", new TextField(), Account::getName, Account::setName);
    }
}