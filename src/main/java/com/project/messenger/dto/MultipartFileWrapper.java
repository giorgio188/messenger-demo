package com.project.messenger.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MultipartFileWrapper {
    private int chatId;
    private MultipartFile file;
}
