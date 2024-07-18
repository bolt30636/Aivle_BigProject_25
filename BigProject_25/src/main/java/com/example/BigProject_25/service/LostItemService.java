package com.example.BigProject_25.service;

import com.example.BigProject_25.model.LostItem;
import com.example.BigProject_25.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.MultipartBodyBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LostItemService {

    @Autowired
    private LostItemRepository lostItemRepository; // LostItemRepository를 주입받음

    private final RestTemplate restTemplate = new RestTemplate(); // RestTemplate 인스턴스 생성
    private final String flaskUrl = "http://localhost:6000/api/predict"; // Flask 서버 URL

    // 루트 경로 불러오기
    private final String rootPath = System.getProperty("user.dir");
    // 프로젝트 루트 경로에 있는 files 디렉토리 경로 설정
    private final String fileDir = rootPath + "/files/";

    public LostItemService() {
        // 파일 저장 디렉토리 생성
        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // 분실물 아이템 저장 메서드
    public LostItem saveLostItem(LostItem lostItem, MultipartFile file) throws IOException {
        // 이미지 파일 저장 및 파일명 설정
        String filename = saveImageFile(file);
        lostItem.setImgFilename(filename); // 파일명을 imgFilename으로 설정
        String category = classifyImage(file);
        lostItem.setCategory(category);
        return lostItemRepository.save(lostItem);
    }

    // 분실물 아이템 업데이트 메서드
    public LostItem updateLostItem(int id, LostItem updatedLostItem, MultipartFile file) throws IOException {
        LostItem existingLostItem = lostItemRepository.findById(id).orElse(null);
        if (existingLostItem != null) {
            if (!file.isEmpty()) {
                String filename = saveImageFile(file);
                updatedLostItem.setImgFilename(filename);
            } else {
                updatedLostItem.setImgFilename(existingLostItem.getImgFilename());
            }
            updatedLostItem.setLostID(existingLostItem.getLostID());
            lostItemRepository.save(updatedLostItem);
        }
        return updatedLostItem;
    }

    // 특정 카테고리의 분실물 아이템 목록을 반환하는 메서드
    public Page<LostItem> getLostItemsByCategory(String category, Pageable pageable) {
        return lostItemRepository.findByCategory(category, pageable);
    }

    // 특정 ID의 분실물 아이템을 반환하는 메서드
    public LostItem getLostItemById(int id) {
        return lostItemRepository.findById(id).orElse(null);
    }

    // 분실물 아이템을 삭제하는 메서드
    public void deleteLostItem(int id) {
        lostItemRepository.deleteById(id);
    }

    // 이미지 파일을 저장하는 메서드
    private String saveImageFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(fileDir + filename);
        Files.write(filePath, file.getBytes());
        return filename;
    }

    // 이미지를 분류하는 메서드
    private String classifyImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<?> requestEntity = new HttpEntity<>(builder.build(), headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                flaskUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> predictions = response.getBody();
        if (predictions != null && !predictions.isEmpty()) {
            return (String) predictions.get(0).get("class");
        }

        return "Unknown";
    }
}
