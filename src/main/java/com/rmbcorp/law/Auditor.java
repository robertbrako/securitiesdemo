package com.rmbcorp.law;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rmbcorp.law.LawController.NODE_FACTORY;

class Auditor {

    private static final String SCHEMA_FILE = "src/main/resources/schema/audit.json";
    private static Auditor instance;

    private ObjectNode template;
    private Map<Integer, JsonNode> auditLog = new HashMap<>();
    private int aId = 0;

    private Auditor() {
        try (FileInputStream inputStream = new FileInputStream(SCHEMA_FILE)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode schemaTree = mapper.readTree(inputStream);
            template = LawController.iterate(schemaTree, new ObjectNode(NODE_FACTORY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Auditor getInstance() {
        if (instance == null) instance = new Auditor();
        return instance;
    }

    String getLog() {
        return auditLog.entrySet().stream()
                .map(Map.Entry::getValue)
                .map(v -> "id" + v.get("id") +
                        ", ref:" + v.get("ref") +
                        ", dateAdded:" + v.get("dateAdded") +
                        ", ipAddress:" + v.get("ipAddress") +
                        skippable(v, "random") +
                        skippable(v, "ipForward"))
                .collect(Collectors.joining("\n"));
    }

    private String skippable(JsonNode v, String key) {
        JsonNode node = v.get(key);
        return node == null || node.asText().equals("null") ? "" : ", " + key + ":" + node;
    }

    String add(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode inputTree = mapper.readTree(data);
            ObjectNode root = template.deepCopy();
            int id = ++aId;
            root.put("id", id);
            root.putPOJO("ref", inputTree.get("ref"));
            root.putPOJO("dateAdded", inputTree.get("dateAdded"));
            root.putPOJO("ipAddress", inputTree.get("ipAddress"));
            root.putPOJO("ipForward", inputTree.get("ipForward"));
            auditLog.put(id, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
