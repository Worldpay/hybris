import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'cxUrl',
  standalone: false
})
export class MockUrlPipe implements PipeTransform {
  transform(): any {}
}