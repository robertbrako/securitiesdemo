package com.rmbcorp.securitiesdemo.remoteservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmbcorp.securitiesdemo.Persistence;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Quandl {

    private static final Logger logger = Logger.getLogger(Quandl.class.getName());
    private static final String SECURITIES_DATA_FILE = "securitiesData.txt";
    private static final String STARTDATE = "20170101";
    private static final String ENDDATE = "20170701";
    private static final String TICKER = "COF,MSFT,GOOGL";
    private static final String COLUMNS = "ticker,date,open,close,volume";
    private static final String QUANDL_URL = "https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date.gte={startdate}&date.lt={enddate}&ticker={ticker}&qopts.columns={columns}&api_key={quandlkey}";

    private String quandlKey;
    private Persistence persistence;
    private RestTemplate restTemplate = new RestTemplate();

    public Quandl(Map<String, String> environment, Persistence persistence) {
        quandlKey = environment.getOrDefault("quandlkey", "s-GMZ_xkw6CrkGYUWs1p");
        this.persistence = persistence;
    }

    public String loadData(QuandlParams params) {
        String data = persistence.load(SECURITIES_DATA_FILE).stream().collect(Collectors.joining("\n"));
        if (data.isEmpty()) {
            params.setQuandlkey(quandlKey);
            String securitiesData = restCall(params.asMap());
            persistence.save(SECURITIES_DATA_FILE, securitiesData);
            return securitiesData;
        }
        return data;
    }

    private String restCall(Map<String, String> vars) {
        try {
            return restTemplate.getForObject(QUANDL_URL, String.class, vars);
        } catch (RestClientException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static QuandlParams getDemoParams() {
        QuandlParams quandlParams = new QuandlParams();
        quandlParams.setStartDate(STARTDATE);
        quandlParams.setEndDate(ENDDATE);
        quandlParams.setTicker(TICKER);
        quandlParams.setColumns(COLUMNS);
        return quandlParams;
    }

    public Map<String, List<SecuritiesInfo>> mapToObjects(String jsonData) {
        Map<String, List<SecuritiesInfo>> fullMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode readTree = mapper.readTree(jsonData);
            JsonNode data = readTree.get("datatable").get("data");

            String currentName;
            Iterator<JsonNode> iterator = data.iterator();
            for (JsonNode currentNode = iterator.next(); iterator.hasNext(); currentNode = iterator.next()) {
                currentName = currentNode.get(0).asText();
                fullMap.computeIfAbsent(currentName, key -> new ArrayList<>());
                SecuritiesInfo securitiesInfo = new SecuritiesInfo();
                securitiesInfo.setTicker(currentName);
                securitiesInfo.setDate(LocalDate.parse(currentNode.get(1).asText()));
                securitiesInfo.setOpen(new BigDecimal(currentNode.get(2).asText()));
                securitiesInfo.setClose(new BigDecimal(currentNode.get(3).asText()));
                securitiesInfo.setVolume(new BigDecimal(currentNode.get(4).asText()));
                fullMap.get(currentName).add(securitiesInfo);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return fullMap;
    }

}