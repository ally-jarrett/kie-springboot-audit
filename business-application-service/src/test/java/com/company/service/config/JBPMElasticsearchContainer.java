package com.company.service.config;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class JBPMElasticsearchContainer extends ElasticsearchContainer {

    private static final String ELASTIC_SEARCH_DOCKER = "elasticsearch:6.4.3";
    private static final String CLUSTER_NAME = "cluster.name";
    private static final String ELASTIC_SEARCH = "elasticsearch";

    public JBPMElasticsearchContainer() {
        super(ELASTIC_SEARCH_DOCKER);
        this.addFixedExposedPort(9200, 9200);
        this.addFixedExposedPort(9300, 9300);
        this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
    }
}