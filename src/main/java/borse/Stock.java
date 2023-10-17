package borse;

public class Stock {
    private String abbreviation;
    private double price;

    public Stock(String abbreviation, double price){
        this.abbreviation = abbreviation;
        this.price=price;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
