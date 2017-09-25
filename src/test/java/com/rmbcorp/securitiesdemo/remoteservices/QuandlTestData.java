package com.rmbcorp.securitiesdemo.remoteservices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class QuandleTestData {

    private static final String TEST_DATE = "2017-06";
    private static final String FIRST_DAY = "2017-06-21";
    private static final String SECOND_DAY = "2017-06-22";
    private static final String THIRD_DAY = "2017-06-23";

    static Map<String, Map<String, List<SecuritiesInfo>>> testData() {
        Map<String, Map<String, List<SecuritiesInfo>>> result = new HashMap<>();
        result.put("COF", QuandleTestData.getCOF());
        result.put("MSFT", QuandleTestData.getMSFT());
        result.put("GOOGL", QuandleTestData.getGOOGL());
        return result;
    }

    private static Map<String, List<SecuritiesInfo>> getMSFT() {
        Map<String, List<SecuritiesInfo>> msftGroups = new HashMap<>();
        String ticker = "MSFT";
        msftGroups.put(TEST_DATE, Arrays.asList(
                getStockData(ticker, 70.21, 70.27, FIRST_DAY),//up
                getStockData(ticker, 70.54, 70.26, SECOND_DAY),//dn
                getStockData(ticker, 70.09, 71.21, THIRD_DAY)));//up
        return msftGroups;
    }

    private static Map<String, List<SecuritiesInfo>> getCOF() {
        Map<String, List<SecuritiesInfo>> msftGroups = new HashMap<>();
        String ticker = "COF";
        msftGroups.put(TEST_DATE, Arrays.asList(
                getStockData(ticker, 81.3,80.88, FIRST_DAY),
                getStockData(ticker, 80.73,80.39, SECOND_DAY),
                getStockData(ticker, 80.75,79.52, THIRD_DAY)));
        return msftGroups;
    }

    private static Map<String, List<SecuritiesInfo>> getGOOGL() {
        Map<String, List<SecuritiesInfo>> msftGroups = new HashMap<>();
        String ticker = "GOOGL";
        msftGroups.put(TEST_DATE, Arrays.asList(
                getStockData(ticker, 970.79,978.59, FIRST_DAY),
                getStockData(ticker, 976.87, 976.62, SECOND_DAY),
                getStockData(ticker, 975.5, 986.09, THIRD_DAY)));
        return msftGroups;
    }

    private static SecuritiesInfo getStockData(String ticker, double open, double close, String testDate) {
        SecuritiesInfo securitiesInfo = new SecuritiesInfo();
        securitiesInfo.setTicker(ticker);
        securitiesInfo.setDate(LocalDate.parse(testDate));
        securitiesInfo.setOpen(BigDecimal.valueOf(open));
        securitiesInfo.setClose(BigDecimal.valueOf(close));
        return securitiesInfo;
    }
}
