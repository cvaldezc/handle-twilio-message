package com.ib.poc.whatsapp.infrastructure.web.mapper;

import com.ib.poc.whatsapp.domain.model.InboundMessage;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.domain.model.OutboundMessage;
import com.ib.poc.whatsapp.infrastructure.web.dto.SendMessageRequestDto;
import com.ib.poc.whatsapp.infrastructure.web.dto.TwilioWebhookParamsDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WebhookMapper {

    /**
     * Maps Twilio webhook params to the domain InboundMessage.
     * - Strips "whatsapp:" prefix from From/To
     * - mediaAttachments populated in @AfterMapping from mediaUrls/mediaContentTypes
     */
    @Mapping(target = "from", expression = "java(stripWhatsAppPrefix(dto.getFrom()))")
    @Mapping(target = "to",   expression = "java(stripWhatsAppPrefix(dto.getTo()))")
    @Mapping(target = "mediaAttachments", ignore = true)
    InboundMessage toInboundMessage(TwilioWebhookParamsDto dto);

    @AfterMapping
    default void mapMediaAttachments(TwilioWebhookParamsDto dto,
                                     @MappingTarget InboundMessage.InboundMessageBuilder builder) {
        if (dto.getNumMedia() <= 0 || dto.getMediaUrls() == null) {
            return;
        }
        List<MediaAttachment> list = new ArrayList<>();
        for (int i = 0; i < dto.getNumMedia(); i++) {
            String url = dto.getMediaUrls().size() > i ? dto.getMediaUrls().get(i) : null;
            if (url == null) continue;
            String ct = dto.getMediaContentTypes() != null && dto.getMediaContentTypes().size() > i
                    ? dto.getMediaContentTypes().get(i)
                    : "application/octet-stream";
            list.add(MediaAttachment.builder().index(i).url(url).contentType(ct).build());
        }
        builder.mediaAttachments(list);
    }

    /** Maps send request DTO to domain OutboundMessage (message field → body) */
    @Mapping(target = "body", source = "message")
    OutboundMessage toOutboundMessage(SendMessageRequestDto dto);

    default String stripWhatsAppPrefix(String number) {
        if (number == null) return null;
        return number.startsWith("whatsapp:") ? number.substring("whatsapp:".length()) : number;
    }
}
