package denobo.centralcommand.designer.dialogs.monitor;

import denobo.Message;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alex Mullen
 */
public class MessageTableModel extends DefaultTableModel {
    
    /**
     * Creates a new instance of MessageTableModel for displaying Message objects.
     */
    public MessageTableModel() {
        super(new Object[] {"id", "to", "from", "data"}, 0);
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        // We want to make the entire table un-editable.
        return false;
    }
    
    /**
     * Adds a Message into this table model as a new row.
     * 
     * @param message   The message to add.
     */
    public void addRow(Message message) {
        this.addRow(new Object[] {message.getId(), message.getRecipient(), message.getOriginator(), message.getData()});
    }

}
