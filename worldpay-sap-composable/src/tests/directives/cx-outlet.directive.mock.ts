import { Directive, Input } from '@angular/core';
import { OutletDirective } from '@spartacus/storefront';

@Directive({
  selector: '[cxOutlet]',
  standalone: false,
})
export class MockCxOutletDirective implements Partial<OutletDirective> {
  @Input() cxOutlet: string;
}