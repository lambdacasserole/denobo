package chatroom;

import boris.utils.ArgList;
import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import denobo.socket.SocketAgent;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// this version loads via the monitor

public class Chat3Client extends JFrame
{
	private String name, room, roomManager;
	TextArea msgArea;

	public Chat3Client()
	{	this("");
	}
	
	public Chat3Client(String cmd )
	{	
                super();

		//--- cmd line ----------------------
		ArgList args = new ArgList( cmd );
		name = args.get( "name" );
		if (name == null)
		{	name = "user1";
			sysout( "WARNING: no name supplied, naming as "+name );
		}
		room = args.get( "room" );
		if (room == null)
		{	room = "room1";
			sysout( "WARNING: no room supplied, using "+room );
		}
		roomManager = room+"manager";

		//--- agent ------------------
		final SocketAgent agent = new SocketAgent(name);
		agent.addMessageListener(new MessageHandler() 
                {
                    @Override
                    public void messageRecieved(Agent agent, Message message)
                    {	
                        msgArea.append( "\n" + message.getData() );
                    }
		});

                agent.addConnection(1234);

		//--- gui setup ---------------
		setTitle( "ChatClient: " +name );

		Container main = getContentPane();
		main.setLayout( new BorderLayout() );

		Container topLine = new Container();
		topLine.setLayout( new FlowLayout() );
		Container btmLine = new Container();
		btmLine.setLayout( new FlowLayout() );

		main.add( topLine, BorderLayout.NORTH );
		main.add( btmLine, BorderLayout.SOUTH );

		msgArea = new TextArea( 12, 60 );
		main.add( msgArea, BorderLayout.CENTER );

		Button joinBtn = new Button("join");
		joinBtn.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	agent.sendMessage( roomManager, "join" );
			}
		});
		topLine.add( joinBtn );

		Button leaveBtn = new Button("leave");
		leaveBtn.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	agent.sendMessage( roomManager, "leave" );
			}
		});
		topLine.add( leaveBtn );

		final TextField chatMsg = new TextField(20);
		btmLine.add( chatMsg );
		
		Button sendBtn = new Button("send");
		sendBtn.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	agent.sendMessage( room, chatMsg.getText() );
			}
		});
		btmLine.add( sendBtn );

		pack();
		this.setSize(350, 217);
		setVisible(true);
	}
	
	private void sysout( String str )
	{	System.out.println("["+name+"] "+ str);
	}
}
