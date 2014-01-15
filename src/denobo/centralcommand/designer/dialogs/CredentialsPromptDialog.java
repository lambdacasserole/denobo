package denobo.centralcommand.designer.dialogs;

import denobo.socket.connection.ConnectionCredentialsHandler;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Credentials;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * A dialog that will appear whenever a connection request requires some
 * credentials before a session can occur.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class CredentialsPromptDialog implements ConnectionCredentialsHandler {

    private final JDialog dialog;
    private final JPasswordField passwordField;
    private final JButton okButton;
    
    private Credentials credentialsToReturn;
    
    public CredentialsPromptDialog() {
        
        dialog = new JDialog();
        dialog.setTitle("Credentials Requested");
        dialog.setLayout(new BorderLayout());
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        
        passwordField = new JPasswordField(10);
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Broken username.
                credentialsToReturn = 
                        new Credentials(null, String.valueOf(passwordField.getPassword()));

                dialog.dispose();
            }
            
        });
        
        
        
        final JPanel passwordRow = new JPanel(new FlowLayout());
        passwordRow.add(new JLabel("Password:"));
        passwordRow.add(passwordField);
        dialog.add(passwordRow, BorderLayout.CENTER);
        
        final JPanel buttonRow = new JPanel(new FlowLayout());
        buttonRow.add(okButton);
        dialog.add(buttonRow, BorderLayout.SOUTH);

        dialog.pack();
        
    }
    
    
    
    @Override
    public Credentials credentialsRequested(DenoboConnection connection) {
 
        credentialsToReturn = null;
        
        passwordField.setText(null);
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return credentialsToReturn;
        
    }

}
