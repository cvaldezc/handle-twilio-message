package com.ib.poc.whatsapp.application.port.out;

public interface TwimlBuilderPort {

    /**
     * Builds a TwiML XML response string.
     *
     * @param replyText text for the WhatsApp reply message; null or blank produces an empty Response (no reply)
     * @return TwiML XML string
     */
    String build(String replyText);
}
