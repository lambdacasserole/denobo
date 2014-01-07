package denobo.centralcommand.designer;

import denobo.centralcommand.designer.dialogs.AgentPropertiesDialog;
import denobo.centralcommand.designer.dialogs.CreateAgentDialog;
import denobo.Agent;
import denobo.centralcommand.designer.dialogs.AgentConnectionsDialog;
import denobo.socket.SocketAgent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
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
    private final JMenuItem menuOptionCreateAgent;
    
    // Menu for right-clicking on a agent
    private final JPopupMenu agentSelectedPopup;
    private final JMenuItem menuOptionLink;
    private final JMenuItem menuOptionsConnections;
    private final JMenuItem menuOptionProperties;
    private final JMenuItem menuOptionMonitor;
    private final JMenuItem menuOptionDebugWindow;
    private final JMenuItem menuOptionDelete;
    
    // Dialogs
    private final CreateAgentDialog createAgentDialog;
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
        
        
//        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteSelectedAgent");
//        this.getActionMap().put("deleteSelectedAgent", new AbstractAction() {
//
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                removeSelectedAgent();
//            }
//            
//        });
//        
        
        
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
        
        menuOptionCreateAgent = new JMenuItem("Create Agent");
        menuOptionCreateAgent.addActionListener(this);
        emptySpacePopup.add(menuOptionCreateAgent);
        
        
        
        
        createAgentDialog = new CreateAgentDialog();
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
     * Returns whether two AgentDisplayable's are already linked in the designer.
     * 
     * @param agent1    the first agent
     * @param agent2    the second agent
     * @return          true if they are already linked, otherwise false
     */
    private boolean linkAlreadyExistsBetween(AgentDisplayable agent1, AgentDisplayable agent2) {
        
        for (AgentLink currentAgentLink : agentLinks) {
            if (currentAgentLink.contains(agent1, agent2)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Links two AgentDisplayable instances together in this NetworkDesigner.
     * <p>
     * If the two agents are the same or the two agents are already linked, this
     * method will not link them.
     * 
     * @param agent1 the first agent to link
     * @param agent2 the second agent to link
     */
    private void linkAgents(AgentDisplayable agent1, AgentDisplayable agent2) {
        
        if ((agent1 == agent2) || (linkAlreadyExistsBetween(agent1, agent2))) {
            return;
        }
        
        final AgentLink link = new AgentLink(agent1, agent2);
        agentLinks.add(link);
        
        for (DesignerEventListener currentListener : designerEventListeners) {
            currentListener.linkCreated(link);
        }
        
    }
    
    /**
     * Shutdown and removes the specified AgentDisplayable from the network.
     * 
     * @param agent The agent to shutdown and remove.
     */
    public void removeAgent(AgentDisplayable agent) {
        
        agent.getDebugWindow().dispose();
        agent.getMonitorDialog().hide();
        
        agent.getAgent().shutdown();
        
        agents.remove(agent);
        removeAnyLinksContaining(agent);
        
        if (agentSelected == agent) {
            clearSelection();
        }
        
        this.repaint();
        
        for (DesignerEventListener currentListener : designerEventListeners) {
            currentListener.agentDeleted(agent);
        }
        
    }
    
    /**
     * Shutdown and removes the current selected agent if one is selected.
     */
    public void removeSelectedAgent() {
        
        if (agentSelected != null) {
            removeAgent(agentSelected);
        }
        
    }
    
    /**
     * Removes any AgentLink's containing the specified agent.
     * 
     * @param agent The agent to remove any links to.
     */
    public void removeAnyLinksContaining(AgentDisplayable agent) {

        final Iterator<AgentLink> iter = agentLinks.iterator();
        while (iter.hasNext()) {
            
            final AgentLink currentAgentLink = iter.next();
            if (currentAgentLink.contains(agent)) {
                
                currentAgentLink.breakLink();
                iter.remove();
                
                for (DesignerEventListener currentListener : designerEventListeners) {
                    currentListener.linkDeleted(currentAgentLink);
                }
                
            }
            
        }

        this.repaint();

    }
    
    /**
     * Removes any AgentLink's between the specified two agents.
     * 
     * @param agent1 The first agent.
     * @param agent2 The second agent.
     */
    public void removeAnyLinksContaining(AgentDisplayable agent1, AgentDisplayable agent2) {
        
        final Iterator<AgentLink> iter = agentLinks.iterator();
        while (iter.hasNext()) {
            
            final AgentLink currentAgentLink = iter.next();
            if (currentAgentLink.contains(agent1, agent2)) {
                
                currentAgentLink.breakLink();
                iter.remove();
                
                for (DesignerEventListener currentListener : designerEventListeners) {
                    currentListener.linkDeleted(currentAgentLink);
                }
                
            }
            
        }

        this.repaint();

    }
    
    /**
     * Set's the specified agent as the one selected.
     * 
     * @param agent The agent to set as the selected agent.
     */
    public void selectAgent(AgentDisplayable agent) {
        
        agentSelected = agent;
        state = new AgentSelectedState();
        this.repaint();
        
        for (DesignerEventListener currentListener : designerEventListeners) {
            currentListener.agentSelected(agent);
        }
        
    }
    
    /** 
     * Clears the current selected agent so that nothing is selected.
     */
    public void clearSelection() {

        agentSelected = null;
        state = new DefaultState();
        this.repaint();
        
        for (DesignerEventListener currentListener : designerEventListeners) {
            currentListener.selectionCleared();
        }
        
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
        
        if (e.getSource() == menuOptionCreateAgent) {
            
            final Agent agentToAdd = createAgentDialog.showDialog(lastMenuClickPosition);
            if (agentToAdd != null) {

                if (agentToAdd instanceof SocketAgent) {
                          
                    agents.add(new SocketAgentDisplayable(agentToAdd, 
                                lastMenuClickPosition.x - (AgentDisplayable.width / 2), 
                                lastMenuClickPosition.y - (AgentDisplayable.height / 2)));
                
                } else {
                    
                    agents.add(new AgentDisplayable(agentToAdd, 
                                lastMenuClickPosition.x - (AgentDisplayable.width / 2), 
                                lastMenuClickPosition.y - (AgentDisplayable.height / 2)));

                }

                this.repaint();

            }
            
        } else if (e.getSource() == menuOptionsConnections) {
            
            agentConnectionsDialog.showDialog(agentSelected.getBounds().getLocation(), agentSelected);

        } else if (e.getSource() == menuOptionMonitor) {
            
            agentSelected.getMonitorDialog().show(agentSelected.getBounds().getLocation());
            
        } else if (e.getSource() == menuOptionDebugWindow) {
                    
            agentSelected.getDebugWindow().showAt(lastMenuClickPosition);
            
        } else if (e.getSource() == menuOptionDelete) {
            
            removeAgent(agentSelected);
            
        } else if (e.getSource() == menuOptionProperties) {
            
            agentPropertiesDialog.showDialog(agentSelected.getBounds().getLocation(), agentSelected.getAgent());

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
            if (currentAgent != agentSelected) {
                currentAgent.draw(g);
            }
        }
        
        if (agentSelected != null) { 
            
            agentSelected.draw(g);
            
            // Dotted bounding box on selected agent.
            final Rectangle agentBounds = agentSelected.getBounds();
            g.setColor(Color.WHITE);
            g.fillRect((int) agentBounds.getX() - 10, (int) agentBounds.getY() - 10, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRect((int) agentBounds.getX() - 10, (int) agentBounds.getY() - 10, 10, 10);
            g.setColor(selectionBoundingBoxColor);
            g.setStroke(selectionBoundingBoxStroke);
            g.draw(agentBounds);

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
                    selectAgent(agentClicked);
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
                    
                    selectAgent(agentClicked);

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
                    selectAgent(agentClicked);
                } else {
                    clearSelection();
                }
                
            } else if (SwingUtilities.isRightMouseButton(e)) {
                
                if (agentClicked != null) {
                    
                    selectAgent(agentClicked);
                    
                    lastMenuClickPosition = e.getPoint();
                    agentSelectedPopup.show(e.getComponent(), e.getX(), e.getY());
                    
                } else {
                    
                    clearSelection();
                    
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
            
            for (DesignerEventListener currentListener : designerEventListeners) {
                currentListener.agentMoved(agentSelected);
            }

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
                    
                    linkAgents(agentSelected, agentClicked);
                    isSelectedAgentTryingToLink = false;
                    selectAgent(agentClicked);

                } else {
                    
                    isSelectedAgentTryingToLink = false;
                    clearSelection();
                    
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
