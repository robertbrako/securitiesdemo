package com.rmbcorp.securitiesdemo.remoteservices;

import java.util.HashMap;
import java.util.Map;

class QuandlParams {
    private String startDate;
    private String endDate;
    private String ticker;
    private String columns;
    private String quandlkey;

    Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        result.put("startdate", startDate);
        result.put("enddate", endDate);
        result.put("ticker", ticker);
        result.put("columns", columns);
        result.put("quandlkey", quandlkey);
        return result;
    }

    void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    void setTicker(String ticker) {
        this.ticker = ticker;
    }

    void setColumns(String columns) {
        this.columns = columns;
    }

    void setQuandlkey(String quandlkey) {
        this.quandlkey = quandlkey;
    }
}
