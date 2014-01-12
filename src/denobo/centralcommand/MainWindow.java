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
 * @author Saul Johnson
 */
public class MainWindow extends DenoboWindow {
        
    private final JTabbedPane tabHolder;
    private final JMenuItem networkDesignItem;
    private final JMenuItem exitItem;
    
    /**
     * Initialises the program main window.
     */
    public MainWindow() {
        
        super();
        
        this.setTitle("Denobo Central Command");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        tabHolder = new JTabbedPane();
        tabHolder.setPreferredSize(new Dimension(640, 480));
        this.add(tabHolder, BorderLayout.CENTER);
        
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        
        final JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        networkDesignItem = new JMenuItem("New Network Design");
        networkDesignItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tabHolder.addTab("Network Design [" + NetworkDesignTab.nextTabNumber() + "]", new NetworkDesignTab());
            }
        });
        fileMenu.add(networkDesignItem);
        
        exitItem = new JMenuItem("Exit");
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
