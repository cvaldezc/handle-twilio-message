package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.DownloadedMedia;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;

public interface MediaDownloaderPort {

    DownloadedMedia download(MediaAttachment attachment);
}
