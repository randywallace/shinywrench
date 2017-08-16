package com.randywallace.shinywrench.aws;

import com.randywallace.shinywrench.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;

import com.amazonaws.services.securitytoken.model.AWSSecurityTokenServiceException;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;

public class GenerateSessionCredentials {
	private static Logger LOG = LoggerFactory.getLogger(GenerateSessionCredentials.class);

	private AWSSecurityTokenService sts_client;

	public GenerateSessionCredentials(Profile profile) {
       AWSCredentials request_credentials = new BasicAWSCredentials(
               profile.getAccessKeyId(),
               profile.getSecretAccessKey()
       );
       setupStsClient(request_credentials, profile.getRegion());
    }

	public GenerateSessionCredentials(String aws_access_key_id, String aws_secret_access_key, String region) {
        AWSCredentials request_credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);
        setupStsClient(request_credentials, region);
    }

	private void setupStsClient(AWSCredentials request_credentials, String region) {
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
		LOG.info("Successfully Generated Session Credentials with " + mfa_serial);
		return session_credentials;
	}

    public Credentials getSessionCredentials() throws AWSSecurityTokenServiceException {
        GetSessionTokenRequest sts_request = new GetSessionTokenRequest()
                .withDurationSeconds(60 * 60 * 8);
        Credentials session_credentials;
        session_credentials = this.sts_client.getSessionToken(sts_request).getCredentials();
        LOG.info("Successfully Generated Session Credentials");
        return session_credentials;
    }
}
