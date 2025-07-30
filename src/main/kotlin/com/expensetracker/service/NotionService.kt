package com.expensetracker.service

import com.expensetracker.config.NotionConfig
import com.expensetracker.model.Transaction
import com.expensetracker.model.TransactionType
import com.expensetracker.util.MonthIds
import kotlinx.serialization.json.*
import mu.KotlinLogging
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

class NotionService(private val config: NotionConfig) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .build()

    fun addTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("parent", buildJsonObject {
                    put("database_id", when (transaction.type) {
                        TransactionType.EXPENSE -> config.expenseDatabaseId
                        TransactionType.INCOME -> config.incomeDatabaseId
                    })
                })
                put("properties", buildJsonObject {
                    putJsonObject("Source") {
                        putJsonArray("title") {
                            addJsonObject {
                                put("type", "text")
                                putJsonObject("text") {
                                    put("content", transaction.title)
                                }
                            }
                        }
                    }
                    putJsonObject("Amount") {
                        put("number", transaction.amount)
                    }
                    putJsonObject("Date") {
                        putJsonObject("date") {
                            put("start", transaction.date.toString())
                        }
                    }
                    putJsonObject("Month") {
                        putJsonArray("relation") {
                            addJsonObject {
                                put("id", MonthIds.fromMonth(transaction.date.monthNumber))
                            }
                        }
                    }
                    putJsonObject("Tags") {
                        putJsonObject("select") {
                            put("name", transaction.category)
                        }
                    }
                })
            }

            val requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString())
            val request = Request.Builder()
                .url("https://api.notion.com/v1/pages")
                .addHeader("Authorization", "Bearer ${config.token}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Notion-Version", "2022-06-28")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()

            if (!response.isSuccessful) {
                logger.error { "Notion API call failed: ${response.code()} - $responseBody" }
                Result.failure(Exception("Notion API error: ${response.code()}"))
            } else {
                logger.info { "Successfully added transaction to Notion: ${transaction.title}" }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to add transaction to Notion" }
            Result.failure(e)
        }
    }
} 