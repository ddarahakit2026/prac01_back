package com.example.demo.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// 오늘 날짜 별로 파일이 생기게 메소드 만들기
// 파일 중복 X 설정
@Component
public class UploadUtill {
    @Value("${project.upload.path}")
    private String defaultUploadPath;

    public String makeFolder() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = date.replace("/", File.separator);

        File uploadPath = new File(defaultUploadPath + File.separator + folderPath);

        if (!uploadPath.exists()) {
            uploadPath.mkdirs();

        }

        return uploadPath.getPath();
    }
}
