package com.company.service.repository;

import com.company.service.entity.ProcessInstanceViewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProcessInstanceMongoRepository extends MongoRepository<ProcessInstanceViewEntity, String> {

    public ProcessInstanceViewEntity findByProcessInstanceId(Long processInstanceId);

    public List<ProcessInstanceViewEntity> findByProcessId(String processId);
}
