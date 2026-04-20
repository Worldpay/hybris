import { Observable, of } from 'rxjs';

export class MockTranslationService {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  translate(key: string, params: any): Observable<string> {
    if (!params) {
      return of(key);
    }
    if (params) {
      Object.entries(params).forEach(([objKey, value]: [string, unknown]): void => {
        key += ` ${objKey}:${value}`;
      });
    }
    return of(key);
  }
}