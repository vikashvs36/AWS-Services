package com.aws.services.s3service.serviceImpl;

import java.io.File;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aws.services.s3service.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

	@Value("${file.upload-dir}")
    public String uploadDir;
	
	@Override
	public String store(MultipartFile file) {
		final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
           
			/*
			 * // Copy file to the target location (Replacing existing file with the same
			 * name) final Path copyLocation = Paths.get(uploadDir+File.separator+fileName);
			 * Files.copy(file.getInputStream(), copyLocation,
			 * StandardCopyOption.REPLACE_EXISTING);
			 */
            
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.write(path, bytes);

            return fileName;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return null;
	}

	@Override
	public Stream<Path> loadAll() {
		return null;
	}

	@Override
	public Path load(String filename) {
		return null;
	}

	@Override
	public Resource loadAsResource(String filename) {
		return null;
	}

	@Override
	public void deleteAll() {
	}
}
