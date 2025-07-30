# Expense Tracker Bot

A Telegram bot for tracking expenses and income that integrates with Notion databases.

## Features

- 📱 Telegram bot interface for easy expense/income tracking
- 🔗 Integration with Notion databases
- 📊 Automatic categorization of transactions
- 📅 Flexible date selection (today or custom date)
- 🏷️ Predefined categories with custom tag support
- 📝 Detailed transaction logging

## Prerequisites

- Java 17 or higher
- Kotlin 1.9.22
- Telegram Bot Token
- Notion API Token
- Notion databases for income and expenses

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd ExpenseTrackerBot
```

### 2. Configure Environment Variables

Create a `.env` file in the project root or set the following environment variables:

```bash
# Telegram Bot Configuration
TELEGRAM_BOT_TOKEN=your_telegram_bot_token_here

# Notion Configuration
NOTION_TOKEN=your_notion_integration_token_here
NOTION_INCOME_DATABASE_ID=your_income_database_id_here
NOTION_EXPENSE_DATABASE_ID=your_expense_database_id_here
```

### 3. Notion Database Setup

Your Notion databases should have the following properties:

**Required Properties:**
- `Source` (Title) - Transaction description
- `Amount` (Number) - Transaction amount
- `Date` (Date) - Transaction date
- `Month` (Relation) - Link to month pages
- `Tags` (Select) - Transaction category

### 4. Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run
```

## Configuration

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `TELEGRAM_BOT_TOKEN` | Your Telegram bot token from @BotFather | Yes |
| `NOTION_TOKEN` | Your Notion integration token | Yes |
| `NOTION_INCOME_DATABASE_ID` | ID of your income database | Yes |
| `NOTION_EXPENSE_DATABASE_ID` | ID of your expense database | Yes |

### Configuration File

Alternatively, you can create an `application.yml` file in `src/main/resources/`:

```yaml
telegram:
  token: ${TELEGRAM_BOT_TOKEN}

notion:
  token: ${NOTION_TOKEN}
  incomeDatabaseId: ${NOTION_INCOME_DATABASE_ID}
  expenseDatabaseId: ${NOTION_EXPENSE_DATABASE_ID}
```

## Usage

### Starting the Bot

1. Start the application using `./gradlew run`
2. Open Telegram and search for your bot
3. Send `/start` to begin

### Adding Transactions

1. **Select Type**: Choose between Expense or Income
2. **Select Category**: Pick from predefined categories or enter a custom one
3. **Enter Amount**: Type the transaction amount
4. **Enter Title**: Provide a description for the transaction
5. **Choose Date**: Use today's date or pick a custom date

### Predefined Categories

**Expenses:**
- Rent/Mortgage
- Utilities My Family
- Groceries
- Healthcare
- Dining Out
- Entertainment
- Transportation
- Retail
- Utilities Abah Family
- Installment
- Credit

**Income:**
- Salary
- Bonus
- Freelance
- Dividends
- Interest
- Side Hustle

## Project Structure

```
src/main/kotlin/com/expensetracker/
├── config/          # Configuration management
├── model/           # Data models
├── service/         # Business logic services
├── bot/             # Telegram bot implementation
└── util/            # Utility classes
```

## Development

### Building

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Code Style

The project uses Kotlin coding conventions and includes:
- Proper error handling with Result types
- Comprehensive logging
- Configuration management
- Separation of concerns

## Troubleshooting

### Common Issues

1. **Bot not responding**: Check your `TELEGRAM_BOT_TOKEN`
2. **Notion API errors**: Verify your `NOTION_TOKEN` and database IDs
3. **Build failures**: Ensure you have Java 17+ installed

### Logs

Logs are written to:
- Console output
- `logs/expense-tracker-bot.log` (with daily rotation)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 