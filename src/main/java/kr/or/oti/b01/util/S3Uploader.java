package kr.or.oti.b01.util;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3; // AmazonS3Client -> AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

	// AmazonS3 인터페이스 타입으로 주입받아야 XML 빈과 100% 매핑됩니다.
	private final AmazonS3 amazonS3Client;
	
	@Value("${cloud.aws.s3.bucket}")
	public String bucket;

	public String upload(String filepath) throws RuntimeException {
		File targetFile = new File(filepath);
		// S3에 로컬 파일을 업로드
		String uploadImageUrl = putS3(targetFile, targetFile.getName());
		
		// S3에 업로드 된 로컬 파일을 삭제
		removeOriginalFile(targetFile);
		
		// S3에 파일을 업로드 한 URL 경로를 리턴
		return uploadImageUrl;
	}

	private String putS3(File targetFile, String name) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, name, targetFile)
						.withCannedAcl(CannedAccessControlList.PublicRead));
		
		return amazonS3Client.getUrl(bucket, name).toString();
	}

	private void removeOriginalFile(File targetFile) {
		if(targetFile.exists() && targetFile.delete()) {
			log.info("삭제가 되었습니다.");
			return;
		}
		log.info("파일 삭제가 실패하였습니다.");
	}

	// S3에 업로드된 파일을 삭제
	public void removeS3File(String filename) {
		final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, filename);
		amazonS3Client.deleteObject(deleteObjectRequest);
	}

	// S3에 업로드된 파일 url 가져오기
	public String getS3URL(String filename) {
		return amazonS3Client.getUrl(bucket, filename).toString();
	}
}