package com.company.homeworkloans.screen.requestloan;

import com.company.homeworkloans.entity.Client;
import com.company.homeworkloans.entity.Loan;
import com.company.homeworkloans.entity.LoanStatus;
import io.jmix.core.DataManager;
import io.jmix.core.TimeSource;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

@UiController("RequestLoan")
@UiDescriptor("request-loan.xml")
@DialogMode(forceDialog = true, width = "AUTO", height = "AUTO")
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
    @Autowired
    private ScreenValidation screenValidation;
    @Autowired
    private Form form;

    // вызов диалога с заполнением поля Client - конструктор
    public RequestLoan withClient(Client client) {
        loanDc.setItem(dataManager.create(Loan.class));
        loanDc.getItem().setClient(client);
        return this;
    }

    // действие "Направить запрос"
    @Subscribe("request")
    public void onRequest(Action.ActionPerformedEvent event) {

        // валидация данных
        ValidationErrors validationErrors = screenValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            screenValidation.showValidationErrors(this, validationErrors);
        } else {
            // сохранение запроса займа
            Loan loan = loanDc.getItem();
            loan.setRequestDate(LocalDate.now());
            loan.setStatus(LoanStatus.REQUESTED);
            dataManager.save(loan);
            this.close(StandardOutcome.COMMIT);
        }
        
    }

    // действие "Закрыть экран"
    @Subscribe("cancel")
    public void onCancel(Action.ActionPerformedEvent event) {
        this.close(StandardOutcome.CLOSE);
    }

}