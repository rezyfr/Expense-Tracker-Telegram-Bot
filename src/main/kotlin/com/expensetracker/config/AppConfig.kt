package com.expensetracker.config

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@kotlinx.serialization.Serializable
data class AppConfig(
    val telegram: TelegramConfig,
    val notion: NotionConfig
) {
    companion object {
        fun load(): AppConfig {
            logger.info { "Loading configuration from environment variables" }
            return AppConfig(
                telegram = TelegramConfig(
                    token = System.getenv("TELEGRAM_BOT_TOKEN") 
                        ?: throw IllegalStateException("TELEGRAM_BOT_TOKEN environment variable is required")
                ),
                notion = NotionConfig(
                    token = System.getenv("NOTION_TOKEN") 
                        ?: throw IllegalStateException("NOTION_TOKEN environment variable is required"),
                    incomeDatabaseId = System.getenv("NOTION_INCOME_DATABASE_ID") 
                        ?: throw IllegalStateException("NOTION_INCOME_DATABASE_ID environment variable is required"),
                    expenseDatabaseId = System.getenv("NOTION_EXPENSE_DATABASE_ID") 
                        ?: throw IllegalStateException("NOTION_EXPENSE_DATABASE_ID environment variable is required")
                )
            )
        }
    }
}

@kotlinx.serialization.Serializable
data class TelegramConfig(
    val token: String
)

@kotlinx.serialization.Serializable
data class NotionConfig(
    val token: String,
    val incomeDatabaseId: String,
    val expenseDatabaseId: String
) 