import { WindowRef } from '@spartacus/core';
import { createApplePaySession } from './worldpay-applepay-session';

describe('createApplePaySession', () => {
  let windowRefMock: jasmine.SpyObj<WindowRef>;

  beforeEach(() => {
    windowRefMock = jasmine.createSpyObj('WindowRef', ['isBrowser'], {
      nativeWindow: {}
    });
  });

  it('should return null if not in browser environment', () => {
    windowRefMock.isBrowser.and.returnValue(false);

    const result = createApplePaySession(windowRefMock);

    expect(windowRefMock.isBrowser).toHaveBeenCalled();
    expect(result).toBeNull();
  });

  it('should return ApplePaySession if in browser and ApplePaySession is supported', () => {
    windowRefMock.isBrowser.and.returnValue(true);
    windowRefMock.nativeWindow['ApplePaySession'] = 'MockApplePaySession';

    const result = createApplePaySession(windowRefMock);

    expect(windowRefMock.isBrowser).toHaveBeenCalled();
    expect(result).toBe('MockApplePaySession');
  });

  it('should return null if in browser but ApplePaySession is not supported', () => {
    windowRefMock.isBrowser.and.returnValue(true);
    windowRefMock.nativeWindow['ApplePaySession'] = undefined;

    const result = createApplePaySession(windowRefMock);

    expect(windowRefMock.isBrowser).toHaveBeenCalled();
    expect(result).toBeUndefined();
  });
});