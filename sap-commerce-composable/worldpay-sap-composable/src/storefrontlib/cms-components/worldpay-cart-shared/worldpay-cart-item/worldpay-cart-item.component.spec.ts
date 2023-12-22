import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayCartItemComponent } from './worldpay-cart-item.component';
import { Component, DebugElement, Directive, Injector, Input, Pipe, PipeTransform, SimpleChange, TemplateRef, ViewContainerRef } from '@angular/core';
import { AtMessageModule, OutletDirective, OutletModule } from '@spartacus/storefront';
import { RouterTestingModule } from '@angular/router/testing';
import { ControlContainer, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { FeaturesConfigModule, GlobalMessageEntities, GlobalMessageService, I18nTestingModule } from '@spartacus/core';
import { By } from '@angular/platform-browser';
import { CartItemContextSource } from '@spartacus/cart/base/components';
import { CartItemContext, PromotionLocation } from '@spartacus/cart/base/root';
import { Observable, of } from 'rxjs';
import createSpy = jasmine.createSpy;

@Directive({
  selector: '[cxFeatureLevel]',
})
export class MockFeatureLevelDirective {
  constructor(
    protected templateRef: TemplateRef<any>,
    protected viewContainer: ViewContainerRef
  ) {
  }

  @Input() set cxFeatureLevel(_feature: string | number) {
    this.viewContainer.createEmbeddedView(this.templateRef);
  }
}

@Pipe({
  name: 'cxUrl',
})
class MockUrlPipe implements PipeTransform {
  transform() {
  }
}

/*
@Directive({
  selector: '[cxModal]',
})
class MockModalDirective implements Partial<ModalDirective> {
  @Input() cxModal;
}
*/

@Directive({
  selector: '[cxOutlet]',
})
class MockOutletDirective implements Partial<OutletDirective> {
  @Input() cxOutlet: string;
}

@Component({
  template: '',
  selector: 'cx-media',
})
class MockMediaComponent {
  @Input() container;
  @Input() format;
}

@Component({
  template: '',
  selector: 'cx-item-counter',
})
class MockItemCounterComponent {
  @Input() control;
  @Input() readonly;
  @Input() max;
  @Input() allowZero;
}

@Component({
  template: '',
  selector: 'cx-cart-item-validation-warning',
})
class MockCartItemValidationWarningComponent {
  @Input() code;
}

@Component({
  template: '',
  selector: 'cx-promotions',
})
class MockPromotionsComponent {
  @Input() promotions;
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

class MockGlobalMessageService {
  add = createSpy('add').and.callThrough();

  get(): Observable<GlobalMessageEntities> {
    return of({});
  }
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          I18nTestingModule,
          FeaturesConfigModule,
          OutletModule,
          AtMessageModule,
        ],
        declarations: [
          WorldpayCartItemComponent,
          MockMediaComponent,
          MockItemCounterComponent,
          MockPromotionsComponent,
          MockUrlPipe,
          MockFeatureLevelDirective,
          MockOutletDirective,
          MockCartItemValidationWarningComponent
        ],
        providers: [
          {
            provide: ControlContainer,
          },
          {
            provide: GlobalMessageService,
            useClass: MockGlobalMessageService
          }
        ],
      })
      .compileComponents();
  });

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
