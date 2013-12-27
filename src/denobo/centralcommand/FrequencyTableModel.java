package denobo.centralcommand;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Saul Johnson
 */
public class FrequencyTableModel extends DefaultTableModel {
    
    public FrequencyTableModel() {
        super(new Object[][] {}, new Object[] {"Byte", "Frequency", "Prefix Code"});
    }
    
}
