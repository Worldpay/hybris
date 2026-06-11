import { inject, Injectable } from '@angular/core';
import { Image } from '@spartacus/core';
import { filter, take, tap } from 'rxjs/operators';
import { WorldpayACHFacade } from '../facade/worldpay-ach.facade';
import { ACHPaymentForm, ApmData, ApmPaymentDetails, OccApmData, OccApmDataConfiguration, PaymentMethod, WorldpayApmPaymentInfo } from '../interfaces';
import { BaseImageNormalizer } from './base-image.normalizer';

@Injectable({
  providedIn: 'root'
})
export class ApmNormalizer extends BaseImageNormalizer<OccApmData, ApmData> {
  protected worldpayACHFacade: WorldpayACHFacade = inject(WorldpayACHFacade);

  /**
   * Converts OccApmData to ApmData
   *
   * This method converts the provided OccApmData source object to an ApmData target object.
   * If the target object is not provided, a new ApmData object is created.
   * The method maps the code, name, media, and bank configurations from the source to the target.
   *
   * @param source - The source OccApmData object to be converted
   * @param target - The optional target ApmData object to be populated
   * @returns ApmData - The populated ApmData object
   */
  convert(source: OccApmData, target?: ApmData): ApmData {
    const config: OccApmDataConfiguration = source.apmConfiguration;
    if (target === undefined) {
      target = {
        code: config?.code,
        name: config?.name
      };
    }

    target.code = config?.code;
    target.name = config?.name;

    if (source?.media) {
      target.media =
        {
          mobile: {
            url: this.normalizeImageUrl(source.media.url),
            altText: config.name
          } as Image
        };
    }

    target.bankConfigurations = config?.bankConfigurations?.map(({
      bankCode,
      bankName
    }: { bankCode?: string, bankName?: string }): { code: string; name: string } => ({
      code: bankCode,
      name: bankName
    }));

    return target;
  }

  /**
   * Normalizes APM data
   *
   * This method normalizes the provided WorldpayApmPaymentInfo source object to a target ApmPaymentDetails object.
   * If the target object is not provided, a new target object is created by copying the source object.
   * The method sets the code property based on the cardType or apmCode properties of the source.
   * If the source name is 'ideal', it sets the shopperBankCode property to the value of the apmName property.
   * If the source apmCode is ACH, it populates the achPaymentForm property using the WorldpayACHFacade.
   * If both apmName and apmCode are present in the source, they are deleted from the target.
   *
   * @param source - The source WorldpayApmPaymentInfo object to be normalized
   * @param target - The optional target WorldpayApmPaymentInfo object to be populated
   * @returns ApmPaymentDetails - The populated ApmPaymentDetails object
   */
  normalizeApmData(source: WorldpayApmPaymentInfo, target?: WorldpayApmPaymentInfo): ApmPaymentDetails {
    if (target === undefined) {
      target = { ...source };
    }

    if (source.cardType || source.code === 'CARD') {
      target.code = PaymentMethod.Card;
    }

    if (source.apmCode) {
      target.code = source.apmCode;
    }

    if (source.save || source.saved) {
      target.save = source.save || source.saved;
    }

    if (source?.isAPM) {
      target.isAPM = source.isAPM;
    }

    if (source?.subscriptionId) {
      target.subscriptionId = source.subscriptionId;
    }

    if (source?.name?.toLowerCase() === 'ideal') {
      target.shopperBankCode = source.apmName;
    }

    if (source.apmCode === PaymentMethod.ACH) {
      this.worldpayACHFacade.getACHPaymentFormValue().pipe(
        filter((value: ACHPaymentForm): boolean => !!value),
        tap((value: ACHPaymentForm): void => {
          target.achPaymentForm = value;
        }),
        take(1)
      ).subscribe();
    }

    if (source.apmName && source.apmCode) {
      delete target.apmCode;
      delete target.apmName;
    }
    return target as ApmPaymentDetails;
  }
}
