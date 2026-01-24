# LinkConverter

LinkConverter is a modern Android application built with Jetpack Compose that converts various file-sharing links into direct downloadable links. It supports Mediafire, Google Drive, and Dropbox links with a beautiful Material Design 3 interface.

## ✨ Features

- **🔗 Multi-Service Support**: Convert Mediafire, Google Drive, and Dropbox links to direct download links
- **🎨 Modern UI**: Built entirely with Jetpack Compose and Material Design 3
- **✨ Smooth Animations**: Beautiful transitions, gradient backgrounds, and animated components
- **🌈 Service-Specific Theming**: Dynamic colors and icons for each service
- **📱 Responsive Design**: Clean, intuitive interface with smooth navigation
- **📋 Quick Copy**: One-tap clipboard copy for converted links
- **🔄 Real-time Processing**: Fast link conversion with loading indicators

## 🏗️ Technical Stack

### Architecture & Frameworks
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material Design 3** - Latest Material Design components
- **MVVM Architecture** - ViewModel with StateFlow for reactive state management
- **Kotlin Coroutines** - Asynchronous programming with Dispatchers

### Key Libraries
- **Compose BOM 2024.02.00** - Compose library versions management
- **Jsoup 1.22.1** - HTML parsing for link extraction
- **Google Play Billing 8.3.0** - In-app billing support
- **Lifecycle ViewModel Compose 2.7.0** - State management
- **Activity Compose 1.8.2** - Compose integration

### Build Configuration
- **Kotlin 2.0.21**
- **Gradle 9.0.0**
- **Min SDK 26** (Android 8.0)
- **Target SDK 36** (Android 14+)
- **Compile SDK 36**

## Project Structure

```
app/src/main/java/xyz/illuminate/dlinks/
├── ui/
│   ├── screens/
│   │   └── LinkConverterScreen.kt      # Main UI screen with animations
│   ├── theme/
│   │   ├── Color.kt                     # Material3 color scheme
│   │   ├── Theme.kt                     # App theme configuration
│   │   └── Type.kt                      # Typography definitions
│   └── components/
│       └── Animations.kt                # Reusable animation components
├── viewmodel/
│   └── LinkConverterViewModel.kt        # State management & business logic
└── MainActivity.kt                      # Entry point with billing setup
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 26+
- Kotlin 2.0+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/isekhon/LinkConverter.git
```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Build and run on your Android device or emulator

### Building Release APK

```bash
./gradlew assembleRelease
```

The signed APK will be generated in `app/build/outputs/apk/release/`

## 📱 Usage

1. **Launch the app** on your Android device
2. **Select service** from the bottom navigation (Mediafire, Google Drive, or Dropbox)
3. **Paste your link** in the input field
4. **Tap Convert** to generate the direct download link
5. **Copy the link** using the Copy Link button
6. **Enjoy** seamless animations and smooth transitions!

## 🎨 UI Features

- **Animated gradient background** with rotating colors
- **Service-specific icons** with radial gradient effects
- **Smooth page transitions** with slide and fade animations
- **Material3 loading indicators** with blur effects
- **Dynamic color tinting** for navigation icons
- **Spring animations** for interactive elements
- **Floating action buttons** with elevation effects

## 📥 Download

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=xyz.illuminate.dlinks)

## 🤝 Community

Join our Telegram community for updates, support, and discussions:

[📢 Team Illuminate Telegram Channel](https://t.me/team_illuminate)

## 🛠️ Development

### Version Catalog (TOML)

This project uses Gradle version catalogs for dependency management. All versions are centralized in `gradle/libs.versions.toml`.

### Code Style

- Follows Kotlin coding conventions
- Material Design 3 guidelines
- Compose best practices
- MVVM architecture pattern

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Areas for Contribution
- Add support for more file-sharing services
- Improve UI/UX animations
- Add unit and integration tests
- Optimize link parsing algorithms
- Translations for multiple languages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Jsoup](https://jsoup.org/) - Powerful HTML parsing library
- [Material Design 3](https://m3.material.io/) - Beautiful design system
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Kotlin](https://kotlinlang.org/) - Amazing programming language

## 📊 Version History

### v6.0 (Current)
- ✅ Complete Jetpack Compose migration
- ✅ Material Design 3 implementation
- ✅ MVVM architecture with ViewModel
- ✅ Smooth animations and transitions
- ✅ Modern UI with gradient backgrounds
- ✅ Service-specific theming
- ✅ Improved link conversion logic
- ✅ Animated page transitions
- ✅ Dynamic icon tinting
- ✅ Material3 loading indicators

### v1.0 - v5.0
- Previous releases with XML layouts
- Basic link conversion functionality
- Incremental improvements and bug fixes

---

**Made with ❤️ by Jagdeep**
