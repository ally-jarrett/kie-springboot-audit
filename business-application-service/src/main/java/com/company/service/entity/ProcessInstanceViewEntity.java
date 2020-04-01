package com.company.service.entity;

import lombok.*;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Document
public class ProcessInstanceViewEntity implements Serializable {

    @Id
    private String compositeId;
    private Long processInstanceId;
    private String processId;
    private String processName;
    private String processVersion;
    private Integer state;
    private String containerId;
    private String initiator;
    private Date date;
    private String processInstanceDescription;
    private String correlationKey;
    private Long parentId;
    private Map<String, Object> variables;

    public ProcessInstanceViewEntity(ProcessInstanceView source) {
        this.compositeId = source.getCompositeId();
        this.containerId = source.getContainerId();
        this.correlationKey = source.getCorrelationKey();
        this.date = source.getDate();
        this.processInstanceId = source.getId();
        this.initiator = source.getInitiator();
        this.parentId = source.getParentId();
        this.processId = source.getProcessId();
        this.processInstanceDescription = source.getProcessInstanceDescription();
        this.processName = source.getProcessName();
        this.processVersion = source.getProcessVersion();
        this.state = source.getState();
        this.variables = source.getVariables();
    }
}
