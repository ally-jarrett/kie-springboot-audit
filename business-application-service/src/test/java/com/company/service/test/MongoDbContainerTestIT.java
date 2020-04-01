package com.company.service.test;

import com.company.service.config.MongoDbContainer;
import com.company.service.entity.ProcessInstanceViewEntity;
import com.company.service.entity.TaskInstanceViewEntity;
import com.company.service.util.KIEUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ContextConfiguration(initializers = MongoDbContainerTestIT.MongoDbInitializer.class)
@ExtendWith(SpringExtension.class)
public class MongoDbContainerTestIT {

    private String processCollectionName = Character.toLowerCase(ProcessInstanceViewEntity.class.getSimpleName().charAt(0))
            + ProcessInstanceViewEntity.class.getSimpleName().substring(1);

    private String taksCollectionName = Character.toLowerCase(TaskInstanceViewEntity.class.getSimpleName().charAt(0))
            + TaskInstanceViewEntity.class.getSimpleName().substring(1);

    @Autowired
    KIEUtil utils;

    @Autowired
    MongoTemplate mongoTemplate;

    private static MongoDbContainer mongoDbContainer;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDbContainer = new MongoDbContainer();
        mongoDbContainer.start();
    }

    @AfterAll
    public static void tearDown() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1); // Prevent Connection Exception on log
        mongoDbContainer.stop();
    }

    @BeforeEach
    public void testSetup() throws InterruptedException {
        if (!mongoTemplate.collectionExists(ProcessInstanceViewEntity.class)) {
            mongoTemplate.createCollection(ProcessInstanceViewEntity.class);
            utils.initialiseProcessInstances();

            await().atMost(10000, MILLISECONDS).until(() ->
                    mongoTemplate.getCollection(processCollectionName).countDocuments() == 100);
        }
    }

    /**
     * Generic Test Mongo Test Container is working ..
     */
    @Test
    public void testMongoContainer() {
        MongoClient mongoClient = new MongoClient(mongoDbContainer.getContainerIpAddress(), mongoDbContainer.getMappedPort(27017));
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("testCollection");

        Document doc = new Document("name", "foo")
                .append("value", 1);
        collection.insertOne(doc);

        Document doc2 = collection.find(new Document("name", "foo")).first();
        Assert.assertEquals("A record can be inserted into and retrieved from MongoDB", 1, doc2.get("value"));
    }

    @Test
    public void testMongoTemplate() {
        Assert.assertTrue(mongoDbContainer.isRunning());
        Assert.assertEquals(mongoTemplate.getCollection(processCollectionName).countDocuments(), 100);
        Assert.assertEquals(mongoTemplate.getCollection(taksCollectionName).countDocuments(), 100);

        // Basic Query on ProcessID
        Query query = new Query();
        query.addCriteria(Criteria.where("processId").is(KIEUtil.PROCESS_ID));
        List<ProcessInstanceViewEntity> processInstances = mongoTemplate.find(query, ProcessInstanceViewEntity.class);
        Assert.assertEquals(processInstances.size(), 100);

        // Basic Query on ProcessInstanceID
        query = new Query();
        query.addCriteria(Criteria.where("processInstanceId").is(1));
        ProcessInstanceViewEntity processInstance = mongoTemplate.findOne(query, ProcessInstanceViewEntity.class);
        Assert.assertNotNull(processInstance);
    }

    // Initialise MongoDB and update properties with respective vals
    public static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            System.setProperty("org.jbpm.event.emitters.mongodb.host", mongoDbContainer.getContainerIpAddress());
            System.setProperty("org.jbpm.event.emitters.mongodb.port", mongoDbContainer.getMappedPort(27017).toString());

            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoDbContainer.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + mongoDbContainer.getMappedPort(27017)
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
