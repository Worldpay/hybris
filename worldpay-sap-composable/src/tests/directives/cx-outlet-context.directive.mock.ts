import { Directive, Input } from '@angular/core';

@Directive({
  selector: '[cxOutletContext]',
})
export class MockCxOutletContextDirective {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Input() cxOutletContext: any; // Mock the property
}