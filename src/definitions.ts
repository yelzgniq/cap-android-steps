export interface CapAndroidStepsPlugin {
  /**
   * Inverts the provided string (reverses it).
   * This method has nothing to do with screen orientation.
   */
  invertString(options: InvertStringOptions): Promise<InvertStringResult>;
  /**
   * Requests the ACTIVITY_RECOGNITION permission on Android 10+ devices.
   * This permission is required for step counting functionality.
   */
  requestActivityRecognitionPermission(): Promise<PermissionResult>;
  /**
   * Gets the step count for a specified period.
   * Requires activity recognition permission on Android 10+.
   */
  getStepsForPeriod(options?: StepPeriodOptions): Promise<StepCountResult>;
  /**
   * Gets the raw sensor values from the step counter sensor.
   * Requires activity recognition permission on Android 10+.
   */
  getRawSensorValues(): Promise<SensorValuesResult>;
}

export interface InvertStringOptions {
  /**
   * The string to invert.
   */
  value: string;
}

export interface InvertStringResult {
  /**
   * The inverted string.
   */
  value: string;
}

export interface PermissionResult {
  /**
   * Whether the permission was granted.
   */
  granted: boolean;
}

export interface StepPeriodOptions {
  /**
   * The period for which to get step data.
   * Valid values are "hour", "day", or "all".
   * - "hour": Steps taken in the last hour
   * - "day": Steps taken in the last 24 hours
   * - "all": All steps recorded since device boot (raw step counter value)
   * Defaults to "day" if not specified.
   */
  period?: 'hour' | 'day' | 'all';
}

export interface StepCountResult {
  /**
   * The number of steps counted during the specified period.
   */
  count: number;
  /**
   * The period for which the step count was calculated.
   */
  period: 'hour' | 'day' | 'all';
  /**
   * The start time of the period in milliseconds.
   * For 'all' period, this is the device boot time.
   */
  startTime: number | null;
  /**
   * The end time of the period in milliseconds (current time).
   */
  endTime: number;
}

export interface SensorValuesResult {
  /**
   * Object containing the raw sensor values.
   * The step counter sensor typically only has one value at index "0",
   * but this provides access to all values the sensor might report.
   */
  values: Record<string, number>;
  
  /**
   * The step count value (same as values["0"] for convenience).
   */
  stepCount: number;
  
  /**
   * The name of the step counter sensor.
   */
  sensorName: string;
  
  /**
   * The vendor of the step counter sensor.
   */
  sensorVendor: string;
  
  /**
   * The version of the step counter sensor.
   */
  sensorVersion: number;
}
