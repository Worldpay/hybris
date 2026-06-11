import { Directive, Input } from '@angular/core';
import { OutletDirective } from '@spartacus/storefront';

@Directive({
  selector: '[cxOutlet]',
})
export class MockCxOutletDirective implements Partial<OutletDirective> {
  @Input() cxOutlet: string;
}