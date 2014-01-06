package denobo.centralcommand.designer.dialogs;

import denobo.centralcommand.designer.AgentDisplayable;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A ListCellRendered implementation that renders the name of an AgentDisplayable
 * in a cell.
 *
 * @author Alex Mullen
 */
public class AgentDisplayableListCellRenderer extends JLabel implements ListCellRenderer {

    /**
     * Creates an instance of a AgentDisplayableListCellRenderer.
     */
    public AgentDisplayableListCellRenderer() {
        
        setOpaque(true);
        
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (value == null) {
            
            this.setText(null);
            
        } else if (value instanceof AgentDisplayable) {
            
            final AgentDisplayable agent = (AgentDisplayable) value;
            this.setText(agent.getAgent().getName());

        }
        
        if (isSelected) {
            
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            
        } else {
            
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            
        }
        
        return this;
    }
    
}
