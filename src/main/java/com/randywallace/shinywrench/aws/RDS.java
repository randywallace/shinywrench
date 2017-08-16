package com.randywallace.shinywrench.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.randywallace.shinywrench.model.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;

import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.DescribeDBClustersResult;

import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;

import java.util.ArrayList;

public class RDS {
    private static Logger LOG = LoggerFactory.getLogger(RDS.class);

    private AmazonRDS rds_session;
    private String profile;
    private String region;
    private AWSCredentialsProvider credentialsProvider;

    public RDS() {
        LOG.info("Using DefaultCredentialsProviderChain for " + RDS.class);
        this.rds_session = AmazonRDSClientBuilder.defaultClient();
        this.region = new DefaultAwsRegionProviderChain().getRegion();
    }

    public RDS(Profile profile) {
        this.region = profile.getRegion();
        this.profile = profile.getProfile();
        LOG.info("Using ProfileCredentials for " + RDS.class);
        AWSCredentials creds = getCredentialsProvider().getCredentials();
        this.rds_session = AmazonRDSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(profile.getRegion()).build();
    }

    public DescribeDBClustersResult getDatabases() {
        return this.rds_session.describeDBClusters();
    }

    public ArrayList<DBCluster> getIAMAuthEnabledEndpoints() {
        ArrayList<DBCluster> cluster_endpoint_list = new ArrayList<>();
        for (DBCluster db_cluster : getDatabases().getDBClusters()) {
            if (db_cluster.getIAMDatabaseAuthenticationEnabled()) {
                cluster_endpoint_list.add(db_cluster);
            }
        }
        return cluster_endpoint_list;
    }

    public String generateIAMPasswordWriterEndpoint(DBCluster cluster, String userName) {
        return generateIAMPassword(cluster.getEndpoint(), cluster.getPort(), userName);
    }

    public String generateIAMPasswordReaderEndpoint(DBCluster cluster, String userName) {
        return generateIAMPassword(cluster.getReaderEndpoint(), cluster.getPort(), userName);
    }

    private String generateIAMPassword(String hostname, int port, String userName) {
        GetIamAuthTokenRequest password_request = GetIamAuthTokenRequest.builder()
                .hostname(hostname)
                .port(port)
                .userName(userName)
                .build();
        RdsIamAuthTokenGenerator token = RdsIamAuthTokenGenerator.builder()
                .credentials(getCredentialsProvider())
                .region(this.region)
                .build();
        String token_response = token.getAuthToken(password_request);
        return token_response;
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        if ( this.profile == null ) {
            this.credentialsProvider = new AWSStaticCredentialsProvider(new DefaultAWSCredentialsProviderChain().getCredentials());
        } else {
            if (this.credentialsProvider == null) {
                this.credentialsProvider = new ProfileCredentialsProvider(this.profile);
            }
        }
        return this.credentialsProvider;
    }
}
