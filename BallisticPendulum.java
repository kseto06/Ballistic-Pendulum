import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JMenu;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BallisticPendulum implements ActionListener, ChangeListener {
    // Properties
    JFrame frame = new JFrame("Ballistic Pendulum Simulation");
    //JPanel panel = new JPanel();
    AnimationPanel panel = new AnimationPanel();
    AboutPanel about_panel = new AboutPanel();
    Timer timer = new Timer(1000/48, this); //The timer goes off at 48fps

    JMenuBar menuBar = new JMenuBar();
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutMenu = new JMenuItem("About");
    
    //Sliders for length because we want it to be int, and initial velocity
    JSlider lengthSlider; 
    JTextField lengthValue; //To display the slider value
    JSlider ViSlider;
    JTextField ViValue; 
    
    //JTextFields for mass because we allow user to input doubles:
    JTextField massBulletInput; 
    JTextField massBobInput; 

    //Buttons:
    JButton resetButton = new JButton("Reset"); //Button to reset the pendulum setup
    JButton launchButton = new JButton("Launch"); //Button to start the bullet launch

    //Labels:
    JLabel angleResultLabel = new JLabel();

    //Since there are two parts to the animation, we need booleans to keep track of what is going on
    boolean bulletLaunchFinished = false;
    boolean startLaunch = false;
    static boolean pendulumLaunchFinished = false;

    // Event Listeners
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == timer) {

            //Only launch if button is pressed:
            if (startLaunch == true) {
                //Animating bullet launch:
                if (panel.bulletX < (panel.pendulumBobX - panel.bobDimension/4) && !bulletLaunchFinished) {
                    panel.bulletX += (0.48*panel.bulletVi); //Bullet moves faster if vi is increased.
                    System.out.println(0.48*panel.bulletVi);

                    if ((panel.pendulumBobX - panel.bobDimension/4) < panel.bulletX) { //If current bulletX overshoots the goal, set them equal and break out of this loop
                        panel.bulletX = panel.pendulumBobX - panel.bobDimension/4;
                        bulletLaunchFinished = true;
                    } 
                }

                //Go to Part 2 Swinging animation once bullet hits the pendulum
                //System.out.println(panel.bulletX+" = "+(panel.pendulumBobX - panel.bobDimension/4));
                if (panel.bulletX == (panel.pendulumBobX - panel.bobDimension/4)) {
                    System.out.println("Bullet launch successful");
                    bulletLaunchFinished = true;
                }

                //Animating pendulum swinging with the bullet, using angle calculations
                if (panel.currentTheta < panel.goalTheta && bulletLaunchFinished && !pendulumLaunchFinished) {
                    panel.currentTheta += (0.0005*panel.bulletVi); //Angle adjusts faster if vi is faster
                    
                    if (panel.goalTheta < panel.currentTheta) { //If current theta overshoots the goal, set them equal and break out of this loop
                        panel.currentTheta = panel.goalTheta;
                        pendulumLaunchFinished = true;
                    } 
                }

                System.out.println(panel.currentTheta+ " = " + panel.goalTheta);
                if (panel.currentTheta == panel.goalTheta) {
                    launchButton.setText("Launch successful!");
                    panel.RESETTING_FACTOR = 1;
                    startLaunch = false;
                    bulletLaunchFinished = false;
                    pendulumLaunchFinished = true;
                    angleResultLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    angleResultLabel.setText("   = "+Math.round(Math.toDegrees(panel.goalTheta))+"°");
                }
            }
            panel.repaint();

        } else if (evt.getSource() == aboutMenu) {
            frame.setContentPane(about_panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setVisible(true);
        } else if (evt.getSource() == massBulletInput) {
            //Catch NumberFormatException
            try {
                panel.bulletMass = Double.parseDouble(massBulletInput.getText());
            } catch (NumberFormatException e) {
                massBulletInput.setText("Please input a double");
                e.printStackTrace();
            }
        } else if (evt.getSource() == massBobInput) {
            //Catch NumberFormatException
            try {
                panel.pendulumMass = Double.parseDouble(massBobInput.getText());
            } catch (NumberFormatException e) {
                massBobInput.setText("Please input a double");
                e.printStackTrace();
            }
        } else if (evt.getSource() == launchButton) {
            launchButton.setText("Launching...");
            startLaunch = true;
        } else if (evt.getSource() == resetButton) {
            resetSimulation();
        }
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        if (evt.getSource() == lengthSlider) {
            lengthValue.setText(lengthSlider.getValue()+"m");
            panel.pendulumMeter = lengthSlider.getValue();
        }

        if (evt.getSource() == ViSlider) {
            ViValue.setText(ViSlider.getValue()+"m/s");
            panel.bulletVi = ViSlider.getValue();
        }
    }

    // Methods
    //Function to reset the entire simulation:
    private void resetSimulation() {
        //Reset JComponents and AnimationPanel Properties:
        lengthSlider.setValue(5);
        ViSlider.setValue(0);
        massBulletInput.setText("Enter a double value for bullet mass. Default = 0.1kg");
        massBobInput.setText("Enter a double value for bob mass. Default = 1.0kg");
        angleResultLabel.setText("");
        launchButton.setText("Launch");
        panel.pendulumMeter = 5;
        panel.pendulumBobX = 600;
        panel.pendulumBobY = 375;
        panel.pendulumMass = 1.0;
        panel.bulletX = 500.0;
        panel.bulletMass = 0.1;
        panel.bulletVi = 0.0;
        panel.currentTheta = 0;
        panel.goalTheta = 0;
        panel.RESETTING_FACTOR = 0;
    }

    // Constructor
    public BallisticPendulum() {
        panel.setPreferredSize(new Dimension(960, 540));
        panel.setLayout(null);

        //Add JComponents 
        JLabel lengthSliderLabel = new JLabel("Length Slider (m)");
        lengthSliderLabel.setSize(200, 40);
        lengthSliderLabel.setLocation(10, 10);
        panel.add(lengthSliderLabel);

        lengthSlider = new JSlider(5, 45, 5); //Slider from 0-100m, with delta value of 5
        lengthSlider.setValue(5); //Set default value of 5m
        lengthSlider.setSize(250, 50);
        lengthSlider.setLocation(10, 50);
        lengthSlider.addChangeListener(this);
        lengthSlider.setMinorTickSpacing(5); //Set slider spacing 
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setLabelTable(lengthSlider.createStandardLabels(5)); //Display slider spacing increments
        panel.add(lengthSlider);

        lengthValue = new JTextField(lengthSlider.getValue()+"m"); //To display the length of the pendulum
        lengthValue.setSize(50, 50);
        lengthValue.setLocation(260, 50);
        panel.add(lengthValue);

        JLabel viSliderLabel = new JLabel("Initial Velocity Slider (m/s)");
        viSliderLabel.setSize(200, 40);
        viSliderLabel.setLocation(10, 125);
        panel.add(viSliderLabel);

        ViSlider = new JSlider(0, 50, 0); //Create a new slider for initial velocity
        ViSlider.setValue(0); //Set default value of vi at 0m/s
        ViSlider.setSize(250, 50);
        ViSlider.setLocation(10, 175);
        ViSlider.addChangeListener(this);
        ViSlider.setMinorTickSpacing(10); //Set slider spacing 
        ViSlider.setPaintTicks(true);
        ViSlider.setPaintLabels(true);
        ViSlider.setLabelTable(lengthSlider.createStandardLabels(10)); //Display slider spacing increments
        panel.add(ViSlider);

        ViValue = new JTextField(ViSlider.getValue()+"m/s"); //To display the Vi of bullet value
        ViValue.setSize(50, 50);
        ViValue.setLocation(260, 175);
        panel.add(ViValue);

        //TextField for bullet mass input
        massBulletInput = new JTextField("Enter a double value for bullet mass. Default = 0.1kg");
        massBulletInput.setSize(300, 30);
        massBulletInput.setLocation(10, 230);
        panel.add(massBulletInput);

        //TextField for pendulum bob mass input
        massBobInput = new JTextField("Enter a double value for bob mass. Default = 1.0kg");
        massBobInput.setSize(300, 30);
        massBobInput.setLocation(10, 265);
        panel.add(massBobInput);

        //Reset button
        resetButton.setSize(200, 50);
        resetButton.setLocation(50, 300);
        resetButton.addActionListener(this);
        panel.add(resetButton);

        //Launch button
        launchButton.setSize(200, 50);
        launchButton.setLocation(50, 355);
        launchButton.addActionListener(this);
        panel.add(launchButton);

        //Write the formula on the screen, convert formula label from HTML Code:
        JLabel formulaLabel = new JLabel();
        String formulaText = 
        "<html>" + 
            "<style>" + 
                " body {" + 
                    "font-size: 14px;" + 
                "}" + 
            "</style>" + 
            "  " + 
            "<body>" + 
                "  <i>θ</i> = <i>cos</i><sup>-1</sup> [ 1 - " + 
                "<sup> (m<sub>B</sub>v<sub>iB</sub>)<sup>2</sup>" + 
                "</sup> &frasl;" + 
                "<sub> (m<sub>B</sub>+m<sub>P</sub>)<sup>2</sup> 2gL</sub> ]" + 
            "</body>" + 
        "</html>";
        formulaLabel.setText(formulaText); //Display angle from rad to deg
        formulaLabel.setHorizontalAlignment(JLabel.LEFT);
        formulaLabel.setSize(400, 200);
        formulaLabel.setLocation(10, 340);
        panel.add(formulaLabel);

        //JLabel to show the final angle result based on the equation:
        angleResultLabel.setHorizontalAlignment(JLabel.LEFT);
        angleResultLabel.setSize(100, 50);
        angleResultLabel.setLocation(10, 460);
        panel.add(angleResultLabel);


        //Add menu items/panels 
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);
        frame.setJMenuBar(menuBar);

        //Start the timer:
        timer.start();

        //Add listeners for required properties:
        aboutMenu.addActionListener(this);

        //Add default settings
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    

    // Main
    public static void main(String[] args) {
        new BallisticPendulum();
    }
}