package chatroom;

import boris.utils.ArgList;
import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import denobo.MessageListener;
import denobo.socket.SocketAgent;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import java.util.*;


/*
this version loads via the monitor so constructor methods take portal as argument,
other argument to constructor is initialisation string, use eg: "name=fred" to set the
room name. Name is room1 by default.
the ChatServer builds 2 agents...
(i) named after the room (room1 by default) to handle messaging, chat-room members 
should send messages to this agent to have them forwarded to other chat room members
(ii) named <room-name>+"manager" (room1manager by default) to handle joining 
leaving the chatroom. Clients should send the message "join" to the management agent 
to join a chatroom and "leave" to leave it
*/

public class Chat3Server extends JFrame
{	
	private final int frameWidth  = 650, frameHeight = 250;

	private String name;
	private SocketAgent manager;
        private Agent chatAgent;
	private Set<String> members;

	private TextArea msgArea, memberArea;
	
	// constructor with portal arg
	public Chat3Server( )
	{	
            this("");
	}
	
	// constructor and initialisation arg
	// initialise with eg: name=room1
	// to set the room name
	public Chat3Server(String cmd )
	{	
                super();
		members = Collections.synchronizedSet( new HashSet<String>() );

		//--- cmd line ----------------------
		ArgList args = new ArgList( cmd );
		name = args.get( "name" );
		if (name == null)
		{	
                    name = "room1";
                    sysout( "WARNING: no name supplied, naming as "+name );
		}

		//--- manager agent ------------------
		manager = new SocketAgent( name+"manager" );
		manager.addMessageListener(new MessageHandler()
		{
                    @Override
                    public void messageRecieved(Agent agent, Message message) 
                    {
                        final String from = message.getOriginator();
                        final String msg = message.getData();
                        
                        if (msg.equals("join")) {
                            addMember( from );
                        } else if (msg.equals("leave")) {
                            removeMember( from );
                        }
			
                        manager.sendMessage( from, msg+" status=ok" );
                    }
		});
                
                // Begin advertising
                try {
                    manager.startAdvertising(1234);
                } catch (IOException ex) {
                    System.out.println("Unable to advertisie: " + ex.getMessage());
                }

		//--- chat agent ---------------------
		chatAgent = new Agent( name );
		chatAgent.addMessageListener(new MessageHandler()
		{
                    @Override
                    public void messageRecieved(Agent agent, Message message) {
                        final String from = message.getOriginator();
                        final String msg = message.getData();
                        
                        if (members.contains(from.intern()))
                        {	
                            String msgTxt = from + " \t" + msg;
                            msgArea.append( "\n" + msgTxt );
                            broadcast(msgTxt);
                        }
                    }                 
		});
                
                // Connect the local chatAgent to the manager agent
                chatAgent.connectAgent(manager);

		//--- GUI set up -------------------
		setTitle( "ChatRoom: " +name );
		msgArea = new TextArea( 12, 60 );
		memberArea = new TextArea( 12, 20 );

		Container main = getContentPane();
		main.setLayout( new FlowLayout() );
		main.add( memberArea );
		refreshMemberArea();

		main.add( msgArea );
		msgArea.setText( "Messages" );

		pack();
		this.setSize( frameWidth, frameHeight );
		setVisible(true);
		//addWindowListener(
		//	new WindowAdapter()
		//		{	public void windowClosing( WindowEvent w )
		//			{	System.exit(0);
		//			}
		//		});
	}
	
	private void sysout( String str )
	{	System.out.println("["+name+"] "+ str);
	}
	private void broadcast( String msg )
	{	for( String m : members )
			chatAgent.sendMessage( m, msg );
	}
	public void addMember( String name )
	{	members.add( name.intern() );		// intern shouldnt be reqd cz backed by HashMap bt safer
		refreshMemberArea();
	}
	public void removeMember( String name )
	{	members.remove( name.intern() );
		refreshMemberArea();
	}
	public void refreshMemberArea()
	{	sysout( "refreshing members" );
		memberArea.setText( "Members" );
		for( String m : members )
		{	sysout( "member = " + m );
			memberArea.append( "\n"+ m );
		}
	}
}
