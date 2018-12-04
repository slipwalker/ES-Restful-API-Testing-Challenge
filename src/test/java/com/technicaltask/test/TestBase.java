package com.technicaltask.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.Optional;

import static com.technicaltask.DefaultProperties.getEsDockerImage;

public abstract class TestBase {

    protected Logger log = LoggerFactory.getLogger(TestBase.class);

    protected ElasticsearchContainer container;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteSetup() {
        log.info("Starting Elastic search container...");

        container = new ElasticsearchContainer(getEsDockerImage());
        container.start();

        log.info("Elastic search container is ready to use.");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteCleanup() {
        log.info("Stop Elastic search container.");

        Optional.ofNullable(container).ifPresent(ElasticsearchContainer::stop);
    }
}