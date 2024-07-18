package com.example.BigProject_25.controller;

import com.example.BigProject_25.model.LostItem;
import com.example.BigProject_25.service.LostItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@RestController
@RequestMapping("/lost-items")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService; // LostItemService를 주입받음

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환을 위한 ObjectMapper 주입

    // 루트 경로 불러오기
    private final String rootPath = System.getProperty("user.dir");
    // 프로젝트 루트 경로에 있는 files 디렉토리 경로 설정
    private final String fileDir = rootPath + "/files/";

    // 분실물 아이템을 생성하는 엔드포인트
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')") // 관리자 권한 필요
    public LostItem createLostItem(@RequestPart("file") MultipartFile file,
                                   @RequestPart("lostItem") String lostItemJson) throws IOException {
        // JSON 문자열을 LostItem 객체로 변환
        LostItem lostItem = objectMapper.readValue(lostItemJson, LostItem.class);
        // LostItem 객체와 파일을 저장하고 결과를 반환
        return lostItemService.saveLostItem(lostItem, file);
    }

    // 특정 카테고리의 분실물 아이템 목록을 반환하는 엔드포인트
    @GetMapping("/category/{category}")
    public Page<LostItem> getLostItemsByCategory(@PathVariable String category, Pageable pageable) {
        return lostItemService.getLostItemsByCategory(category, pageable);
    }

    // 특정 ID의 분실물 아이템을 반환하는 엔드포인트
    @GetMapping("/{id}")
    public LostItem getLostItemById(@PathVariable int id) {
        return lostItemService.getLostItemById(id);
    }

    // 분실물 아이템을 삭제하는 엔드포인트
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 관리자 권한 필요
    public void deleteLostItem(@PathVariable int id) {
        lostItemService.deleteLostItem(id);
    }

    // 분실물 아이템을 수정하는 엔드포인트
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 관리자 권한 필요
    public LostItem updateLostItem(@PathVariable int id,
                                   @RequestPart("file") MultipartFile file,
                                   @RequestPart("lostItem") String lostItemJson) throws IOException {
        // JSON 문자열을 LostItem 객체로 변환
        LostItem updatedLostItem = objectMapper.readValue(lostItemJson, LostItem.class);
        // 업데이트된 LostItem 객체와 파일을 저장하고 결과를 반환
        return lostItemService.updateLostItem(id, updatedLostItem, file);
    }

    // 파일명을 통해 이미지를 반환하는 엔드포인트
    @GetMapping("/display/{filename}")
    public ResponseEntity<Resource> display(@PathVariable String filename) {
        log.info("잘 들어오나요?");
        // 파일 경로로부터 리소스를 생성
        Resource resource = new FileSystemResource(fileDir + filename);

        // 파일이 존재하지 않으면 404 Not Found 응답 반환
        if (!resource.exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        HttpHeaders header = new HttpHeaders();
        Path filePath = null;
        try {
            // 파일 경로 설정
            filePath = Paths.get(fileDir + filename);
            // 파일의 Content-Type을 헤더에 추가
            header.add("Content-Type", Files.probeContentType(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 파일 리소스와 헤더를 포함하여 200 OK 응답 반환
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }
}
