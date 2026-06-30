package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.MediaAttachment;

import java.nio.file.Path;

public interface MediaStoragePort {

    /**
     * Persists the given bytes for a media attachment to local storage.
     *
     * @param messageId used to create a dedicated subfolder per message
     * @return the path where the file was saved
     */
    Path store(MediaAttachment attachment, String messageId, byte[] content);
}
