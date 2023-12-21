import { PlatformLocation } from '@angular/common';

/**
 * Trim last slash from URL
 * @param baseUrl - Base URL
 * @returns string | null
 */
export const trimLastSlashFromUrl = (baseUrl: string): string | null => {
  if (baseUrl &&
      !baseUrl.includes('/spartacus/') &&
      baseUrl[baseUrl.length - 1] === '/'
  ) {
    return baseUrl.substring(0, baseUrl.length - 1);
  }
  return baseUrl;
};

export const getBaseHref = (platformLocation: PlatformLocation): string => trimLastSlashFromUrl(platformLocation.getBaseHrefFromDOM());
