package com.rmbcorp.securitiesdemo;

import com.rmbcorp.securitiesdemo.remoteservices.Quandl;
import com.rmbcorp.securitiesdemo.businesslogic.Securities;
import com.rmbcorp.securitiesdemo.remoteservices.SecuritiesInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Application {

    public static void main(String[] args) {
        Persistence persistence = new Persistence();
        Securities securities = new Securities();
        Quandl quandl = new Quandl(System.getenv(), persistence);
        String result = quandl.loadData(Quandl.getDemoParams());
        Map<String, List<SecuritiesInfo>> stringListMap = quandl.mapToObjects(result);
        Map<String, Map<String, List<SecuritiesInfo>>> averagedSet = securities.getAverageOpenClose(stringListMap);
        if (args.length == 0) {
            DemoPrinter.print(securities.processAverages(averagedSet));
            DemoPrinter.print(Collections.singletonList(securities.biggestLoser(averagedSet)));
        } else {
            switch (args[0]) {
                case "--averageMonthly":
                    DemoPrinter.print(securities.processAverages(averagedSet));
                    break;
                case "--biggestLoser":
                    DemoPrinter.print(Collections.singletonList(securities.biggestLoser(averagedSet)));
                    break;
                default:
                    usage();
            }
        }
    }

    private static void usage() {
        System.out.println("Usage:\n  java Application [option]");
        System.out.println("\nOptions:\n");
        System.out.println("  --averageMonthly - show average monthly open and close for COF,MSFT,GOOGL");
        System.out.println("  --biggestLoser - show biggest loser among COF,MSFT,GOOGL");
        System.out.println("  [none] - runs both averageMonthly and biggestLoser");
        System.out.println("\nNotes: date range is set as Jan2017 through Jun2017");
    }

}
