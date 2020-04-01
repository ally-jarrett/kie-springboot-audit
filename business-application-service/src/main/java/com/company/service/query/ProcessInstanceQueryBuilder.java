//package com.company.service.query;
//
//import org.apache.ibatis.jdbc.SQL;
//import org.jbpm.services.api.model.ProcessDefinition;
//
//public class ProcessInstanceQueryBuilder {
//
//    // Interface to Set From
//    interface ProcessInstanceSQL {
//        ProcessInstanceSQL setProcessInstance(ProcessDefinition definition);
//    }
//
//    // Interface to Set From
//    interface VariableInstanceSQL {
//        VariableInstanceSQL setVariableInstance(String name, String type);
//    }
//
//    public static class ProcessQueryBuilder implements ProcessInstanceSQL, VariableInstanceSQL {
//
//        @Override
//        public ProcessInstanceSQL setProcessInstance(ProcessDefinition definition) {
//            String sql = new SQL()
//                    .C
//                    .INSERT_INTO("PERSON")
//                    .VALUES("ID, FIRST_NAME", "#{id}, #{firstName}")
//                    .VALUES("LAST_NAME", "#{lastName}")
//                    .toString();
//
//            return null;
//        }
//
//        @Override
//        public VariableInstanceSQL setVariableInstance(String name, String type) {
//            return null;
//        }
//
//    }
//}
