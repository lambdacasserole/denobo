package denobo.centralcommand.designer;

import denobo.centralcommand.designer.dialogs.AgentPropertiesDialog;
import denobo.centralcommand.designer.dialogs.AddAgentDialog;
import denobo.Agent;
import denobo.centralcommand.designer.dialogs.AgentConnectionsDialog;
import denobo.socket.SocketAgent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
public class NetworkDesigner extends JComponent implements ActionListener {
    
    // Gridline constants.
    private final int gridSpacing = 15;
    private final Color gridLineColor = new Color(0, 0, 0, 25);      // 0xF0F4F5    
    
    // Selection line constants.
    private final Color selectionBoundingBoxColor = new Color(0, 0, 0, 100);
    private final float[] selectionBoundingBoxDash = new float[] {2.0f};
    private final BasicStroke selectionBoundingBoxStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, selectionBoundingBoxDash, 0.0f);
   
    // Menu for right-clicking on empty space
    private final JPopupMenu emptySpacePopup;
    private final JMenuItem menuOptionAddAgent;
    private final JMenuItem menuOptionAddSocketAgent;
    
    // Menu for right-clicking on a agent
    private final JPopupMenu agentSelectedPopup;
    private final JMenuItem menuOptionLink;
    private final JMenuItem menuOptionsConnections;
    private final JMenuItem menuOptionProperties;
    private final JMenuItem menuOptionMonitor;
    private final JMenuItem menuOptionDebugWindow;
    private final JMenuItem menuOptionDelete;
    
    // Dialogs
    private final AddAgentDialog addAgentDialog;
    private final AgentPropertiesDialog agentPropertiesDialog;
    private final AgentConnectionsDialog agentConnectionsDialog;
    
    // Collections to hold the data for this designer
    private final List<AgentDisplayable> agents;
    private final List<AgentLink> agentLinks;
    private final List<DesignerEventListener> designerEventListeners;
    
    // State data to save in-between events
    private Point lastMenuClickPosition;
    private Point selectedComponentDragOffset;    // The offset of the cursor relative to the initial click on a component before dragging.
    private NetworkDesignerState state;
    private AgentDisplayable agentSelected;
    private boolean isSelectedAgentTryingToLink;
    
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
        
        menuOptionDebugWindow = new JMenuItem("Debug Window");
        menuOptionDebugWindow.addActionListener(this);
        agentSelectedPopup.add(menuOptionDebugWindow);
        
        agentSelectedPopup.addSeparator();
        
        menuOptionDelete = new JMenuItem("Delete");
        menuOptionDelete.addActionListener(this);
        agentSelectedPopup.add(menuOptionDelete);
        
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
        
        
        
        
        addAgentDialog = new AddAgentDialog();
        agentPropertiesDialog = new AgentPropertiesDialog();
        agentConnectionsDialog = new AgentConnectionsDialog(this);
        
        
        state = new DefaultState();
        
        
        this.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                state.handleMouseClicked(e);

            }

            @Override
            public void mousePressed(MouseEvent e) {

                state.handleMousePressed(e);

            }

            @Override
            public void mouseReleased(MouseEvent e) {

                state.handleMouseReleased(e);

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                state.handleMouseEntered(e);

            }

            @Override
            public void mouseExited(MouseEvent e) {

                state.handleMouseExited(e);

            }

        });

        this.addMouseMotionListener(new MouseMotionListener() {
            
            @Override
            public void mouseDragged(MouseEvent e) {

                state.handleMouseDragged(e);

            }

            @Override
            public void mouseMoved(MouseEvent e) {

                state.handleMouseMoved(e);

            }
            
        });
        
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
    
    /**
     * Returns the list of AgentLink objects that visually represent the links
     * between agents.
     * 
     * @return The list of AgentLink objects in this designer.
     */
    public List<AgentLink> getAgentLinks() {
        return agentLinks;
    }
    
    /**
     * Returns the list of AgentDisplayable objects that visually represent the
     * agents in the designer.
     * 
     * @return The list of AgentDisplayable objects in this designer.
     */
    public List<AgentDisplayable> getAgentDisplayables() {
        return agents;
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
    
    /**
     * Shutdowns and removes the specified AgentDisplayable from the network.
     * 
     * @param agent The agent to shutdown and remove.
     */
    public void removeAgentDisplayable(AgentDisplayable agent) {
        
        agent.getDebugWindow().hide();
        agent.getMonitorDialog().hide();
        
        agent.getAgent().shutdown();
        
        agents.remove(agent);
        removeAnyAgentLinksContaining(agent);
        
        if (agentSelected == agent) {
            agentSelected = null;
            state = new DefaultState();
        }
        
        this.repaint();
        
    }
    
    /**
     * Removes any AgentLink's containing the specified agent.
     * 
     * @param agent The agent to remove any links to.
     */
    public void removeAnyAgentLinksContaining(AgentDisplayable agent) {
        
        final List<AgentLink> linksToRemove = new ArrayList<>();
        
        for (AgentLink currentAgentLink : agentLinks) {
            if (currentAgentLink.contains(agent)) {
                currentAgentLink.breakLink();
                linksToRemove.add(currentAgentLink);
            }
        }
        
        agentLinks.removeAll(linksToRemove);
        this.repaint();
        
    }
    
    /**
     * Removes any AgentLink's between the specified two agents.
     * 
     * @param agent1 The first agent.
     * @param agent2 The second agent.
     */
    public void removeAnyAgentLinksContaining(AgentDisplayable agent1, AgentDisplayable agent2) {
        
        final List<AgentLink> linksToRemove = new ArrayList<>();
        
        for (AgentLink currentAgentLink : agentLinks) {
            if (currentAgentLink.contains(agent1, agent2)) {
                currentAgentLink.breakLink();
                linksToRemove.add(currentAgentLink);
            }
        }
        
        agentLinks.removeAll(linksToRemove);
        this.repaint();
        
    }
    
//    
//    /**
//     * Nudges the selected component in the specified direction by the size of one grid square, or by 1 pixel if grid is not enabled.
//     *
//     * @param nudgeType The direction in which to nudge the component.
//     */
//    public void nudgeSelectedComponent(int nudgeType) {
//        
//        if(!isComponentSelected()) { // Abort if no component selected.
//            return;
//        }
//        
//        int xOffset = 0;
//        int yOffset = 0;
//        
//        if(showGrid) { // If the grid is shown, nudge by the grid spacing in the specified direction.
//            xOffset = (nudgeType == NUDGE_LEFT ? -gridSpacing : (nudgeType == NUDGE_RIGHT ? gridSpacing : 0));
//            yOffset = (nudgeType == NUDGE_UP ? -gridSpacing : (nudgeType == NUDGE_DOWN ? gridSpacing : 0));
//        }
//        else { // If the grid is not shown, nudge by 1 pixel in the specified direction.
//            xOffset = (nudgeType == NUDGE_LEFT ? -1 : (nudgeType == NUDGE_RIGHT ? 1 : 0));
//            yOffset = (nudgeType == NUDGE_UP ? -1 : (nudgeType == NUDGE_DOWN ? 1 : 0));
//        }
//        
//        // Move selected component.
//        PlaceableComponent currentComponent = getSelectedComponent();
//        currentComponent.setLocation(currentComponent.getX() + xOffset, currentComponent.getY() + yOffset);
//        
//        this.repaint(); // Repaint to show changes.
//        
//    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == menuOptionAddAgent) {
            
            final Agent agentToAdd = addAgentDialog.showDialog(lastMenuClickPosition);
            if (agentToAdd != null) {

                agents.add(new AgentDisplayable(agentToAdd, 
                                lastMenuClickPosition.x - (AgentDisplayable.width / 2), 
                                lastMenuClickPosition.y - (AgentDisplayable.height / 2)));
                this.repaint();

            }
            
        } else if (e.getSource() == menuOptionsConnections) {
            
            agentConnectionsDialog.showDialog(agentSelected.getBounds().getLocation(), agentSelected);

        } else if (e.getSource() == menuOptionMonitor) {
            
            agentSelected.getMonitorDialog().show(agentSelected.getBounds().getLocation());
            
        } else if (e.getSource() == menuOptionDebugWindow) {
                    
            agentSelected.getDebugWindow().show(lastMenuClickPosition);
            
        } else if (e.getSource() == menuOptionDelete) {
            
            removeAgentDisplayable(agentSelected);
            
        } else if (e.getSource() == menuOptionProperties) {
            
            agentPropertiesDialog.showDialog(agentSelected.getBounds().getLocation(), agentSelected.getAgent());

        } else if (e.getSource() == menuOptionAddSocketAgent) { 

            agents.add(new SocketAgentDisplayable(new SocketAgent("test-socket-agent", 32), 
                            lastMenuClickPosition.x - (AgentDisplayable.width / 2), 
                            lastMenuClickPosition.y - (AgentDisplayable.height / 2)));
            this.repaint();

        } else if (e.getSource() == menuOptionLink) {

            isSelectedAgentTryingToLink = true;
            state = new AgentLinkingState();
            this.repaint();
            
        }
        
    }
   
    /**
     * Performs a repaint of this designer component.
     * 
     * @param g The graphics context we are going to paint to.
     */
    protected void paintComponent(Graphics2D g) {

        // Fill in background.
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        if (showGrid) { // Are we drawing a grid?
        
            g.setColor(gridLineColor);

            for (int x = 0; x < this.getWidth(); x += gridSpacing) { // Draw vertical gridlines.
                g.drawLine(x, 0, x, this.getHeight());
            }
            
            for (int y = 0; y < this.getHeight(); y += gridSpacing) { // Draw horizontal gridlines.
                g.drawLine(0, y, this.getWidth(), y);
            }
            
        }

        if (isSelectedAgentTryingToLink) {    // Are we dragging a link around?
            
            final int x = agentSelected.getBounds().x + (agentSelected.getBounds().width / 2);
            final int y = agentSelected.getBounds().y + (agentSelected.getBounds().height / 2);
            
            g.setColor(Color.black);
            // TODO: NPE when mouse is behind something
            g.drawLine(x, y, this.getMousePosition().x, this.getMousePosition().y);
            
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
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * A class that represents a state that a NetworkDesigner object can be in.
     */
    private abstract class NetworkDesignerState {
        
        protected void handleMouseClicked(MouseEvent e) { }
        protected void handleMouseDragged(MouseEvent e) { }
        protected void handleMouseMoved(MouseEvent e) { }
        protected void handleMousePressed(MouseEvent e) { }
        protected void handleMouseReleased(MouseEvent e) { }
        protected void handleMouseEntered(MouseEvent e) { }
        protected void handleMouseExited(MouseEvent e) { }
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * A class that represents the default state where nothing is really currently 
     * happening. (Nothing selected, nothing getting dragged or nothing linking)
     */
    private class DefaultState extends NetworkDesignerState {

        @Override
        protected void handleMouseClicked(MouseEvent e) {

            if (SwingUtilities.isLeftMouseButton(e)) {
                
                final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
                
                if (agentClicked != null) {
                    
                    agentSelected = agentClicked;
                    state = new AgentSelectedState();
                    NetworkDesigner.this.repaint();

                }
                
            } else if (SwingUtilities.isRightMouseButton(e)) {
                
                lastMenuClickPosition = e.getPoint();
                emptySpacePopup.show(e.getComponent(), e.getX(), e.getY());
                
            }

        }

        @Override
        protected void handleMousePressed(MouseEvent e) {

            if (SwingUtilities.isLeftMouseButton(e)) {
                
                final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
                
                if (agentClicked != null) {
                    
                    agentSelected = agentClicked;
                    state = new AgentDraggingState();
                    selectedComponentDragOffset = new Point(e.getX() - agentSelected.getBounds().x, e.getY() - agentSelected.getBounds().y);
                    NetworkDesigner.this.repaint();

                }               
                
            }

        }

        @Override
        protected void handleMouseReleased(MouseEvent e) {
            
            if (SwingUtilities.isRightMouseButton(e)) {
                
                final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
                
                if (agentClicked != null) {
                    
                    agentSelected = agentClicked;
                    state = new AgentSelectedState();
                    NetworkDesigner.this.repaint();
                    
                    lastMenuClickPosition = e.getPoint();
                    agentSelectedPopup.show(e.getComponent(), e.getX(), e.getY());
                    
                } else {
                    
                    lastMenuClickPosition = e.getPoint();
                    emptySpacePopup.show(e.getComponent(), e.getX(), e.getY());
                    
                }
  
            }

        }

    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * A class that represents a state where an agent has been selected.
     */
    private class AgentSelectedState extends NetworkDesignerState {

        @Override
        protected void handleMouseClicked(MouseEvent e) {

            final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
            
            if (SwingUtilities.isLeftMouseButton(e)) {
                
                if (agentClicked != null) {
                    
                    agentSelected = agentClicked;
                    // We are already in the selected state so no need to change
                    NetworkDesigner.this.repaint();
                    
                } else {
                    
                    agentSelected = null;
                    state = new DefaultState();
                    NetworkDesigner.this.repaint();
                    
                }
                
            } else if (SwingUtilities.isRightMouseButton(e)) {
                
                if (agentClicked != null) {
                    
                    agentSelected = agentClicked;
                    // We are already in the selected state so no need to change
                    NetworkDesigner.this.repaint();
                    
                    lastMenuClickPosition = e.getPoint();
                    agentSelectedPopup.show(e.getComponent(), e.getX(), e.getY());
                    
                } else {
                    
                    agentSelected = null;
                    state = new DefaultState();
                    NetworkDesigner.this.repaint();
                    
                    lastMenuClickPosition = e.getPoint();
                    emptySpacePopup.show(e.getComponent(), e.getX(), e.getY());
                    
                }
                
            }

        }

        @Override
        protected void handleMousePressed(MouseEvent e) {
            
            if (SwingUtilities.isLeftMouseButton(e)) {
                
                final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
                
                if (agentClicked != null) {
                    
                    agentSelected = agentClicked;
                    state = new AgentDraggingState();
                    selectedComponentDragOffset = new Point(e.getX() - agentSelected.getBounds().x, e.getY() - agentSelected.getBounds().y);
                    NetworkDesigner.this.repaint();

                }               
                
            }
            
        }

        @Override
        protected void handleMouseReleased(MouseEvent e) {

            handleMouseClicked(e);

        }

    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * A class that represents a state where an agent is currently being dragged
     * in the designer.
     */
    private class AgentDraggingState extends NetworkDesignerState {

        @Override
        protected void handleMouseDragged(MouseEvent e) {
            
            int newX = (e.getX() - selectedComponentDragOffset.x);
            int newY = (e.getY() - selectedComponentDragOffset.y);

            if (showGrid) {
                newX = newX - (newX % gridSpacing);
                newY = newY - (newY % gridSpacing);
            }

            agentSelected.getBounds().x = newX;
            agentSelected.getBounds().y = newY;
            NetworkDesigner.this.repaint();

        }
        
        @Override
        protected void handleMouseReleased(MouseEvent e) {
            
            state = new AgentSelectedState();
            
        }

    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * A class that represents a state where there is a link attached to the
     * selected agent with the other end attached to the mouse cursor position.
     */
    private class AgentLinkingState extends NetworkDesignerState {

        @Override
        protected void handleMouseClicked(MouseEvent e) {
            
            if (SwingUtilities.isLeftMouseButton(e)) {
                
                final AgentDisplayable agentClicked = getAgentAt(e.getPoint());
                
                if (agentClicked != null) {
                    
                    agentLinks.add(new AgentLink(agentSelected, agentClicked));
                    isSelectedAgentTryingToLink = false;
                    agentSelected = agentClicked;
                    state = new AgentSelectedState();
                    NetworkDesigner.this.repaint();

                } else {
                    
                    agentSelected = null;
                    isSelectedAgentTryingToLink = false;
                    state = new AgentSelectedState();
                    NetworkDesigner.this.repaint();
                    
                }
                
            }

        }

        @Override
        protected void handleMouseDragged(MouseEvent e) {

            NetworkDesigner.this.repaint();
            
        }

        @Override
        protected void handleMouseMoved(MouseEvent e) {

            NetworkDesigner.this.repaint();
            
        }

        @Override
        protected void handleMousePressed(MouseEvent e) {

            handleMouseClicked(e);
            
        }

    }

}
