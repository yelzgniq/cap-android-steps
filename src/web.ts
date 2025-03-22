import { WebPlugin } from '@capacitor/core';

import type {
  InvertStringOptions,
  InvertStringResult,
  PermissionResult,
  CapAndroidStepsPlugin,
  SensorValuesResult,
  StepCountResult,
  StepPeriodOptions,
} from './definitions';

export class CapAndroidStepsWeb
  extends WebPlugin
  implements CapAndroidStepsPlugin
{

  public async invertString(options: InvertStringOptions): Promise<InvertStringResult> {
    const { value } = options;
    if (!value) {
      throw new Error('Must provide a string value');
    }
    return {
      value: value.split('').reverse().join('')
    };
  }

  public async requestActivityRecognitionPermission(): Promise<PermissionResult> {
    // This is just a stub for web - activity recognition is not supported in browsers
    return {
      granted: true
    };
  }

  public async getStepsForPeriod(options?: StepPeriodOptions): Promise<StepCountResult> {
    // Web implementation cannot access device step counters
    // Return a dummy value of 0 steps
    const period = options?.period || 'day';
    return {
      count: 0,
      period
    };
  }

  public async getRawSensorValues(): Promise<SensorValuesResult> {
    // Web implementation cannot access device sensors
    // Return dummy values
    return {
      values: { "0": 0 },
      stepCount: 0,
      sensorName: "Web (Not Available)",
      sensorVendor: "Web (Not Available)",
      sensorVersion: 0
    };
  }
}
