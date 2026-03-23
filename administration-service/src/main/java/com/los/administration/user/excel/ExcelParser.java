package com.los.administration.user.excel;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelParser<T> {

    List<T> parse(MultipartFile file);

}
