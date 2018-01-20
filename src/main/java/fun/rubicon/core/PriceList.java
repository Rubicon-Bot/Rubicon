package fun.rubicon.core;

/**
 * @author Yannick Seeger / ForYaSee
 */
public enum PriceList {

    PREMIUM(500000);

    private int price;

    PriceList(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
