# Weather app

This is a simple weather app that uses the OpenWeatherMap API to get the weather data for a given place.

# How to build

1. Clone the repository
2. Open the project in Android Studio
3. Run

# How to use

The app sends lands in a "Place Search" screen.

## Place Search screen

Place searching can be done in one of two ways:

1. By beginning to click the place name. Google geocoding API will provide suggestions
2. By entering the exact place name and clicking the send button

## Weather screen

The weather screen will show the current weather data for the place that was searched for.

In case of an error - it will display an error message at the bottom of the screen.
The error will disappear when refreshed and the error is resolved.

Note that a different background will appear in case of an error.

### Controls

#### Refresh button

Clicking the refresh button will refresh the weather data for the current place.

#### Edit location button

Clicking the edit location button will take you back to the Place Search screen.

#### Fahrenheit/Celsius selection

Clicking the temperature line will toggle between Fahrenheit and Celsius.

# Libraries used

1. Dependency injection: Koin
2. Networking: Ktor
3. JSON parsing: kotlinx.serialization
4. Image loading: Coil
5. UI: Jetpack Compose
6. Navigation: Jetpack Navigation

