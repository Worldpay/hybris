/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccUserPaymentAdapter, tryNormalizeHttpError } from '@spartacus/core';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { WorldpayUserPaymentAdapter } from '../../../connectors';
import { ApmPaymentDetails, ApmPaymentDetailsListResponse } from '../../../interfaces';

const CONTENT_TYPE_JSON_HEADER: { 'Content-Type': string } = { 'Content-Type': 'application/json' };

@Injectable()
export class OccWorldpayUserPaymentAdapter extends OccUserPaymentAdapter implements WorldpayUserPaymentAdapter {

  loadAllForCart(userId: string, cartId: string): Observable<ApmPaymentDetails[]> {
    const url: string = this.occEndpoints.buildUrl('paymentDetailsAllForCart', {
      urlParams: {
        userId,
        cartId
      },
      queryParams: {
        saved: true
      }
    });

    const headers: HttpHeaders = new HttpHeaders({
      ...CONTENT_TYPE_JSON_HEADER,
    });

    return this.http.get<ApmPaymentDetailsListResponse>(url, { headers }).pipe(
      catchError((error: unknown): never => {
        throw tryNormalizeHttpError((): unknown => error, this.logger);
      }),
      map((paymentList: ApmPaymentDetailsListResponse): ApmPaymentDetails[] => paymentList.payments ?? []),
      // this.converter.pipeableMany(APM_NORMALIZER)
    );
  }
}
