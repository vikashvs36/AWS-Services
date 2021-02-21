package com.aws.services.s3service.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.aws.services.s3service.service.AmazonClient;

@Service
public class AmazonClientImpl implements AmazonClient {
	
	@Value("${s3.endpointUrl}")
	private String endpointUrl;
	@Value("${s3.bucketName}")
	private String bucketName;
	@Value("${s3.accessKeyId}")
	private String accessKeyId;
	@Value("${s3.secretKey}")
	private String secretKey;
	@Value("${s3.region}")
	private String region;
	
	@Value("${file.upload-dir}")
	private String localDir;
	
	private AmazonS3 s3client;

	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		
		
	}
	
	@Override
	public void createBucket(String bucketName) throws Exception {
		if (s3client.doesBucketExist(bucketName)) {
			return;
		}
		s3client.createBucket(bucketName);
	}

	@Override
	public Set<String> listBuckets() {
		return s3client.listBuckets()
				.stream()
				.map(Bucket::getName)
				.collect(Collectors.toSet());
	}
	
	@Override
	public void deleteBucket(String bucketName) throws AmazonServiceException {
		s3client.deleteBucket(bucketName);
	}

	@Override
	public String uploadObject(MultipartFile multipartFile) throws Exception {
		String fileUrl = "";
		File file = convertMultiPartToFile(multipartFile);
		String fileName = generateFileName(multipartFile);
		fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
		uploadFileTos3bucket(fileName, file);
		file.delete();
		return fileUrl;
	}

	@Override
	public Set<String> listObjects() throws Exception {
		ObjectListing objectListing = s3client.listObjects(bucketName);
		return objectListing.getObjectSummaries()
				.stream()
				.map(S3ObjectSummary::getKey)
				.collect(Collectors.toSet());
	}

	@Override
	public String downloadObject(String fileName) throws IOException {
		
		// get object from source means Amazon bucket
		S3Object s3object = s3client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		
		// set into destination which is local directory
	    Path path = Paths.get(localDir+fileName);
	    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		return null;
	}

	@Override
	public String deleteObject(String fileUrl) throws Exception {
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		s3client.deleteObject(bucketName, fileName);
		return "Successfully deleted";
	}

	@Override
	public List<DeletedObject> deletingMultipleObjects(String[] objkeyArr) throws Exception {
		List<DeletedObject> deletedObjects = new ArrayList<>();
		DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucketName).withKeys(objkeyArr);
		final DeleteObjectsResult deleteObjects = s3client.deleteObjects(delObjReq);
		if (deleteObjects != null) {
			deletedObjects = deleteObjects.getDeletedObjects();
		}
		return deletedObjects;
	}

	/**
	 * @param fileName
	 * @param file
	 */
	private void uploadFileTos3bucket(String fileName, File file) throws Exception {
		s3client.putObject(bucketName, fileName, file);
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	/**
	 * @param multiPart
	 * @return
	 */
	private String generateFileName(MultipartFile multiPart) throws Exception {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}
}