package com.randywallace.shinywrench.aws;

import static org.junit.Assert.*;

import com.amazonaws.services.rds.model.DBCluster;
import com.randywallace.shinywrench.logback.ConfigureLogback;
import com.randywallace.shinywrench.model.Profile;
import com.randywallace.shinywrench.model.SystemProfile;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

//@Ignore
public class RDSTest {
    private static Logger LOG = LoggerFactory.getLogger(RDSTest.class);

    @BeforeClass
    public static void setUp() {
        new ConfigureLogback().default_configure();
    }

    @After
    public void tearDown() {
    }

    @Test
    public final void testRdsAccessDefaultProfile() {
        RDS inst = new RDS();
        ArrayList<DBCluster> endpoints = inst.getIAMAuthEnabledEndpoints();
        assertTrue(endpoints.size() > 0 );
        inst.generateIAMPasswordReaderEndpoint(endpoints.get(0), "test");
    }

    @Test
    public final void testRdsAccessNonMFAProfile() {
        Profile profile = SystemProfile.getInstance().getProfileByName("non-mfa");
        ArrayList<DBCluster> endpoints = new RDS(profile).getIAMAuthEnabledEndpoints();
        assertTrue(endpoints.size() > 0 );
    }

    @Test
    public final void testGetIamAuthToken() {
        Profile profile = SystemProfile.getInstance().getProfileByName("non-mfa");
        RDS rds_session = new RDS(profile);
        DBCluster test_endpoint = rds_session.getIAMAuthEnabledEndpoints().get(0);
        LOG.info(rds_session.generateIAMPasswordReaderEndpoint(test_endpoint, "test"));
        LOG.info(rds_session.generateIAMPasswordWriterEndpoint(test_endpoint, "test"));
    }
}
