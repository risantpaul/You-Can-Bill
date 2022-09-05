import java.util.ArrayList;
import java.awt.image.BufferedImage;

public interface Customer {
    public static ArrayList<Item> receiptItems = new ArrayList<Item>();
    public String getName();
    public void addStitchedImage(BufferedImage img);
    public BufferedImage getStitched();
    public boolean isCredit();
}

class CreditUser implements Customer {
    private final String name;
    private BufferedImage stitched;

    public CreditUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addStitchedImage(BufferedImage img) {
        this.stitched = img;
    }

    public BufferedImage getStitched() {
        return this.stitched;
    }

    public boolean isCredit() {return true;}
}

class CashPayer implements Customer {
    private final String name;
    private BufferedImage stitched;

    public CashPayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addStitchedImage(BufferedImage img) {
        this.stitched = img;
    }

    public BufferedImage getStitched() {
        return this.stitched;
    }

    public boolean isCredit() {return false;}
}