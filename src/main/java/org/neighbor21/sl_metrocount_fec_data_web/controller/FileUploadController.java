package org.neighbor21.sl_metrocount_fec_data_web.controller;

import org.neighbor21.sl_metrocount_fec_data_web.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.controller
 * fileName       : FileUploadController.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 파일 업로드를 처리하는 컨트롤러 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
@Controller
public class FileUploadController {

    @Autowired
    private FileProcessingService fileProcessingService;

    /**
     * 메인 페이지로 이동하는 요청을 처리합니다.
     *
     * @return 업로드 페이지를 반환합니다.
     */
    @GetMapping("/")
    public String index() {
        return "upload";
    }

    /**
     * 파일 업로드 요청을 처리합니다.
     *
     * @param files 업로드된 MultipartFile 객체 배열
     * @return 업로드 성공 또는 실패에 따른 리다이렉트 URL을 반환합니다.
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("files") MultipartFile[] files) {
        try {
            // 업로드된 파일 리스트 로그 출력
            System.out.println("Files received:");
            for (MultipartFile file : files) {
                System.out.println(" - " + file.getOriginalFilename());
            }

            fileProcessingService.processFiles(files);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/?error";  // 업로드 실패 시 에러 메시지와 함께 리다이렉트
        }
        return "redirect:/?success";  // 업로드 성공 시 성공 메시지와 함께 리다이렉트
    }

}
