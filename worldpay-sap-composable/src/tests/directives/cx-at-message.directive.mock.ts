import { Directive, Input } from '@angular/core';

@Directive({
  selector: '[cxAtMessage]'
})
export class MockAtMessageDirective {
  @Input() cxAtMessage: string | string[] | undefined;
}
