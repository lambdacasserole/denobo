package denobo.centralcommand.designer.dialogs.monitor;

import denobo.socket.connection.DenoboConnection;
import java.util.concurrent.TimeoutException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alex Mullen
 */
public class ConnectionTableModel extends DefaultTableModel {
    
    public ConnectionTableModel() {
        super(new Object[] {"ip", "port", "ping"}, 0);
    }
    
    @Override
    public boolean isCellEditable(int i, int i1) {
        // We want to make the entire table un-editable.
        return false;
    }
    
    public void addRow(DenoboConnection connection) {
        try {
            this.addRow(new Object[] {connection.getRemoteAddress(), connection.getRemotePort(), connection.poke(3000)});
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
        }
        
    }

}
