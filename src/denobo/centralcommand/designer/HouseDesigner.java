//package denobo.centralcommand.designer;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.util.ArrayList;
//import javax.swing.*;
//
///**
// * An drag-and-drop control for adding, removing and editing appliances.
// * 
// * @author      Saul Johnson <M2082166@tees.ac.uk>
// * @version     1.0
// * @since       2013-03-17
// */
//public class HouseDesigner extends JComponent implements MouseListener, MouseMotionListener {
//
//    // Different directional nudge codes.
//    public static final int NUDGE_UP = 0;
//    public static final int NUDGE_DOWN = 1;
//    public static final int NUDGE_LEFT = 2;
//    public static final int NUDGE_RIGHT = 3;
//
//    public static final int MAX_COMPONENT_COUNT = 16; // Maximum number of components allowed in designer.
//    
//    // Gridline constants.
//    private final int gridSpacing = 10;
//    private final Color gridLineColor = new Color(0, 0, 0, 25);
//    private final float[] gridLineDash = new float[] {2.0f};
//    private final BasicStroke gridLineStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, gridLineDash, 0.0f);
//    
//    // Selection line constants.
//    private final int selectionTagSize = 10;
//    private final Color selectionBoundingBoxColor = new Color(0, 0, 0, 100);
//    private final float[] selectionBoundingBoxDash = new float[] {1.0f};
//    private final BasicStroke selectionBoundingBoxStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, selectionBoundingBoxDash, 0.0f);
//        
//    private PlaceableComponent[] components; // Array of components in designer.
//    private int selectedComponentIndex; // Index of currently selected component.
//    private boolean isSelectedComponentDragging; // Whether or not the selected component is currently being dragged (repositioned).
//    private Point selectedComponentDragOffset; // The offset of the cursor relative to the initial click on a component before dragging.
//    
//    private boolean showGrid = true; // Whether or not the grid (and snap-to-grid features) are currently enabled.
//    private ImageIcon currentBackdrop; // The current background image (backdrop) on which components are laid.
//
//    private ArrayList<DesignerEventListener> designerEventListeners; // Event listners for house design events.
//    
//    
//        
//    /**
//     * Initialises a new instance of a visual house appliance positioning/design component.
//     */
//    public HouseDesigner() {
//        
//        // Create custom observer array.
//        designerEventListeners = new ArrayList<DesignerEventListener>();
//        
//        // Initialise class-level variables.
//        components = new PlaceableComponent[MAX_COMPONENT_COUNT];
//        setSelectedComponentIndex(-1);
//        isSelectedComponentDragging = false;
//        currentBackdrop = null;
//        
//        // Register listeners.
//        this.addMouseListener(this);
//        this.addMouseMotionListener(this);
//        
//    }
//    
//    /**
//     * Gets the selected component in the designer. If no component is selected, returns null.
//     */
//    public PlaceableComponent getSelectedComponent() {
//        
//        return (isComponentSelected() ? components[selectedComponentIndex] : null);
//        
//    }    
//    
//    /**
//     * Sets the index of the selected component in the designer.
//     */
//    private void setSelectedComponentIndex(int index) {
//        
//        selectedComponentIndex = index;
//        
//        if(index == -1) { // Do we have no selected component?
//        
//            for(int e = 0; e < designerEventListeners.size(); e++) { // For each event listener, raise the selectionCleared event.
//                
//                designerEventListeners.get(e).selectionCleared(new DesignerEvent(null, -1));
//                
//            }
//        
//        }
//        else {
//            
//            for(int e = 0; e < designerEventListeners.size(); e++) { // For each event listener, raise the componentSelected event.
//                
//                designerEventListeners.get(e).componentSelected(new DesignerEvent(components[index], index));
//                
//            }
//                
//        }
//        
//    }    
//    
//    /**
//     * Gets whether or not a component is currently selected in the designer.
//     */
//    public boolean isComponentSelected() {
//        
//        return (selectedComponentIndex > -1 && selectedComponentIndex < components.length && components[selectedComponentIndex] != null);
//        
//    }    
//    
//    /**
//     * Sets whether or not the grid (and snap-to-grid features) are currently enabled in the designer.
//     *
//     * @param showGrid Whether or not to show the grid and enable snap-to-grid features.
//     */
//    public void setShowGrid(boolean showGrid) {
//    
//        this.showGrid = showGrid;
//        
//    }
//    
//    /**
//     * Gets whether or not the grid (and snap-to-grid features) are currently enabled in the designer.
//     */
//    public boolean getShowGrid() {
//    
//        return showGrid;
//        
//    }
//    
//    /**
//     * Gets the current background image (backdrop) on which the components are laid.
//     */
//    public ImageIcon getBackdrop() {
//        
//        return currentBackdrop;
//        
//    }
//    
//    /**
//     * Sets the current background image (backdrop) on which the components are laid.
//     *
//     * @param backdrop The ImageIcon to use as the new backdrop.
//     */
//    public void setBackdrop(ImageIcon backdrop) {
//        
//        currentBackdrop = backdrop;
//        this.repaint();
//        
//    }
//    
//    /**
//     * Loads a background image (backdrop) from the specified file and displays it in the designer.
//     *
//     * @param filepath The filepath of the image file to load.
//     */
//    public void loadBackdrop(String filepath) {
//    
//        this.setBackdrop(new ImageIcon(filepath));
//        
//    }
//    
//    /**
//     * Clears the backdrop from the designer.
//     */
//    public void clearBackdrop() {
//    
//        currentBackdrop = null;
//        this.repaint();
//    
//    }
//    
//    /**
//     * Adds a listener class to the designer, which recieves all design events triggered by the user.
//     *
//     * @param listener The listener object to add.
//     */
//    public void addDesignerEventListener(DesignerEventListener listener) {
//        
//        designerEventListeners.add(listener);
//        
//    }
//    
//    /**
//     * Clears the current design and backdrop from the control.
//     */
//    public void clearDesign() {
//        
//        components = new PlaceableComponent[MAX_COMPONENT_COUNT]; // Clear components.
//        selectComponentAtLocation(0, 0); // Clear selection.
//        
//        currentBackdrop = null; // Remove backdrop.
//        
//        this.repaint(); // Repaint to show cleared designer.
//        
//    }
//    
//    /**
//     * Selects the topmost component at the specified coordinates in the designer.
//     *
//     * @param x The x-coordinate of the component.
//     * @param y The y-coordinate of the component.
//     */
//    private void selectComponentAtLocation(int x, int y) {
//        
//        setSelectedComponentIndex(-1); // Set selection index to -1 (no selection).
//        
//        for(int i = 0; i < components.length; i++) { // For each component in component array.
//        
//            PlaceableComponent c = components[i];
//            if(c == null) {
//            
//                break; // Reached end of component array.
//                
//            }
//            
//            if(x >= c.getX() && y >= c.getY() && x < c.getX() + c.getWidth() && y < c.getY() + c.getHeight()) { // Component bounding box checking.
//            
//                setSelectedComponentIndex(i); // Given coordinate inside component bounding box. Select component.
//                
//            } 
//        
//        }
//        
//        this.repaint(); // Repaint panel to show new selection.
//        
//    }
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
//    
//    /**
//     * Deletes the component at the specified index in the component array.
//     *
//     * @param index The index of the component to delete.
//     */
//    public void deleteComponentAt(int index) {
//    
//        if(index >= 0 && index < components.length) {
//    
//            PlaceableComponent componentToDelete = components[index];
//    
//            for(int e = 0; e < designerEventListeners.size(); e++) { // Raise componentDeleting events on all listeners.
//            
//                boolean confirm = designerEventListeners.get(e).componentDeleting(new DesignerEvent(componentToDelete, index));
//                
//                if(!confirm) {
//                
//                    return; // If any componentDeleting event returned false, cancel deletion.
//                    
//                }
//                
//            }
//    
//            components[index] = null; // Nullify component.
//            for(int i = index + 1; i < components.length; i++) { // Shift other components down in array.
//            
//                components[i - 1] = components[i];
//                
//            }
//            
//            this.repaint(); // Repaint to show component deleted.
//            
//            for(int e = 0; e < designerEventListeners.size(); e++) { // Raise componentDeleted events on all listeners.
//            
//                designerEventListeners.get(e).componentDeleted(new DesignerEvent(componentToDelete, index));
//                
//            }
//            
//            if(!isComponentSelected()) {
//                setSelectedComponentIndex(-1); // Selected component has been deleted, so revert selection to -1.
//            }
//            
//        }
//        
//    }
//    
//    /**
//     * Deletes the currently selected component, if any.
//     */
//    public boolean deleteSelectedComponent() {
//    
//        if(getSelectedComponent() != null) {
//        
//            deleteComponentAt(selectedComponentIndex);
//            return true;
//            
//        }
//        
//        return false;
//    
//    }
//    
//    /**
//     * Adds a component to the designer.
//     *
//     * @param component The component to be added.
//     */
//    public boolean addComponent(PlaceableComponent component) {
//         
//        // Find first empty index in component array.
//        int i = 0;
//        while(components[i] != null) {
//            
//            i++;
//            
//            if(i == components.length) { // Array full.
//            
//                return false; // Component not added.
//                
//            }
//            
//        }
//        
//        components[i] = component; // Add component to component array.
//        
//        for(int e = 0; e < designerEventListeners.size(); e++) { // For each event listener, raise the componentAdded event.
//        
//            designerEventListeners.get(e).componentAdded(new DesignerEvent(components[i], i));
//            
//        }
//        
//        this.repaint(); // Repaint panel to show new component.
//        
//        return true; // Component added.
//        
//    }
//    
//    /**
//     * Saves the design to a file.
//     *
//     * @param outputPath The file to save the design to.
//     */
//    public void saveFile(String outputPath) throws IOException {
//    
//        SaveFile fileToWrite = new SaveFile(components, currentBackdrop); // Place component array and backdrop into custom object.
//    
//        FileOutputStream fileOut = new FileOutputStream(outputPath);
//        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
//        
//        objOut.writeObject(fileToWrite); // Serialise to file.
//        
//        objOut.close();
//        fileOut.close();
//    
//    }
//    
//    /**
//     * Loads a design from a file.
//     *
//     * @param inputPath The path of the file to read from.
//     */
//    public void loadFile(String inputPath) throws IOException, ClassNotFoundException {
//            
//        FileInputStream fileIn = new FileInputStream(inputPath);
//        ObjectInputStream objIn = new ObjectInputStream(fileIn);
//        
//        SaveFile saveFileData = (SaveFile)objIn.readObject(); // Deserialise custom object containing backdrop and component array.
//        components = saveFileData.getComponents();
//        currentBackdrop = saveFileData.getBackdrop();
//                
//        this.repaint(); // Repaint to show loaded components.
//            
//    }
//     
//    /* 
//     * Begin MouseListener events.
//     */
//     
//    public void mouseExited(MouseEvent m) {
//    
//        // Empty method.
//    
//    }
//    
//    public void mouseEntered(MouseEvent m) {
//    
//        // Empty method.
//    
//    }
//    
//    public void mouseReleased(MouseEvent m) {
//    
//        isSelectedComponentDragging = false; // Mouse released, component no longer dragging.
//        
//    }
//    
//    public void mousePressed(MouseEvent m) {
//    
//        switch(m.getButton()) {
//
//            case MouseEvent.BUTTON1 : // On left-click.
//            
//                selectComponentAtLocation(m.getX(), m.getY()); // Select component at the mouse's location (if any).
//                isSelectedComponentDragging = isComponentSelected(); // If a component was selected, we are now dragging that component.
//                
//                if(isSelectedComponentDragging) { // If we're currently dragging a component.
//                
//                    PlaceableComponent c = getSelectedComponent(); 
//                    selectedComponentDragOffset = new Point(m.getX() - c.getX(), m.getY() - c.getY()); // Record initial offset of cursor from component top-left.
//                    
//                }
//                
//                break;
//        
//        }
//        
//    }
//    
//    public void mouseClicked(MouseEvent m) {
//    
//        switch(m.getButton()) {
//
//            case MouseEvent.BUTTON3 : // On right-click.
//            
//                selectComponentAtLocation(m.getX(), m.getY()); // Select right-clicked component.
//                if(isComponentSelected()) {
//                
//                    for(int e = 0; e < designerEventListeners.size(); e++) { // For each event listener, raise the componentRightClicked event.
//        
//                        designerEventListeners.get(e).componentRightClicked(new DesignerEvent(getSelectedComponent(), selectedComponentIndex));
//                    
//                    }
//                    
//                }
//                
//                break;
//                
//            case MouseEvent.BUTTON2 : // On middle-click.
//            
//                selectComponentAtLocation(m.getX(), m.getY()); // Select middle-clicked component.
//                deleteSelectedComponent();
//        
//        }
//        
//    }
//    
//    /*
//     * End MouseListener events.
//     */
//    
//    /*
//     * Begin MouseMotionListener events.
//     */
//    
//    public void mouseDragged(MouseEvent m) {
//        
//        if(isSelectedComponentDragging) { // If we're currently dragging a component.
//        
//            int newX = m.getX() - (int)selectedComponentDragOffset.getX(); // Calculate new position of component.
//            int newY = m.getY() - (int)selectedComponentDragOffset.getY();
//            if(showGrid) { // If the grid is shown.
//            
//                newX = newX - (newX % gridSpacing); // Snap component to grid.
//                newY = newY - (newY % gridSpacing);
//                
//            }
//    
//            PlaceableComponent c = getSelectedComponent();
//            c.setLocation(newX, newY); // Reposition component.
//        
//            for(int e = 0; e < designerEventListeners.size(); e++) { // For each event listener, raise the componentMoved event.
//                
//                designerEventListeners.get(e).componentMoved(new DesignerEvent(getSelectedComponent(), selectedComponentIndex));
//                
//            }
//        
//            this.repaint(); // Repaint panel to show changes.
//            
//        }
//                
//    }
//    
//    public void mouseMoved(MouseEvent m) {
//            
//        // Empty method.
//        
//    }
//    
//    /*
//     * End MouseMotionListener events.
//     */
//     
//    protected void paintComponent(Graphics2D g) {
//
//        Stroke defaultStroke = g.getStroke();
//        
//        // Fill in background.
//        g.setColor(this.getBackground());
//        g.fillRect(0, 0, this.getWidth(), this.getHeight());
//        
//        // Paint backdrop in background;
//        if(currentBackdrop != null) {
//        
//            currentBackdrop.paintIcon(this, g, 0, 0);
//            
//        }
//    
//        if(showGrid) { // Are we drawing a grid?
//        
//            g.setColor(gridLineColor);
//            g.setStroke(gridLineStroke);
//
//            for(int x = 0; x < this.getWidth(); x += gridSpacing) { // Draw vertical gridlines.
//            
//                g.drawLine(x, 0, x, this.getHeight());
//            
//            }
//            
//            for(int y = 0; y < this.getHeight(); y += gridSpacing) { // Draw horizontal gridlines.
//            
//                g.drawLine(0, y, this.getWidth(), y);
//            
//            }
//            
//        }
//        
//        for(int i = 0; i < components.length; i++) { // Draw each placable appliance in the house in turn.
//        
//            PlaceableComponent c = components[i]; // Get the placable component from the array. 
//            if(c == null) {
//            
//                break; // Reached a null component. End of the list, so exit.
//                
//            }
//            
//            c.getAppliance().getPicture().paintIcon(this, g, c.getX(), c.getY()); // Draw appliance icon.
//            
//            if(i == selectedComponentIndex) { // If the current appliance is the selected appliance.
//            
//                // Dotted bounding box on selected appliance.
//                g.setColor(selectionBoundingBoxColor);
//                g.setStroke(selectionBoundingBoxStroke);
//                g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
//            
//                // White selection tag on selected appliance.
//                g.setStroke(defaultStroke);    
//                g.setColor(Color.WHITE);
//                g.fillRect(c.getX() - selectionTagSize, c.getY() - selectionTagSize, selectionTagSize, selectionTagSize);
//                g.setColor(Color.BLACK);
//                g.drawRect(c.getX() - selectionTagSize, c.getY() - selectionTagSize, selectionTagSize, selectionTagSize);
//                
//            }
//            
//        }
//        
//    }
//    
//    protected void paintComponent(Graphics g) {
//    
//        super.paintComponent(g);
//        paintComponent((Graphics2D)g); // We need Graphics2D for advanced drawing methods.
//        
//    }
//
//}
