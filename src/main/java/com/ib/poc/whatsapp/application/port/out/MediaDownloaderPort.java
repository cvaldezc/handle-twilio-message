package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.MediaAttachment;

public interface MediaDownloaderPort {

    /**
     * Downloads raw bytes for the given media attachment using Twilio auth.
     */
    byte[] download(MediaAttachment attachment);
}
