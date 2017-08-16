package com.randywallace.shinywrench.model;

import com.amazonaws.services.rds.model.DBCluster;
import com.randywallace.shinywrench.aws.RDS;

import java.util.ArrayList;

public class RDSIamDatabaseGenerator {
    private RDS rds_session;
    private ArrayList<DBCluster> db_clusters;

    public RDSIamDatabaseGenerator() {
        this.rds_session = new RDS();
    }

    public RDSIamDatabaseGenerator(Profile profile) {
        this.rds_session = new RDS(profile);
    }


}
