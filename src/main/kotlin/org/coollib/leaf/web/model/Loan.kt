package org.coollib.leaf.web.model

import java.time.LocalDate

data class Loan(
    var id: Int,
    var bookId: Int,
    var borrowDate: LocalDate,
    var dueDate: LocalDate,
    var returnDate: LocalDate? = null,
){
    val status: LoanStatus
        get() {
            val rDate = returnDate
            return when {
                rDate == null -> if (LocalDate.now() > dueDate) LoanStatus.Overdue else LoanStatus.Borrowed
                rDate > dueDate -> LoanStatus.Overdue
                else -> LoanStatus.Returned
            }
        }
}