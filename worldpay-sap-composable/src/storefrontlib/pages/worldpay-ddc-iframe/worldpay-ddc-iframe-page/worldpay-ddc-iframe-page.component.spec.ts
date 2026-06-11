import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { LoggerService, WindowRef } from '@spartacus/core';
import { WorldpayDdcIframePageComponent } from './worldpay-ddc-iframe-page.component';

class DomSanitizerStub {
  bypassSecurityTrustResourceUrl(url: string) {
    return url;
  }
}

describe('WorldpayDdcIframePageComponent', () => {
  let component: WorldpayDdcIframePageComponent;
  let fixture: ComponentFixture<WorldpayDdcIframePageComponent>;
  let activatedRoute: ActivatedRoute;
  let sanitizer: DomSanitizer;
  let logger: LoggerService;
  let windowRef: WindowRef;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorldpayDdcIframePageComponent],
      providers: [
        LoggerService,
        WindowRef,
        {
          provide: DomSanitizer,
          useClass: DomSanitizerStub
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: {
                action: 'https://example.com/action',
                bin: 'test-bin-value',
                jwt: 'test-jwt-value'
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorldpayDdcIframePageComponent);
    component = fixture.componentInstance;
    activatedRoute = TestBed.inject(ActivatedRoute);
    sanitizer = TestBed.inject(DomSanitizer);
    logger = TestBed.inject(LoggerService);
    windowRef = TestBed.inject(WindowRef);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should extract all query parameters from route', () => {
      component.ngOnInit();

      expect(component.action).toBeTruthy();
      expect(component.bin).toBe('test-bin-value');
      expect(component.jwt).toBe('test-jwt-value');
    });

    it('should sanitize action URL for security', () => {
      spyOn(sanitizer, 'bypassSecurityTrustResourceUrl').and.callThrough();

      component.ngOnInit();

      expect(sanitizer.bypassSecurityTrustResourceUrl).toHaveBeenCalledWith('https://example.com/action');
    });

    it('should handle missing action query parameter', () => {
      (activatedRoute.snapshot.queryParams as any)['action'] = undefined;

      component.ngOnInit();

      expect(component.action).toBeFalsy();
    });

    it('should handle missing bin query parameter', () => {
      (activatedRoute.snapshot.queryParams as any)['bin'] = undefined;

      component.ngOnInit();

      expect(component.bin).toBeUndefined();
    });

    it('should handle missing jwt query parameter', () => {
      (activatedRoute.snapshot.queryParams as any)['jwt'] = undefined;

      component.ngOnInit();

      expect(component.jwt).toBeUndefined();
    });

    it('should handle empty query parameters object', () => {
      (activatedRoute.snapshot.queryParams as any) = {};

      component.ngOnInit();

      expect(component.action).toBeFalsy();
      expect(component.bin).toBeUndefined();
      expect(component.jwt).toBeUndefined();
    });
  });

  describe('ngAfterViewInit', () => {
    it('should skip form submission during server-side rendering', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(logger, 'log');

      component.ngAfterViewInit();

      expect(logger.log).toHaveBeenCalledWith('SSR - skipping WorldpayDdcIframePageComponent After View Init');
    });

    it('should find and submit collection form in browser environment', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockForm = document.createElement('form');
      const mockSubmit = spyOn(mockForm, 'submit');

      const mockDocument = {
        querySelector: jasmine.createSpy('querySelector').and.returnValue(mockForm)
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument,
        configurable: true
      });

      component.ngAfterViewInit();

      expect(mockSubmit).toHaveBeenCalled();
    });

    it('should handle missing collection form element', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);

      const mockDocument = {
        querySelector: jasmine.createSpy('querySelector').and.returnValue(null)
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument,
        configurable: true
      });

      expect(() => component.ngAfterViewInit()).not.toThrow();
    });

    it('should query for correct form selector', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);

      const querySelectorSpy = jasmine.createSpy('querySelector').and.returnValue(null);
      const mockDocument = {
        querySelector: querySelectorSpy
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument,
        configurable: true
      });

      component.ngAfterViewInit();

      expect(querySelectorSpy).toHaveBeenCalledWith('#collectionForm');
    });
  });

  describe('action property', () => {
    it('should be of type SafeResourceUrl', () => {
      component.ngOnInit();

      expect(component.action).toBeTruthy();
    });
  });

  describe('bin property', () => {
    it('should store the bin value', () => {
      component.ngOnInit();

      expect(component.bin).toBe('test-bin-value');
    });

    it('should be string type', () => {
      component.ngOnInit();

      expect(typeof component.bin).toBe('string');
    });
  });

  describe('jwt property', () => {
    it('should store the jwt value', () => {
      component.ngOnInit();

      expect(component.jwt).toBe('test-jwt-value');
    });

    it('should be string type for iFrame', () => {
      component.ngOnInit();

      expect(typeof component.jwt).toBe('string');
    });
  });

  describe('complete workflow', () => {
    it('should initialize component and prepare for form submission on browser', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockForm = document.createElement('form');
      const mockSubmit = spyOn(mockForm, 'submit');

      const mockDocument = {
        querySelector: jasmine.createSpy('querySelector').and.returnValue(mockForm)
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument,
        configurable: true
      });

      component.ngOnInit();
      component.ngAfterViewInit();

      expect(component.bin).toBe('test-bin-value');
      expect(component.jwt).toBe('test-jwt-value');
      expect(mockSubmit).toHaveBeenCalled();
    });

    it('should initialize component without form interaction on server', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(logger, 'log');

      component.ngOnInit();
      component.ngAfterViewInit();

      expect(component.bin).toBe('test-bin-value');
      expect(component.jwt).toBe('test-jwt-value');
      expect(logger.log).toHaveBeenCalledWith('SSR - skipping WorldpayDdcIframePageComponent After View Init');
    });
  });
});