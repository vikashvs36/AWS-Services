package com.aws.services.s3service.serviceImpl;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aws.services.s3service.service.AmazonClient;
import com.aws.services.s3service.service.StorageService;

@Service("AwsStorageService")
public class AwsStorageServiceImpl implements StorageService {

	@Autowired
	private AmazonClient amazonClient;

	@Override
	public String uploadObject(MultipartFile file) {
		String uploadFile = null;
		try {
			return amazonClient.uploadObject(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uploadFile;
	}

	@Override
	public Path load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource loadAsResource(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<Path> uploadAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteObject(String fileUrl) {
		try {
			return amazonClient.deleteObject(fileUrl);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public String deleteMultipleObjects(String[] fileUrls) {
		try {
			return amazonClient.deletingMultipleObjects(fileUrls).stream().map(obj -> obj.getKey())
					.collect(Collectors.joining(","));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
