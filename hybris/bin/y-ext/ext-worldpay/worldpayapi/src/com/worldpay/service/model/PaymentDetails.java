package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.Info3DSecure;
import com.worldpay.internal.model.PaResponse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.List;

/**
 * POJO representation of the payment details
 */
public class PaymentDetails implements InternalModelTransformer, Serializable {

    private Payment payment;
    private Session session;
    private String paResponse;

    /**
     * Constructor with full list of fields
     * @param payment
     * @param session
     * @param paResponse
     */
    public PaymentDetails(Payment payment, Session session, String paResponse) {
        this.payment = payment;
        this.session = session;
        this.paResponse = paResponse;
    }

    /**
     * Constructor taking payment and session objects
     * @param payment
     * @param session
     */
    public PaymentDetails(Payment payment, Session session) {
        this.payment = payment;
        this.session = session;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.PaymentDetails intPaymentDetails = new com.worldpay.internal.model.PaymentDetails();
        if (payment != null) {
            final List<Object> paymentDtl = intPaymentDetails
                    .getVISASSLOrECMCSSLOrBHSSSLOrNEWDAYSSLOrIKEASSLOrAMEXSSLOrELVSSLOrSEPADIRECTDEBITSSLOrDINERSSSLOrCBSSLOrAIRPLUSSSLOrUATPSSLOrCARTEBLEUESSLOrSOLOGBSSLOrLASERSSLOrDANKORTSSLOrDISCOVERSSLOrJCBSSLOrAURORESSLOrGECAPITALSSLOrHIPERCARDSSLOrSOROCREDSSLOrELOSSLOrARGENCARDSSLOrCABALSSLOrCENCOSUDSSLOrCOOPEPLUSSSLOrCREDIMASSSLOrITALCREDSSLOrNARANJASSLOrNATIVASSLOrNEVADASSLOrNEXOSSLOrTARJETASHOPPINGSSLOrPERMANENTSIGNEDDDNLFAXOrSINGLEUNSIGNEDDDNLSSLOrSINGLEUNSIGNEDDDESSSLOrSINGLEUNSIGNEDDDFRSSLOrPERMANENTSIGNEDDDGBSSLOrPERMANENTUNSIGNEDDDGBSSLOrPAYOUTBANKOrVMESSLOrMASTERPASSSSLOrPAYPALEXPRESSOrGIROPAYSSLOrMAESTROSSLOrSWITCHSSLOrNCPB2BSSLOrNCPSEASONSSLOrNCPGMMSSLOrIDEALSSLOrACHSSLOrCARDSSLOrABAQOOSSSLOrAGMOSSLOrALIPAYSSLOrALIPAYMOBILESSLOrBALOTOSSLOrBANKAXESSSSLOrBANKLINKNORDEASSLOrBILLDESKSSLOrBILLINGPARTNERSSLOrCASHUSSLOrDINEROMAIL7ELEVENSSLOrDINEROMAILOXXOSSLOrDINEROMAILONLINEBTSSLOrDINEROMAILSERVIPAGSSLOrEKONTOSSLOrEPAYSSLOrEUTELLERSSLOrEWIREDKSSLOrEWIRENOSSLOrEWIRESESSLOrHALCASHSSLOrINSTADEBITSSLOrKONBINISSLOrLOBANETARSSLOrLOBANETBRSSLOrLOBANETCLSSLOrLOBANETMXSSLOrLOBANETPESSLOrLOBANETUYSSLOrMISTERCASHSSLOrMULTIBANCOSSLOrNEOSURFSSLOrPAGASSLOrPAGAVERVESSLOrPAYSAFECARDSSLOrPAYUSSLOrPLUSPAYSSLOrPOLISSLOrPOLINZSSLOrPOSTEPAYSSLOrPRZELEWYSSLOrQIWISSLOrSAFETYPAYSSLOrSIDSSLOrSKRILLSSLOrSOFORTSSLOrSOFORTCHSSLOrSPEEDCARDSSLOrSPOROPAYSSLOrSWIFFSSLOrTELEINGRESOSSLOrTICKETSURFSSLOrTRUSTLYSSLOrTRUSTPAYCZSSLOrTRUSTPAYEESSLOrTRUSTPAYSKSSLOrWEBMONEYSSLOrYANDEXMONEYSSLOrASTROPAYCARDSSLOrBANCOSANTANDERSSLOrBOLETOSSLOrBOLETOHTMLOrMONETASSLOrTODITOCARDSSLOrONLINETRANSFERBRSSLOrONLINETRANSFERMYSSLOrONLINETRANSFERTHSSLOrONLINETRANSFERVNSSLOrSEVENELEVENMYSSLOrPETRONASSSLOrENETSSGSSLOrCASHTHSSLOrATMIDSSLOrTOKENSSLOrENETSSSLOrCHINAUNIONPAYSSLOrENVOYTRANSFERAUDBANKOrENVOYTRANSFERCADBANKOrENVOYTRANSFERCHFBANKOrENVOYTRANSFERCZKBANKOrENVOYTRANSFERDKKBANKOrENVOYTRANSFEREURBANKOrENVOYTRANSFERGBPBANKOrENVOYTRANSFERHKDBANKOrENVOYTRANSFERHUFBANKOrENVOYTRANSFERJPYBANKOrENVOYTRANSFERNOKBANKOrENVOYTRANSFERNZDBANKOrENVOYTRANSFERPLNBANKOrENVOYTRANSFERRUBBANKOrENVOYTRANSFERSEKBANKOrENVOYTRANSFERSGDBANKOrENVOYTRANSFERTHBBANKOrENVOYTRANSFERTRYBANKOrENVOYTRANSFERUSDBANKOrENVOYTRANSFERZARBANKOrTRANSFERATBANKOrTRANSFERBEBANKOrTRANSFERCHBANKOrTRANSFERDEBANKOrTRANSFERDKBANKOrTRANSFERESBANKOrTRANSFERFIBANKOrTRANSFERFRBANKOrTRANSFERGBBANKOrTRANSFERGRBANKOrTRANSFERITBANKOrTRANSFERJPBANKOrTRANSFERLUBANKOrTRANSFERNLBANKOrTRANSFERNOBANKOrTRANSFERPLBANKOrTRANSFERSEBANKOrTRANSFERUSBANKOrEMVCOTOKENSSLOrAPPLEPAYSSLOrANDROIDPAYSSLOrSAMSUNGPAYSSLOrKLARNASSLOrBILLKEYSSLOrINIPAYSSLOrWEBPAYSSLOrZAPPSSLOrSVSGIFTCARDSSLOrTROYSSLOrCardNumberOrExpiryDateOrCardHolderNameOrCvcOrIssueNumberOrStartDateOrPOSRequestOrCardSwipeOrCSEDATA();

            InternalModelObject internalPayment = payment.transformToInternalModel();
            paymentDtl.add(internalPayment);
        }
        if (session != null) {
            intPaymentDetails.setSession((com.worldpay.internal.model.Session) session.transformToInternalModel());
        }
        if (getPaResponse() != null) {
            Info3DSecure intInfo3dSecure = new Info3DSecure();
            PaResponse intPaResponse = new PaResponse();
            intPaResponse.setvalue(getPaResponse());
            intInfo3dSecure.getPaResponseOrMpiProviderOrMpiResponseOrXidOrCavvOrEciOrAttemptedAuthentication().add(intPaResponse);
            intPaymentDetails.setInfo3DSecure(intInfo3dSecure);
        }
        return intPaymentDetails;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPaResponse() {
        return paResponse;
    }

    public void setPaResponse(String paResponse) {
        this.paResponse = paResponse;
    }

    /**
     * (non-Javadoc)
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "PaymentDetails [payment=" + payment + ", session=" + session + ", paResponse=" + paResponse + "]";
    }
}
