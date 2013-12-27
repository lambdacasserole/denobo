package denobo.centralcommand;

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
public class MainWindow extends DenoboWindow implements ActionListener {
        
    private final JTabbedPane tabHolder;
    private final JMenuItem huffmanCodingItem;
    
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
        
        final JMenu testMenu = new JMenu("Test");
        menuBar.add(testMenu);
        
        huffmanCodingItem = new JMenuItem("Huffman Coding...");
        huffmanCodingItem.addActionListener(this);
        testMenu.add(huffmanCodingItem);
        
        this.pack();
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == huffmanCodingItem) {
            tabHolder.addTab("Huffman Coding [" + HuffmanTestTab.nextTabNumber() + "]", new HuffmanTestTab());
        }
        
    }
    
}
