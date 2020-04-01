package com.company.service.util;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.UserTaskAdminService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.query.QueryService;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KIEUtil {

    public static final String DEPLOYMENT_ID = "business-application-kjar-1_0-SNAPSHOT";
    public static final String PROCESS_ID = "usertaskprocess";
    public static final String VERSION = "1.0.0";

    @Autowired
    public ProcessService processService;

    @Autowired
    public DeploymentService deploymentService;

    @Autowired
    public UserTaskService userTaskService;

    @Autowired
    public QueryService queryService;

    @Autowired
    public UserTaskAdminService userTaskAdminService;

    @Autowired
    public RuntimeDataService runtimeDataService;

    public void initialiseProcessInstances() throws InterruptedException {

        // Ensure Deployed Unit is active
        deploymentService.getDeployedUnits().forEach(deployedUnit -> System.out.println(deployedUnit.getDeploymentUnit().getIdentifier() + " :: " + deployedUnit.isActive()));
        DeployedUnit deployment = deploymentService.getDeployedUnit(DEPLOYMENT_ID);
        Assert.assertNotNull(deployment);
        Assert.assertTrue(deployment.isActive());

        // Initialise 100 process instances
        System.out.println(":::::::::::::::::::: INITIALISING PROCESSES STARTING ::::::::::::::::::::");
        int count = 0;
        Map<String, Object> payload;

        while (count < 100) {
            count++;
            payload = new HashMap<>();
            payload.put("name", "test_process_" + count);
            payload.put("age", 30);
            payload.put("count", count);

            long processInstanceId = processService.startProcess(DEPLOYMENT_ID, PROCESS_ID, payload);
            System.out.println("Created New Process Instances : " + processInstanceId + " for count: " + count);
        }

        System.out.println(":::::::::::::::::::: INITIALISING PROCESSES COMPLETE ::::::::::::::::::::");
    }
}
