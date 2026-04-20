import { HttpHeaders } from '@angular/common/http';
import { InterceptorUtil, OCC_USER_ID_ANONYMOUS, USE_CLIENT_TOKEN } from '@spartacus/core';

/**
 * Retrieves the HTTP headers for the specified user ID.
 *
 * @param {string} userId - The ID of the user.
 * @param {string} [contentType='application/json'] - The content type for the headers.
 * @returns {HttpHeaders} The HTTP headers for the specified user ID.
 * @since 2211.31.1
 */
export const getHeadersForUserId: (userId: string, contentType?: string) => HttpHeaders  = (userId: string, contentType: string = 'application/json'): HttpHeaders => {
  let headers: HttpHeaders = new HttpHeaders({
    'Content-Type': contentType,
  });

  if (userId === OCC_USER_ID_ANONYMOUS) {
    headers = InterceptorUtil.createHeader(USE_CLIENT_TOKEN, true, headers);
  }

  return headers;
};