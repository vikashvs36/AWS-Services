package com.aws.services.s3service.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.aws.services.s3service.payload.UploadFileResponse;
import com.aws.services.s3service.service.AmazonClient;
import com.aws.services.s3service.service.StorageService;

@RestController
@RequestMapping(value = "/api/aws/s3")
public class AwsStorageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsStorageController.class);

	@Autowired
	@Qualifier("AwsStorageService")
	private StorageService storageService;

	@Autowired
	private AmazonClient amazonClient;

	@PostMapping(value = "bucket")
	public void createBucket(@RequestPart(name = "bucketName") String bucketName) {
		LOGGER.info("Creating Bucket in AWS-S3 server");
		try {
			amazonClient.createBucket(bucketName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping(value = "bucket")
	public Set<String> listBuckets() {
		return amazonClient.listBuckets();
	}

	@DeleteMapping(value = "bucket")
	public void deleteBucket(@RequestPart(name = "bucketName") String bucketName) {
		amazonClient.deleteBucket(bucketName);
	}

	@PostMapping(value = "/file")
	public UploadFileResponse uploadObject(@RequestPart("file") MultipartFile file) {
		LOGGER.info("Uploading files.....");

		String fileName = storageService.uploadObject(file);

		final String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("downloadFile")
				.path(fileName).toUriString();

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping(value = "/files")
	public List<UploadFileResponse> uploadMultipleObject(@RequestPart("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> uploadObject(file)).collect(Collectors.toList());
	}

	@GetMapping(value = "/file")
	public Set<String> listObjects() {
		try {
			return amazonClient.listObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping(value = "/downloadFile")
	public String downloadObject(@RequestPart(name = "fileName") String fileName) {
		try {
			return amazonClient.downloadObject(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@DeleteMapping(value = "/file")
	public String deleteObject(@RequestPart(value = "url") String fileUrl) {
		return storageService.deleteObject(fileUrl);
	}

	/*
	 * @DeleteMapping(value = "/files") public List<DeletedObject>
	 * deletingMultipleObjects(@RequestBody List<String> objkeyArr) { try { final
	 * String[] array = (String[]) objkeyArr.toArray(); return
	 * amazonClient.deletingMultipleObjects(array); } catch (Exception e) {
	 * e.printStackTrace(); } return null; }
	 */
}
