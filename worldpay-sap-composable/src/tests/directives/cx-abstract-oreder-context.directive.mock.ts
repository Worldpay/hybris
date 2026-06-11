
import { Directive, Input } from '@angular/core';
import { AbstractOrderKeyInput } from '@spartacus/cart/base/components';

@Directive({
  selector: '[cxAbstractOrderContext]',
})
export class MockAbstractOrderContextDirective {
  @Input() cxAbstractOrderContext: AbstractOrderKeyInput;
}
