package com.example.packagist_scimport;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.packagist_scimport.metadata.MetadataHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class PackagistScimportApplication {

    public static void main(String[] args) throws Exception {
        // 초기설정 후에 메인 기능 실행.
        // importer 에서 사용될 기본적인 인증정보
        final List<String> bucket_names = new ArrayList<>(Arrays.asList("sca-packagist-md-crawler-0001"));
        //아래 정보는 고정됨.
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();
        final String gitID = "sca.github@gmail.com";
        final String gitPW = "!sparrow1605";

        // 각 bucket_names에 대하여 아래와 같은 프로세스를 진행함
        for(String bucket_name : bucket_names){

            // 레코드별 key를 가져오므로써 전체 다운로드 되어야 하는 리스트 생성
            ArrayList<S3ObjectSummary> objects = MetadataHandler.getObejctKeys(bucket_name, s3);

            for(S3ObjectSummary object : objects){
                MetadataHandler.getJSONfiles(object, bucket_name);
            }
        }
    }
}
