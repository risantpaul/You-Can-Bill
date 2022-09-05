import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.awt.CardLayout;
import java.math.BigInteger;

//Creating a linked list to store Customers
class Node {
    Node prev;
    Customer customer;
    public Node(Customer customer) {
        this.customer = customer;
    }
}

class CustomerHolder{
    Node end;
    public CustomerHolder(){
        end = null;
    }
 
    //Method called append to add customers to linked list
    public  void append(Customer toAppend){
        Node toAdd = new Node(toAppend);
        toAdd.prev = end;
        end = toAdd;
    }
 
    //Method called getCustomer to get customer based off an index
    public  Customer getCustomer(int index) {
            Node n = end;
            int indices = length() - 1;
            if(index == indices) {
                return n.customer;
            } else {
                while(indices > index) {
                    n = n.prev;
                    indices--;
                }
            }
            return n.customer;
    }
 
    //Method called length to get the length of the linked list
    public  int length() {
        Node n = end;
        int toReturn = 0;
        while(n != null) {
            n = n.prev;
            toReturn++;
        }
    
        return toReturn;
    }

    public Customer isCredit() {
        Node n = end;
        boolean done = false;
        while(n != null && !done) {
            if(n.customer.isCredit()) {
                    done = true;
                    return n.customer;
            }
            n = n.prev;
        }
        return n.customer;
    }
}
 
//Main class that contains first JFrame seen by user and calls YouCanBill class to continue using the application
public class Main extends JPanel{
    public static void main(String[] args) {
        new YouCanBill();
        File creditCardNumsFile = new File("CreditCardNumber.txt");
        
        // To get rid of pre-existing file
        // (If file exists, the system will add again and mess it up)
        if (creditCardNumsFile.exists()){
            creditCardNumsFile.delete();
        }
    }
}
 
class YouCanBill {
    //Creating an instance of RSA to use to encrypt credit card information
    RSA rsa = new RSA();
    //integer to track where the user (0 means at login panel, 1 means at main menu)
    int tracker = 0;

    //Customer information/storage
    static CustomerHolder customers;
    String ccnameinfo;

    //Frame, Layout and buttons
    static JFrame frame;
    static CardLayout layout;
    static JPanel deck;
    JButton mMButton;
    JButton help;
    JButton credituser;
    JButton cashpayer;
    static JButton addccinfo;

    public YouCanBill() {
        customers = new CustomerHolder();
        //Creating the frame for application
        frame = new JFrame("YouCanBill™");
        JMenuBar mbFrame = new JMenuBar();//MenuBar frame
        frame.pack();
        frame.setSize(400, 400);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Adding a component listener to the frame so that when it is moved, it will return to its original location
        frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                frame.setLocation(0,0);
            }
        });

        //Creating a CardLayout layout for the purpose of going inbetween panels/options as the user desires
        layout = new CardLayout();
        deck = new JPanel();
        deck.setLayout(layout);

        
        /////Main Menu
        JPanel mainMenu = new JPanel();
        JPanel paymentOptions = new JPanel();//Payment Options Quesiton
        JLabel paymOpt = new JLabel("How would you like pay for your bill?");//Payment Option JLabel
        paymentOptions.add(paymOpt);

        //Button called contin to take user directly to inputting their image and then billing
        JButton contin = new JButton("Input a Receipt");
        contin.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                layout.show(deck, "Input Image");
                help.setVisible(false);
                mMButton.setVisible(true);
            }
        });

        //Button called addCust to take user to addPeople panel where they are able to add whoever else they want
        JButton addCust = new JButton("Add People");
        addCust.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                layout.show(deck, "Add People");
                mMButton.setVisible(true);
                help.setVisible(false); 
            }
        });

        //Adding buttons to panel
        mainMenu.add(contin);
        mainMenu.add(addCust);
        mainMenu.add(BorderLayout.NORTH, paymentOptions);
        /////Main Menu


        /////Input Image
        JPanel inputImage = new JPanel();
        //Button to open directory and pick an image
        JButton openFiles= new JButton("Open");
        openFiles.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.setVisible(false);
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & JPEG Images", "jpg", "jpeg");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    //If user presses "Open" use that file as a parameter for ImagePopup.drawNew
                    File input = new File(chooser.getSelectedFile().getPath());
                    new ImagePopup().drawNew(input);
                } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                    //If user presses cancel, take user back to the program instead of closing
                    frame.setVisible(true);
                    layout.show(deck, "Input Image");
                }
            }
        });

        //Adding button to panel
        inputImage.add(openFiles);
        /////Input Image

 
        /////Add People
        JPanel addPeople = new JPanel();
        addPeople.setLayout(null);

        //Creating textfield and directions for users input
        JLabel task = new JLabel("Enter Name");
        task.setBounds(100, 58, 100, 20);

        JTextField textfield = new JTextField();
        textfield.setBounds(100, 77, 193, 20);
        addPeople.add(BorderLayout.SOUTH, task);
        addPeople.add(BorderLayout.SOUTH, textfield);

        //Warning in case no name is inputted
        JLabel nameWarning = new JLabel("Please enter a name");
        nameWarning.setBounds(130, 160, 300, 25);
        nameWarning.setVisible(false);

        JLabel pickone = new JLabel("Please pick payment method");
        pickone.setBounds(110, 160, 300, 25);
        pickone.setVisible(false);

        //Button to make an instance of Customer that will use their credit card
        credituser = new JButton("Credit Card");
        credituser.setBounds(100, 110, 100, 25);
        credituser.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent d) {
                //If textfield is empty, show directions, if not empty reverse the possible warnings and continue
                if(textfield.getText().isEmpty()) {
                    if(pickone.isVisible()) {
                        nameWarning.setBounds(130, 180, 300, 25);
                    }
                    nameWarning.setVisible(true);
                    task.setForeground(Color.RED);
                } else {
                    Customer creditp = new CreditUser(textfield.getText());
                    customers.append(creditp);
                    textfield.setText("");
                    pickone.setVisible(false);
                    nameWarning.setVisible(false);
                    credituser.setForeground(Color.BLACK);
                    cashpayer.setForeground(Color.BLACK);
                    task.setForeground(Color.BLACK);
                }
            }
        });

        //Button to make an instance of Customer that will pay in cash
        cashpayer = new JButton("Cash");
        cashpayer.setBounds(200, 110, 90, 25);
        cashpayer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent d) {
                //If textfield is empty, show directions, if not empty reverse the possible warnings and continue
                if(textfield.getText().isEmpty()) {
                    if(pickone.isVisible()) {
                        nameWarning.setBounds(130, 180, 300, 25);
                    }
                    nameWarning.setVisible(true);
                    task.setForeground(Color.RED);
                } else {
                    Customer cashp = new CashPayer(textfield.getText());
                    customers.append(cashp);
                    textfield.setText("");
                    pickone.setVisible(false);
                    nameWarning.setVisible(false);
                    credituser.setForeground(Color.BLACK);
                    cashpayer.setForeground(Color.BLACK);
                    task.setForeground(Color.BLACK);
                }
            }
        });
        
        //Button to continue to inputImage after user is done adding people
        JButton done = new JButton("Done");
        done.setBounds(145, 140, 90, 25);
        done.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent d) {
                /*If the linked list has a length of 2 (i.e., no one was added apart from original user and dummy customer) give warning since 
                user is in a frame that is meant to add more people. */
                if(customers.length() == 2) {
                    if(nameWarning.isVisible()) {
                        pickone.setBounds(110, 180, 300, 25);
                    }
                    credituser.setForeground(Color.RED);
                    cashpayer.setForeground(Color.RED);
                    pickone.setVisible(true);
                } else {
                    layout.show(deck, "Input Image");
                }
            }
        });

        
        //Adding buttons to frame
        addPeople.add(pickone);
        addPeople.add(nameWarning);
        addPeople.add(done);
        addPeople.add(credituser);
        addPeople.add(cashpayer);
        frame.setVisible(true);
        /////Add People

       
        /////Login Window
        //Creating "Login Window" for when users first open application, will add their name to the data structure immediately
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);

        JLabel enterName = new JLabel("Enter Name");
        enterName.setBounds(100, 58, 100, 20);

        //Text field for the users name
        JTextField nameTF = new JTextField();//name text field
        nameTF.setBounds(100, 77, 193, 28);

        //JLabel for warning in case textfield is empty
        JLabel isEmpty = new JLabel("Please enter your name before continuing!");
        isEmpty.setBounds(70, 100, 300, 100);
        isEmpty.setVisible(false);
                
        //Button for the puprose of entering the users name into the data structure and sending them to the application
        JButton credit = new JButton("Credit Card");
        credit.setBounds(100, 110, 100, 25);
        credit.setForeground(Color.BLACK);
        credit.setBackground(Color.WHITE);
        credit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!nameTF.getText().isEmpty()) {
                    //tracks when user is done "logging in"
                    tracker = 1;
                    //"Dummy" instance of Customer that will hold stitched image until it is distributed to a customer
                    Customer dummy = new CreditUser("Dummy");
                    //Instance of Customer of the user who "logged in"
                    Customer initial = new CreditUser(nameTF.getText());
                    customers.append(dummy);
                    customers.append(initial);
                    JLabel welcome = new JLabel("Welcome " + customers.getCustomer(1).getName() + "!");
                    mbFrame.add(welcome);
                    layout.show(deck, "Main Menu");
                } else {
                    enterName.setForeground(Color.RED);
                    isEmpty.setVisible(true);
                }
            }
        });

        //Button for the puprose of entering the users name into the data structure and sending them to the application
        JButton cash = new JButton("Cash");
        cash.setBounds(200, 110, 90, 25);
        cash.setForeground(Color.BLACK);
        cash.setBackground(Color.WHITE);
        cash.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!nameTF.getText().isEmpty()) {
                    //Tracks when user is done "logging in"
                    tracker = 1;
                    //"Dummy" instance of Customer that will hold stitched image until it is distributed to a customer
                    Customer dummy = new CreditUser("Dummy");
                    //Instance of Customer of the user who "logged in"
                    Customer initial = new CashPayer(nameTF.getText());
                    customers.append(dummy);
                    customers.append(initial);
                    JLabel welcome = new JLabel("Welcome " + customers.getCustomer(1).getName() + "!");
                    mbFrame.add(welcome);
                    layout.show(deck, "Main Menu");
                } else {
                    enterName.setForeground(Color.RED);
                    isEmpty.setVisible(true);
                }
            }
        });

        //Adding components to the panel
        loginPanel.add(enterName);
        loginPanel.add(nameTF);
        loginPanel.add(isEmpty);
        loginPanel.add(credit);
        loginPanel.add(cash);
        /////Login Window
        

        /////Credit Card Billing
        JPanel ccbilling = new JPanel();
        ccbilling.setLayout(null);

        JLabel ccinfo = new JLabel("Enter Credit Card Information");
        ccinfo.setBounds(100, 0, 300, 30);
        
        JLabel infowarning = new JLabel("Please Input Missing Information");
        infowarning.setBounds(75, 240, 300, 25);
        infowarning.setVisible(false); 

        JLabel numberWarning = new JLabel("Please enter a number between 0-9");
        numberWarning.setBounds(75, 240, 300, 25);
        numberWarning.setVisible(false); 

        //Credit Card name information
        JLabel ccname = new JLabel("Enter Name on Credit Card");
        ccname.setBounds(20, 50, 300, 30);

        JTextField ccnameTF = new JTextField();//name text field
        ccnameTF.setBounds(20, 80, 175, 30);

        JButton ccnameButton = new JButton("Enter");
        ccnameButton.setBounds(200, 83, 90, 25);
        ccnameButton.setForeground(Color.BLACK);
        ccnameButton.setBackground(Color.WHITE);
        ccnameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //If textfield is empty give warning and ask for information
                if(ccnameTF.getText().isEmpty()) {
                    ccname.setForeground(Color.RED);
                    infowarning.setVisible(true);
                    if(numberWarning.getY() == 260) {
                        infowarning.setBounds(75, 240, 300, 25);
                    } else {
                        infowarning.setBounds(75, 260, 300, 25);
                    }
                } else {
                    String newText = "";
                    for(int i = ccnameTF.getText().length(); i > 0; i--) {
                        newText = newText + "*";
                    }
                    infowarning.setBounds(75, 240, 300, 25);
                    numberWarning.setBounds(75, 240, 300, 25);
                    ccnameTF.setText(newText);
                    ccname.setForeground(Color.BLACK);
                    infowarning.setVisible(false);
                }
            }
        });

        //Credit Card Number components
        JLabel ccnumber = new JLabel("Enter Credit Card Number");
        ccnumber.setBounds(20, 140, 300, 30);

        JTextField ccnumberTF = new JTextField();//name text field
        ccnumberTF.setBounds(20, 170, 175, 30);

        JButton ccnumberButton = new JButton("Enter");
        ccnumberButton.setBounds(200, 173, 90, 25);
        ccnumberButton.setForeground(Color.BLACK);
        ccnumberButton.setBackground(Color.WHITE);
        ccnumberButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //If textfield is empty gives warning otherwise continue
                if(ccnumberTF.getText().isEmpty()) {
                    ccnumber.setForeground(Color.RED);
                    infowarning.setVisible(true);
                } else if(!ccnumberTF.getText().matches("[0-9]+")) { 
                    if(infowarning.isVisible()) {
                        if(infowarning.getY() == 260) {
                            numberWarning.setBounds(75, 240, 300, 25);
                        } else {
                            numberWarning.setBounds(75, 260, 300, 25);
                        }
                    }
                    ccnumber.setForeground(Color.RED);
                    numberWarning.setVisible(true);
                } else {
                    BigInteger ccnumberBI = new BigInteger(ccnumberTF.getText());
                    rsa.encryptAndSave(ccnumberBI, ccnameinfo);
                    String newText = "";
                    for(int i = ccnumberTF.getText().length(); i > 0; i--) {
                        newText = newText + "*";
                    }
                    infowarning.setBounds(75, 240, 300, 25);
                    numberWarning.setBounds(75, 240, 300, 25);
                    ccnumberTF.setText(newText);
                    numberWarning.setVisible(false);
                    ccnumber.setForeground(Color.BLACK);
                    infowarning.setVisible(false);
                }
            }
        });

        //Add and done buttons for cc information
        addccinfo = new JButton("Add Billing Info");
        addccinfo.setBounds(70, 210, 130, 25);
        addccinfo.setForeground(Color.BLACK);
        addccinfo.setBackground(Color.WHITE);
        addccinfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //If textfield is empty give warning otherwise continue
                if(!ccnameTF.getText().isBlank() && !ccnumberTF.getText().isBlank()) {
                    ccnameTF.setText("");
                    ccnumberTF.setText("");
                    ccname.setForeground(Color.BLACK);
                    ccnumber.setForeground(Color.BLACK);
                    infowarning.setVisible(false);
                } else {
                    infowarning.setVisible(true);
                    if(ccnameTF.getText().isBlank()) {
                        ccname.setForeground(Color.RED);
                    }
                    if(ccnumberTF.getText().isBlank()) {
                        ccnumber.setForeground(Color.RED);
                    }
                }
            }
        });

        JButton ccdone = new JButton("Finished");
        ccdone.setBounds(200, 210, 90, 25);
        ccdone.setForeground(Color.BLACK);
        ccdone.setBackground(Color.WHITE);
        ccdone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!ccnameTF.getText().isBlank() && !ccnumberTF.getText().isBlank()) {
                    mMButton.setVisible(true);
                    layout.show(deck, "Finished");
                    help.setVisible(false);
                } else {
                    infowarning.setVisible(true);
                    if(ccnameTF.getText().isBlank()) {
                        ccname.setForeground(Color.RED);
                    }
                    if(ccnumberTF.getText().isBlank()) {
                        ccnumber.setForeground(Color.RED);
                    }
                }
            }
        });


        ccbilling.add(numberWarning);
        ccbilling.add(ccdone);
        ccbilling.add(infowarning);
        ccbilling.add(addccinfo);
        ccbilling.add(ccnumberButton);
        ccbilling.add(ccnameButton);
        ccbilling.add(ccnumberTF);
        ccbilling.add(ccnameTF);
        ccbilling.add(ccnumber);
        ccbilling.add(ccname);
        ccbilling.add(ccinfo);
        /////Credit Card Billing


        /////Help Panel
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(null);
        JLabel label1 = new JLabel();
        label1.setText("<html><h2>Welcome to YouCanBill™!</h2></html>");
        label1.setBounds(5, 3, 400, 20);
        
        JLabel label2 = new JLabel();
        label2.setText("Press Main Menu to return to the main menu and continue");
        label2.setBounds(5, 25, 400, 15);

        JLabel label3 = new JLabel();
        label3.setText("<html><h3>Instructions to Use YouCanBill™</h2></html>");
        label3.setBounds(5, 40, 400, 15);
        label3.setForeground(Color.RED);

        JLabel label4 = new JLabel();
        label4.setText("1. Start off by entering your name and then choosing");
        label4.setBounds(10, 60, 400, 15);

        JLabel label5 = new JLabel();
        label5.setText("your preferred payment type.");
        label5.setBounds(25, 80, 400, 15);

        JLabel label6 = new JLabel();
        label6.setText("2. Once done, choose whether you are paying for the bill,");
        label6.setBounds(10, 100, 400, 15);

        JLabel label7 = new JLabel();
        label7.setText("or would like to add people.");
        label7.setBounds(25, 120, 400, 15);

        JLabel label8 = new JLabel();
        label8.setText("3. Given your previous choice, either input your receipt");
        label8.setBounds(10, 140, 400, 15);

        JLabel label9 = new JLabel();
        label9.setText("(only JPG or JPEG images are allowed), or first add whoever");
        label9.setBounds(25, 160, 400, 15);

        JLabel label10 = new JLabel();
        label10.setText("you would like to join you in paying for the bill.");
        label10.setBounds(25, 180, 400, 15);

        JLabel label11 = new JLabel();
        label11.setText("4. After that, continue on to billing. If there is a credit card");
        label11.setBounds(10, 200, 400, 15);

        JLabel label12 = new JLabel();
        label12.setText("user amongst your party you will be prompted to enter");
        label12.setBounds(25, 220, 400, 15);

        JLabel label13 = new JLabel();
        label13.setText("their credit card information. Please do not include");
        label13.setBounds(25, 240, 400, 15);

        JLabel label14 = new JLabel();
        label14.setText("spaces in the credit card number.");
        label14.setBounds(25, 260, 400, 15);

        JLabel label15 = new JLabel();
        label15.setText("5. When done, press enter and then press done.");
        label15.setBounds(10, 280, 400, 15);

        JLabel label16 = new JLabel();
        label16.setText("6. You are finally done! And have access to your");
        label16.setBounds(10, 300, 400, 15);

        JLabel label17 = new JLabel();
        label17.setText("individual receipts.");
        label17.setBounds(25, 320, 400, 15);
        helpPanel.add(label1);
        helpPanel.add(label2);
        helpPanel.add(label3);
        helpPanel.add(label4);
        helpPanel.add(label5);
        helpPanel.add(label6);
        helpPanel.add(label7);
        helpPanel.add(label8);
        helpPanel.add(label9);
        helpPanel.add(label10);
        helpPanel.add(label11);
        helpPanel.add(label12);
        helpPanel.add(label13);
        helpPanel.add(label14);
        helpPanel.add(label15);
        helpPanel.add(label16);
        helpPanel.add(label17);
        /////Help Panel


        /////Finished Panel
        //Creating panel for when user is done with the program
        JPanel finished = new JPanel();
        finished.setLayout(null);
        JLabel flabel1 = new JLabel();
        flabel1.setText("<html><h1>You Are Done!</h1></html>");
        flabel1.setBounds(100, 40, 400, 20);
        
        JLabel flabel2 = new JLabel();
        flabel2.setText("Your images are readily accessible to you");
        flabel2.setBounds(50, 70, 400, 15);

        JLabel flabel3 = new JLabel();
        flabel3.setText("within your local directory. As well as");
        flabel3.setBounds(60, 90, 400, 15);

        JLabel flabel4 = new JLabel();
        flabel4.setText("clearly visible on your desktop!");
        flabel4.setBounds(80, 110, 400, 15);

        JLabel flabel5 = new JLabel();
        flabel5.setText("Thank you!");
        flabel5.setBounds(140, 130, 400, 15);

        finished.add(flabel1);
        finished.add(flabel2);
        finished.add(flabel3);
        finished.add(flabel4);
        finished.add(flabel5);
        /////Finished Panel


        //Creating and Adding help and main menu button for the frame menubar
        //Button to go back to the Main Menu
        mMButton = new JButton("Main Menu");
        mMButton.setVisible(false);
        mMButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(tracker == 0) {
                    layout.show(deck, "Login Panel");
                    help.setVisible(true);
                    mMButton.setVisible(false);
                } else {
                    layout.show(deck, "Main Menu");
                    help.setVisible(true);
                    mMButton.setVisible(false);
                }
                
            }
        });

        //Button to take user to help panel in case they need instructions
        help = new JButton("Help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                help.setVisible(false);
                layout.show(deck, "Help");
                mMButton.setVisible(true);
            }
        });
        mbFrame.add(help);
        mbFrame.add(mMButton);

        //Adding panels to the "deck" in order/semantically
        deck.add(loginPanel, "Login Panel");
        deck.add(mainMenu, "Main Menu");
        deck.add(addPeople, "Add People");
        deck.add(inputImage, "Input Image");
        deck.add(ccbilling, "CC Billing");
        deck.add(finished, "Finished");
        deck.add(helpPanel, "Help");

        //More frame configuration
        frame.add(BorderLayout.NORTH, mbFrame);
        frame.getContentPane().add(deck);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
   }
}
