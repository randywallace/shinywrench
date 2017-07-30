package com.randywallace.shinywrench.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AWSSecurityTokenServiceException;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;

public class GenerateSessionCredentials {

	private AWSSecurityTokenService sts_client;

	public GenerateSessionCredentials(String aws_access_key_id, String aws_secret_access_key, String region) {
		AWSCredentials request_credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);
		this.sts_client = AWSSecurityTokenServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(request_credentials))
				.withRegion(region)
				.build();
	}

	public Credentials getMFACredentials(String mfa_serial, String mfaCode) throws AWSSecurityTokenServiceException {
		GetSessionTokenRequest sts_request = new GetSessionTokenRequest()
				.withDurationSeconds(60 * 60 * 8)
				.withSerialNumber(mfa_serial)
				.withTokenCode(mfaCode);
		Credentials session_credentials;
		session_credentials = this.sts_client.getSessionToken(sts_request).getCredentials();
		//System.out.println(this.session_credentials.toString());
		return session_credentials;
	}
}
