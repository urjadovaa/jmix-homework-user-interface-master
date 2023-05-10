package com.company.homeworkloans.screen.requestloan;

import com.company.homeworkloans.entity.Client;
import com.company.homeworkloans.entity.Loan;
import com.company.homeworkloans.entity.LoanStatus;
import io.jmix.core.DataManager;
import io.jmix.core.TimeSource;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.EntityComboBox;
import io.jmix.ui.component.TextField;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

@UiController("RequestLoan")
@UiDescriptor("request-loan.xml")
@DialogMode(forceDialog = true)
public class RequestLoan extends Screen {

    @Autowired
    private InstanceContainer<Loan> loanDc;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private EntityComboBox<Client> clientBox;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private TextField<BigDecimal> amountField;
    @Autowired
    private TimeSource timeSource;

    // вызов диалога с заполнением поля Client - конструктор
    public RequestLoan withClient(Client client) {
        loanDc.setItem(dataManager.create(Loan.class));
        loanDc.getItem().setClient(client);
        return this;
    }

    // действие "Направить запрос"
    @Subscribe("request")
    public void onRequest(Action.ActionPerformedEvent event) {
        // проверка заполненности обязательных полей
        if(clientBox.getValue() == null ||
                amountField.getValue() == null ||
                amountField.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messageBundle.getMessage("enterRequiredFieldsHeader"))
                    .withDescription(messageBundle.getMessage("enterRequiredFields"))
                    .show();
        } else {
            // сохранение запроса займа
            Loan loan = loanDc.getItem();
            loan.setRequestDate(LocalDate.now());
            loan.setStatus(LoanStatus.REQUESTED);
            dataManager.save(loan);
            this.close(StandardOutcome.CLOSE);
        }
    }

    // действие "Закрыть экран"
    @Subscribe("cancel")
    public void onCancel(Action.ActionPerformedEvent event) {
        this.close(StandardOutcome.CLOSE);
    }

}