package com.rmbcorp.securitiesdemo.remoteservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.rmbcorp.securitiesdemo.businesslogic.Securities;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class SecuritiesTest {

    private Securities securities = new Securities();

    @Test
    public void biggestLoserTest() {
        ObjectNode objectNode = securities.biggestLoser(QuandleTestData.testData());
        assertEquals(objectNode.get("ticker").asText(), "COF");
        assertEquals(objectNode.get("daysDown").asInt(), 3);
        assertEquals(objectNode.get("daysAnalyzed").asInt(), 3);
    }

    @Test
    public void processAveragesTest() {
        List<ObjectNode> objectNode = securities.processAverages(QuandleTestData.testData());
        assertEquals(objectNode.size(), 3);

        POJONode COF = (POJONode) objectNode.stream()
                .filter(item -> "COF".endsWith(item.fieldNames().next()))
                .map(entry -> entry.get("COF"))
                .findFirst().get();
        assertNotNull(COF);

        List<JsonNode> pojo = (List<JsonNode>) COF.getPojo();
        JsonNode testDataItem = pojo.get(0);
        double open = testDataItem.get("average_open").asDouble();
        double close = testDataItem.get("average_close").asDouble();
        assertEquals("2017-06", testDataItem.get("month").asText());
        assertTrue(open > 80.73);
        assertTrue(open < 81.3);
        assertTrue(close > 79.52);
        assertTrue(close < 80.88);
    }

}