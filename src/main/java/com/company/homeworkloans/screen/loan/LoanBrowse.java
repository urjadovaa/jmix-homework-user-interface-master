package com.company.homeworkloans.screen.loan;

import io.jmix.ui.screen.*;
import com.company.homeworkloans.entity.Loan;

@UiController("Loan.browse")
@UiDescriptor("loan-browse.xml")
@LookupComponent("loansTable")
public class LoanBrowse extends StandardLookup<Loan> {
}