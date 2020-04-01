package com.company.service.entity;

import lombok.*;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Document
public class TaskInstanceViewEntity implements Serializable {
    private static final long serialVersionUID = 8214656362310328071L;

    @Id
    private String compositeId;
    private Long id;
    private Integer priority;
    private String name;
    private String subject;
    private String description;
    private String taskType;
    private String formName;
    private String status;
    private String actualOwner;
    private String createdBy;
    private Date createdOn;
    private Date activationTime;
    private Date expirationDate;
    private Boolean skipable;
    private Long workItemId;
    private Long processInstanceId;
    private Long parentId;
    private String processId;
    private String containerId;
    private List<String> potentialOwners;
    private List<String> excludedOwners;
    private List<String> businessAdmins;
    private Map<String, Object> inputData;
    private Map<String, Object> outputData;

    public TaskInstanceViewEntity(TaskInstanceView source) {
        this.compositeId = source.getCompositeId();
        this.activationTime = source.getActivationTime();
        this.actualOwner = source.getActualOwner();
        this.businessAdmins = source.getBusinessAdmins();
        this.containerId = source.getContainerId();
        this.createdBy = source.getCreatedBy();
        this.createdOn = source.getCreatedOn();
        this.description = source.getDescription();
        this.excludedOwners = source.getExcludedOwners();
        this.expirationDate = source.getExpirationDate();
        this.formName = source.getFormName();
        this.id = source.getId();
        this.inputData = source.getInputData();
        this.name = source.getName();
        this.outputData = source.getOutputData();
        this.parentId = source.getParentId();
        this.potentialOwners = source.getPotentialOwners();
        this.priority = source.getPriority();
        this.processId = source.getProcessId();
        this.processInstanceId = source.getProcessInstanceId();
        this.skipable = source.getSkipable();
        this.status = source.getStatus();
        this.subject = source.getSubject();
        this.taskType = source.getTaskType();
        this.workItemId = source.getWorkItemId();

    }
}