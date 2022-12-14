package com.lilinxi.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author lilinxi lilinxi015@163.com
 **/
public class MongoCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String database = context.getEnvironment().getProperty("linxi.database");
        return "mongodb".equalsIgnoreCase(database);
    }
}
