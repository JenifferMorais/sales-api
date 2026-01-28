import { Theme } from '../app/shared/services/theme/theme.service';

export const environment = {
  production: true,
  // Replaced at container startup by `sales-web/docker/docker-entrypoint.sh` using the `API_URL` env var.
  apiUrl: '__API_URL__',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme
};
