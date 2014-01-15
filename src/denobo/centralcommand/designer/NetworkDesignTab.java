package denobo.centralcommand.designer;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * Represents a Network Design tab.
 *
 * @author Alex Mullen
 */
public class NetworkDesignTab extends JPanel {
    
    /**
     * The current unique tab number.
     */
    private static int tabNumber = 0;

    /**
     * Initialises an instance of a NetworkDesignTab.
     */
    public NetworkDesignTab() {
        
        super();

        this.setLayout(new BorderLayout());

        final NetworkDesigner designer = new NetworkDesigner();
        this.add(designer, BorderLayout.CENTER);
        
    }
    
    /**
     * Returns the next unique tab number.
     * 
     * @return the next unique tab number
     */
    public static int nextTabNumber() {
        return tabNumber++;
    }
    
}
