package com.jsm.boardgame.service.file;

import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileService {

    private final Path uploadPath;
    private final long imageLimitSize;
    private final String imageServerHost;
    private final Tika tika;

    public FileService(@Value("${file.upload-path}") String uploadPath,
                       @Value("${file.image-limit-size}") String imageLimitSize,
                       @Value("${file.image-server-host}") String imageServerHost,
                       Tika tika) {
        this.uploadPath = Paths.get(uploadPath);
        this.imageLimitSize = DataSize.parse(imageLimitSize).toBytes();
        this.imageServerHost = imageServerHost;
        this.tika = tika;
    }

    public String uploadImage(MultipartFile image) throws IOException {
        if (imageLimitSize < image.getSize()) {
            throw new ApiException(ErrorCodeType.IMAGE_SIZE_LIMIT_EXCEEDED);
        }

        String mimeType = tika.detect(image.getInputStream());
        if (!mimeType.startsWith("image")) {
            throw new ApiException(ErrorCodeType.NOT_IMAGE);
        }

        UploadFile imageFile = new UploadFile(uploadPath, imageServerHost, image.getOriginalFilename());
        Files.createDirectories(imageFile.getSavePath().getParent());
        image.transferTo(imageFile.getSavePath());

        return imageFile.saveUrl;
    }

    @Getter
    public static class UploadFile {

        private final Path savePath;
        private final String saveUrl;

        public UploadFile(Path uploadPath, String host, String filename) {
            SecureRandom random = new SecureRandom();
            String ext = FilenameUtils.getExtension(filename);
            String randomFilename = new BigInteger(30, random) + "_" + UUID.randomUUID() + "." + ext;

            String[] date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).split("/");

            this.savePath = uploadPath.resolve(date[0]).resolve(date[1]).resolve(date[2]).resolve(randomFilename);
            this.saveUrl = UriComponentsBuilder.fromHttpUrl(host)
                    .pathSegment(date[0], date[1], date[2], randomFilename)
                    .toUriString();
        }
    }
}
