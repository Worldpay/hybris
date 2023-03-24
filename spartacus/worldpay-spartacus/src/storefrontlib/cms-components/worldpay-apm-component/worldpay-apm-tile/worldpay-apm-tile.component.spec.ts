import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WorldpayApmTileComponent } from './worldpay-apm-tile.component';
import { WorldpayApmService } from '../../../../core/services/worldpay-apm/worldpay-apm.service';
import { of } from 'rxjs';
import { ApmData, PaymentMethod } from '../../../../core/interfaces';
import { MediaModule } from '@spartacus/storefront';

const selectedApm: ApmData = {
  code: PaymentMethod.Card,
  name: 'credit'
};

class MockWorldpayApmService {
  selectAPM() {
  }

  getSelectedAPMFromState() {
    return of(selectedApm);
  }
}

describe('WorldpayApmTileComponent', () => {
  let component: WorldpayApmTileComponent;
  let fixture: ComponentFixture<WorldpayApmTileComponent>;
  let worldpayApmService: WorldpayApmService;

  const apm: ApmData = {
    code: PaymentMethod.Card,
    name: 'Credit Card'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        WorldpayApmTileComponent,
      ],
      imports: [
        MediaModule
      ],
      providers: [
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmTileComponent);
    component = fixture.componentInstance;
    component.apm = apm;

    fixture.detectChanges();

    worldpayApmService = TestBed.inject(WorldpayApmService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test should get selected apms', (done) => {
    component.selectedApm$
      .subscribe((res) => {
        expect(res).toEqual(selectedApm);
        done();
      })
      .unsubscribe();
  });

  it('should handle click and fire event', () => {
    spyOn(worldpayApmService, 'selectAPM').and.callThrough();

    fixture.nativeElement.click();

    expect(worldpayApmService.selectAPM).toHaveBeenCalledWith(apm);
  });
});
