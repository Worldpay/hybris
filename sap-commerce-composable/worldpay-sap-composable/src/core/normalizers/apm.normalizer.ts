import { Injectable } from '@angular/core';
import { Converter, Image, OccConfig } from '@spartacus/core';
import { filter, take, tap } from 'rxjs/operators';
import { WorldpayACHFacade } from '../facade/worldpay-ach.facade';
import { ACHPaymentForm, ApmData, ApmPaymentDetails, OccApmData, OccApmDataConfiguration, PaymentMethod, WorldpayApmPaymentInfo } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class ApmNormalizer implements Converter<OccApmData, ApmData> {

  /**
   * Constructor
   *
   * Initializes the ApmNormalizer with the provided OccConfig and WorldpayACHFacade.
   *
   * @param config - OccConfig instance used for configuration settings
   * @param worldpayACHFacade - WorldpayACHFacade instance used for handling ACH payment forms
   */
  constructor(
    protected config: OccConfig,
    protected worldpayACHFacade: WorldpayACHFacade
  ) {
  }

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
        code: config.code,
        name: config.name
      };
    }

    target.code = config.code;
    target.name = config.name;

    if (source.media) {
      target.media =
        {
          mobile: {
            url: this.normalizeImageUrl(source.media.url),
            altText: config.name
          } as Image
        };
    }

    target.bankConfigurations = config.bankConfigurations?.map(({
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

    if (source.cardType) {
      target.code = PaymentMethod.Card;
    }

    if (source.apmCode) {
      target.code = source.apmCode;
    }

    if (source?.name?.toLowerCase() === 'ideal') {
      target.shopperBankCode = source.apmName;
    }

    if (source.apmCode === PaymentMethod.ACH) {
      this.worldpayACHFacade.getACHPaymentFormValue().pipe(
        filter((value: ACHPaymentForm) => !!value),
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

  /** taken from product-image-normalizer.ts
   * Traditionally, in an on-prem world, medias and other backend related calls
   * are hosted at the same platform, but in a cloud setup, applications are are
   * typically distributed cross different environments. For media, we use the
   * `backend.media.baseUrl` by default, but fallback to `backend.occ.baseUrl`
   * if none provided.
   *
   * @param url - The image URL to be normalized
   * @returns string - The normalized image URL
   */
  private normalizeImageUrl(url: string): string {
    if (new RegExp(/^(http|data:image|\/\/)/i).test(url)) {
      return url;
    }
    return (
      (this.config.backend.media.baseUrl ||
       this.config.backend.occ.baseUrl ||
       '') + url
    );
  }
}
