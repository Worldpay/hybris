import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'cxUrl',
})
export class MockUrlPipe implements PipeTransform {
  transform(): any {}
}