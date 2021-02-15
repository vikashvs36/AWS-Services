package com.aws.services.s3service.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aws.services.s3service.payload.UploadFileResponse;
import com.aws.services.s3service.service.StorageService;

@RestController
@RequestMapping(value = "/api/aws/s3")
public class S3StorageController {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(S3StorageController.class);
	
	@Autowired
	private StorageService storageService;	
	
	@PostMapping(value = "/uploadFile")
	public UploadFileResponse uploadData(@RequestParam("file") MultipartFile file) {
		LOGGER.info("Uploading files.....");

		String fileName = storageService.store(file);

		final String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("downloadFile")
				.path(fileName)
				.toUriString();

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}
	
	@PostMapping(value = "/uploadFiles")
	public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("file") MultipartFile[] files) {
		return Arrays.asList(files)
				.stream()
				.map(file -> uploadData(file))
				.collect(Collectors.toList());
	}

}
