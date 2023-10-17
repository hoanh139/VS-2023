package borse;

import java.util.Map;
import java.util.HashMap;

public class Message {
  private String abbreviation;
  private int quantity ;
  private double price;


  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }
  public Message(){
    StockGenerator stockGenerator = new StockGenerator();
    Map<Stock, Integer> stockList = stockGenerator.generateValuePapers(1);
    for(Stock key: stockList.keySet())
    {
      this.price = key.getPrice();
      this.abbreviation = key.getAbbreviation();
      this.quantity = stockList.get(key);
    }
  }
  public Message(String abbreviation, int quantity, double price){
    this.abbreviation = abbreviation;
    this.price = price;
    this.quantity = quantity;
  }
  /*
  public int generateData(int min, int max){
    return random.nextInt(max - min + 1) + min;

  }*/

    public String toString(){
    return quantity+"," +abbreviation+ ","+ price;
  }


}
