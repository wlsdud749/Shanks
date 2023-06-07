package Team.TeamProject.service;

import Team.TeamProject.dto.ImageDto;
import Team.TeamProject.entity.Board;
import Team.TeamProject.entity.Image;
import Team.TeamProject.repository.BoardRepository;
import Team.TeamProject.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.*;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private static final String UPLOAD_PATH = "C:/summernote_image/"; // 이미지 업로드 경로 설정
    private List<Image> uploadedImages = new ArrayList<>(); // 업로드된 이미지 리스트

    /**
     * 이미지 업로드
     */
    public String uploadFile(String originalFileName, byte[] fileData) throws Exception {
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 파일 확장자
        String savedFileName = UUID.randomUUID().toString() + extension; // 저장할 파일명
        String fileUploadPath = UPLOAD_PATH + "/" + savedFileName; // 파일 저장 경로
        ImageDto imageDto = new ImageDto();
        imageDto.setImgName(savedFileName);
        imageDto.setImgPath(fileUploadPath);
        Image image = new Image();
        image.setImgName(imageDto.getImgName());
        image.setImgPath(imageDto.getImgPath());
        try (OutputStream outputStream = new FileOutputStream(fileUploadPath)) {
            outputStream.write(fileData); // 파일 저장
            imageRepository.save(image);
            uploadedImages.add(image);
        }
        return savedFileName;
    }

    /**
     * 이미지 삭제
     */
    public void deleteFile(String fileName) throws Exception {
        String filePath = UPLOAD_PATH + fileName;
        File deleteFile = new File(filePath);
        Optional<Image> optionalImage = imageRepository.findByImgName(fileName);
        Image image = optionalImage.get();
        if (deleteFile.exists()) {
            if (deleteFile.delete()) {
                imageRepository.delete(image);
                if(uploadedImages != null && uploadedImages.isEmpty()) {
                    if (uploadedImages.contains(image)) {
                        uploadedImages.remove(image);
                    }
                }
                log.info("파일을 삭제하였습니다.");
            } else {
                log.error("파일 삭제 실패");
                throw new Exception("파일 삭제 실패");
            }
        } else {
            log.info("파일이 존재하지 않습니다.");
            throw new FileNotFoundException(fileName +": 파일이 존재하지 않습니다.");
        }
    }

    /**
     * DB에 저장되지 않은 로컬 이미지 비우기
     */
    public void deleteImgList() throws Exception {
        if (uploadedImages != null && !uploadedImages.isEmpty()) {
            Iterator<Image> iterator = uploadedImages.iterator();
            while (iterator.hasNext()) {
                Image image = iterator.next();
                Optional<Image> optionalImage = imageRepository.findByImgName(image.getImgName());
                if (optionalImage.isPresent()) {
                    Image dbImage = optionalImage.get();
                    if (dbImage.getBoard() == null) {
                        deleteFile(dbImage.getImgName());
                        iterator.remove();
                    }
                }
            }
        }
    }
}
