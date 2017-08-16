package com.randywallace.shinywrench.aws;

import com.randywallace.shinywrench.logback.ConfigureLogback;
import com.randywallace.shinywrench.model.Profile;
import com.randywallace.shinywrench.model.SystemProfile;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class GenerateSessionCredentialsTest {
    private static Logger LOG = LoggerFactory.getLogger(GenerateSessionCredentialsTest.class);

    @BeforeClass
    public static void setUp() {
        new ConfigureLogback().default_configure();
    }

    private final Profile getProfile(String profile_name) {
        return SystemProfile.getInstance().getProfileByName(profile_name);
    }

    @Test
    public final void testGenerateSessionToken() {
        GenerateSessionCredentials sessionCredential = new GenerateSessionCredentials(getProfile("non-mfa"));
        assertTrue(sessionCredential.getSessionCredentials().getAccessKeyId().length() > 0 );
    }
}
