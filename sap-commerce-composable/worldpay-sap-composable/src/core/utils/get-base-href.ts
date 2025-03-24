import { PlatformLocation } from '@angular/common';

/**
 * Trim last slash from URL
 * @param baseUrl - Base URL
 * @returns string | null
 */
// eslint-disable-next-line @typescript-eslint/typedef
export const trimLastSlashFromUrl: (baseUrl: string) => string | null = (baseUrl: string): string | null => {
  if (baseUrl &&
        !baseUrl.includes('/spartacus/') &&
        baseUrl[baseUrl.length - 1] === '/'
  ) {
    return baseUrl.substring(0, baseUrl.length - 1);
  }
  return baseUrl;
};

// eslint-disable-next-line @typescript-eslint/typedef
export const getBaseHref: (platformLocation: PlatformLocation) => string =
  (platformLocation: PlatformLocation): string => trimLastSlashFromUrl(platformLocation.getBaseHrefFromDOM());
