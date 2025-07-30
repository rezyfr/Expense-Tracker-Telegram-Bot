package com.expensetracker

import com.expensetracker.bot.TelegramBotManager
import com.expensetracker.config.AppConfig
import com.expensetracker.service.NotionService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    try {
        logger.info { "Starting Expense Tracker Bot..." }
        
        // Load configuration
        val config = AppConfig.load()
        logger.info { "Configuration loaded successfully" }
        
        // Initialize services
        val notionService = NotionService(config.notion)
        val botManager = TelegramBotManager(config.telegram, notionService)
        
        // Start the bot
        botManager.startBot()
        
        logger.info { "Bot started successfully" }
    } catch (e: Exception) {
        logger.error(e) { "Failed to start the application" }
        System.exit(1)
    }
} 