This document is best viewed with a markdown viewer, and a plantuml plugin (such as markdown plantuml).

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

1. By beginning to type the place name. Google geocoding API will provide suggestions
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

# Implementation

## Libraries used

1. Dependency injection: Koin
2. Networking: Ktor
3. JSON parsing: kotlinx.serialization
4. Image loading: Coil
5. UI: Jetpack Compose
6. Navigation: Jetpack Navigation

## Architecture

### Runtime

Coroutines are used for asynchronous operations. The app is built around the `ViewModel` and `StateFlow`/`SharedFlow` objects.

### Top level

Screens are governed by a single activity, with the navigation being handled by Jetpack Navigation.
Data is passed between screens via shared `rememberSavable` object. In this case, it it just the `LocationModel` object.

ViewModel - View coupling is implemented using MVI (Model-View-Intent) pattern.

#### MVI

##### A whoosh on MVI

MVI defines 3 pipes of communication between ViewModel and View:

1. State - Represents the screen with data. Prepared by the ViewModel and consumed by the View via StateFlow. Typically, ViewModels would prepare States as close as possible to what the View requires,
   to avoid complex logic.
2. Effect/Action - Represents a one-time event that the View should act upon. Prepared by the ViewModel and consumed by the View via SharedFlow.
3. Event - Reposted by the view to the viewModel. E.g. Button pressed.

### Place search screen

#### Screen architecture

```plaintext
PlaceSearchScreen -> PlaceSearchViewModel -> GeolocationRepo -> GoogleGeocodingAPI
```

The following sequence diagram (PlantUML) shows the flow of data in the Place Search screen:
```plantuml
PlaceSearchScreen -> PlaceSearchViewModel : Event: Search text changed
PlaceSearchViewModel -> GeolocationRepo : Injected with Koin
GeolocationRepo -> GoogleGeocodingAPI : Search for place
activate GoogleGeocodingAPI
GeolocationRepo <-- GoogleGeocodingAPI : Results
deactivate GoogleGeocodingAPI
PlaceSearchViewModel <-- GeolocationRepo : Results over Flow
PlaceSearchScreen <- PlaceSearchViewModel : State: Display search results
...
PlaceSearchScreen -> PlaceSearchViewModel : Event: Suggestion clicked
PlaceSearchViewModel -> PlaceSearchViewModel : Compose location record
PlaceSearchScreen <- PlaceSearchViewModel : Effect: Navigate to (Weather screen)
```

The screen is based on a chain of responsibility.
The View posts events to the ViewModel, which issues requests to the GeolocationRepo, which in turn issues requests to the GoogleGeocodingAPI.

Responses return in the reverse order.

#### Implementation details

Project definitions require that the user should be able to search for a place for to display the weather data.
The simplest, fastest and most reliable way to do that is via Google Geocoding API. The API provides a list of suggestions based on the user input.

Moreover, should the user enter an exact place name, the app should present it to OpenWeatherMap API, directly.

Therefore `GeolocationRepo` searches for the coordinates of a place given a string. It is piped via StateFlow to the PlaceSearchViewModel, which updates the state with the search results.
Should the user click a suggestion, the PlaceSearchViewModel will compose a location record based on the suggestion and the coordinates.

If the user clicks the send button, the PlaceSearchViewModel will compose a location record based on name only.

When a place is available, the PlaceSearchViewModel will post an effect to navigate to the Weather screen.

### Weather screen

The following sequence diagram (PlantUML) shows the flow of data in the Weather screen:
```plantuml
WeatherScreen -> WeatherViewModel: Event: Location record
WeatherViewModel -> WeatherRepo: Get weather
WeatherRepo -> WeatherAPI: Fetch weather
activate WeatherAPI
WeatherAPI -> OpenWeatherMapAPI: Fetch weather
WeatherAPI <-- OpenWeatherMapAPI: Weather JSON
WeatherRepo <-- WeatherAPI: WeatherModel
deactivate WeatherAPI
WeatherViewModel <-- WeatherRepo: WeatherModel
WeatherScreen <- WeatherViewModel: State: Display weather
```

The Weather screen receives the location record via the shared `rememberSavable` object.

The screen signals the view model. The view model fetches the weather data from the OpenWeatherMap API.
Note that the API key is stored in the manifest. The `SecretsRepo` is used to fetch the key.

WeatherAPI is injected to WeatherRepo, which is injected to WeatherViewModel.

WeatherViewModel prepares the state and posts it to the view.

## Tree structure

The project is built - feature-first.

As such, the project is divided into screens. Each has its unique View, ViewModel and models.

The repositories are shared between the screens (though, practically for this project they could have been separate).

The following PlantUML diagram shows the project hierarchy:

```plantuml
skinparam packageStyle rect
skinparam linetype ortho

package DI {
    [AppModule]
}

package screens {
    [MainScreen]
    rectangle Navigation {
    }
    [WeatherScreen]
    [PlaceSearchScreen]
}

[MainScreen] --> [Navigation]
[Navigation] --> [PlaceSearchScreen]
[Navigation] --> [WeatherScreen]

package repositories {
    [GeolocationRepo]
    [Secrets]
    [WeatherRepo]
    package API {
        [WeatherAPI]
        [NetworkingEngine]
    }
}

[PlaceSearchScreen] --> [GeolocationRepo]
[WeatherScreen] --> [WeatherRepo]
[WeatherAPI] --> [NetworkingEngine]
[WeatherRepo] --> [WeatherAPI]

DI ---> [Secrets] : APIKey
[Secrets] ..> [WeatherAPI] : APIKey

```
