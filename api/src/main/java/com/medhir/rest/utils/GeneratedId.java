package com.medhir.rest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GeneratedId {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Generate Employee ID: always >= 100
     */
    public <T> String generateId(String prefix, Class<T> modelClass, String idFieldName) {
        String collectionName = modelClass.getAnnotation(Document.class).collection();
        return generateNextId(prefix, collectionName, idFieldName, 100);
    }

    /**
     * Generate Admin ID: prefers 001-099 block, falls back to normal
     */
    public <T> String generateAdminId(String prefix, Class<T> modelClass, String idFieldName) {
        String collectionName = modelClass.getAnnotation(Document.class).collection();

        Query query = new Query();
        query.addCriteria(Criteria.where(idFieldName).regex("^" + prefix + "\\d+$"));
        List<Object> documents = mongoTemplate.find(query, Object.class, collectionName);

        Set<Integer> usedNumbers = new HashSet<>();
        Pattern pattern = Pattern.compile("^" + prefix + "(\\d+)$");

        for (Object doc : documents) {
            try {
                Field idField = doc.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                String id = (String) idField.get(doc);

                Matcher matcher = pattern.matcher(id);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    usedNumbers.add(number);
                }
            } catch (Exception e) {
                // skip
            }
        }

        // Try to find smallest free number 1-99
        for (int i = 1; i < 100; i++) {
            if (!usedNumbers.contains(i)) {
                return prefix + String.format("%03d", i);
            }
        }

        // Fallback: use normal employee generator
        return generateNextId(prefix, collectionName, idFieldName, 100);
    }

    /**
     * Shared helper
     */
    private String generateNextId(String prefix, String collectionName, String idFieldName, int minNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where(idFieldName).regex("^" + prefix + "\\d+$"));

        List<Object> documents = mongoTemplate.find(query, Object.class, collectionName);

        int highestNumber = minNumber - 1;

        Pattern pattern = Pattern.compile("^" + prefix + "(\\d+)$");

        for (Object doc : documents) {
            try {
                Field idField = doc.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                String id = (String) idField.get(doc);

                Matcher matcher = pattern.matcher(id);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    if (number >= minNumber && number > highestNumber) {
                        highestNumber = number;
                    }
                }
            } catch (Exception e) {
                // skip
            }
        }

        int nextNumber = highestNumber + 1;
        return prefix + String.format("%03d", nextNumber);
    }

}
