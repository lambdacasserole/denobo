package denobo.centralcommand.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author Alex Mullen
 */
public class NetworkDesignTab extends JPanel {
    
    private static int tabNumber = 0;
    
    public static int nextTabNumber() {
        return tabNumber++;
    }
    
    public NetworkDesignTab() {
        
        super();

        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(800, 600));

        final NetworkDesigner centerComponent = new NetworkDesigner();
        this.add(centerComponent, BorderLayout.CENTER);
        
    }
    
}