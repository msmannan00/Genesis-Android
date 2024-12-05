[![Codacy Badge](https://app.codacy.com/project/badge/Grade/94c252c8ce904c4cbbc4146a463b4d9e)](https://app.codacy.com/gh/msmannan00/Genesis-Android/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

![WebApp](https://github.com/msmannan00/Orion-Search/blob/trusted_main/documentation/homepage.png?raw=true)
# Orion Search
<table>
<tr>
<td>
<br>
Orion Search Engine is a web-based search tool built on top of Docker that provides a user-friendly interface to explore and visualize data extracted by the Orion Crawler. The engine supports a vast array of functionalities, offering users the ability to search, filter, and visualize data across multiple categories. It integrates machine learning models for enhanced search relevance and content analysis.
<br>
<br>
</td>
</tr>
<br>
<tr>
<td>
<br>

**1. Unrestricted Browsing**: Allows access to censored or restricted websites using a robust, privacy-focused search engine powered by the Tor network.

**2. Privacy and Security**: Ensures anonymity by not utilizing cookies, JavaScript, or third-party tracking codes, safeguarding your identity and data.

**3. Deep Web Exploration**: Facilitates exploration of .onion sites and other hidden web resources not indexed by traditional search engines.

**4. Ad Blocking**: Blocks intrusive advertisements, creating a seamless and distraction-free browsing experience.

**5. Simple User Interface**: Provides an intuitive and user-friendly interface, making navigation and filtering search results effortless.

**6. Multi-Layer Protection**: Adds multiple layers of IP address protection to maintain browsing privacy and secure communications.

**7. Lightweight and Efficient**: Optimized for devices running Android 6.0 and above, with minimal APK size (~96 MB) for faster downloads and installation.

<br><br>
</td>
</tr>
</table>

# Orion Browser

Orion Browser is an Android application designed to provide a secure, private browsing experience by leveraging onion routing technology. This browser empowers users to access hidden web content anonymously, unblock restricted sites, and browse freely while safeguarding their online identity.

## Features

### Technical Highlights
- Built-in onion search engine for accessing the hidden web.
- Multiple layers of IP address protection.
- Compatible with Android 6.0 and above.
- Lightweight APK size (~96 MB).

## Installation

1. **Enable Unknown Sources**:
   - Navigate to **Settings** > **Security**.
   - Enable **Unknown Sources** to allow installations outside the Google Play Store.

2. **Download APK**:
   - Visit the [Releases](https://github.com/msmannan00/Genesis-Android/releases) section to download the latest APK.

3. **Install APK**:
   - Locate the downloaded APK using a file manager and install it.
   - Follow on-screen instructions to complete the process.

4. **Disable Unknown Sources** (Recommended):
   - Navigate to **Settings** > **Security** and disable **Unknown Sources**.

## Development and Build Instructions

### Prerequisites
- Android Studio
- Kotlin and Java support
- Android SDK version 6.0 (API level 23) or above
- Gradle Build System

### Gradle Configuration

Add the following `build.gradle` configuration for the project:

```gradle
plugins {
    id "com.jetbrains.python.envs" version "0.0.26"
}
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'

android {
    compileSdkVersion project.ext.compile_sdk_version
    ndkVersion project.ext.ndk_version

    defaultConfig {
        applicationId project.ext.application_id
        minSdkVersion project.ext.min_sdk_version
        targetSdkVersion project.ext.target_sdk_version
        versionCode project.ext.vcode
        versionName project.ext.vname
        ndk {
            debugSymbolLevel project.ext.debugSymbolLevel
        }
    }
    kotlinOptions {
        jvmTarget = project.ext.jvmTarget
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    buildTypes {
        release {
            minifyEnabled project.ext.minifyEnabled
            shrinkResources project.ext.shrinkResources
            proguardFiles getDefaultProguardFile(project.ext.proguard_file), project.ext.proguard_rule
        }
    }

    productFlavors {
        orion {
            dimension project.ext.dimen
        }
    }

    sourceSets {
        main {
            res.srcDirs = project.ext.resource_directories
        }
    }
    lint {
        disable project.ext.lintoption
    }
    buildFeatures {
        buildConfig true
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.google.android.material:material:1.12.0'
    implementation "androidx.work:work-runtime:2.9.1"
    implementation "org.mozilla.components:browser-engine-gecko:129.0"
    implementation project(path: ':orbotmanager')
}
```

### Building the Project
1. Clone the repository:
   ```bash
   git clone https://github.com/msmannan00/Genesis-Android.git
   cd Genesis-Android
   ```

2. Open the project in Android Studio.

3. Sync Gradle and build the project.

4. Run the application on an Android emulator or connected device.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

**Disclaimer**: Accessing the hidden web carries inherent risks. Use this application responsibly and ensure compliance with local laws and regulations.
