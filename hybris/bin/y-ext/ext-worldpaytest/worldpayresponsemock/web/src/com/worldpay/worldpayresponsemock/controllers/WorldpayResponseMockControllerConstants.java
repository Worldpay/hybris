package com.worldpay.worldpayresponsemock.controllers;

/**
 * Response mock constants
 */
public class WorldpayResponseMockControllerConstants {

    private WorldpayResponseMockControllerConstants() {
    }

    /**
     * Response mock pages
     */
    public static class Pages {

        private Pages() {
        }

        /**
         * Response mock views
         */
        public static class Views {

            private Views() {
            }

            public static final String RESPONSES = "pages/responseView";
            public static final String LIGHTBOX = "pages/lightbox";
            public static final String THREED_SECURE_RESPONSE = "pages/threeDSecureResponse";
            public static final String DDC_IFRAME = "pages/worldpayDDCIframe";
            public static final String OUTER_DDC_IFRAME = "pages/outerDDCIframe";
        }
    }
}
