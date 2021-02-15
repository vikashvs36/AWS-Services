package com.aws.services.s3service.serviceImpl;

import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

			// There are two way to save File on local Storage.
			// > First Way
			/*
			 * // Copy file to the target location (Replacing existing file with the same
			 * name) final Path copyLocation = Paths.get(uploadDir+File.separator+fileName);
			 * Files.copy(file.getInputStream(), copyLocation,
			 * StandardCopyOption.REPLACE_EXISTING);
			 */

			// > Second Way
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
	public Resource loadAsResource(String fileName) {
		Resource resource;
		Path path = Paths.get(uploadDir + fileName);
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		return resource;
	}

	@Override
	public void deleteAll() {
	}
}
