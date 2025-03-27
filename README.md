## CapAndroidSteps

<h3 align="center">CapAndroidSteps</h3>
<p align="center"><strong><code>cap-android-steps</code></strong></p>
<p align="center">
  Capacitor plugin for counting steps on Android devices using the Android ACTIVITY_RECOGNITION permission or sensor. This plugin can function independently without requiring Health Connect.
</p>

## Installation

```bash
npm install cap-android-steps
npx cap sync
```

### Android

Ensure your app has the `ACTIVITY_RECOGNITION` permission in your `AndroidManifest.xml` for Android 10+ devices.

## Configuration

No configuration required for this plugin.

## Demo

TBD

## Usage

```typescript
import { CapAndroidSteps } from 'cap-android-steps';

const requestPermission = async () => {
  const result = await CapAndroidSteps.requestActivityRecognitionPermission();
  return result.granted;
};

const getStepsForPeriod = async (period: 'hour' | 'day' | 'all') => {
  const result = await CapAndroidSteps.getStepsForPeriod({ period });
  return result.count;
};

const getRawSensorValues = async () => {
  const result = await CapAndroidSteps.getRawSensorValues();
  return result;
};
```

## API

<docgen-index>

* [`invertString(...)`](#invertstring)
* [`requestActivityRecognitionPermission()`](#requestactivityrecognitionpermission)
* [`getStepsForPeriod(...)`](#getstepsforperiod)
* [`getRawSensorValues()`](#getrawsensorvalues)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### invertString(...)

```typescript
invertString(options: InvertStringOptions) => Promise<InvertStringResult>
```

Inverts the provided string (reverses it).
This method has nothing to do with screen orientation.

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#invertstringoptions">InvertStringOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#invertstringresult">InvertStringResult</a>&gt;</code>

--------------------


### requestActivityRecognitionPermission()

```typescript
requestActivityRecognitionPermission() => Promise<PermissionResult>
```

Requests the ACTIVITY_RECOGNITION permission on Android 10+ devices.
This permission is required for step counting functionality.

**Returns:** <code>Promise&lt;<a href="#permissionresult">PermissionResult</a>&gt;</code>

--------------------


### getStepsForPeriod(...)

```typescript
getStepsForPeriod(options?: StepPeriodOptions | undefined) => Promise<StepCountResult>
```

Gets the step count for a specified period.
Requires activity recognition permission on Android 10+.

| Param         | Type                                                            |
| ------------- | --------------------------------------------------------------- |
| **`options`** | <code><a href="#stepperiodoptions">StepPeriodOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#stepcountresult">StepCountResult</a>&gt;</code>

--------------------


### getRawSensorValues()

```typescript
getRawSensorValues() => Promise<SensorValuesResult>
```

Gets the raw sensor values from the step counter sensor.
Requires activity recognition permission on Android 10+.

**Returns:** <code>Promise&lt;<a href="#sensorvaluesresult">SensorValuesResult</a>&gt;</code>

--------------------


### Interfaces


#### InvertStringResult

| Prop        | Type                | Description          |
| ----------- | ------------------- | -------------------- |
| **`value`** | <code>string</code> | The inverted string. |


#### InvertStringOptions

| Prop        | Type                | Description           |
| ----------- | ------------------- | --------------------- |
| **`value`** | <code>string</code> | The string to invert. |


#### PermissionResult

| Prop          | Type                 | Description                         |
| ------------- | -------------------- | ----------------------------------- |
| **`granted`** | <code>boolean</code> | Whether the permission was granted. |


#### StepCountResult

| Prop            | Type                                  | Description                                                                                   |
| --------------- | ------------------------------------- | --------------------------------------------------------------------------------------------- |
| **`count`**     | <code>number</code>                   | The number of steps counted during the specified period.                                      |
| **`period`**    | <code>'hour' \| 'day' \| 'all'</code> | The period for which the step count was calculated.                                           |
| **`startTime`** | <code>number \| null</code>           | The start time of the period in milliseconds. For 'all' period, this is the device boot time. |
| **`endTime`**   | <code>number</code>                   | The end time of the period in milliseconds (current time).                                    |


#### StepPeriodOptions

| Prop         | Type                                  | Description                                                                                                                                                                                                                                                                  |
| ------------ | ------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`period`** | <code>'hour' \| 'day' \| 'all'</code> | The period for which to get step data. Valid values are "hour", "day", or "all". - "hour": Steps taken in the last hour - "day": Steps taken in the last 24 hours - "all": All steps recorded since device boot (raw step counter value) Defaults to "day" if not specified. |


#### SensorValuesResult

| Prop                | Type                                                            | Description                                                                                                                                                                 |
| ------------------- | --------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`values`**        | <code><a href="#record">Record</a>&lt;string, number&gt;</code> | Object containing the raw sensor values. The step counter sensor typically only has one value at index "0", but this provides access to all values the sensor might report. |
| **`stepCount`**     | <code>number</code>                                             | The step count value (same as values["0"] for convenience).                                                                                                                 |
| **`sensorName`**    | <code>string</code>                                             | The name of the step counter sensor.                                                                                                                                        |
| **`sensorVendor`**  | <code>string</code>                                             | The vendor of the step counter sensor.                                                                                                                                      |
| **`sensorVersion`** | <code>number</code>                                             | The version of the step counter sensor.                                                                                                                                     |


### Type Aliases


#### Record

Construct a type with a set of properties K of type T

<code>{
 [P in K]: T;
 }</code>

</docgen-api>


## License

See [LICENSE](https://github.com/yelzgniq/cap-android-steps/blob/master/LICENSE).
