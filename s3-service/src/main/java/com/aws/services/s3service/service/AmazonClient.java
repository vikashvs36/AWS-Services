package com.aws.services.s3service.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;

/**
 * @author UTL1266
 *
 */
public interface AmazonClient {

	/**
	 * @param bucketName
	 * @throws Exception
	 */
	public void createBucket(String bucketName)throws Exception;
	
	/**
	 * @return
	 */
	public Set<String> listBuckets();
	
	/**
	 * @param bucketName
	 * @throws AmazonServiceException
	 */
	public void deleteBucket(String bucketName) throws AmazonServiceException;
	
	/**
	 * @param multipartFile
	 * @return
	 * @throws Exception
	 */
	String uploadObject(MultipartFile multipartFile) throws Exception;
	
	
	/**
	 * list of all Objects which is uploaded on respected Buckets
	 * 
	 * @return
	 * @throws Exception
	 */
	Set<String> listObjects() throws Exception;
	
	/**
	 * Download the Object by given filename from the respected Bucket
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	String downloadObject(String fileName) throws IOException; 
	
	/**
	 * Delete object from AWS S3 bucket
	 * 
	 * @param fileUrl
	 * @return
	 * @throws Exception
	 */
	String deleteObject(String fileUrl)  throws Exception;

	
	/**
	 * deleting Multiple Objects from respected Bucket by specifying multiple files names
	 * 
	 * @param objkeyArr
	 * @return
	 * @throws Exception
	 */
	List<DeletedObject> deletingMultipleObjects(String objkeyArr[]) throws Exception;
}
