package com.company.service.emitter;

import com.company.service.entity.CaseInstanceViewEntity;
import com.company.service.entity.ProcessInstanceViewEntity;
import com.company.service.entity.TaskInstanceViewEntity;
import com.company.service.util.BeanUtil;
import com.mongodb.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.jbpm.persistence.api.integration.EventCollection;
import org.jbpm.persistence.api.integration.EventEmitter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.persistence.api.integration.base.BaseEventCollection;
import org.jbpm.persistence.api.integration.model.CaseInstanceView;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MongoEventEmitter implements EventEmitter {

    private String mongoDbName = System.getProperty("org.jbpm.event.emitters.mongodb.name", "test");
    private String host = System.getProperty("org.jbpm.event.emitters.mongodb.host", "localhost");
    private Integer port = Integer.valueOf(System.getProperty("org.jbpm.event.emitters.mongodb.port", "27017"));
    private ExecutorService executor;

    MongoClient mongoClient;
    MongoTemplate mongoTemplate;

    public MongoEventEmitter() {
        // TODO : Create Manual Collections for Process/Task/Case
        log.info("Build Mongo Client : host={} port={}", host, port);
//        mongoClient = new MongoClient(host, port);
//        MongoDatabase database = mongoClient.getDatabase(mongoDbName);
        this.executor = this.buildExecutorService();

        // TODO:  I'm Cheating looking up MongoTemplate - fininsh impl above.
        mongoTemplate = BeanUtil.getBean(MongoTemplate.class);
        this.buildCollections();
    }

    @Override
    public void deliver(Collection<InstanceView<?>> collection) {
        // no op
    }

    @Override
    public void apply(Collection<InstanceView<?>> collection) {
        if (!collection.isEmpty()) {

            this.executor.execute(() -> {
                log.debug("COLLECTION: {}", collection);

                Iterator iterator = collection.iterator();
                while (iterator.hasNext()) {

                    InstanceView view = (InstanceView) iterator.next();
                    if (view instanceof ProcessInstanceView) {
                        ProcessInstanceViewEntity entity = new ProcessInstanceViewEntity((ProcessInstanceView) view);
                        log.debug("PROCESS INSTANCE VIEW : {}", entity);
                        mongoTemplate.insert(entity);
                    } else if (view instanceof TaskInstanceView) {
                        TaskInstanceViewEntity entity = new TaskInstanceViewEntity((TaskInstanceView) view);
                        log.debug("TASK INSTANCE VIEW : {}", entity);
                        mongoTemplate.insert(entity);
                    } else if (view instanceof CaseInstanceView) {
                        log.debug("CASE INSTANCE VIEW : {}", view);
                    }
                }
            });
        }
        return;
    }

    @Override
    public void drop(Collection<InstanceView<?>> collection) {
        System.out.println("MONGO EVENT DROP");
        return;
    }

    @Override
    public EventCollection newCollection() {
        return new BaseEventCollection();
    }

    @Override
    public void close() {
//        this.mongoClient.close();
        this.executor.shutdown();
        log.info("Mongo event emitter closed successfully");
    }

    protected ExecutorService buildExecutorService() {
        return Executors.newCachedThreadPool();
    }

    protected void buildCollections() {
        if (!mongoTemplate.collectionExists(ProcessInstanceViewEntity.class)) {
            mongoTemplate.createCollection(ProcessInstanceViewEntity.class);
        }
        if (!mongoTemplate.collectionExists(TaskInstanceViewEntity.class)) {
            mongoTemplate.createCollection(TaskInstanceViewEntity.class);
        }
        if (!mongoTemplate.collectionExists(CaseInstanceViewEntity.class)) {
            mongoTemplate.createCollection(CaseInstanceViewEntity.class);
        }
    }
}
