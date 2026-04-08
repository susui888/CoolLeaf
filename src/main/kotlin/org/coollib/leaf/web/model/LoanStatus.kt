package org.coollib.leaf.web.model

enum class LoanStatus(val description: String) {

    Borrowed("Borrowed"),

    Returned("Returned"),

    Overdue("Overdue");

    fun getUserFriendlyDescription(): String {
        return description
    }
}