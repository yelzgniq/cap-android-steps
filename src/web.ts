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
    const now = Date.now();
    let startTime: number | null = null;
    
    if (period === 'hour') {
      startTime = now - (60 * 60 * 1000); // 1 hour ago
    } else if (period === 'day') {
      startTime = now - (24 * 60 * 60 * 1000); // 24 hours ago
    } else if (period === 'all') {
      startTime = now - (7 * 24 * 60 * 60 * 1000); // Simulate a week ago as boot time
    }
    
    return {
      count: 0,
      period,
      startTime,
      endTime: now
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
