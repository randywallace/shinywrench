package com.randywallace.shinywrench.aws;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAWSAccess {

	private static Logger LOG = LoggerFactory.getLogger(TestAWSAccess.class);

	private AWSCredentials request_credentials;
	private String region;

	public TestAWSAccess(String aws_access_key_id, String aws_secret_access_key, String aws_session_token, String region) {
		if (aws_session_token != null && !aws_session_token.isEmpty()) {
			this.request_credentials = new BasicSessionCredentials(aws_access_key_id, aws_secret_access_key, aws_session_token);
		} else {
			this.request_credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);
		}
		this.region = region;
	}

	public boolean testS3ListBuckets() {
		AmazonS3 s3_client = AmazonS3Client.builder()
				.withCredentials(new AWSStaticCredentialsProvider(this.request_credentials))
				.withRegion(this.region)
				.build();
		try {
			List<Bucket> response = s3_client.listBuckets();
			System.out.println(response.size());
			LOG.info("Found " + response.size() + " Buckets");
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
}
