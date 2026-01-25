import { Theme } from '../app/shared/services/theme/theme.service';

export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme
};
