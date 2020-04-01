package com.company.service.test;

import com.company.service.util.KIEUtil;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class JpaDbTestIT {

    @Autowired
    KIEUtil utils;

    @Autowired
    @Qualifier("auditEntityManager")
    EntityManagerFactory emf;
    EntityManager em;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {

    }

    @AfterAll
    public static void tearDown() throws InterruptedException {
        //
    }

    @BeforeEach
    public void testSetup() throws InterruptedException {
        if (em == null) {
            em = emf.createEntityManager();
            Assert.assertNotNull(em);
        }
    }

    @Test
    public void testProcessInstanceLogEventListner() throws InterruptedException {
        utils.initialiseProcessInstances();
        Collection<ProcessInstanceDesc> instances = utils.runtimeDataService.getProcessInstancesByProcessDefinition(KIEUtil.PROCESS_ID, new QueryContext(0, 1000));
        Assert.assertNotNull(instances);
        Assert.assertEquals(100, instances.size());

        Query query = em.createNativeQuery("SELECT * FROM ProcessInstanceLog", ProcessInstanceLog.class);
        List<ProcessInstanceLog> piLogs = (List<ProcessInstanceLog>) query.getResultList();
        Assert.assertNotNull(piLogs);
        Assert.assertEquals(100, piLogs.size());
        piLogs.forEach(p -> System.out.println(p));
    }

}