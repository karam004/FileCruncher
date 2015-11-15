
import signup.Registration;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class GUIClass extends JFrame {

    public GUIClass() {
        initUI();
    }

    private void initUI() {

        JButton driveButton = new JButton("Google Drive");
        JButton dropBoxButton = new JButton("DropBox");
        JButton oneDriveButton = new JButton("OneDrive");
        
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
        
        oneDriveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        
        createLayout(driveButton,dropBoxButton,oneDriveButton);

        setTitle("File Cruncher Login");
        setSize(200, 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        
        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
        		        .addComponent(arg[0])
        		        .addComponent(arg[1])
        		        ));
        gl.setVerticalGroup(gl.createSequentialGroup()
        		.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE))
        		.addComponent(arg[0])
        	    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING))
        	    .addComponent(arg[1]));

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
