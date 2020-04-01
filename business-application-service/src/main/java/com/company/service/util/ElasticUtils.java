package com.company.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.jbpm.persistence.api.integration.model.CaseInstanceView;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticUtils {

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticUtils.class);

    public List<ProcessInstanceView> getProcessInstanceViews(SearchResponse searchResponse) {

        LOGGER.info("");
        List<ProcessInstanceView> processes = new ArrayList<>();

        if (searchResponse == null || searchResponse.getHits() == null) {
            LOGGER.error("Elastic SearchResponse is null, returning empty collection for ProcessInstanceView");
            return processes;
        }

        LOGGER.info("Elastic SearchResponse contains {} hits, converting to ProcessInstanceView", searchResponse.getHits().getTotalHits());
        processes = Arrays.stream(searchResponse.getHits().getHits())
                .filter(hit -> hit.hasSource())
                .map(hit ->
                {
                    try {
                        return objectMapper.readValue(hit.getSourceAsString(),
                                ProcessInstanceView.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

        return processes;
    }

    public List<TaskInstanceView> getTaskInstanceViews(SearchResponse searchResponse) {
        List<TaskInstanceView> tasks = new ArrayList<>();

        if (searchResponse == null || searchResponse.getHits() == null) {
            LOGGER.error("Elastic SearchResponse is null, returning empty collection for TaskInstanceView");
            return tasks;
        }

        LOGGER.error("Elastic SearchResponse contains {} hits, converting to TaskInstanceView", searchResponse.getHits().getTotalHits());
        tasks = Arrays.stream(searchResponse.getHits().getHits())
                .filter(hit -> hit.hasSource())
                .map(hit ->
                {
                    try {
                        return objectMapper.readValue(hit.getSourceAsString(),
                                TaskInstanceView.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

        return tasks;
    }

    public List<CaseInstanceView> getCaseInstanceViews(SearchResponse searchResponse) {
        List<CaseInstanceView> cases = new ArrayList<>();

        if (searchResponse == null || searchResponse.getHits() == null) {
            LOGGER.error("Elastic SearchResponse is null, returning empty collection for CaseInstanceView");
            return cases;
        }

        LOGGER.error("Elastic SearchResponse contains {} hits, converting to CaseInstanceView", searchResponse.getHits().getTotalHits());
        cases = Arrays.stream(searchResponse.getHits().getHits())
                .filter(hit -> hit.hasSource())
                .map(hit ->
                {
                    try {
                        return objectMapper.readValue(hit.getSourceAsString(),
                                CaseInstanceView.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

        return cases;
    }
}
