import { TestBed } from '@angular/core/testing';
import { EventService, QueryService, QueryState, WindowRef } from '@spartacus/core';
import { SetGuaranteedPaymentsSessionIdEvent } from '@worldpay-events/guaranteed-payments.events';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsConnector } from '../../connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.connector';
import { LoadScriptService } from '../../utils/load-script.service';

import { WorldpayGuaranteedPaymentsService } from './worldpay-guaranteed-payments.service';
import createSpy = jasmine.createSpy;

class MockWorldpayGuaranteedPaymentsConnector implements Partial<WorldpayGuaranteedPaymentsConnector> {
  isGuaranteedPaymentsEnabled = createSpy().and.returnValue(of(true));

  setGuaranteedPaymentsEnabledEvent = createSpy().and.returnValue(of(true));
}

class MockQueryService implements Partial<QueryService> {
  create = createSpy().and.callThrough();
}

describe('WorldpayGuaranteedPaymentsService', () => {
  let service: WorldpayGuaranteedPaymentsService;
  let connector: WorldpayGuaranteedPaymentsConnector;
  let loadScriptService: LoadScriptService;
  let winRef: WindowRef;
  let queryService: QueryService;
  let eventService: EventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LoadScriptService,
        WindowRef,
        QueryService,
        WorldpayGuaranteedPaymentsService,
        EventService,
        LoadScriptService,
        {
          provide: WorldpayGuaranteedPaymentsConnector,
          useClass: MockWorldpayGuaranteedPaymentsConnector
        },
      ]
    });
    service = TestBed.inject(WorldpayGuaranteedPaymentsService);
    connector = TestBed.inject(WorldpayGuaranteedPaymentsConnector);
    loadScriptService = TestBed.inject(LoadScriptService);
    winRef = TestBed.inject(WindowRef);
    queryService = TestBed.inject(QueryService);
    eventService = TestBed.inject(EventService);
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
    })
    ;

    it('should return empty string if session id is null', () => {
      const event = { sessionId: null };
      spyOn(eventService, 'get').and.returnValue(of(event));
      eventService.dispatch(event, SetGuaranteedPaymentsSessionIdEvent);
      const result = eventService.get(SetGuaranteedPaymentsSessionIdEvent).pipe(
        map((event: SetGuaranteedPaymentsSessionIdEvent): string => event.sessionId || '')
      );
      result.subscribe(sessionId => expect(sessionId).toEqual(''));
    });
  });

  it('should call generate script', () => {
    spyOn(loadScriptService, 'loadScript').and.callThrough();
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
    spyOn(service, 'setSessionId').and.callThrough();
    spyOn(loadScriptService, 'loadScript').and.callThrough();
    service.generateScript('testSessionId');
    expect(service.setSessionId).toHaveBeenCalledWith('testSessionId');
    expect(loadScriptService.loadScript).toHaveBeenCalledWith({
      idScript: 'sig-api',
      src: 'https://cdn-scripts.signifyd.com/api/script-tag.js',
      defer: true,
      attributes: { 'data-order-session-id': 'testSessionId' },
    });
  });

  it('should update existing script if node is found', () => {
    const mockNode = document.createElement('script');
    mockNode.id = 'sig-api';
    mockNode.type = 'text/javascript';
    mockNode.defer = true;
    mockNode.setAttribute('data-order-session-id', 'testSessionId');

    document.body.appendChild(mockNode);
    service.window['SIGNIFYD_GLOBAL'] = {
      init: () => {
      }
    };
    spyOn(service, 'removeScript').and.callThrough();
    spyOn(loadScriptService, 'updateScript').and.callThrough();
    spyOn(service.window.SIGNIFYD_GLOBAL, 'init').and.callThrough();
    service.generateScript('testSessionId');
    expect(service.removeScript).toHaveBeenCalled();
    expect(service.window.SIGNIFYD_GLOBAL.init).toHaveBeenCalled();
    document.body.removeChild(mockNode);
  });

  it('should not generate script if session id is not provided', () => {
    spyOn(loadScriptService, 'loadScript').and.callThrough();
    service.generateScript('');
    expect(loadScriptService.loadScript).not.toHaveBeenCalled();
  });

  it('should call remove script', () => {
    spyOn(loadScriptService, 'removeScript').and.callThrough();
    loadScriptService.removeScript('test');
    expect(loadScriptService.removeScript).toHaveBeenCalledWith('test');
  });

  it('should call removeScript with the correct script tag id', () => {
    spyOn(loadScriptService, 'removeScript').and.callThrough();
    service.removeScript();
    expect(loadScriptService.removeScript).toHaveBeenCalledWith('script-tag-tmx');
  });

  it('should not throw an error when removeScript is called', () => {
    expect(() => service.removeScript()).not.toThrow();
  });

});
