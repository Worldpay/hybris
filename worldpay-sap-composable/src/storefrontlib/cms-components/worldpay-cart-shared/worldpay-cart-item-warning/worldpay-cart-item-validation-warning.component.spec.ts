import { Component, DebugElement, Input, Pipe, PipeTransform, } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { CartModification, CartValidationFacade, CartValidationStatusCode, } from '@spartacus/cart/base/root';
import { TranslatePipe, UrlPipe } from '@spartacus/core';
import { ICON_TYPE, IconComponent } from '@spartacus/storefront';
import { ReplaySubject } from 'rxjs';
import { WorldpayCartItemValidationWarningComponent } from './worldpay-cart-item-validation-warning.component';

const mockCode = 'productCode1';
const mockData = [
  {
    statusCode: CartValidationStatusCode.LOW_STOCK,
    entry: {
      product: {
        code: mockCode,
      },
    },
  },
  {
    statusCode: CartValidationStatusCode.LOW_STOCK,
    entry: {
      product: {
        code: 'productCode2',
      },
    },
  },
];

// eslint-disable-next-line rxjs/no-ignored-replay-buffer
const dataReplaySubject = new ReplaySubject<CartModification[]>();

class MockCartValidationFacade {
  getValidationResults() {
    return dataReplaySubject;
  }
}

@Component({
  selector: 'cx-icon',
  template: '',
})
class MockCxIconComponent {
  @Input() type: ICON_TYPE;
}

@Pipe({ name: 'cxTranslate' })
class MockTranslatePipe implements PipeTransform {
  transform(): any {
  }
}

@Pipe({ name: 'cxUrl' })
class MockUrlPipe implements PipeTransform {
  transform() {
  }
}

describe('WorldpayCartItemValidationWarningComponent', () => {
  let component: WorldpayCartItemValidationWarningComponent;
  let fixture: ComponentFixture<WorldpayCartItemValidationWarningComponent>;
  let cartValidationFacade: CartValidationFacade;
  let el: DebugElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WorldpayCartItemValidationWarningComponent],
      providers: [
        {
          provide: CartValidationFacade,
          useClass: MockCartValidationFacade,
        },
      ],
    }).overrideComponent(WorldpayCartItemValidationWarningComponent, {
      remove: {
        imports: [IconComponent, TranslatePipe, UrlPipe],
      },
      add: {
        imports: [MockCxIconComponent, MockTranslatePipe, MockUrlPipe],
      },
    }).compileComponents();

    fixture = TestBed.createComponent(WorldpayCartItemValidationWarningComponent);
    component = fixture.componentInstance;
    el = fixture.debugElement;
    cartValidationFacade = TestBed.inject(CartValidationFacade);

    (
      cartValidationFacade.getValidationResults() as ReplaySubject<
        CartModification[]
      >
    ).next([]);
    component.code = mockCode;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should find proper cart modification object', () => {
    (
      cartValidationFacade.getValidationResults() as ReplaySubject<
        CartModification[]
      >
    ).next(mockData);
    let result;

    component.cartModification$.subscribe((value) => (result = value));

    // @ts-ignore
    expect(result.entry.product.code).toEqual(mockCode);
  });

  it('should close / hide warning when clicked icon', () => {
    let button = el.query(By.css('.close')) as any;
    expect(button).toBeNull();

    (
      cartValidationFacade.getValidationResults() as ReplaySubject<
        CartModification[]
      >
    ).next(mockData);
    fixture.detectChanges();

    button = el.query(By.css('.close')).nativeElement;
    expect(button).toBeDefined();
    button.click();

    fixture.detectChanges();

    expect(component.isVisible).toEqual(false);
    const alert = el.query(By.css('.alert'));
    expect(alert).toBeNull();
  });
});
