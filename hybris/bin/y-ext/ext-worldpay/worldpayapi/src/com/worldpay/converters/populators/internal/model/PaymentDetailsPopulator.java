package com.worldpay.converters.populators.internal.model;

import com.worldpay.converters.internal.model.payment.PaymentConverterStrategy;
import com.worldpay.enums.PaymentAction;
import com.worldpay.internal.model.Info3DSecure;
import com.worldpay.internal.model.PaResponse;
import com.worldpay.data.PaymentDetails;
import com.worldpay.data.Session;
import com.worldpay.data.payment.StoredCredentials;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PaymentDetails} with the information of a {@link PaymentDetails}.
 */
public class PaymentDetailsPopulator implements Populator<PaymentDetails, com.worldpay.internal.model.PaymentDetails> {

    protected final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter;
    protected final Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter;
    protected final PaymentConverterStrategy internalPaymentConverterStrategy;

    public PaymentDetailsPopulator(final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter,
                                   final Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter,
                                   final PaymentConverterStrategy internalPaymentConverterStrategy) {
        this.internalSessionConverter = internalSessionConverter;
        this.internalStoredCredentialsConverter = internalStoredCredentialsConverter;
        this.internalPaymentConverterStrategy = internalPaymentConverterStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final PaymentDetails source, final com.worldpay.internal.model.PaymentDetails target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final List<Object> paymentDtl = target.getVISASSLOrECMCSSLOrBHSSSLOrNEWDAYSSLOrIKEASSLOrAMEXSSLOrELVSSLOrSEPADIRECTDEBITSSLOrDINERSSSLOrCBSSLOrAIRPLUSSSLOrUATPSSLOrCARTEBLEUESSLOrSOLOGBSSLOrLASERSSLOrDANKORTSSLOrDISCOVERSSLOrJCBSSLOrAURORESSLOrGECAPITALSSLOrHIPERCARDSSLOrSOROCREDSSLOrELOSSLOrCARNETSSLOrARGENCARDSSLOrCABALSSLOrCENCOSUDSSLOrCOOPEPLUSSSLOrCREDIMASSSLOrITALCREDSSLOrNARANJASSLOrNATIVASSLOrNEVADASSLOrNEXOSSLOrTARJETASHOPPINGSSLOrPERMANENTSIGNEDDDNLFAXOrSINGLEUNSIGNEDDDNLSSLOrSINGLEUNSIGNEDDDESSSLOrSINGLEUNSIGNEDDDFRSSLOrPERMANENTSIGNEDDDGBSSLOrPERMANENTUNSIGNEDDDGBSSLOrPAYOUTBANKOrVISACHECKOUTSSLOrMASTERPASSSSLOrPAYPALEXPRESSOrGIROPAYSSLOrMAESTROSSLOrSWITCHSSLOrNCPB2BSSLOrNCPSEASONSSLOrNCPGMMSSLOrIDEALSSLOrACHSSLOrACHDIRECTDEBITSSLOrCARDSSLOrABAQOOSSSLOrAGMOSSLOrALIPAYSSLOrALIPAYMOBILESSLOrBALOTOSSLOrBANKAXESSSSLOrBANKLINKNORDEASSLOrBILLDESKSSLOrBILLINGPARTNERSSLOrCASHUSSLOrDINEROMAIL7ELEVENSSLOrDINEROMAILOXXOSSLOrDINEROMAILONLINEBTSSLOrDINEROMAILSERVIPAGSSLOrEKONTOSSLOrEPAYSSLOrEUTELLERSSLOrEWIREDKSSLOrEWIRENOSSLOrEWIRESESSLOrHALCASHSSLOrINSTADEBITSSLOrKONBINISSLOrLOBANETARSSLOrLOBANETBRSSLOrLOBANETCLSSLOrLOBANETMXSSLOrLOBANETPESSLOrLOBANETUYSSLOrMISTERCASHSSLOrMULTIBANCOSSLOrNEOSURFSSLOrPAGASSLOrPAGAVERVESSLOrPAYSAFECARDSSLOrPAYUSSLOrPLUSPAYSSLOrPOLISSLOrPOLINZSSLOrPOSTEPAYSSLOrPRZELEWYSSLOrQIWISSLOrSAFETYPAYSSLOrSIDSSLOrSKRILLSSLOrSOFORTSSLOrSOFORTCHSSLOrSPEEDCARDSSLOrSPOROPAYSSLOrSWIFFSSLOrTELEINGRESOSSLOrTICKETSURFSSLOrTRUSTLYSSLOrTRUSTPAYCZSSLOrTRUSTPAYEESSLOrTRUSTPAYSKSSLOrWEBMONEYSSLOrYANDEXMONEYSSLOrASTROPAYCARDSSLOrBANCOSANTANDERSSLOrBOLETOSSLOrBOLETOHTMLOrMONETASSLOrTODITOCARDSSLOrONLINETRANSFERBRSSLOrONLINETRANSFERMYSSLOrONLINETRANSFERTHSSLOrONLINETRANSFERVNSSLOrOPENBANKINGSSLOrSEVENELEVENMYSSLOrPETRONASSSLOrENETSSGSSLOrCASHTHSSLOrATMIDSSLOrTOKENSSLOrENETSSSLOrCHINAUNIONPAYSSLOrENVOYTRANSFERAUDBANKOrENVOYTRANSFERCADBANKOrENVOYTRANSFERCHFBANKOrENVOYTRANSFERCZKBANKOrENVOYTRANSFERDKKBANKOrENVOYTRANSFEREURBANKOrENVOYTRANSFERGBPBANKOrENVOYTRANSFERHKDBANKOrENVOYTRANSFERHUFBANKOrENVOYTRANSFERJPYBANKOrENVOYTRANSFERNOKBANKOrENVOYTRANSFERNZDBANKOrENVOYTRANSFERPLNBANKOrENVOYTRANSFERRUBBANKOrENVOYTRANSFERSEKBANKOrENVOYTRANSFERSGDBANKOrENVOYTRANSFERTHBBANKOrENVOYTRANSFERTRYBANKOrENVOYTRANSFERUSDBANKOrENVOYTRANSFERZARBANKOrTRANSFERATBANKOrTRANSFERBEBANKOrTRANSFERCHBANKOrTRANSFERDEBANKOrTRANSFERDKBANKOrTRANSFERESBANKOrTRANSFERFIBANKOrTRANSFERFRBANKOrTRANSFERGBBANKOrTRANSFERGRBANKOrTRANSFERITBANKOrTRANSFERJPBANKOrTRANSFERLUBANKOrTRANSFERNLBANKOrTRANSFERNOBANKOrTRANSFERPLBANKOrTRANSFERSEBANKOrTRANSFERUSBANKOrEMVCOTOKENSSLOrAPPLEPAYSSLOrANDROIDPAYSSLOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrKLARNASSLOrKLARNAPAYLATERSSLOrKLARNAPAYNOWSSLOrKLARNASLICEITSSLOrKLARNAV2SSLOrWECHATPAYSSLOrBILLKEYSSLOrINIPAYSSLOrWEBPAYSSLOrPBBASSLOrMERCADOPAGOSSLOrPAYPALSSLOrFPXSSLOrAFTERPAYSSLOrCLEARPAYSSLOrALIPAYHKSSLOrGRABPAYSSLOrMAESSLOrTRUEMONEYSSLOrBANKTRANSFERSSLOrTOUCHNGOSSLOrBOOSTSSLOrSVSGIFTCARDSSLOrTROYSSLOrUPISSLOrUPEXSSLOrFFDISBURSESSLOrFFMONEYTRANSFERSSLOrEPSENVSSLOrCardNumberOrExpiryDateOrCardHolderNameOrCvcOrIssueNumberOrStartDateOrPOSRequestOrCardSwipeOrCSEDATA();
        Optional.ofNullable(source.getPayment())
            .map(internalPaymentConverterStrategy::convertPayment)
            .ifPresent(paymentDtl::add);

        Optional.ofNullable(source.getSession())
            .map(internalSessionConverter::convert)
            .ifPresent(target::setSession);

        Optional.ofNullable(source.getPaResponse()).ifPresent(paResponse -> {
            final Info3DSecure intInfo3dSecure = new Info3DSecure();
            final PaResponse intPaResponse = new PaResponse();
            intPaResponse.setvalue(paResponse);
            intInfo3dSecure.getPaResponseOrMpiProviderOrMpiResponseOrAttemptedAuthenticationOrCompletedAuthenticationOrThreeDSVersionOrMerchantNameOrXidOrDsTransactionIdOrCavvOrEciOrDelegatedAuthenticationOrTransactionStatusReasonOrChallengeCancelIndicatorOrNetworkScoreOrCardBrandOrCavvAlgorithm().add(intPaResponse);
            target.setInfo3DSecure(intInfo3dSecure);
        });

        Optional.ofNullable(source.getStoredCredentials())
            .map(internalStoredCredentialsConverter::convert)
            .ifPresent(target::setStoredCredentials);

        Optional.ofNullable(source.getAction())
            .map(PaymentAction::name)
            .ifPresent(target::setAction);
    }
}
