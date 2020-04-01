package com.company.service.test;

import com.company.service.util.KIEUtil;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JpaDbTestIT {

    @Autowired
    KIEUtil utils;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        //
    }

    @AfterAll
    public static void tearDown() throws InterruptedException {
        //
    }

    @BeforeEach
    public void testSetup() throws InterruptedException {

    }

    @Test
    public void testMongoContainer() throws InterruptedException {
        utils.initialiseProcessInstances();
        Collection<ProcessInstanceDesc> instances = utils.runtimeDataService.getProcessInstancesByProcessDefinition(KIEUtil.PROCESS_ID, new QueryContext(0, 1000));
        Assert.assertNotNull(instances);
        Assert.assertEquals(100, instances.size());
        TimeUnit.MINUTES.sleep(5);
    }

}