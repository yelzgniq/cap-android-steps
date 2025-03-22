# CapAndroidSteps Plugin Knowledge

## Plugin Overview
- The CapAndroidSteps plugin (previously named ScreenOrientation) provides Android step counting functionality through a Capacitor plugin
- The plugin interfaces with the Android STEP_COUNTER sensor
- It requires ACTIVITY_RECOGNITION permission on Android 10+ devices

## Files Structure
- Android implementation is in Java: `/android/src/main/java/com/kingsleymichael/capandroidsteps/`
- iOS implementation in Swift: `/ios/Plugin/`
- TypeScript interface: `/src/definitions.ts`
- Web implementation: `/src/web.ts`

## Methods
- `invertString()`: Utility method to invert a string
- `requestActivityRecognitionPermission()`: Requests permission for step counting
- `getStepsForPeriod()`: Gets step count for hour/day/all
- `getRawSensorValues()`: Gets raw step sensor values

## Known Issues
- Web implementation uses type assertions (`as any`) to work around conflicts with built-in TypeScript DOM lib definitions
- The plugin is primarily focused on Android step counting; iOS and web implementations provide basic orientation support 