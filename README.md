# WeatherMate

WeatherMate is a simple weather application that allows users to check the weather conditions for different cities. It provides real-time weather information such as temperature, humidity, and wind speed, helping users plan their day accordingly.

## Features

- **City List**: Users can add multiple cities to their city list and easily switch between them to view the weather details.
- **Weather Details**: Users can view detailed weather information for each city, including the current temperature, weather description, humidity, and wind speed. Also can see weather details for the next three days (midday).
- **User Authentication**: The app supports user authentication using Google Sign-In, allowing users to sign in with their Google accounts and access their saved city list across devices.
- **Location-based Weather**: The app automatically detects the user's current location and displays the weather details for that location. Users can also manually add cities to their list.

## Technologies Used

- **Android**: The app is developed using the Android platform, specifically Kotlin programming language.
- **Firebase**: Firebase Authentication is used for user authentication, and Firebase Firestore is used for storing user-specific city lists.
- **Google Sign-In**: The app utilizes Google Sign-In API to authenticate users with their Google accounts.
- **OpenWeatherMap API**: Weather data is fetched from the OpenWeatherMap API, which provides accurate and up-to-date weather information.

## Getting Started

To run the app on your local machine, follow these steps:

1. Clone the repository: `git clone https://github.com/saislam10/WeatherMate.git`
2. Open the project in Android Studio.
3. Set up your Firebase project and configure the necessary authentication settings.
4. Obtain an API key from OpenWeatherMap API and add it to the project.
5. Build and run the app on an Android device or emulator.

## License

WeatherMate is licensed under the [MIT License](https://opensource.org/licenses/MIT). Feel free to use, modify, and distribute the code as per the terms of the license.

## Acknowledgements

- The app utilizes the following open-source libraries:
  - [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the user interface.
  - [Firebase Authentication](https://firebase.google.com/docs/auth) for user authentication.
  - [Firebase Firestore](https://firebase.google.com/docs/firestore) for storing user data.
  - [Retrofit](https://square.github.io/retrofit/) for making API requests.
- Special thanks to the developers and contributors of these libraries.

## Contact

For any inquiries or feedback, please contact [saislam@davidson.edu](mailto:saislam@davidson.edu), [taawal@davidson.edu](taawal@davidson.edu), or [miremezo@davidson.edu](mailto:miremezo@davidson.edu).

---
