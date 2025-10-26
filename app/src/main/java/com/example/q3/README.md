# Simple Temperature App

This is a basic Android app built with Jetpack Compose that shows a live-updating temperature dashboard.

## How It Works

*   A new random temperature (between 65°F and 85°F) is generated every 2 seconds.
*   The screen displays the current, average, min, and max of the last 20 readings.
*   It includes a line chart to visualize the temperature trend.
*   It shows a history list of the most recent readings with timestamps.
*   A button allows the user to pause and resume the live data generation.

## File Breakdown

*   `MainActivity.kt`: The app's entry point. It sets up the main screen, initializes the `TemperatureViewModel`, and passes its state to the UI.
*   `TemperatureViewModel.kt`: The brain of the app. It holds the UI state (`TempState`) and all the business logic. It uses a coroutine to generate new temperature data periodically and calculates all statistics (current, average, min, max).
*   `TemperatureScreens.kt`: Contains all the Jetpack Compose UI components (`@Composable` functions). This includes the main `TemperatureDashboard` screen, the `StatsRow` for statistics, the `TemperatureChart` drawn with a `Canvas`, and the list items for the reading history.
