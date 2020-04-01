package com.company.service.type;

import static com.company.service.type.QueryMapperType.*;

public enum QueryAttribute {

    TASK_OWNER("actualOwner", TASK),
    TASK_GROUP("group", TASK),
    CASE_OWNER("caseOwner", TASK),
    PROCESS_DEF_ID("processId", TASK),
    TASK_COMMENT("comment", TASK),
    COMPOSITE_ID("compositeId", TASK);

    private String fieldId;
    private QueryMapperType type;

    QueryAttribute(String fieldId, QueryMapperType type){
        this.fieldId = fieldId;
        this.type = type;
    }


}
