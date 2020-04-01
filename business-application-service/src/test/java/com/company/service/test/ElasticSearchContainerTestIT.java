package com.company.service.test;

import com.company.service.config.JBPMElasticsearchContainer;
import com.company.service.util.ElasticUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.UserTaskAdminService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ElasticSearchContainerTestIT {

    static final String DEPLOYMENT_ID = "business-application-kjar-1_0-SNAPSHOT";
    static final String PROCESS_ID = "usertaskprocess";
    static final String VERSION = "1.0.0";
    private static boolean createProcessInstances = false;

    private KModuleDeploymentUnit unit = null;

    @Autowired
    private ProcessService processService;

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    UserTaskAdminService userTaskAdminService;

    @Autowired
    private RuntimeDataService runtimeDataService;

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ElasticUtils elasticUtils;

    ObjectMapper objectMapper = new ObjectMapper();

    @Container
    private static ElasticsearchContainer elasticsearchContainer = new JBPMElasticsearchContainer();

    @BeforeAll
    static void setUp() {
        elasticsearchContainer.start();
        assertTrue(elasticsearchContainer.isRunning());
        System.out.println("Container Info: " + elasticsearchContainer.getHttpHostAddress());
    }

    @BeforeEach
    public void initialiseProcessInstances() throws InterruptedException {
        if (createProcessInstances) {
            return;
        }

        deploymentService.getDeployedUnits().forEach(deployedUnit -> System.out.println(deployedUnit.getDeploymentUnit().getIdentifier() + " :: " + deployedUnit.isActive()));

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
        createProcessInstances = true;
        System.out.println(":::::::::::::::::::: INITIALISING PROCESSES COMPLETE ::::::::::::::::::::");
        // Wait for Async
        Thread.sleep(10000L);
    }

//    @Test
//    void testIsContainerRunning() throws InterruptedException, IOException {
//
//        deploymentService.getDeployedUnits().forEach(deployedUnit -> System.out.println(deployedUnit.getDeploymentUnit().getIdentifier() + " :: " + deployedUnit.isActive()));
//
//        long initialProcessCount = getProcessInstanceViewSize();
//        this.initialiseProcessInstances();
//
//        // Elastic Async Call, wait until Process / Task data pushed.
//        await().atMost(3, TimeUnit.SECONDS)
//                .until(this::getProcessInstanceViewSize, count -> count > 0);
//
//        // Build Elastic Search Match Query
//        SearchRequest searchRequest = new SearchRequest("processes");
////        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("id", 1).p;
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(100);
////        sourceBuilder.query(matchQueryBuilder);
//        searchRequest.source(sourceBuilder);
//
//        // Execute Request
//        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
//
//        List<ProcessInstanceView> processes = elasticUtils.getProcessInstanceViews(searchResponse);
//        assertEquals(100,processes.size());
//        processes.forEach(System.out::println);
//
//        // Build Elastic Search Match Query
//        searchRequest = new SearchRequest("tasks");
////        matchQueryBuilder = QueryBuilders.matchQuery("processInstanceId", 1);
////        sourceBuilder = new SearchSourceBuilder();
////        sourceBuilder.query(matchQueryBuilder);
//        searchRequest.source(sourceBuilder);
//
//        // Execute Request
//        searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
//        System.out.println("ES Response: " + searchResponse);
//
//        List<TaskInstanceView> tasks = elasticUtils.getTaskInstanceViews(searchResponse);
//
//        assertEquals(100,tasks.size());
//        tasks.forEach(System.out::println);
//
//    }

    @Test
    void testUpdateProcessVariable() throws InterruptedException, IOException {

        this.initialiseProcessInstances();
        long rProcessInstanceId = ThreadLocalRandom.current().nextInt(1, 101);

        String newVar = "newVar";
        String newValue = "newValue";
        String newTaskVar = "newTaskVar";
        String newTaskValue = "newTaskValue";

        // Update Process Instance Var
        System.out.println( "Updating Process with Instance ID : " + rProcessInstanceId );
        ProcessInstance p = processService.getProcessInstance(rProcessInstanceId);
        processService.setProcessVariable(p.getId(), newVar, newValue);
        assertNotNull(p);

        // Wait for Async
        System.out.println(":::::::::::::::::::: PROCESSES VAR UPDATED ::::::::::::::::::::");
        Thread.sleep(5000L);

        String value = (String) processService.getProcessInstanceVariable(p.getId(), newVar);
        assertNotNull(value);
        assertEquals(newValue, value);

        // Get Tasks for Process Instances
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(p.getId());
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());

        // Update Task Var
//        System.out.println("::::::::::::::::::::::::::::::::::::::::");
        userTaskAdminService.addTaskInput(taskIds.get(0), newTaskVar, newTaskValue);
//        System.out.println("::::::::::::::::::::::::::::::::::::::::");
        Task task = userTaskService.getTask(taskIds.get(0));
        //userTaskService.delegate(task.getId(), task.getTaskData().getActualOwner().getId(), "testUser");
        userTaskService.forward(task.getId(), task.getTaskData().getActualOwner().getId(), "Ally");

        System.out.println(":::::::::::::::::::: TASK VAR UPDATED ::::::::::::::::::::");

        // Wait for Async
        Thread.sleep(3000L);

        Map<String, Object> taskVars = userTaskService.getTaskInputContentByTaskId(taskIds.get(0));
        assertNotNull(taskVars);
        assertTrue(taskVars.containsKey(newTaskVar));
        assertEquals(newTaskValue, (String) taskVars.get(newTaskVar));

        // Build Elastic Search Match Query
        SearchRequest searchRequest = new SearchRequest("processes");
        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("id", p.getId());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(100);
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);

        // Execute Request
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

        // Marshall Response
        List<ProcessInstanceView> processes = elasticUtils.getProcessInstanceViews(searchResponse);

        // Assert Results
        assertNotNull(processes);
        assertEquals(1, processes.size());
        assertTrue(processes.get(0).getVariables().containsKey(newVar));
        assertEquals(newValue, (String) processes.get(0).getVariables().get(newVar));

        searchRequest = new SearchRequest("tasks");
        matchQueryBuilder = QueryBuilders.matchQuery("id", taskIds.get(0));
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);

        // Execute Request
        searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

        // Marshall Response
        List<TaskInstanceView> tasks = elasticUtils.getTaskInstanceViews(searchResponse);

        // Assert Results
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).getInputData().containsKey(newTaskVar));
        assertEquals(newTaskValue, (String) tasks.get(0).getInputData().get(newTaskVar));
        System.out.println(":::::::::::::::::::: TASK :: " + tasks.get(0));
    }


    @AfterAll
    static void destroy() throws InterruptedException {
        Thread.sleep(1000L);
        elasticsearchContainer.stop();
    }

    private long getProcessInstanceViewSize() throws IOException {
        SearchResponse searchResponse = null;
        try {
            searchResponse = esClient.search(new SearchRequest("processes"), RequestOptions.DEFAULT);
            System.out.println("Processes Found : " + searchResponse.getHits().getTotalHits()
                    + " :: " + new Timestamp(System.currentTimeMillis()));
        } catch (ElasticsearchStatusException e) {
            // Index not created, created on 1st push
            return 0l;
        }

        return searchResponse.getHits().getTotalHits();
    }

}
