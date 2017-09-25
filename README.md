# com.rmbcorp.securitiesdemo

Program accesses Quandl API, caches data on filesystem, and performs analysis.

<br>
Compile command: mvn:package
<br>
<br>
Run command: java -jar target/securitiesdemo-0.1-SNAPSHOT.jar
<br>
<br>
Test command: mvn:test
<br>
<br>

Two analyses are supported, for tickers COF, MSFT, and GOOGL, for all days between 2017-01-01 and 2017-06-30 (inclusive):
1. Average Daily Open and Close<br>
   Average open, average close, per day, grouped by ticker and month
Run command: java -jar target/securitiesdemo-0.1-SNAPSHOT.jar  --averageMonthly
2. Biggest loser<br>
   Ticker which was down the days over the 6 month period
Run command: java -jar target/securitiesdemo-0.1-SNAPSHOT.jar --biggestLoser