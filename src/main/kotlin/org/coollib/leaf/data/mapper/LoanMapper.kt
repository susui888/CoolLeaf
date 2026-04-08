package org.coollib.leaf.data.mapper

import org.coollib.leaf.data.entity.LoanEntity
import org.coollib.leaf.web.model.Loan

fun LoanEntity.toLoan() = Loan(
    this.id,
    this.book.id,
    borrowDate = this.borrowdate,
    dueDate = this.duedate,
    returnDate = this.returndate,
)