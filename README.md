# PasswordlessAuthApp

PasswordlessAuthApp is a local-logic-only Android application demonstrating passwordless authentication using Login, OTP, and Session screens.  
The app is built with Kotlin and Jetpack Compose and simulates backend behavior entirely in memory.

---

## Architecture

The application follows the MVVM (Model–View–ViewModel) architecture.

### Data Layer
- **OtpManager**
  - Handles OTP generation, storage, and validation
  - Uses an in-memory `MutableMap<String, OtpData>` as a simulated backend
  - Responsible for expiry checks and attempt limits

### ViewModel Layer
- **AuthViewModel**
  - Manages UI state via `AuthState`
  - Coordinates OTP logic with `OtpManager`
  - Controls session timing using coroutines

### UI Layer
- Built with Jetpack Compose
- Screens:
  - LoginScreen
  - OtpScreen
  - SessionScreen
- Navigation is handled by swapping composables based on the current `AuthState` inside `MainActivity`

---

## Data Structures

### OtpData
Stores OTP-related information:
- `otp: String`
- `expiryTime: Long`
- `attempts: Int`

### otpStore
- Defined in `OtpManager`
- Type: `mutableMapOf<String, OtpData>`
- Acts as a transient, in-memory database keyed by email

---

## Analytics & Logging

The app uses Timber for logging, initialized in `PasswordlessAuthApplication`.

### Logged Events
- OTP generation
- OTP validation success
- OTP validation failure
- OTP expiry
- Logout events

---

## Logic & Edge Cases

### OTP Expiry
- OTPs are valid for 60 seconds
- Expiry is checked using `System.currentTimeMillis()` against `expiryTime`

### Attempt Limits
- Maximum 3 attempts per OTP
- Exceeding the limit blocks validation until a new OTP is requested

### Session Timer
- The Session screen displays a live session duration (`mm:ss`)
- Updated every second using a coroutine in `AuthViewModel`
- Timer survives configuration changes

### Rotation Handling
- User inputs (`email`, `otp`) use `rememberSaveable`
- Session timer state is preserved in the ViewModel

---

## Setup & Build

1. Open the project in Android Studio
2. Sync Gradle with the project files
3. Run the app on an emulator or physical device
4. Open Logcat and filter by:
   - `Timber`
   - `PasswordlessAuth`

OTP values are logged for testing purposes.

---

## How to Test

### Login
1. Enter an email address
2. Click **Send OTP**

### Enter OTP
- **Success**
- Enter the correct OTP
- Navigates to the Session screen
- **Failure**
- Enter an incorrect OTP
- Error message is displayed
- **Expired**
- Wait more than 60 seconds
- Error message: `OTP Expired`
- **Too Many Attempts**
- Enter the wrong OTP 3 times
- Error message: `Too many attempts`

### Session
- Observe the session timer counting up
- Click **Logout** to reset the app state

### Retrieve OTP
- Check Logcat for:

---

## Demo

**Project Demo Video:**  
https://drive.google.com/file/d/16xJHSi8HTjfBcXncBryQ6DwwTFLPOgk3/view?usp=sharing
