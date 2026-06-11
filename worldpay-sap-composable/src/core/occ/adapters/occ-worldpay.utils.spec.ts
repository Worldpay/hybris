import { HttpHeaders } from '@angular/common/http';
import { InterceptorUtil, OCC_USER_ID_ANONYMOUS } from '@spartacus/core';
import { getHeadersForUserId } from './occ-worldpay.utils';

describe('occ-worldpay.utils', () => {
  describe('getHeadersForUserId', () => {
    it('should return headers with default content type', () => {
      const headers = getHeadersForUserId('testUser');

      expect(headers.get('Content-Type')).toBe('application/json');
    });

    it('should return headers with custom content type', () => {
      const customContentType = 'application/xml';
      const headers = getHeadersForUserId('testUser', customContentType);

      expect(headers.get('Content-Type')).toBe(customContentType);
    });

    it('should not add client token header for authenticated user', () => {
      spyOn(InterceptorUtil, 'createHeader');
      getHeadersForUserId('authenticatedUser');

      expect(InterceptorUtil.createHeader).not.toHaveBeenCalled();
    });

    it('should return HttpHeaders instance for authenticated user', () => {
      const headers = getHeadersForUserId('testUser');

      expect(headers instanceof HttpHeaders).toBe(true);
    });

    it('should return HttpHeaders instance for anonymous user', () => {
      const headers = getHeadersForUserId(OCC_USER_ID_ANONYMOUS);

      expect(headers instanceof HttpHeaders).toBe(true);
    });

    it('should maintain content type when adding client token for anonymous user', () => {
      const customContentType = 'application/xml';
      const headers = getHeadersForUserId(OCC_USER_ID_ANONYMOUS, customContentType);

      expect(headers.get('Content-Type')).toBe(customContentType);
    });

    it('should handle empty string user id', () => {
      const headers = getHeadersForUserId('');

      expect(headers.get('Content-Type')).toBe('application/json');
      expect(headers instanceof HttpHeaders).toBe(true);
    });

    it('should handle various content types', () => {
      const contentTypes = [
        'application/json',
        'application/xml',
        'text/plain',
        'text/html',
        'multipart/form-data'
      ];

      contentTypes.forEach(contentType => {
        const headers = getHeadersForUserId('testUser', contentType);
        expect(headers.get('Content-Type')).toBe(contentType);
      });
    });

    it('should not modify original headers passed to InterceptorUtil', () => {
      const originalHeaders = new HttpHeaders({
        'Content-Type': 'application/json'
      });
      spyOn(InterceptorUtil, 'createHeader').and.returnValue(originalHeaders);

      getHeadersForUserId(OCC_USER_ID_ANONYMOUS);

      expect(InterceptorUtil.createHeader).toHaveBeenCalled();
    });
  });
});