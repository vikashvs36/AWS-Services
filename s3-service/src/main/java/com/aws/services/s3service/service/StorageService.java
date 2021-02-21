package com.aws.services.s3service.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	String uploadObject(MultipartFile file);

	Stream<Path> uploadAllObjects();

	Path load(String filename);

	Resource loadAsResource(String filename);

	String deleteObject(String fileUrl);
	
	String deleteMultipleObjects(String[] fileUrls);

}