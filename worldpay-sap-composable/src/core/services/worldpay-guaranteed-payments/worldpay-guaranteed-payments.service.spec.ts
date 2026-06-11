import { TestBed } from '@angular/core/testing';
import { EventService, QueryService, QueryState, WindowRef } from '@spartacus/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsConnector } from '../../connectors';
import { SetGuaranteedPaymentsSessionIdEvent } from '../../events';
import { LoadScriptService } from '../../utils';
import { WorldpayGuaranteedPaymentsService } from './worldpay-guaranteed-payments.service';
import createSpy = jasmine.createSpy;

class MockWorldpayGuaranteedPaymentsConnector implements Partial<WorldpayGuaranteedPaymentsConnector> {
  isGuaranteedPaymentsEnabled = createSpy().and.returnValue(of(true));

  setGuaranteedPaymentsEnabledEvent = createSpy().and.returnValue(of(true));
}

describe('WorldpayGuaranteedPaymentsService', () => {
  let service: WorldpayGuaranteedPaymentsService;
  let connector: WorldpayGuaranteedPaymentsConnector;
  let loadScriptService: jasmine.SpyObj<LoadScriptService>;
  let winRef: WindowRef;
  let eventService: EventService;

  beforeEach(() => {
    const loadScriptServiceMock: jasmine.SpyObj<LoadScriptService> = jasmine.createSpyObj('LoadScriptService', [
      'loadScript',
      'updateScript',
      'removeScript'
    ]);

    TestBed.configureTestingModule({
      providers: [
        {
          provide: LoadScriptService,
          useValue: loadScriptServiceMock
        },
        WindowRef,
        QueryService,
        WorldpayGuaranteedPaymentsService,
        EventService,
        {
          provide: WorldpayGuaranteedPaymentsConnector,
          useClass: MockWorldpayGuaranteedPaymentsConnector
        },
      ]
    });
    service = TestBed.inject(WorldpayGuaranteedPaymentsService);
    connector = TestBed.inject(WorldpayGuaranteedPaymentsConnector);
    loadScriptService = TestBed.inject(LoadScriptService) as jasmine.SpyObj<LoadScriptService>;
    winRef = TestBed.inject(WindowRef);
    eventService = TestBed.inject(EventService);
  });

  beforeEach(() => {
    winRef.document.querySelectorAll('script').forEach((el) => el.remove());
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('GuaranteedPayments Enabled', () => {
    let enabled = false;

    it('should call isGuaranteedPaymentsEnabledState method', () => {
      spyOn(service, 'isGuaranteedPaymentsEnabledState').and.callThrough();
      service.isGuaranteedPaymentsEnabledState()
        .subscribe((response: QueryState<boolean>) => {
          enabled = response.data;
        });

      expect(enabled).toBeTrue();
      expect(service.isGuaranteedPaymentsEnabledState).toHaveBeenCalled();
      expect(connector.isGuaranteedPaymentsEnabled).toHaveBeenCalled();
    });

    it('should get isGuaranteedPaymentsEnabled value using isGuaranteedPaymentsEnabled method', () => {
      service.isGuaranteedPaymentsEnabled().subscribe((response) => {
        enabled = response;
      });

      expect(enabled).toBeTrue();
    });

    it('should update isGuaranteedPaymentsEnabled value using setGuaranteedPaymentsEnabledEvent method', () => {
      service.isGuaranteedPaymentsEnabledState().subscribe().unsubscribe();
      service.isGuaranteedPaymentsEnabled().subscribe((response) => {
        enabled = response;
      });

      expect(enabled).toBeTrue();
    });

    it('should trigger getGuaranteedPaymentsEnabledEvent', () => {
      spyOn(service, 'setGuaranteedPaymentsEnabledEvent').and.callThrough();
      spyOn(service, 'getGuaranteedPaymentsEnabledEvent').and.callThrough();

      service.isGuaranteedPaymentsEnabledState()
        .subscribe()
        .unsubscribe();

      expect(service.setGuaranteedPaymentsEnabledEvent).toHaveBeenCalledWith(true);

      service.getGuaranteedPaymentsEnabledEvent()
        .subscribe((response) => expect(response).toBeTrue())
        .unsubscribe();

      expect(service.getGuaranteedPaymentsEnabledEvent).toHaveBeenCalled();
    });

  });

  describe('Session Id', () => {
    beforeEach(() => {
      spyOn(service, 'setSessionIdEvent').and.callThrough();
      service.setSessionId('testId');
    });

    it('should get session id using getSessionId method', () => {
      let id = '';
      service.getSessionId().subscribe((sessionId) => {
        id = sessionId;
      });

      expect(id).toEqual('testId');
    });

    it('should update session id', () => {
      let id = '';
      service.setSessionId('testId2');
      service.getSessionId().subscribe((sessionId) => {
        id = sessionId;
      });
      expect(id).toEqual('testId2');
    });

    it('should update session id event using setSessionIdEvent method', () => {
      expect(service.setSessionIdEvent).toHaveBeenCalledWith('testId');
    });

    it('should get session id event using getSessionIdEvent method', () => {
      spyOn(service, 'getSessionIdEvent').and.callThrough();
      service.getSessionIdEvent().subscribe(result => expect(result).toBe('testId'));
      expect(service.getSessionIdEvent).toHaveBeenCalled();
    });

    it('should return session id from event', () => {
      let sessionId = 'testSessionId';
      eventService.dispatch({ sessionId }, SetGuaranteedPaymentsSessionIdEvent);
      service.getSessionIdEvent().subscribe(id => sessionId = id);
      expect(sessionId).toEqual('testSessionId');
    });

    it('should return empty string if no session id event is found', () => {
      let sessionId = '';
      eventService.dispatch({ sessionId: null }, SetGuaranteedPaymentsSessionIdEvent);
      service.getSessionIdEvent().subscribe(id => sessionId = id);
      expect(sessionId).toEqual('');
    });

    it('should map event to session id', (doneFn) => {
      const event = { sessionId: 'testSessionId' };
      spyOn(eventService, 'get').and.returnValue(of(event));
      eventService.dispatch(event, SetGuaranteedPaymentsSessionIdEvent);
      const result = eventService.get(SetGuaranteedPaymentsSessionIdEvent).pipe(
        map((event: SetGuaranteedPaymentsSessionIdEvent): string => event.sessionId || '')
      );
      result.subscribe(sessionId => {
        expect(sessionId).toEqual('testSessionId');
        doneFn();
      });
    });

    it('should return empty string if session id is null', () => {
      const event: SetGuaranteedPaymentsSessionIdEvent = { sessionId: null };
      spyOn(eventService, 'get').and.returnValue(of(event));
      eventService.dispatch(event, SetGuaranteedPaymentsSessionIdEvent);
      const result = eventService.get(SetGuaranteedPaymentsSessionIdEvent).pipe(
        map((event: SetGuaranteedPaymentsSessionIdEvent): string => event.sessionId || '')
      );
      result.subscribe(sessionId => expect(sessionId).toEqual(''));
    });
  });

  it('should call generate script', () => {
    loadScriptService.loadScript({
      idScript: 'test',
      src: 'https://google.com',
      async: false,
      defer: false,
      attributes: {
        'data-test': 'Test Data'
      }
    });
    expect(loadScriptService.loadScript).toHaveBeenCalledWith({
      idScript: 'test',
      src: 'https://google.com',
      async: false,
      defer: false,
      attributes: {
        'data-test': 'Test Data'
      }
    });
  });

  it('should set session id and generate script when session id is provided', () => {
    spyOn(winRef, 'isBrowser').and.returnValue(true);
    spyOn(service, 'setSessionId').and.callThrough();

    const mockNode = winRef.document.createElement('script');
    spyOn(winRef.document, 'createElement').and.returnValue(mockNode);
    spyOn(winRef.document.head, 'appendChild').and.callThrough();

    const sessionId = 'newSessionId';
    service.generateScript(sessionId);

    expect(service.setSessionId).toHaveBeenCalledWith(sessionId);
    expect(loadScriptService.loadScript).toHaveBeenCalledWith({
      idScript: 'sig-api',
      src: 'https://cdn-scripts.signifyd.com/api/script-tag.js',
      defer: true,
      attributes: { 'data-order-session-id': sessionId },
    });
  });

  it('should update existing script if node is found', () => {
    const mockNode = winRef.document.createElement('script');
    mockNode.id = 'sig-api';
    mockNode.type = 'text/javascript';
    mockNode.defer = true;
    mockNode.setAttribute('data-order-session-id', 'testSessionId');

    winRef.document.body.appendChild(mockNode);
    service.window['SIGNIFYD_GLOBAL'] = {
      init: () => {
      }
    };
    spyOn(service, 'removeScript').and.callThrough();
    spyOn(service.window.SIGNIFYD_GLOBAL, 'init');
    service.generateScript('testSessionId');
    expect(service.removeScript).toHaveBeenCalled();
    expect(service.window.SIGNIFYD_GLOBAL.init).toHaveBeenCalled();
    winRef.document.body.removeChild(mockNode);
  });

  it('should not generate script if session id is not provided', () => {
    service.generateScript('');
    expect(loadScriptService.loadScript).not.toHaveBeenCalled();
  });

  it('should call remove script', () => {
    const sessionId = 'test';
    loadScriptService.loadScript({
      idScript: service.idScript,
      src: 'https://cdn-scripts.signifyd.com/api/script-tag.js',
      defer: true,
      attributes: { 'data-order-session-id': sessionId },
    });
    loadScriptService.removeScript(sessionId);
    expect(loadScriptService.removeScript).toHaveBeenCalledWith(sessionId);
  });

  it('should call removeScript with the correct script tag id', () => {
    service.removeScript();
    expect(loadScriptService.removeScript).toHaveBeenCalledWith('script-tag-tmx');
  });

  it('should not throw an error when removeScript is called', () => {
    expect(() => service.removeScript()).not.toThrow();
  });

});
