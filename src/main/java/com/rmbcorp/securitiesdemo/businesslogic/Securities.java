package com.rmbcorp.securitiesdemo.businesslogic;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rmbcorp.securitiesdemo.remoteservices.SecuritiesInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Securities {

    private static final String DATE_FORMAT = "YYYY-MM";
    private static final JsonNodeFactory JSON_NODE_FACTORY = new JsonNodeFactory(true);

    public Map<String, Map<String, List<SecuritiesInfo>>> getAverageOpenClose(Map<String, List<SecuritiesInfo>> data) {
        Map<String, Map<String, List<SecuritiesInfo>>> result = new HashMap<>();
        for (Map.Entry<String, List<SecuritiesInfo>> entry : data.entrySet()) {
            Map<String, List<SecuritiesInfo>> split = split(entry.getValue());
            result.put(entry.getKey(), split);
        }
        return result;
    }

    private Map<String, List<SecuritiesInfo>> split(List<SecuritiesInfo> inputs) {
        Map<String, List<SecuritiesInfo>> result = new LinkedHashMap<>();
        for (SecuritiesInfo securitiesInfo : inputs) {
            String newDate = securitiesInfo.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            result.computeIfAbsent(newDate, dateKey -> new ArrayList<>());
            result.get(newDate).add(securitiesInfo);
        }
        return result;
    }

    public List<ObjectNode> processAverages(Map<String, Map<String, List<SecuritiesInfo>>> data) {
        List<ObjectNode> results = new ArrayList<>(data.size());
        for (Map.Entry<String, Map<String, List<SecuritiesInfo>>> group : data.entrySet()) {
            String ticker = group.getKey();
            ObjectNode tickerNode = new ObjectNode(JSON_NODE_FACTORY);
            List<ObjectNode> nodeList = new ArrayList<>();
            tickerNode.putPOJO(ticker, nodeList);
            for (Map.Entry<String, List<SecuritiesInfo>> dateGroup : group.getValue().entrySet()) {
                int count = 0;
                BigDecimal opens = BigDecimal.ZERO;
                BigDecimal closes = BigDecimal.ZERO;
                String date = dateGroup.getKey();
                for (SecuritiesInfo securitiesInfo : dateGroup.getValue()) {
                    count++;
                    opens = opens.add(securitiesInfo.getOpen());
                    closes = closes.add(securitiesInfo.getClose());
                }
                ObjectNode node = new ObjectNode(JSON_NODE_FACTORY);
                node.put("month", date);
                node.put("average_open", getAvg(opens, count));
                node.put("average_close", getAvg(closes, count));
                nodeList.add(node);
            }
            results.add(tickerNode);
        }
        return results;
    }

    private BigDecimal getAvg(BigDecimal openSum, int divisor) {
        return openSum.divide(BigDecimal.valueOf(divisor), RoundingMode.HALF_UP);
    }

    public ObjectNode biggestLoser(Map<String, Map<String, List<SecuritiesInfo>>> data) {
        ObjectNode worst = new ObjectNode(JSON_NODE_FACTORY);
        worst.put("ticker", "");
        worst.put("daysDown", 0);
        for (Map.Entry<String, Map<String, List<SecuritiesInfo>>> group : data.entrySet()) {
            int totalDays = 0;
            int totalCount = 0;
            String ticker = group.getKey();
            ObjectNode tickerNode = new ObjectNode(JSON_NODE_FACTORY);
            List<ObjectNode> nodeList = new ArrayList<>();
            tickerNode.putPOJO(ticker, nodeList);
            for (Map.Entry<String, List<SecuritiesInfo>> dateGroup : group.getValue().entrySet()) {
                int downCount = 0;
                String date = dateGroup.getKey();
                for (SecuritiesInfo securitiesInfo : dateGroup.getValue()) {
                    if (securitiesInfo.getClose().compareTo(securitiesInfo.getOpen()) < 0) {
                        downCount++;
                    }
                    totalDays++;
                }
                ObjectNode node = new ObjectNode(JSON_NODE_FACTORY);
                node.put("month", date);
                node.put("daysDown", downCount);
                nodeList.add(node);
                totalCount += downCount;
            }
            if (worst.get("daysDown").asInt() < totalCount) {
                worst.put("ticker", ticker);
                worst.put("daysDown", totalCount);
                worst.put("daysAnalyzed", totalDays);
            }
        }
        return worst;
    }
}
