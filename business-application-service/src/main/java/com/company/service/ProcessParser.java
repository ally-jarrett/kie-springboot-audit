package com.company.service;

import com.company.service.util.SQLUtil;
import lombok.extern.slf4j.Slf4j;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.Assert;
import org.kie.internal.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.StringJoiner;

@Slf4j
@Component
public class ProcessParser {

    @Autowired
    @Qualifier("auditEntityManager")
    EntityManagerFactory emf;

    @Autowired
    @Qualifier("auditJDBCTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    RuntimeDataService runtimeDataService;

    EntityManager em;

    /**
     * Parses deployed process definitions and creates a table..
     * // TODO : Explore whether this could be useful ;)
     * @throws SQLException
     */
    @EventListener(ApplicationReadyEvent.class)
    public void setup() throws SQLException {
        Assert.assertNotNull(emf);
        em = emf.createEntityManager();

        Assert.assertNotNull(em);
        Assert.assertNotNull(runtimeDataService);

        Collection<ProcessDefinition> processDefinitions = runtimeDataService.getProcesses(new QueryContext(0, 100));

        processDefinitions.forEach(pd -> {
            log.info("PROCESS DEFINITION ID: {}", pd.getId());
            log.info("PROCESS NAME: {}", pd.getName());
            log.info("PROCESS VARIABLES: {}", pd.getProcessVariables());
            log.info("PROCESS DEFINITION: {}", pd.getType());
            log.info("PROCESS DEFINITION: {}", pd.getVersion());
        });

        Optional<ProcessDefinition> pd = processDefinitions.stream().findFirst();
        Assert.assertNotNull(pd.get());
        String statement = this.buildSQLTable(pd.get());
        this.createTable(statement);

        try {
            System.out.println("Connection: " + jdbcTemplate.getDataSource().getConnection().getMetaData().getURL());
            ResultSet rs = jdbcTemplate.getDataSource().getConnection().getMetaData().getTables(null, null, "PROCESS_DEF_USERTASKPROCESS", null);
            if (rs.next()) {
                System.out.println("Table exists");
            } else {
                System.out.println("Table does not exist");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void createTable(String sqlStatement) throws SQLException {
        jdbcTemplate.execute(sqlStatement);
    }

    private String buildSQLTable(ProcessDefinition definition) {
        StringBuilder sb = new StringBuilder("CREATE TABLE PROCESS_DEF_");
        sb.append(definition.getName().toUpperCase());
        sb.append(" ");
        sb.append(SQLUtil.PROCESS_CORE_VARIABLES);

        if (!definition.getProcessVariables().isEmpty()) {
            sb.append(",");
            StringJoiner vars = new StringJoiner(",");
            definition.getProcessVariables().entrySet().forEach(e -> {
                log.info("VAR NAME : {} :: VAR TYPE :: {}", e.getKey(), e.getValue());
                vars.add(buildVarSqlElement(e.getKey(), e.getValue()));
            });
            sb.append(vars.toString());
        }

        sb.append(SQLUtil.PROCESS_CORE_END);
        return sb.toString();
    }

    private String buildVarSqlElement(String key, String type) {
        key = "VAR_" + key.toUpperCase();
        String var = null;
        switch (type.toUpperCase()) {
            case "STRING":
                var = key + " VARCHAR(255)";
                break;
            case "BOOLEAN":
                var = key + " BIT";
                break;
            case "INTEGER":
                var = key + " INTEGER";
                break;
            default:
                System.out.println("no match");
        }
        return var;
    }
}