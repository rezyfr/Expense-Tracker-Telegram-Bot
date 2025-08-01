package com.expensetracker

import com.expensetracker.bot.TelegramBotManager
import com.expensetracker.config.AppConfig
import com.expensetracker.service.NotionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.routing
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    try {
        startHttpServer()
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

fun startHttpServer() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        routing {
            get("/") {
                call.respondText("Bot is running!")
            }
            head("/") {
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = false)
}