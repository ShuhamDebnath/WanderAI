# WanderAI 🤖✈️

[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-1.6.10-brightgreen.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> Your AI-Powered Travel Companion. WanderAI is a Kotlin Multiplatform mobile application that leverages AI to generate personalized, detailed travel itineraries. Built with a modern, offline-first architecture for a seamless user experience.

## 📸 Screenshots

*(Here you can add screenshots of the application. Create a `screenshots` folder in the root of your project.)*

| Login Screen | Home Screen | Trip Details |
| :---: |:---:|:---:|
| <img src="screenshots/login.png" width="200"/> | <img src="screenshots/home.png" width="200"/> | <img src="screenshots/details.png" width="200"/> |

| Saved Trips | Map View | Profile |
| :---: |:---:|:---:|
| <img src="screenshots/trips_gallery.png" width="200"/> | <img src="screenshots/map.png" width="200"/> | <img src="screenshots/profile.png" width="200"/> |

## ✨ Features

- **🤖 AI-Powered Itinerary Generation:** Get a complete, day-by-day travel plan using powerful AI models from OpenRouter.
- **👤 User Authentication:** Secure sign-up and login flow powered by Firebase Authentication.
- **✈️ Offline-First:** All generated trips are saved locally to a Room database, making them available anytime, even without an internet connection.
- **🗺️ Interactive Map:** View your daily activities pinned on a native Google Map (Android) or Apple Map (iOS).
- **🏙️ Smart Suggestions:** Autocomplete for city destinations using the Photon API to ensure accurate planning.
- **🖼️ Dynamic Content:** Fetches location images and coordinates on the fly to enrich the trip plan.
- **🎨 Modern UI:** Sleek, responsive, and beautiful UI built with Compose Multiplatform, supporting both Light and Dark themes.
- **CRUD Operations:** Easily manage your travel history with options to view, delete, search, and sort trips.

## 🏗️ Architectural Overview

This project is built following modern Android & KMP best practices, emphasizing a clean, scalable, and maintainable codebase.

- **Clean Architecture:** Separated into `data`, `domain`, and `presentation` layers.
- **MVVM + UDF:** Uses the Model-View-ViewModel pattern with a Unidirectional Data Flow (State, Actions/Events).
- **Repository Pattern:** Abstracted data sources (network and local database) for clean data management.
- **Dependency Injection:** Using **Koin** for managing dependencies across the entire application.
- **`expect`/`actual` for Platform APIs:** Used for platform-specific implementations like the native MapView, LocationService, and Database drivers.

## 🛠️ Built With

- **Core:** [Kotlin](https://kotlinlang.org/), [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **UI:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Networking:** [Ktor Client](https://ktor.io/docs/client-create-new-application.html)
- **Dependency Injection:** [Koin](https://insert-koin.io/)
- **Database:** [Room](https://developer.android.com/training/data-storage/room) (with SQLite native driver)
- **Serialization:** [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Authentication:** [Firebase Auth KMP](https://firebase.google.com/docs/auth)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **AI Service:** [OpenRouter](https://openrouter.ai/)
- **Geo Services:** [Photon (OpenStreetMap)](https://photon.komoot.io/) for city search, [Wikipedia API](https://www.mediawiki.org/wiki/API:Main_page) for images.

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- Android Studio Koala or newer
- Kotlin Multiplatform Mobile plugin for Android Studio
- Xcode for running the iOS app

### Installation

1.  **Clone the repo**
    ```sh
    git clone https://github.com/ShuhamDebnath/WanderAI.git
    ```
2.  **Set up API Keys**
    - Create a `local.properties` file in the root directory of the project.
    - Add your API keys to the file:
      ```properties
      OPENROUTER_API_KEY="your_openrouter_api_key"
      MAPS_API_KEY="your_google_maps_api_key_for_android"
      ```
3.  **Set up Firebase**
    - Go to your Firebase project console.
    - For the Android app, download the `google-services.json` file.
    - Place the `google-services.json` file in the `composeApp/` directory.
4.  **Sync and Run**
    - Open the project in Android Studio, wait for Gradle to sync.
    - Select `composeApp` as the run configuration and choose an emulator or device.
    - Build and run the project!

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

## 👤 Contact

Shubham Debnath - [@shubhamdnath](https://twitter.com/shubhamdnath) - shubhamdebmath@gmail.com

Project Link: [https://github.com/ShuhamDebnath/WanderAI](https://github.com/ShuhamDebnath/WanderAI)
