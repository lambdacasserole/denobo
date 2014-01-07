package denobo.centralcommand.designer;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lee Oliver
 */
public class DebugAgentTableModel extends DefaultTableModel {

    /**
     * Initialise a new instance of DebugAgentTableModel.
     */
    public DebugAgentTableModel() {
        super(new Object[] {"Name", "Message"}, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
