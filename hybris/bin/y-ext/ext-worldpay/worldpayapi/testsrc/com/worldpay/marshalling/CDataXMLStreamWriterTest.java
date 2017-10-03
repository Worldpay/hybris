package com.worldpay.marshalling;

import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.request.validation.impl.DefaultWorldpayXMLValidator;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.xml.writer.CDataXMLStreamWriter;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.worldpay.util.WorldpayConstants.JAXB_CONTEXT;
import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;
import static org.junit.Assert.*;


public class CDataXMLStreamWriterTest {

    @Test
    public void testMarshalXMLForSubmitOrder() throws WorldpayValidationException, XMLStreamException, JAXBException {
        final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        marshaller.setProperty(JAXB_FRAGMENT, TRUE);

        final PaymentService service = new PaymentService();
        final Submit submit = new Submit();
        final List<Object> submitOrder = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate();

        final Order order = new Order();
        order.setOrderCode("DS1347889928107_3");
        final List<Object> orderElements = order.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrInfo3DSecureOrSession();

        Description intDescription = new Description();
        intDescription.setvalue("Your Order & Order desc");
        orderElements.add(intDescription);

        final Amount amount = new Amount();
        amount.setCurrencyCode("EUR");
        amount.setExponent("2");
        amount.setValue("100");
        orderElements.add(amount);

        OrderContent intOrderContent = new OrderContent();
        intOrderContent.setvalue("<style type=\"text/css\">\n" +
                "body {                     font-size:  13px;     font-family:  Verdana, Helvetica, Arial, sans-serif;    margin: 0px;    padding: 0px;    border: 0px;    }    font {    font-size: 1.8em;    }     a {    font-weight: bold;    text-decoration: none;    }        center {                 display: block;    background-color: white;    margin: 125px 25% 0px 125px;    font-size: 0.55em;    }        div.topbar {    position: absolute;    top: 0px;    width: 100%;    right: 0px;    left: 0px;    margin: 0px;    background-color: white;    background-image: url(/pictures/branded-logo.png);    background-repeat: no-repeat;    border-bottom: 15px solid #369;    border-top: 28px solid #369;    height: 52px;    }        div.topbar div  {    background-color: white;    margin: 0px 0px 0px 275px;     height: 52px;    }     div.topbar div p {    padding-left: 15px;    padding-top: 15px;    margin: 0px;     font-style: italic;    font-weight: normal;    font-size: 1.5em;    text-align: left;    }            table td {    background-color: white;    font-size: 0.8em;    }     table td font {    font-size: 1em;    }     table.cart {         margin: 20px;    background-color: #aaa;    }        table.cart td {    padding: 0.3em 2em 0.3em 2em;    }        input, select {    background-color: #eee;    border: 1px solid #666;    }    </style>" +
                "<div class=\"topbar\"><div>\n" +
                "<p>RBS Worldpay Demoshop</p>\n" +
                "</div></div>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<table class=\"cart\">\n" +
                "<tr><th>Products</th><th>Price</th></tr>\n" +
                "<tr><td>Small donation</td>\n" +
                "<td>EUR 1.00</td></tr>\n" +
                "<tr><td style=\"border: 0px\"><b>Total</b></td>\n" +
                "<td style=\"border: 0px\"><b>EUR 1.00</b></td></tr>\n" +
                "</table>");
        orderElements.add(intOrderContent);

        final PaymentMethodMask paymentMethodMask = new PaymentMethodMask();
        final Include include = new Include();
        include.setCode("ALL");
        paymentMethodMask.getIncludeOrExclude().add(include);
        orderElements.add(paymentMethodMask);

        submitOrder.add(order);
        service.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(submit);
        service.setVersion("1_4");

        final WorldpayXMLValidator validator = new DefaultWorldpayXMLValidator();
        validator.validate(service);

        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLStreamWriter streamWriter = xof.createXMLStreamWriter(System.out, "UTF-8");
        final CDataXMLStreamWriter cdataStreamWriter = new CDataXMLStreamWriter(streamWriter);
        cdataStreamWriter.writeDTD("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE paymentService PUBLIC \"-//Worldpay//DTD Worldpay PaymentService v1//EN\" \"http://dtd.worldpay.com/paymentService_v1.dtd\">\n");
        marshaller.marshal(service, cdataStreamWriter);
    }

    @Test
    public void testUnmarshalXMLForSubmitOrder() throws JAXBException, UnsupportedEncodingException {
        final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<paymentService merchantCode=\"DEMO\" version=\"2.4\">\n    <reply>\n      <orderStatus orderCode=\"DS1347889928107_3\">\n         <reference id=\"2452787063\">https://secure.worldpay.com/sc2/jsp/shopper/SelectPaymentMethod.jsp?OrderKey=DEMO%5EDS1347889928107_3</reference>\n      </orderStatus>\n   </reply>\n</paymentService>";
        final byte[] bytes = xml.getBytes("UTF-8");
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        final Object unmarshal = unmarshaller.unmarshal(byteArrayInputStream);

        if (unmarshal == null) {
            fail("unmarshal object should not be null.");
        }
        if (unmarshal instanceof PaymentService) {
            final PaymentService paymentService = (PaymentService) unmarshal;
            final String merchantCode = paymentService.getMerchantCode();
            final List<Object> reply = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
            assertEquals("Merchant code unmarshalled incorrectly", "DEMO", merchantCode);
            assertNotNull("Reply object should not be null", reply);
        } else {
            fail("unmarshal object incorrect type.");
        }
    }
}
