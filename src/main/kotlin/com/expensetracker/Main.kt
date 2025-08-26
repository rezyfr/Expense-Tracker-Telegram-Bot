package com.expensetracker

import com.expensetracker.bot.TelegramBotManager
import com.expensetracker.bot.bot
import com.expensetracker.config.AppConfig
import com.expensetracker.service.NotionService
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kotlintelegrambot.entities.Update
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
        botManager.startBotWebhook()
        
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
            post("/webhook") {
                try {
                    val body = call.receiveText()
                    val mapper  = jacksonObjectMapper()
                        .findAndRegisterModules()
                        .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE)
                        .enable(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                        .addMixIn(com.github.kotlintelegrambot.entities.Message::class.java, MessageMixin::class.java)

                    val update = mapper.readValue<Update>(body)

                    call.respond(HttpStatusCode.OK)

                    bot.processUpdate(update)

                } catch (e: Exception) {
                    logger.error(e) { "Webhook processing failed ${e.message}" }
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }.start(wait = false)
}

@JsonIgnoreProperties("reply_markup")
interface MessageMixin