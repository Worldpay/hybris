import { Directive, Input } from '@angular/core';

@Directive({
  selector: '[cxAtMessage]',
  standalone: false,
})
export class MockAtMessageDirective {
  @Input() cxAtMessage: string | string[] | undefined;
}
