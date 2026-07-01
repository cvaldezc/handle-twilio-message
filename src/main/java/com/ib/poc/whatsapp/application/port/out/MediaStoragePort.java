package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.DownloadedMedia;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;

import java.nio.file.Path;

public interface MediaStoragePort {

    Path store(MediaAttachment attachment, String messageId, DownloadedMedia downloaded);
}
