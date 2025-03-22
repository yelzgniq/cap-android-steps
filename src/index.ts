import { registerPlugin } from '@capacitor/core';

import type { CapAndroidStepsPlugin } from './definitions';

const CapAndroidSteps = registerPlugin<CapAndroidStepsPlugin>(
  'CapAndroidSteps',
  {
    web: () => import('./web').then(m => new m.CapAndroidStepsWeb()),
  },
);

export * from './definitions';
export { CapAndroidSteps };
