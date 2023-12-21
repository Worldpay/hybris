import { TestBed } from '@angular/core/testing';

import { WorldpayGuaranteedPaymentsService } from './worldpay-guaranteed-payments.service';
import { LoadScriptService } from '../../utils/load-script.service';
import { EventService, QueryService, QueryState, WindowRef } from '@spartacus/core';
import { WorldpayGuaranteedPaymentsConnector } from '../../connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.connector';
import { of } from 'rxjs';
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
      service.getSessionIdEvent()
        .subscribe(result => expect(result).toBe('testId'));
      expect(service.getSessionIdEvent).toHaveBeenCalled();
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

  it('should call remove script', () => {
    spyOn(loadScriptService, 'removeScript').and.callThrough();
    loadScriptService.removeScript('test');
    expect(loadScriptService.removeScript).toHaveBeenCalledWith('test');
  });
});
