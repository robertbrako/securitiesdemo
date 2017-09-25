package com.rmbcorp.law;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@RestController
public class LawController {

    static final JsonNodeFactory NODE_FACTORY = new JsonNodeFactory(true);
    private static final String SCHEMA_FILE = "src/main/resources/schema/balanceSheet.json";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest headers) {
        audit(headers, "/");
        return "Greetings from Spring Boot!";
    }

    private void audit(HttpServletRequest headers, String ref) {
        Auditor.getInstance().add("{\"ref\":\"law/"+ref+"\"," +
                "\"dateAdded\":\""+ Instant.now()+"\"," +
                "\"ipAddress\":\""+headers.getRemoteAddr()+"\"}" +
                "\"ipForward\":\""+headers.getHeader("x-forwarded-for")+"\"}");
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
    public String index1(@RequestBody String data, HttpServletRequest headers) {
        audit(headers, "balanceSheet.json");
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        try (FileInputStream inputStream = new FileInputStream(SCHEMA_FILE)) {
            JsonNode inputTree = mapper.readTree(data);

            JsonNode schemaTree = mapper.readTree(inputStream);//cache it?

            ObjectNode root = iterate(schemaTree, new ObjectNode(NODE_FACTORY));//empty object

            inputTree.fields().forEachRemaining(entry -> {
                if (root.findValue(entry.getKey()) != null)
                    root.putPOJO(entry.getKey(), entry.getValue());
            });
            result = mapper.writer(new DefaultPrettyPrinter()).writeValueAsString(root);
        } catch (IOException e) {
            e.printStackTrace();
            result = "Greetings from Spring Boot!  Error!";
        }
        return result + "\n";
    }

    static ObjectNode iterate(JsonNode root, ObjectNode dest) {
        JsonNode properties = root.get("properties");
        JsonNode definitions = root.get("definitions");
        Iterator<Map.Entry<String, JsonNode>> entryIterator = properties.fields();
        while (entryIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = entryIterator.next();
            JsonNode type = entry.getValue().get("type");
            String key = entry.getKey();
            if (type.asText().equals("array")) {
                JsonNode itemType = getItemType(entry);
                if (definitions != null) {
                    JsonNode subNode = definitions.get(itemType.asText().replaceAll(".*/", ""));
                    ObjectNode newDest = iterate(subNode, new ObjectNode(NODE_FACTORY));
                    dest.putPOJO(key, Collections.singletonList(newDest));
                } else {
                    mapType(itemType, dest, key);
                }
            } else {
                mapType(type, dest, key);
            }
        }
        return dest;
    }

    private static JsonNode getItemType(Map.Entry<String, JsonNode> entry) {
        JsonNode items = entry.getValue().get("items");
        JsonNode node = items.get("$ref");
        return node == null ? items.get("type") : node;
    }

    private static void mapType(JsonNode value, ObjectNode dest, String key) {
        String type = value.asText();
        switch (type) {
            case "number": dest.put(key, 0); break;
            case "string": dest.put(key, ""); break;
            case "boolean": dest.put(key, false); break;
            case "date": dest.putPOJO(key, Instant.EPOCH.toString()); break;
        }
    }
}
