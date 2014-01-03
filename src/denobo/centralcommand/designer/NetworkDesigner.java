package denobo.centralcommand.designer;

import denobo.centralcommand.designer.dialogs.AgentPropertiesDialog;
import denobo.centralcommand.designer.dialogs.AddAgentDialog;
import denobo.Agent;
import denobo.centralcommand.DebugAgent;
import denobo.centralcommand.designer.dialogs.AgentConnectionsDialog;
import denobo.socket.SocketAgent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * A Multi-Agent-System network designer for Denobo.
 *
 * @author Alex Mullen, Saul Johnson
 */
public class NetworkDesigner extends JComponent implements ActionListener, MouseListener, MouseMotionListener {
    
    private static int debugAgentCounter = 1;
    
    
    // Gridline constants.
    private final int gridSpacing = 15;
    private final Color gridLineColor = new Color(0, 0, 0, 25);      // 0xF0F4F5    
    private final float[] gridLineDash = new float[] {2.0f};
    private final BasicStroke gridLineStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, gridLineDash, 0.0f);
    
    // Selection line constants.
    private final Color selectionBoundingBoxColor = new Color(0, 0, 0, 100);
    private final float[] selectionBoundingBoxDash = new float[] {2.0f};
    private final BasicStroke selectionBoundingBoxStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, selectionBoundingBoxDash, 0.0f);
   
    // Menu for right-clicking on empty space
    private final JPopupMenu emptySpacePopup;
    private final JMenuItem menuOptionAddAgent;
    private final JMenuItem menuOptionAddSocketAgent;
    private final JMenuItem menuOptionAddDebugAgent;
    
    // Menu for right-clicking on a agent
    private final JPopupMenu agentSelectedPopup;
    private final JMenuItem menuOptionLink;
    private final JMenuItem menuOptionsConnections;
    private final JMenuItem menuOptionProperties;
    private final JMenuItem menuOptionMonitor;
    
    // Dialogs
    private final AddAgentDialog addAgentDialog;
    private final AgentPropertiesDialog agentPropertiesDialog;
    private final AgentConnectionsDialog agentConnectionsDialog;
    
    // Collections to hold the data for this designer
    private final List<AgentDisplayable> agents;
    private final List<AgentLink> agentLinks;
    private final List<DesignerEventListener> designerEventListeners;
    
    // State data to save in-between events
    private Point mouseCursorPosition;
    private Point selectedComponentDragOffset;    // The offset of the cursor relative to the initial click on a component before dragging.
    private AgentDisplayable agentSelected;
    private boolean isSelectedAgentTryingToLink;
    private boolean isSelectedAgentDragging;
    
    private boolean showGrid = true; // Whether or not the grid (and snap-to-grid features) are currently enabled.

    
    
    public NetworkDesigner() {

        super();
 
        this.setBackground(Color.WHITE);

        agents = new ArrayList<>();
        agentLinks = new ArrayList<>();
        designerEventListeners = new ArrayList<>();
        
        
        agentSelectedPopup = new JPopupMenu();
        
        menuOptionLink = new JMenuItem("Link");
        menuOptionLink.addActionListener(this);
        agentSelectedPopup.add(menuOptionLink);
       
        agentSelectedPopup.addSeparator();
        
        menuOptionsConnections = new JMenuItem("Connections");
        menuOptionsConnections.addActionListener(this);
        agentSelectedPopup.add(menuOptionsConnections);
        
        menuOptionMonitor = new JMenuItem("Monitor");
        menuOptionMonitor.addActionListener(this);
        agentSelectedPopup.add(menuOptionMonitor);       
        
        agentSelectedPopup.addSeparator();
        
        menuOptionProperties = new JMenuItem("Properties");
        menuOptionProperties.addActionListener(this);
        agentSelectedPopup.add(menuOptionProperties);
        

        
        emptySpacePopup = new JPopupMenu();
        
        menuOptionAddAgent = new JMenuItem("Add Agent");
        menuOptionAddAgent.addActionListener(this);
        emptySpacePopup.add(menuOptionAddAgent);
        
        menuOptionAddSocketAgent = new JMenuItem("Add Socket Agent");
        menuOptionAddSocketAgent.addActionListener(this);
        emptySpacePopup.add(menuOptionAddSocketAgent);
        
        menuOptionAddDebugAgent = new JMenuItem("Add Debug Agent");
        menuOptionAddDebugAgent.addActionListener(this);
        emptySpacePopup.add(menuOptionAddDebugAgent);
        
        
        
        addAgentDialog = new AddAgentDialog();
        agentPropertiesDialog = new AgentPropertiesDialog();
        agentConnectionsDialog = new AgentConnectionsDialog(this);
        
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
    }
    
    /**
     * Sets whether or not the grid (and snap-to-grid features) are currently enabled in the designer.
     *
     * @param showGrid Whether or not to showDialog the grid and enable snap-to-grid features.
     */
    public void setShowGrid(boolean showGrid) {
    
        this.showGrid = showGrid;
        
    }
    
    /**
     * Gets whether or not the grid (and snap-to-grid features) are currently enabled in the designer.
     * 
     * @return true if the grid is set to showDialog otherwise false.
     */
    public boolean getShowGrid() {
    
        return showGrid;
        
    }
    
    /**
     * Adds a listener class to the designer, which receives all design events triggered by the user.
     *
     * @param listener The listener object to add.
     */
    public void addDesignerEventListener(DesignerEventListener listener) {
        
        designerEventListeners.add(listener);
        
    }
    
    public List<AgentLink> getAgentLinks() {
        return agentLinks;
    }
    
    public List<AgentDisplayable> getAgentDisplayables() {
        return agents;
    }
    
    
    private void maybeShowEmptySpacePopup(MouseEvent e) {
        
        if (e.isPopupTrigger()) {
            emptySpacePopup.show(e.getComponent(), e.getX(), e.getY());
        }
        
    }
    
    /**
     * Returns the top-most agent (if any) at a particular position.
     * 
     * @param point     The position to check.
     * @return          The agent selected or null is returned if there is no
     *                  agent at that position.
     */
    private AgentDisplayable getAgentAt(Point point) {
        
        // Start at the end of the array since the agents at the end will be the
        // ones that are rendered last, thus are on top.
        for (int i = (agents.size() - 1); i >= 0; i--) {
            if (agents.get(i).getBounds().contains(point)) {
                return agents.get(i);
            }
        }
        
        return null;
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == menuOptionAddAgent) {
            
            final Agent agentToAdd = addAgentDialog.showDialog(mouseCursorPosition);
            if (agentToAdd != null) {

                agents.add(new AgentDisplayable(agentToAdd, 
                                mouseCursorPosition.x - (AgentDisplayable.width / 2), 
                                mouseCursorPosition.y - (AgentDisplayable.height / 2)));
                this.repaint();                

            }
            
        } else if (e.getSource() == menuOptionsConnections) {
            
            agentConnectionsDialog.showDialog(agentSelected.getBounds().getLocation(), agentSelected);

        } else if (e.getSource() == menuOptionMonitor) {
            
            agentSelected.getMonitorDialog().show(agentSelected.getBounds().getLocation());
            
        } else if (e.getSource() == menuOptionProperties) {
            
            agentPropertiesDialog.showDialog(agentSelected.getBounds().getLocation(), agentSelected.getAgent());

        } else if (e.getSource() == menuOptionAddSocketAgent) { 

            agents.add(new SocketAgentDisplayable(new SocketAgent("test-socket-agent", 32), 
                            mouseCursorPosition.x - (AgentDisplayable.width / 2), 
                            mouseCursorPosition.y - (AgentDisplayable.height / 2)));
            this.repaint();
           
        } else if (e.getSource() == menuOptionAddDebugAgent) {
            
            final DebugAgent nextDebugAgent = new DebugAgent("debug-agent" + debugAgentCounter++);
            nextDebugAgent.show(mouseCursorPosition);
            
            agents.add(new AgentDisplayable(nextDebugAgent, 
                            mouseCursorPosition.x - (AgentDisplayable.width / 2), 
                            mouseCursorPosition.y - (AgentDisplayable.height / 2)));
            this.repaint();

        } else if (e.getSource() == menuOptionLink) {

            isSelectedAgentTryingToLink = true;
            this.repaint();
            
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            if (agentClicked != null) {

                if (isSelectedAgentTryingToLink) {
                    isSelectedAgentTryingToLink = false;
                    agentLinks.add(new AgentLink(agentSelected, agentClicked));
                } else {
                    agentSelected = agentClicked;
                    for (DesignerEventListener currentListener : designerEventListeners) {
                        currentListener.componentSelected(new DesignerEvent(agentSelected));
                    }
                }
                this.repaint();

            } else {

                agentSelected = null;
                isSelectedAgentTryingToLink = false;
                for (DesignerEventListener currentListener : designerEventListeners) {
                    currentListener.selectionCleared(null);
                }
                this.repaint();
            }
            
        } else {
            
            if (agentClicked == null) {
                isSelectedAgentTryingToLink = false;
                agentSelected = null;
                this.repaint();
            }
            
            maybeShowEmptySpacePopup(e);
        }
        
        
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            if (isSelectedAgentTryingToLink) {      // Are we currently dragging a link around?

                if (agentClicked != null) {
                    isSelectedAgentTryingToLink = false;
                    agentLinks.add(new AgentLink(agentSelected, agentClicked));
                } else {
                    isSelectedAgentTryingToLink = false;
                }
                this.repaint();

            } else {

                if (agentClicked != null) {

                    agentSelected = agentClicked;
                    isSelectedAgentDragging = true;
                    selectedComponentDragOffset = new Point(e.getX() - agentSelected.getBounds().x, e.getY() - agentSelected.getBounds().y);
                    this.repaint();
                    
                    for (DesignerEventListener currentListener : designerEventListeners) {
                        currentListener.componentSelected(new DesignerEvent(agentSelected));
                    }

                } else {

                    agentSelected = null;
                    this.repaint();
                    
                    for (DesignerEventListener currentListener : designerEventListeners) {
                        currentListener.selectionCleared(null);
                    }
                    
                }

            }
            
        } else {
            
            if (agentClicked == null) {
                isSelectedAgentTryingToLink = false;
                agentSelected = null;
                this.repaint();
            }
            
        }
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
        isSelectedAgentDragging = false;
        
        final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
        
        // If we clicked with a button that is a popup trigger (right-click) on
        // a agent.
        if ((agentClicked != null) && (e.isPopupTrigger())) {
            agentSelected = agentClicked;
            this.repaint();
            agentSelectedPopup.show(e.getComponent(), e.getX(), e.getY());
        } else {
            if (agentClicked == null) {
                isSelectedAgentTryingToLink = false;
                agentSelected = null;
                this.repaint();
            }
            maybeShowEmptySpacePopup(e);
        }
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
        mouseCursorPosition = e.getPoint();
        if (isSelectedAgentTryingToLink) {
            this.repaint();
        }
        
        if (isSelectedAgentDragging) {
            
            int newX = (e.getX() - selectedComponentDragOffset.x);
            int newY = (e.getY() - selectedComponentDragOffset.y);

            if (showGrid) {
                newX = newX - (newX % gridSpacing);
                newY = newY - (newY % gridSpacing);
            }

            agentSelected.getBounds().x = newX;
            agentSelected.getBounds().y = newY;
            this.repaint();
            
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        mouseCursorPosition = e.getPoint();
        if (isSelectedAgentTryingToLink) {
            this.repaint();
        }
        
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Maybe change cursor
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Maybe change cursor
    }
   
    /**
     * Performs a repaint of this designer component.
     * 
     * @param g     The graphics context we are going to paint to.
     */
    protected void paintComponent(Graphics2D g) {
        
        final Stroke defaultStroke = g.getStroke();
        
        // Fill in background.
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        if (showGrid) { // Are we drawing a grid?
        
            g.setColor(gridLineColor);
            //g.setStroke(gridLineStroke);  // Causes lag

            for (int x = 0; x < this.getWidth(); x += gridSpacing) { // Draw vertical gridlines.
                g.drawLine(x, 0, x, this.getHeight());
            }
            
            for (int y = 0; y < this.getHeight(); y += gridSpacing) { // Draw horizontal gridlines.
                g.drawLine(0, y, this.getWidth(), y);
            }
            
        }

        g.setStroke(defaultStroke);
        
        if (isSelectedAgentTryingToLink) {    // Are we dragging a link around?
            
            final int x = agentSelected.getBounds().x + (agentSelected.getBounds().width / 2);
            final int y = agentSelected.getBounds().y + (agentSelected.getBounds().height / 2);
            
            g.setColor(Color.black);
            g.drawLine(x, y, mouseCursorPosition.x, mouseCursorPosition.y);
        }
        
        for (AgentLink currentLink : agentLinks) {
            currentLink.draw(g);
        }
        
        for (AgentDisplayable currentAgent : agents) {
            currentAgent.draw(g);
        }
        
        if (agentSelected != null) { 
            
            agentSelected.draw(g);
            
            // Dotted bounding box on selected agent.
            g.setColor(selectionBoundingBoxColor);
            g.setStroke(selectionBoundingBoxStroke);
            g.draw(agentSelected.getBounds());
            
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        paintComponent((Graphics2D) g); // We need Graphics2D for advanced drawing methods.

    }

}
