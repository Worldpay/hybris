import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { LoggerService, WindowRef } from '@spartacus/core';
import { WorldpayThreedsChallengeIframePageComponent } from './worldpay-threeds-challenge-iframe-page.component';

class DomSanitizerStub {
  bypassSecurityTrustResourceUrl(url: string) {
    return url;
  }
}

describe('WorldpayThreedsChallengeIframePageComponent', () => {
  let component: WorldpayThreedsChallengeIframePageComponent;
  let fixture: ComponentFixture<WorldpayThreedsChallengeIframePageComponent>;
  let activatedRoute: ActivatedRoute;
  let sanitizer: DomSanitizer;
  let logger: LoggerService;
  let windowRef: WindowRef;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorldpayThreedsChallengeIframePageComponent],
      providers: [
        DomSanitizer,
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
                md: 'test-md-value',
                jwt: 'test-jwt-value'
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorldpayThreedsChallengeIframePageComponent);
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
      expect(component.md).toBe('test-md-value');
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

    it('should handle missing md query parameter', () => {
      (activatedRoute.snapshot.queryParams as any)['md'] = undefined;

      component.ngOnInit();

      expect(component.md).toBeUndefined();
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
      expect(component.md).toBeUndefined();
      expect(component.jwt).toBeUndefined();
    });
  });

  describe('ngAfterViewInit', () => {
    it('should skip form submission during server-side rendering', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(logger, 'log');

      component.ngAfterViewInit();

      expect(logger.log).toHaveBeenCalledWith('SSR - skipping WorldpayThreedsChallengeIframePageComponent After View Init');
    });

    it('should find and submit challenge form in browser environment', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockForm = document.createElement('form');
      const mockSubmit = spyOn(mockForm, 'submit');
      const mockDocument = {
        querySelector: jasmine.createSpy('querySelector').and.returnValue(mockForm)
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument
      });

      component.ngAfterViewInit();

      expect(mockSubmit).toHaveBeenCalled();
    });

    it('should handle missing challenge form element', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockDocument = {
        querySelector: jasmine.createSpy('querySelector').and.returnValue(null)
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument
      });
      
      expect(() => component.ngAfterViewInit()).not.toThrow();
    });

    it('should query for correct form selector', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockDocument = {
        querySelector: jasmine.createSpy('querySelector').and.returnValue(null)
      };
      Object.defineProperty(windowRef, 'document', {
        get: () => mockDocument
      });

      component.ngAfterViewInit();

      const querySelectorSpy = (windowRef.document as any).querySelector as jasmine.Spy;
      expect(querySelectorSpy).toHaveBeenCalledWith('#challengeForm');
    });
  });

  describe('action property', () => {
    it('should be of type SafeResourceUrl', () => {
      component.ngOnInit();

      expect(component.action).toBeTruthy();
    });
  });

  describe('md property', () => {
    it('should store the md challenge value', () => {
      component.ngOnInit();

      expect(component.md).toBe('test-md-value');
    });

    it('should be string type', () => {
      component.ngOnInit();

      expect(typeof component.md).toBe('string');
    });
  });

  describe('jwt property for 3dS', () => {
    it('should store the jwt value', () => {
      component.ngOnInit();

      expect(component.jwt).toBe('test-jwt-value');
    });

    it('should be string type for 3ds', () => {
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
        get: () => mockDocument
      });

      component.ngOnInit();
      component.ngAfterViewInit();

      expect(component.md).toBe('test-md-value');
      expect(component.jwt).toBe('test-jwt-value');
      expect(mockSubmit).toHaveBeenCalled();
    });

    it('should initialize component without form interaction on server', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(logger, 'log');

      component.ngOnInit();
      component.ngAfterViewInit();

      expect(component.md).toBe('test-md-value');
      expect(component.jwt).toBe('test-jwt-value');
      expect(logger.log).toHaveBeenCalledWith('SSR - skipping WorldpayThreedsChallengeIframePageComponent After View Init');
    });
  });
});