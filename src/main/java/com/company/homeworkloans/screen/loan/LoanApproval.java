package com.company.homeworkloans.screen.loan;

import com.company.homeworkloans.entity.LoanStatus;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Label;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import com.company.homeworkloans.entity.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@UiController("Loan.approval")
@UiDescriptor("loan-approval.xml")
@LookupComponent("loansTable")
public class LoanApproval extends StandardLookup<Loan> {

    @Autowired
    private CollectionLoader<Loan> loansDl;
    @Autowired
    private CollectionContainer<Loan> previousLoansDc;
    @Autowired
    private CollectionLoader<Loan> previousLoansDl;

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private CollectionContainer<Loan> loansDc;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;


    // в экран должны загружаться только Loans со статусом Requested
    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        loansDl.setParameter("loanStatus", LoanStatus.REQUESTED);
        loansDl.load();
    }

    // генерируемая колонка "возраст"
    @Install(to = "loansTable.age", subject = "columnGenerator")
    private Component loansTableAgeColumnGenerator(Loan loan) {
        Label component = uiComponents.create(Label.class);
        component.setValue(getClientAge(loan));
        return component;
    }

    // вычислить возвраст клиента
    private String getClientAge(Loan loan) {
        LocalDate birthDate = loan.getClient().getBirthDate();
        LocalDate now = LocalDate.now();
        return String.valueOf(Period.between(birthDate, now).getYears());
    }

    // выбор строки основной таблицы с займами
    @Subscribe(id = "loansDc", target = Target.DATA_CONTAINER)
    public void onLoansDcItemChange(InstanceContainer.ItemChangeEvent<Loan> event) {
        if (event.getItem() == null || event.getItem().getClient() == null) return;
        previousLoansDl.setParameter("client", event.getItem().getClient());
        previousLoansDl.load();
        previousLoansDc.getMutableItems().remove(event.getItem());
    }

    @Subscribe("loansTable.approve")
    public void onLoansTableApprove(Action.ActionPerformedEvent event) {
        // Меняет статус выбранного в таблице Loan на Approved
        Loan loan = loansDc.getItem();
        loan.setStatus(LoanStatus.APPROVED);
        dataManager.save(loan);
        // Показывает уведомление (Notification) "Approved"
        notifications.create(Notifications.NotificationType.HUMANIZED)
                .withCaption(messageBundle.getMessage("loanApproveSuccess"))
                .withDescription(messageBundle.getMessage("loanApproveSuccessDescr"))
                .show();
        // Удаляет Loan из таблицы на экране
        loansDl.load();
        previousLoansDc.getMutableItems().clear();
    }

    @Subscribe("loansTable.reject")
    public void onLoansTableReject(Action.ActionPerformedEvent event) {
        //	Меняет статус выбранного в таблице Loan на Rejected
        Loan loan = loansDc.getItem();
        loan.setStatus(LoanStatus.REJECTED);
        dataManager.save(loan);
        // Показывает уведомление (Notification) "Rejected"
        notifications.create(Notifications.NotificationType.HUMANIZED)
                .withCaption(messageBundle.getMessage("loanRejectedSuccess"))
                .withDescription(messageBundle.getMessage("loanRejectedSuccessDescr"))
                .show();
        // Удаляет Loan из таблицы на экране
        loansDl.load();
        previousLoansDc.getMutableItems().clear();
    }

}