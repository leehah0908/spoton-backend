package com.spoton.spotonbackend.nanum.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Handler {

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;
    private final AmazonS3Client amazonS3Client;

    public void upload(MultipartFile file, String fileName) {
        try {
            // 메타데이터 설정
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(BUCKET + "/nanum",
                    fileName,
                    file.getInputStream(),
                    metadata);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String nanumId){
        // S3 버킷의 경로(prefix)를 지정
        String prefix = "nanum/nanumId=" + nanumId;

        // S3 객체 목록 가져오기
        ListObjectsV2Request listRequest = new ListObjectsV2Request()
                .withBucketName(BUCKET)
                .withPrefix(prefix);

        ListObjectsV2Result listResult;

        do {
            listResult = amazonS3Client.listObjectsV2(listRequest);

            // 객체 삭제
            for (S3ObjectSummary objectSummary : listResult.getObjectSummaries()) {
                amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET, objectSummary.getKey()));
            }

            // 다음 페이지를 위한 ContinuationToken 설정
            listRequest.setContinuationToken(listResult.getNextContinuationToken());
        } while (listResult.isTruncated());
    }

}
