package denobo.centralcommand;

import denobo.centralcommand.designer.NetworkDesignTab;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

/**
 * Represents the program main window.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class MainWindow extends DenoboWindow {
      
    /**
     * Initialises the program main window.
     */
    public MainWindow() {
        
        super();
        
        // Set window title.
        this.setTitle("Denobo Central Command");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        // Add tabbed MDI pane.
        final JTabbedPane tabHolder = new JTabbedPane();
        tabHolder.setPreferredSize(new Dimension(640, 480));
        this.add(tabHolder, BorderLayout.CENTER);
        
        // Add menu bar.
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        
        // Add "File" menu.
        final JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        // Add "New Network Design" item.
        final JMenuItem networkDesignItem = new JMenuItem("New Network Design");
        networkDesignItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tabHolder.addTab("Network Design [" + NetworkDesignTab.nextTabNumber() + "]", new NetworkDesignTab());
            }
        });
        fileMenu.add(networkDesignItem);
        
        // Add "Exit" item.
        final JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);
        
        this.pack();
        
    }

}
