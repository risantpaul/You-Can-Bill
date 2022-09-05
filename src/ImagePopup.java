import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.lang.Math;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ImagePopup extends JFrame implements MouseListener, MouseMotionListener {
    private boolean cropping = false;
    private static BufferedImage image;
    private static int imageWidth, imageHeight;
    private static int scaledImageWidth, scaledImageHeight;
    private static File inputFile;
    private Point imagePosition;
    private int xImage, yImage;
    private int x1, y1, x2, y2;
    private JFrame frame = new JFrame("Selected receipt");
    private JLabel picLabel = new JLabel();
    private JLayeredPane base = new JLayeredPane();
    private JPanel rectangle = new JPanel();
    public JMenu pickPers;

    public void drawNew(File input) {
        //Creates panel that will contain receipt
        JPanel panel = new JPanel();
        panel.setSize(500,500);
        panel.setVisible(true);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Sets image if it hasn't been yet
        try{
            image = ImageIO.read(input);
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            inputFile = input;
        }
        catch(IOException error){}

        //Changes image size until it fits into JPanel
        scaledImageWidth = image.getWidth();
        scaledImageHeight = image.getHeight();

        if (scaledImageWidth > scaledImageHeight){
            while (scaledImageWidth >= 500){
                scaledImageWidth *= 0.7;
                scaledImageHeight *= 0.7;
            }
        }
        if (scaledImageHeight > scaledImageWidth){
            while (scaledImageHeight >= 500){
                scaledImageWidth *= 0.7;
                scaledImageHeight *= 0.7;
            }
        }

        //Resizes image to fit entire upload into JPanel
        Image resized = image.getScaledInstance(scaledImageWidth, scaledImageHeight, Image.SCALE_SMOOTH);

        //Puts resized image in the frame
        picLabel.setIcon(new ImageIcon(resized));
        panel.add(picLabel);
        Rectangle imageBounds = picLabel.getBounds();
        imagePosition = imageBounds.getLocation();
        frame.setVisible(true); // May be redundant

        base.add(panel);
        frame.add(base);

        //In case the image is too small and giving the JPanel some leeway
        int panelwidth = scaledImageWidth + 100;
        if(panelwidth < 500) {
            panelwidth = 500;
        }

        int panelheight = scaledImageHeight + 100;
        if(panelheight < 500) {
            panelheight = 500;
        }

        frame.setSize(panelwidth, panelheight);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
               frame.setSize(frame.getWidth(), frame.getHeight());  // or whatever your full size is
            }
            public void componentMoved(ComponentEvent e) {
               frame.setLocation(0,0);
            }
         });
        frame.setResizable(false);
        frame.setVisible(true);
        
        JMenuBar recieptMenu = new JMenuBar();
        //Button to go "back" to YouCanBill frame
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.setVisible(false);
                YouCanBill.frame.setVisible(true);
                YouCanBill.layout.show(YouCanBill.deck, "Input Image");
            }
        });
        JButton done = new JButton("Continue To Billing");
        done.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.setVisible(false);
                YouCanBill.frame.setVisible(true);
                Customer credit = YouCanBill.customers.isCredit();
                //If no one (except for the dummy customer) is using credit, show the "Finished" panel
                if(credit != null && credit.getName() != "Dummy") {
                    if(YouCanBill.customers.length() == 2) {
                        YouCanBill.addccinfo.setVisible(false);
                    }
                    YouCanBill.layout.show(YouCanBill.deck, "CC Billing");
                }  else {
                    YouCanBill.layout.show(YouCanBill.deck, "Finished");
                }
            }
        });

        //Menu to choose who the cropped images are for
        pickPers = new JMenu("Select Person After Crop");
        for(int i = 1; i < YouCanBill.customers.length(); i++) {
            //Creating JMenuItems for each person named by user
            String name = YouCanBill.customers.getCustomer(i).getName(); // Start at 1 to skip Dummy
            JMenuItem names = new JMenuItem(name);
            pickPers.add(names);
            names.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ImageChange.ImageStitch(name);
                }
            });
        }

        recieptMenu.add(pickPers);
        recieptMenu.add(back);
        recieptMenu.add(done);
        frame.setJMenuBar(recieptMenu);
        frame.setVisible(true);

    }

    // Draws a rectangle around the area of the image that you cropped
    public void drawRect() {
        base.remove(rectangle);
        int width = Math.abs(x1-x2);
        int height = Math.abs(y1-y2);
        rectangle.setBorder(BorderFactory.createLineBorder(Color.black));
        if (x1-x2 >= 0 && y1-y2 >= 0) {
            rectangle.setBounds(x2, y2, width, height);
        } else if (y1-y2 >= 0) {
            rectangle.setBounds(x1, y2, width, height);
        } else if (x1-x2 >= 0) {
            rectangle.setBounds(x2, y1, width, height);
        } else {
            rectangle.setBounds(x1, y1, width, height);
        }
        rectangle.setOpaque(false);
        rectangle.setVisible(true);
        base.add(rectangle);
        base.moveToFront(rectangle);
        frame.validate();
        frame.repaint();
        frame.setVisible(true);
        }
    
    //Calls cropImage function in imageChange class
    public void cropImage() throws Exception {
        float width = Math.abs(x1-x2);
        float height = Math.abs(y1-y2);

        float widthRatio = imageWidth / (float)scaledImageWidth;
        float heightRatio = imageHeight / (float)scaledImageHeight;

        xImage = (int) picLabel.getLocation().getX();
        yImage = (int) picLabel.getLocation().getY();

        int cropWidth = (int) (width * widthRatio);
        int cropHeight = (int) (height * heightRatio);

        // Use dummy Person created to temporarily store Items
        image = ImageIO.read(inputFile);
        ImageChange.imageCrop(image, (int)(Math.min(x1 - xImage, x2 - xImage)*widthRatio), (int)(Math.min(y1 - yImage, y2 - yImage)*heightRatio), cropWidth, cropHeight, "dummy");
    }

    //Record x and y values of when mouse is first pressed
    @Override
    public void mousePressed(MouseEvent e) {
        cropping = true;
        x1 = e.getX()-imagePosition.x;
        y1 = e.getY()-imagePosition.y;
    }

    //Once mouse is released after being dragged, record x and y values
    @Override
    public void mouseReleased(MouseEvent e) {
        this.repaint();
        if(cropping == false) {
            x2 = e.getX()-imagePosition.x;
            y2 = e.getY()-imagePosition.y;
            try {
                cropImage();
            }
            catch(Exception a){
                a.printStackTrace();
            }
        }
    }

    //As mouse is being dragged, record x and y values
    @Override
    public void mouseDragged(MouseEvent e) {
        this.repaint();
        cropping = false;
        x2 = e.getX()-imagePosition.x;
        y2 = e.getY()-imagePosition.y;
        drawRect();
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}
 
    @Override
    public void mouseExited(MouseEvent e) {}
}