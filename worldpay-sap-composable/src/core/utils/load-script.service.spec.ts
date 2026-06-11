import { TestBed } from '@angular/core/testing';
import { LoggerService, WindowRef } from '@spartacus/core';
import { LoadScriptService } from './load-script.service';
import createSpy = jasmine.createSpy;

describe('LoadScriptService', () => {
  let service: LoadScriptService;
  let logger: LoggerService;
  let windowRef: WindowRef;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoadScriptService, LoggerService, WindowRef]
    });
    service = TestBed.inject(LoadScriptService);
    logger = TestBed.inject(LoggerService);
    windowRef = TestBed.inject(WindowRef);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  describe('loadScript', () => {
    it('should skip loading script during SSR', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(logger, 'log');

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js'
      });

      expect(logger.log).toHaveBeenCalledWith('LoadScript service not loaded as test is running or SSR is mode enabled.');
    });

    it('should not load script if already loaded', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScript = document.createElement('script');
      mockScript.setAttribute('src', 'https://example.com/script.js');

      const mockScripts: any = {
        length: 1,
        0: mockScript,
        item: () => mockScript
      };
      const mockHeadElement = {
        appendChild: jasmine.createSpy('appendChild')
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js'
      });

      expect(mockHeadElement.appendChild).not.toHaveBeenCalled();
    });

    it('should load script if not already loaded', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      const mockHeadElement = {
        appendChild: jasmine.createSpy('appendChild')
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });
      windowRef.document.createElement('script');
      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js'
      });

      expect(mockHeadElement.appendChild).toHaveBeenCalled();
    });

    it('should set script source and id', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });
      service.loadScript({
        idScript: 'my-script-id',
        src: 'https://example.com/script.js'
      });

      expect(appendedScript?.id).toBe('my-script-id');
      expect(appendedScript?.src).toContain('https://example.com/script.js');
      expect(appendedScript?.type).toBe('text/javascript');
    });

    it('should generate random id when not provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: '',
        src: 'https://example.com/script.js'
      });

      expect(appendedScript?.id).toBeTruthy();
      expect(!isNaN(parseInt(appendedScript?.id || ''))).toBe(true);
    });

    it('should set async attribute when provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js',
        async: true
      });

      expect(appendedScript?.async).toBe(true);
    });

    it('should set defer attribute when provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js',
        defer: true
      });

      expect(appendedScript?.defer).toBe(true);
    });

    it('should set onload callback when provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      const onloadCallback = createSpy('onloadCallback');
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js',
        onloadCallback
      });

      expect(appendedScript?.onload).toBe(onloadCallback);
    });

    it('should apply custom attributes to script element', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js',
        attributes: {
          'data-custom': 'value',
          'integrity': 'sha384-test'
        }
      });

      expect(appendedScript?.getAttribute('data-custom')).toBe('value');
      expect(appendedScript?.getAttribute('integrity')).toBe('sha384-test');
    });

    it('should handle multiple script loads with different sources', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendCount = 0;
      const mockHeadElement = {
        appendChild: () => {
          appendCount++;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'script1',
        src: 'https://example.com/script1.js'
      });
      service.loadScript({
        idScript: 'script2',
        src: 'https://example.com/script2.js'
      });

      expect(appendCount).toBe(2);
    });

    it('should not set async or defer when not provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js'
      });

      expect(appendedScript.getAttribute('async')).toBe(null);
      expect(appendedScript.getAttribute('defer')).toBe(null);
    });

    it('should set async or defer when provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScripts: any = {
        length: 0
      };
      let appendedScript: HTMLScriptElement | null = null;
      const mockHeadElement = {
        appendChild: (script: HTMLScriptElement) => {
          appendedScript = script;
        }
      };

      spyOn(windowRef.document, 'getElementsByTagName').and.callFake((tagName: string) => {
        if (tagName === 'script') {
          return mockScripts;
        }
        return [mockHeadElement] as any;
      });

      service.loadScript({
        idScript: 'test-script',
        src: 'https://example.com/script.js',
        async: true,
        defer: true
      });

      expect(appendedScript.getAttribute('async')).toBeDefined();
      expect(appendedScript.getAttribute('defer')).toBeDefined();
    });
  });

  describe('removeScript', () => {
    it('should skip removing script during SSR', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(logger, 'log');

      service.removeScript('test-script');

      expect(logger.log).toHaveBeenCalledWith('LoadScript service not loaded as test is running or SSR is mode enabled.');
    });

    it('should remove script element when found', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockScript = {
        remove: jasmine.createSpy('remove')
      };

      spyOn(windowRef.document, 'querySelector').and.returnValue(mockScript as any);

      service.removeScript('test-script');

      expect(mockScript.remove).toHaveBeenCalled();
    });

    it('should query for correct script selector', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const querySelectorSpy = spyOn(windowRef.document, 'querySelector').and.returnValue(null);

      service.removeScript('my-script-id');

      expect(querySelectorSpy).toHaveBeenCalledWith('script#my-script-id');
    });

    it('should handle case when script element is not found', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      spyOn(windowRef.document, 'querySelector').and.returnValue(null);

      expect(() => service.removeScript('nonexistent-script')).not.toThrow();
    });
  });

  describe('updateScript', () => {
    it('should add attributes to script node', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockNode = document.createElement('script');

      service.updateScript(mockNode, {
        'data-custom': 'value',
        'crossorigin': 'anonymous'
      });

      expect(mockNode.getAttribute('data-custom')).toBe('value');
      expect(mockNode.getAttribute('crossorigin')).toBe('anonymous');
    });

    it('should return updated node', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockNode = document.createElement('script');

      const result = service.updateScript(mockNode, { 'test': 'attribute' });

      expect(result).toBe(mockNode);
    });

    it('should return node unchanged when not in browser environment', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      const mockNode = document.createElement('script');

      const result = service.updateScript(mockNode, { 'test': 'attribute' });

      expect(result).toBe(mockNode);
    });

    it('should return node unchanged when attributes are not provided', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockNode = document.createElement('script');

      const result = service.updateScript(mockNode, {});

      expect(result).toBe(mockNode);
    });

    it('should return node unchanged when node is null', () => {
      const result = service.updateScript(null, { 'test': 'attribute' });

      expect(result).toBeNull();
    });

    it('should handle multiple attributes in single call', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      const mockNode = document.createElement('script');

      service.updateScript(mockNode, {
        'attr1': 'value1',
        'attr2': 'value2',
        'attr3': 'value3'
      });

      expect(mockNode.getAttribute('attr1')).toBe('value1');
      expect(mockNode.getAttribute('attr2')).toBe('value2');
      expect(mockNode.getAttribute('attr3')).toBe('value3');
    });
  });
});