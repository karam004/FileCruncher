
import signup.Registration;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GUIClass extends JFrame {

    public GUIClass() {
        initUI();
    }

    private void initUI() {
        BufferedImage dropBoxIcon = null;
        BufferedImage driveIcon = null;

        try {
             dropBoxIcon = ImageIO.read(new File("/home/karamc/Desktop/dropBox.png"));
             driveIcon = ImageIO.read(new File("/home/karamc/Desktop/drive.png"));
        }catch (IOException e)
        {
            e.printStackTrace();
        }
       // JButton driveButton = new JButton("Google Drive");
       // JButton dropBoxButton = new JButton("DropBox");
       // JButton oneDriveButton = new JButton("OneDrive");
        JButton driveButton = new JButton(new ImageIcon(driveIcon));
        JButton dropBoxButton = new JButton(new ImageIcon(dropBoxIcon));

        driveButton.setBorder(BorderFactory.createEmptyBorder());
        driveButton.setContentAreaFilled(false);

        dropBoxButton.setBorder(BorderFactory.createEmptyBorder());
        dropBoxButton.setContentAreaFilled(false);


        Container pane = getContentPane();
        FlowLayout flow =  new FlowLayout();
        pane.setLayout(flow);

        add(driveButton);
        flow.setHgap(100);
        flow.setVgap(10);
        add(dropBoxButton);

        driveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
               try
               {
                   Registration.testDrive();
               } catch (IOException e)
               {
                   e.printStackTrace();
               }
            }
        });

        dropBoxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                    Registration.dropboxAuthentication();

            }
        });


        //createLayout(driveButton,dropBoxButton,oneDriveButton);

        setTitle("File Cruncher Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        //GroupLayout gl = new GroupLayout(pane);
        //pane.setLayout(gl);
        pane.setLayout(new FlowLayout());
        //gl.setAutoCreateContainerGaps(true);
        //gl.setHorizontalGroup(gl.createSequentialGroup()
       /* .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
        		        .addComponent(arg[0])
        		        .addComponent(arg[1])
        		        ));
        gl.setVerticalGroup(gl.createSequentialGroup()
        		.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE))
        		.addComponent(arg[0])
        	    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING))
        	    .addComponent(arg[1]));
*/

        ImageIcon icon = new ImageIcon("/home/karamc/Desktop/dropBox.png");

        JLabel label2 = new JLabel(icon);
        add(label2);

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
        
            @Override
            public void run() {
            	GUIClass ex = new GUIClass();
                ex.setVisible(true);
            }
        });
    }
}
