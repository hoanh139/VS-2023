package borse;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockGenerator {
    private static final int abbreviation_length = 4;
    private static final String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String delimiter = ",";

    public Map<Stock, Integer> generateValuePapers(int counter) {
        Map<Stock, Integer> stockList = new HashMap<>();
        Random random = new Random();

        for (int i = 0; i < counter; i++) {
            String abbreviation = generateAbbreviation(random);
            int quantity = random.nextInt(50) + 1; // generiere eine zufallige Anzahl zwischen 1 und 200
            double price = Math.round(random.nextDouble() * 100.0) / 100.0; // generiere einen zufallige Preis zwischen 0 und 100 mit 2 Nachkommastellen

            Stock stock = new Stock(abbreviation, price);
            stockList.put(stock, quantity);
        }

        return stockList;
    }

    private String generateAbbreviation(Random random) {
        char[] AbbrevitationArray = new char[abbreviation_length];

        for (int i = 0; i < abbreviation_length; i++) {
            AbbrevitationArray[i] = charset.charAt(random.nextInt(charset.length()));
        }
        return new String(AbbrevitationArray);
    }

    public void generateCSV(Map<Stock, Integer> stockList, String fileName) {

        try (PrintWriter pw = new PrintWriter(new File(fileName))) {
            StringBuilder sb = new StringBuilder();
            String arraySize = stockList.size() + delimiter;
            sb.append(arraySize + "Abbreviation" + delimiter + "Quantity" + delimiter + "Price\n");

            for (Map.Entry<Stock, Integer> iter : stockList.entrySet()) {
                sb.append(iter.getKey().getAbbreviation() + delimiter + iter.getValue() + delimiter + iter.getKey().getPrice() + "\n");
            }

            pw.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("File successfully written");
    }

    public Map<Stock, Integer> readCSV(String fileName) {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            //String[] split = line.split(delimiter);
            //int size = Integer.parseInt(split[0]);
            Map<Stock, Integer> stockList = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                String[] split = line.split(delimiter);
                String token = split[0];
                int quantity = Integer.parseInt(split[1]);
                double price = Double.parseDouble(split[2]);

                Stock stock = new Stock(token, price);
                stockList.put(stock, quantity);
            }
            return stockList;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
