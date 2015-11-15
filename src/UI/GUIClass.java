package UI;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import service.FileChruncherService;
import signup.Registration;
import configutils.Constants;

public class GUIClass extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public GUIClass() {
        initUI();
    }

    private void initUI() {
        final int[] count = { 0 };
        BufferedImage dropBoxIcon = null;
        BufferedImage driveIcon = null;
        BufferedImage doneIcon = null;

        try {
            dropBoxIcon = ImageIO.read(new File(Constants.DROPBOX_IMG));
            driveIcon = ImageIO.read(new File(Constants.DRIVE_IMG));

            doneIcon = ImageIO.read(new File(Constants.DONE_IMG));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // JButton driveButton = new JButton("Google Drive");
        // JButton dropBoxButton = new JButton("DropBox");
        // JButton oneDriveButton = new JButton("OneDrive");
        JButton driveButton = new JButton(new ImageIcon(driveIcon));
        JButton dropBoxButton = new JButton(new ImageIcon(dropBoxIcon));
        JButton doneButton = new JButton(new ImageIcon(doneIcon));

        driveButton.setBorder(BorderFactory.createEmptyBorder());
        driveButton.setContentAreaFilled(false);

        dropBoxButton.setBorder(BorderFactory.createEmptyBorder());
        dropBoxButton.setContentAreaFilled(false);

        doneButton.setBorder(BorderFactory.createEmptyBorder());
        doneButton.setContentAreaFilled(false);
        doneButton.setSize(5, 5);

        Container pane = getContentPane();
        FlowLayout flow = new FlowLayout();
        pane.setLayout(flow);

        add(driveButton);
        flow.setHgap(100);
        flow.setVgap(10);
        add(dropBoxButton);
        flow.setHgap(100);
        flow.setVgap(20);
        add(doneButton);

        driveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    Registration.testDrive();
                    count[0]++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        dropBoxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                Registration.dropboxAuthentication();
                count[0]++;

            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (count[0] != 0) {
                    System.out.println("starting action");
                    FileChruncherService.main(new String[2]);
                }
            }
        });

        setTitle("File Cruncher Login");
        setSize(300, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(final JComponent... arg) {

        Container pane = getContentPane();

        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup().addGroup(
                gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(arg[0]).addComponent(arg[1])));
        gl.setVerticalGroup(gl
                .createSequentialGroup()
                .addGroup(
                        gl.createParallelGroup(GroupLayout.Alignment.BASELINE))
                .addComponent(arg[0])
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING))
                .addComponent(arg[1]));

        // GroupLayout gl = new GroupLayout(pane);
        // pane.setLayout(gl);
        pane.setLayout(new FlowLayout());
        // gl.setAutoCreateContainerGaps(true);
        // gl.setHorizontalGroup(gl.createSequentialGroup()
        /*
         * .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
         * .addComponent(arg[0]) .addComponent(arg[1]) ));
         * gl.setVerticalGroup(gl.createSequentialGroup()
         * .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE))
         * .addComponent(arg[0])
         * .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING))
         * .addComponent(arg[1]));
         */

        ImageIcon icon = new ImageIcon("/home/karamc/Desktop/dropBox.png");

        JLabel label2 = new JLabel(icon);
        add(label2);

    }

    public static void main(final String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                GUIClass ex = new GUIClass();
                ex.setVisible(true);
            }
        });
    }
}
