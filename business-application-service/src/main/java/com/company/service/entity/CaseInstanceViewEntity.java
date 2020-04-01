package com.company.service.entity;

import org.drools.core.ClassObjectFilter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.OrganizationalEntity;

import java.util.*;
import java.util.stream.Collectors;

public class CaseInstanceViewEntity implements InstanceView<ProcessInstance> {
    private static final long serialVersionUID = -6518981747861727235L;
    private String compositeId;
    private Long id;
    private String caseDefinitionId;
    private String caseDefinitionName;
    private Integer caseStatus;
    private String containerId;
    private String owner;
    private Date date;
    private String caseDescription;
    private String caseId;
    private Long parentId;
    private Map<String, Object> variables;
    private Map<String, Object> caseVariables;
    private List<String> milestones;
    private List<String> stages;
    private Set<String> participants;
    private transient ProcessInstance source;

    public CaseInstanceViewEntity() {
    }

    public CaseInstanceViewEntity(ProcessInstance source) {
        this.source = source;
    }

    public String getCompositeId() {
        return this.compositeId;
    }

    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseDefinitionId() {
        return this.caseDefinitionId;
    }

    public void setProcessId(String processId) {
        this.caseDefinitionId = processId;
    }

    public String getCaseDefinitionName() {
        return this.caseDefinitionName;
    }

    public void setProcessName(String processName) {
        this.caseDefinitionName = processName;
    }

    public Integer getCaseStatus() {
        return this.caseStatus;
    }

    public void setState(Integer state) {
        this.caseStatus = state;
    }

    public String getContainerId() {
        return this.containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setInitiator(String initiator) {
        this.owner = initiator;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCaseDescription() {
        return this.caseDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.caseDescription = processInstanceDescription;
    }

    public String getCaseId() {
        return this.caseId;
    }

    public void setCorrelationKey(String correlationKey) {
        this.caseId = correlationKey;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Map<String, Object> getVariables() {
        return this.variables;
    }

    public Map<String, Object> getCaseVariables() {
        return this.caseVariables;
    }

    public List<String> getMilestones() {
        return this.milestones;
    }

    public List<String> getStages() {
        return this.stages;
    }

    public Set<String> getParticipants() {
        return this.participants;
    }

    public String toString() {
        return "CaseInstance{id=" + this.id + ", caseDefinitionId='" + this.caseDefinitionId + '\'' + ", caseDefinitionName='" + this.caseDefinitionName + '\'' + ", caseStatus=" + this.caseStatus + ", containerId='" + this.containerId + '\'' + ", caseId='" + this.caseId + '\'' + '}';
    }

    public ProcessInstance getSource() {
        return this.source;
    }

    public void copyFromSource() {
        if (this.id == null) {
            this.compositeId = System.getProperty("org.kie.server.id", "") + "_" + this.source.getId();
            this.containerId = ((WorkflowProcessInstance) this.source).getDeploymentId();
            this.caseId = ((WorkflowProcessInstanceImpl) this.source).getCorrelationKey();
            this.date = new Date();
            this.id = this.source.getId();
            this.parentId = this.source.getParentProcessInstanceId();
            this.caseDefinitionId = this.source.getProcessId();
            this.caseDescription = ((WorkflowProcessInstanceImpl) this.source).getDescription();
            this.caseDefinitionName = this.source.getProcessName();
            this.caseStatus = this.source.getState();
            this.variables = ((WorkflowProcessInstanceImpl) this.source).getVariables();
            this.milestones = new ArrayList();
            this.stages = new ArrayList();
            this.participants = new LinkedHashSet();
            Collection<NodeInstance> instances = ((WorkflowProcessInstanceImpl) this.source).getNodeInstances(true);
            Iterator var2 = instances.iterator();

            while (var2.hasNext()) {
                NodeInstance instance = (NodeInstance) var2.next();
                if (instance instanceof MilestoneNodeInstance) {
                    this.milestones.add(instance.getNodeName());
                } else if (instance instanceof DynamicNodeInstance) {
                    this.stages.add(instance.getNodeName());
                }
            }

            CaseData caseFile = this.internalGetCaseFile(((WorkflowProcessInstanceImpl) this.source).getKnowledgeRuntime());
            if (caseFile != null) {
                this.caseVariables = caseFile.getData();
                Collection<String> roles = ((CaseAssignment) caseFile).getRoles();
                if (roles.contains("owner")) {
                    this.owner = ((OrganizationalEntity) ((CaseAssignment) caseFile).getAssignments("owner").iterator().next()).getId();
                }

                Iterator var4 = roles.iterator();

                while (var4.hasNext()) {
                    String role = (String) var4.next();
                    List<String> assignees = (List) ((CaseAssignment) caseFile).getAssignments(role).stream().map((oe) -> {
                        return oe.getId();
                    }).collect(Collectors.toList());
                    this.participants.addAll(assignees);
                }
            }

        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            CaseInstanceViewEntity other = (CaseInstanceViewEntity) obj;
            if (this.id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!this.id.equals(other.id)) {
                return false;
            }

            return true;
        }
    }

    public CaseData internalGetCaseFile(KieRuntime kruntime) {
        Collection<? extends Object> caseFiles = kruntime.getObjects(new ClassObjectFilter(CaseData.class));
        if (caseFiles.size() != 1) {
            return null;
        } else {
            CaseData caseFile = (CaseData) caseFiles.iterator().next();
            return caseFile;
        }
    }
}

