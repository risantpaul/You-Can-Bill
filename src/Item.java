import java.awt.image.BufferedImage;

public class Item {
    private BufferedImage image;

    public Item(BufferedImage image){
        this.image = image;
    }

    public BufferedImage getImage(){
        return this.image;
    }
}