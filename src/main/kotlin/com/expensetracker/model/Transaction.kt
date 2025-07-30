package com.expensetracker.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val title: String,
    val date: LocalDate
)

enum class TransactionType {
    INCOME, EXPENSE
}

data class UserState(
    var type: TransactionType? = null,
    var category: String? = null,
    var amount: Double? = null,
    var title: String? = null,
    var date: LocalDate? = null,
    var awaitingDate: Boolean = false,
    var awaitingCustomTag: Boolean = false
) 