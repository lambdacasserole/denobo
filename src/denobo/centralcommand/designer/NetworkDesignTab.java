package denobo.centralcommand.designer;

import java.awt.BorderLayout;
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

        final NetworkDesigner designer = new NetworkDesigner();
        this.add(designer, BorderLayout.CENTER);

        //this.add(new AgentPropertiesDialog().tabHolder, BorderLayout.EAST);
    }
    
}
