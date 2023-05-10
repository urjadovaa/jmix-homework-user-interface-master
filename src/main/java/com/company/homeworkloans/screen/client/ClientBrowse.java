package com.company.homeworkloans.screen.client;

import com.company.homeworkloans.screen.requestloan.RequestLoan;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.screen.*;
import com.company.homeworkloans.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Client.browse")
@UiDescriptor("client-browse.xml")
@LookupComponent("clientsTable")
public class ClientBrowse extends StandardLookup<Client> {

    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private GroupTable<Client> clientsTable;

    // действие "Запросить займ"
    @Subscribe("clientsTable.requestLoan")
    public void onClientsTableRequestLoan(Action.ActionPerformedEvent event) {
        screenBuilders.screen(this)
                .withScreenId("RequestLoan")
                .withScreenClass(RequestLoan.class)
                .build()
                .withClient(clientsTable.getSingleSelected()) // инициализация поля "Клиент" на основании контекста (выбранной строки таблицы)
                .show();
    }

}