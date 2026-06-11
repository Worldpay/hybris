import { Component, DebugElement, Directive, Injector, Input, Pipe, PipeTransform, SimpleChange, } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ControlContainer, ReactiveFormsModule, UntypedFormControl, } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { CartItemContextSource } from '@spartacus/cart/base/components';
import { CartItemContext, PromotionLocation } from '@spartacus/cart/base/root';
import { CxDatePipe, FeatureLevelDirective, MockDatePipe, MockTranslatePipe, Promotion, TranslatePipe, UrlPipe, } from '@spartacus/core';
import { AtMessageDirective, ItemCounterComponent, MediaComponent, OutletModule, PromotionsComponent, } from '@spartacus/storefront';
import { MockCxFeatureLevelDirective } from 'worldpay-sap-composable-tests';
import { WorldpayCartItemValidationWarningComponent } from '../worldpay-cart-item-warning/worldpay-cart-item-validation-warning.component';
import { WorldpayCartItemComponent } from './worldpay-cart-item.component';

@Pipe({ name: 'cxUrl' })
class MockUrlPipe implements PipeTransform {
  transform() {
  }
}

@Component({
  template: '',
  selector: 'cx-media',
  imports: [],
})
class MockMediaComponent {
  @Input() container: string;
  @Input() format: string;
}

@Component({
  template: '',
  selector: 'cx-item-counter',
  imports: [],
})
class MockItemCounterComponent {
  @Input() control: UntypedFormControl;
  @Input() readonly: boolean;
  @Input() max: number;
  @Input() allowZero: boolean;
}

@Component({
  template: '',
  selector: 'cx-promotions',
  imports: [],
})
class MockPromotionsComponent {
  @Input() promotions: Promotion[];
}

const mockProduct = {
  baseOptions: [
    {
      selected: {
        variantOptionQualifiers: [
          {
            name: 'Size',
            value: 'XL',
          },
          {
            name: 'Style',
            value: 'Red',
          },
        ],
      },
    },
  ],
  stock: {
    stockLevelStatus: 'outOfStock',
  },
};

@Component({
  selector: 'cx-cart-item-validation-warning',
  template: '',
  imports: [],
})
class MockCartItemValidationWarningComponent {
  @Input() code: string;
}

@Directive({ selector: '[cxAtMessage]' })
class MockAtMessageDirective {
  @Input() cxAtMessage: string | string[] | undefined;
}

describe('WorldpayCartItemComponent', () => {
  let cartItemComponent: WorldpayCartItemComponent;
  let componentInjector: Injector;
  let fixture: ComponentFixture<WorldpayCartItemComponent>;
  let el: DebugElement;

  const featureConfig = jasmine.createSpyObj('FeatureConfigService', [
    'isEnabled',
    'isLevel',
  ]);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        OutletModule,
        WorldpayCartItemComponent,
        RouterModule.forRoot([]),
      ],
      providers: [
        {
          provide: ControlContainer,
        },
      ],
    }).overrideComponent(WorldpayCartItemComponent, {
      remove: {
        imports: [
          TranslatePipe,
          CxDatePipe,
          UrlPipe,
          MediaComponent,
          ItemCounterComponent,
          PromotionsComponent,
          FeatureLevelDirective,
          WorldpayCartItemValidationWarningComponent,
          AtMessageDirective,
        ],
      },
      add: {
        imports: [
          MockTranslatePipe,
          MockDatePipe,
          MockUrlPipe,
          MockMediaComponent,
          MockItemCounterComponent,
          MockPromotionsComponent,
          MockCxFeatureLevelDirective,
          MockCartItemValidationWarningComponent,
          MockAtMessageDirective,
        ],
      },
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayCartItemComponent);
    cartItemComponent = fixture.componentInstance;
    componentInjector = fixture.debugElement.injector;

    cartItemComponent.item = {
      product: mockProduct,
      updateable: true,
    };
    cartItemComponent.quantityControl = new UntypedFormControl('1');
    cartItemComponent.quantityControl.markAsPristine();
    spyOn(cartItemComponent, 'removeItem').and.callThrough();
    fixture.detectChanges();
    el = fixture.debugElement;
  });

  it('should create CartItemComponent', () => {
    expect(cartItemComponent).toBeTruthy();
  });

  it('should provide locally CartItemContextSource', () => {
    expect(componentInjector.get(CartItemContextSource)).toBeTruthy();
  });

  it('should provide locally CartItemContext', () => {
    expect(componentInjector.get(CartItemContext)).toBe(
      componentInjector.get(CartItemContextSource)
    );
  });

  describe('after onChanges fired', () => {
    let cartItemContextSource: CartItemContextSource;

    beforeEach(() => {
      cartItemContextSource = componentInjector.get(CartItemContextSource);
    });

    it('should push change of input "compact" to context', () => {
      spyOn(cartItemContextSource.compact$, 'next');
      cartItemComponent.compact = true;
      cartItemComponent.ngOnChanges({
        compact: { currentValue: cartItemComponent.compact } as SimpleChange,
      });
      expect(cartItemContextSource.compact$.next).toHaveBeenCalledWith(
        cartItemComponent.compact
      );
    });

    it('should push change of input "readonly" to context', () => {
      spyOn(cartItemContextSource.readonly$, 'next');
      cartItemComponent.readonly = true;
      cartItemComponent.ngOnChanges({
        readonly: { currentValue: cartItemComponent.readonly } as SimpleChange,
      });
      expect(cartItemContextSource.readonly$.next).toHaveBeenCalledWith(
        cartItemComponent.readonly
      );
    });

    it('should push change of input "item" to context', () => {
      spyOn(cartItemContextSource.item$, 'next');
      cartItemComponent.item = { orderCode: '123' };
      cartItemComponent.ngOnChanges({
        item: { currentValue: cartItemComponent.item } as SimpleChange,
      });
      expect(cartItemContextSource.item$.next).toHaveBeenCalledWith(
        cartItemComponent.item
      );
    });

    it('should push change of input "quantityControl" to context', () => {
      spyOn(cartItemContextSource.quantityControl$, 'next');
      cartItemComponent.quantityControl = new UntypedFormControl(2);
      cartItemComponent.ngOnChanges({
        quantityControl: {
          currentValue: cartItemComponent.quantityControl,
        } as SimpleChange,
      });
      expect(cartItemContextSource.quantityControl$.next).toHaveBeenCalledWith(
        cartItemComponent.quantityControl
      );
    });

    it('should push change of input "promotionLocation" to context', () => {
      spyOn(cartItemContextSource.location$, 'next');
      cartItemComponent.promotionLocation = PromotionLocation.Order;
      cartItemComponent.ngOnChanges({
        promotionLocation: {
          currentValue: cartItemComponent.quantityControl,
        } as SimpleChange,
      });
      expect(cartItemContextSource.location$.next).toHaveBeenCalledWith(
        cartItemComponent.promotionLocation
      );
    });

    it('should push change of input "options" to context', () => {
      spyOn(cartItemContextSource.options$, 'next');
      cartItemComponent.options = { isSaveForLater: true };
      cartItemComponent.ngOnChanges({
        options: { currentValue: cartItemComponent.options } as SimpleChange,
      });
      expect(cartItemContextSource.options$.next).toHaveBeenCalledWith(
        cartItemComponent.options
      );
    });
  });

  it('should create cart details component', () => {
    featureConfig.isEnabled.and.returnValue(true);
    expect(cartItemComponent).toBeTruthy();

    fixture.detectChanges();

    featureConfig.isEnabled.and.returnValue(false);
    expect(cartItemComponent).toBeTruthy();
  });

  it('should call removeItem()', () => {
    fixture.detectChanges();
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.nativeElement.click();
    fixture.detectChanges();

    expect(cartItemComponent.removeItem).toHaveBeenCalled();
    expect(cartItemComponent.quantityControl.value).toEqual(0);
  });

  it('should mark control "dirty" after removeItem is called', () => {
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.nativeElement.click();
    fixture.detectChanges();
    expect(cartItemComponent.quantityControl.dirty).toEqual(true);
  });

  it('should call isProductOutOfStock()', () => {
    cartItemComponent.isProductOutOfStock(cartItemComponent.item.product);

    expect(cartItemComponent.item).toBeDefined();
    expect(cartItemComponent.item.product).toBeDefined();
    expect(cartItemComponent.item.product.stock).toBeDefined();

    expect(
      cartItemComponent.isProductOutOfStock(cartItemComponent.item.product)
    ).toBeTruthy();

    cartItemComponent.item.product.stock.stockLevelStatus = 'InStock';
    expect(
      cartItemComponent.isProductOutOfStock(cartItemComponent.item.product)
    ).toBeFalsy();
  });

  it('should display variant properties', () => {
    const variants =
      mockProduct.baseOptions[0].selected.variantOptionQualifiers;
    fixture.detectChanges();

    expect(el.queryAll(By.css('.cx-property')).length).toEqual(variants.length);
    variants.forEach((variant) => {
      const infoContainer: HTMLElement = el.query(
        By.css('.cx-info-container')
      ).nativeElement;
      expect(infoContainer.innerText).toContain(
        `${variant.name}: ${variant.value}`
      );
    });
  });
});
