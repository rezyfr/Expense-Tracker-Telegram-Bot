package com.expensetracker.bot

import com.expensetracker.config.TelegramConfig
import com.expensetracker.model.Transaction
import com.expensetracker.model.TransactionType
import com.expensetracker.model.UserState
import com.expensetracker.service.NotionService
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.logging.LogLevel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class TelegramBotManager(
    private val config: TelegramConfig,
    private val notionService: NotionService
) {
    private val userStates = mutableMapOf<Long, UserState>()

    fun startBot() {
        val bot = Bot.Builder().build {
            this.token = config.token
            logLevel = LogLevel.All()
            dispatch {
                command("start") {
                    val keyboard = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData("Expense", "type_expense"),
                            InlineKeyboardButton.CallbackData("Income", "type_income")
                        )
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "What would you like to add?",
                        replyMarkup = keyboard
                    )
                }

                callbackQuery("type_expense") {
                    userStates[callbackQuery.from.id] = UserState(type = TransactionType.EXPENSE)
                    val keyboard = InlineKeyboardMarkup.create(
                        EXPENSE_TAGS.map { tag ->
                            InlineKeyboardButton.CallbackData(tag, "tag_${tag.replace(" ", "_")}")
                        } + listOf(
                            InlineKeyboardButton.CallbackData("Custom Tag", "tag_custom")
                        )
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(callbackQuery.message!!.chat.id),
                        text = "Select a tag or tap 'Custom Tag' to enter manually:",
                        replyMarkup = keyboard
                    )
                }

                callbackQuery("type_income") {
                    userStates[callbackQuery.from.id] = UserState(type = TransactionType.INCOME)
                    val keyboard = InlineKeyboardMarkup.create(
                        INCOME_TAGS.map { tag ->
                            InlineKeyboardButton.CallbackData(tag, "tag_${tag.replace(" ", "_")}")
                        } + listOf(
                            InlineKeyboardButton.CallbackData("Custom Tag", "tag_custom")
                        )
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(callbackQuery.message!!.chat.id),
                        text = "Select a tag or tap 'Custom Tag' to enter manually:",
                        replyMarkup = keyboard
                    )
                }

                callbackQuery("use_today") {
                    val state = userStates[callbackQuery.from.id]
                    if (state != null) {
                        state.date = LocalDate.parse(Clock.System.now().toString().substring(0, 10))
                        handleSubmission(bot, callbackQuery.message!!.chat.id, state)
                        userStates.remove(callbackQuery.from.id)
                    }
                }

                callbackQuery("pick_date") {
                    val state = userStates[callbackQuery.from.id]
                    if (state != null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(callbackQuery.message!!.chat.id),
                            text = "Enter date (YYYY-MM-DD):"
                        )
                        state.awaitingDate = true
                    }
                }

                callbackQuery {
                    val data = callbackQuery.data
                    val state = userStates[callbackQuery.from.id] ?: return@callbackQuery

                    if (data.startsWith("tag_")) {
                        val tag = data.removePrefix("tag_")

                        if (tag == "custom") {
                            bot.sendMessage(
                                chatId = ChatId.fromId(callbackQuery.message!!.chat.id),
                                text = "Enter your custom tag:"
                            )
                            state.awaitingCustomTag = true
                        } else {
                            state.category = tag.replace("_", " ")
                            bot.sendMessage(
                                chatId = ChatId.fromId(callbackQuery.message!!.chat.id),
                                text = "Enter the amount:"
                            )
                        }
                    }
                }

                text {
                    val state = userStates[message.from!!.id] ?: return@text

                    when {
                        state.awaitingCustomTag -> {
                            state.category = message.text
                            state.awaitingCustomTag = false
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Enter the amount:")
                        }
                        state.category == null -> {
                            state.category = message.text
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = "Enter the amount:"
                            )
                        }
                        state.amount == null -> {
                            val amount = message.text?.toDoubleOrNull()
                            if (amount != null) {
                                state.amount = amount
                                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Enter the title:")
                            } else {
                                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Please enter a valid number:")
                            }
                        }
                        state.title == null -> {
                            state.title = message.text
                            val keyboard = InlineKeyboardMarkup.create(
                                listOf(
                                    InlineKeyboardButton.CallbackData("Use today's date", "use_today"),
                                    InlineKeyboardButton.CallbackData("Pick a date", "pick_date")
                                )
                            )
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = "Choose date option:",
                                replyMarkup = keyboard
                            )
                        }
                        state.awaitingDate -> {
                            try {
                                state.date = LocalDate.parse(message.text ?: "")
                                handleSubmission(bot, message.chat.id, state)
                                userStates.remove(message.from!!.id)
                            } catch (e: Exception) {
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = "Invalid date format. Please use YYYY-MM-DD:"
                                )
                            }
                        }
                    }
                }
            }
        }
        
        logger.info { "Starting Telegram bot..." }
        bot.startPolling()
    }

    private fun handleSubmission(bot: Bot, chatId: Long, state: UserState) {
        val transaction = Transaction(
            type = state.type!!,
            amount = state.amount!!,
            category = state.category!!,
            title = state.title!!,
            date = state.date!!
        )

        val summary = """
            ✅ Entry recorded:
            Type: ${transaction.type}
            Category: ${transaction.category}
            Amount: ${transaction.amount}
            Title: ${transaction.title}
            Date: ${transaction.date}
        """.trimIndent()

        bot.sendMessage(chatId = ChatId.fromId(chatId), text = summary, parseMode = ParseMode.MARKDOWN)

        notionService.addTransaction(transaction)
            .onSuccess {
                bot.sendMessage(chatId = ChatId.fromId(chatId), text = "✅ Transaction saved to Notion!")
            }
            .onFailure { error ->
                logger.error(error) { "Failed to save transaction to Notion" }
                bot.sendMessage(chatId = ChatId.fromId(chatId), text = "❌ Failed to save transaction. Please try again.")
            }
    }

    companion object {
        private val EXPENSE_TAGS = listOf(
            "Rent/Mortgage",
            "Utilities My Family",
            "Groceries",
            "Healthcare",
            "Dining Out",
            "Entertainment",
            "Transportation",
            "Retail",
            "Utilities Abah Family",
            "Installment",
            "Credit"
        )

        private val INCOME_TAGS = listOf(
            "Salary",
            "Bonus",
            "Freelance",
            "Dividends",
            "Interest",
            "Side Hustle"
        )
    }
} 