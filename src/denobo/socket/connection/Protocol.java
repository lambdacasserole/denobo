/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo.socket.connection;

import denobo.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Saul Johnson
 */
public interface Protocol {
    
    public String getPacketHeader();
      
    public void writePacket(PrintWriter writer, DenoboPacket packet);
    
    public DenoboPacket readPacket(BufferedReader reader) throws IOException;

    public void writeMessage(PrintWriter writer, Message message);
     
}
