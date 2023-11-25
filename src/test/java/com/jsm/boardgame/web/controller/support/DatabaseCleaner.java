package com.jsm.boardgame.web.controller.support;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @PostConstruct
    public void init() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .map(this::getTableName)
                .filter(v -> !v.equals("region_code"))
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format("truncate table %s restart identity", tableName)).executeUpdate();
        }
    }

    private String getTableName(EntityType<?> entityType) {
        Table tableAnnotation = entityType.getJavaType().getAnnotation(Table.class);
        if (tableAnnotation != null) {
            return tableAnnotation.name();
        }

        return entityType.getName();
    }
}
