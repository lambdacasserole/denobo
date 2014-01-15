package denobo.compression.huffman;

/**
 * Represents a node in a Huffman tree.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Node {
    
    /**
     * The data component of the node.
     */
    private int data;
    
    /**
     * The frequency component of the node.
     */
    private double frequency;
    
    /**
     * This node's parent.
     */
    private Node parent;
    
    /**
     * The node's child nodes.
     */
    private Node[] children;
   
    /**
     * Initialises a new instance of a Huffman tree node.
     * 
     * @param frequency  the node frequency
     * @param data  the node data
     */
    public Node(double frequency, int data) {
        
        this.frequency = frequency;
        this.data = data;
        
    }
    
    /**
     * Initialises a new instance of a Huffman tree node.
     * 
     * @param frequency  the node frequency
     * @param zero  the node's zero child
     * @param one   the node's one child
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Node(double frequency, Node zero, Node one) {
        
        this(frequency, -1);
        
        children = new Node[2];
        zero.setParent(this);
        children[0] = zero;
        one.setParent(this);
        children[1] = one;
        
    }
    
    /**
     * Gets whether or not the node has children.
     * 
     * @return  true if the node has children, otherwise false
     */
    public boolean hasChildren() {
        return children != null;
    }
    
    /**
     * Gets whether or not the node has a parent.
     * 
     * @return true if the node has a parent, otherwise false
     */
    public boolean hasParent() {
        return parent != null;
    }
    
    /**
     * Gets the data element of the node.
     * 
     * @return  and integer representing the data element of the node
     */
    public int getData() {
        return data;
    }
    
    /**
     * Builds a Huffman encoded {@link BitSequence} from this node upwards to the root.
     * 
     * @param child the child node calling the method
     * @param bits  the {@link BitSequence} to place bits into
     */
    private void buildBitSequence(Node child, BitSequence bits) {
        
        if (child != null) { 
            bits.append(child != children[0]); 
        }
        
        if (hasParent()) { parent.buildBitSequence(this, bits); }
        
    }
    
    /**
     * Builds a Huffman encoded {@link BitSequence} from this node upwards to the root.
     * 
     * @return  a Huffman encoded {@link BitSequence}
     */
    public BitSequence buildBitSequence() {
        
        // Create empty set.
        final BitSequence set = new BitSequence();
        buildBitSequence(null, set);
        
        return BitSequence.reverse(set);
        
    }
    
    /**
     * Gets the frequency element of the node.
     * 
     * @return  a double representing the frequency element of the node
     */
    public double getFrequency() {
        return frequency;
    }
    
    /**
     * Sets the parent of the node.
     * 
     * @param parent    the node to make the parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    /**
     * Gets this tree node and its children represented as an XML string.
     * 
     * @return  an XML string representing this tree node and its children
     */
    public String toXml() {
        
        final StringBuilder sb = new StringBuilder();
        sb.append("<node frequency=\"")
                .append(frequency)
                .append("\" data=\"")
                .append(data)
                .append("\"");
        
        // Append XML for children.
        if (hasChildren()) {
            sb.append(">")
                    .append(children[0].toXml())
                    .append(children[1].toXml())
                    .append("</node>");
        } else {
            sb.append("/>");
        }
        
        return sb.toString();
        
    }
    
    /**
     * Sorts an array of nodes by frequency, least frequent first.
     * 
     * @param nodes the array of nodes to sort.
     */
    public static void sortByFrequency(Node[] nodes) {
        
        // Loop while we're still having to perform swaps.
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < nodes.length - 1; i++) {
                
                // Transpose out-of-order values.
                if (nodes[i].getFrequency() > nodes[i + 1].getFrequency()) {
                    final Node temp = nodes[i];
                    nodes[i] = nodes[i + 1];
                    nodes[i + 1] = temp;
                    swapped = true;
                }
                
            }
        }
        
    }
    
    /**
     * Sorts an array of nodes by frequency and combines its two least frequent members.
     * 
     * @param nodes the array of nodes
     * @return      a sorted node array with the combined node as its first member
     */
    public static Node[] combineLeastFrequent(Node[] nodes) {
       
        // Sort by frequency.
        sortByFrequency(nodes);
        
        // Combine first and second node.
        final Node combinedNode = Node.combine(nodes[0], nodes[1]);
        
        // Create new array with combined node as first member.
        final Node[] newNodeArray = new Node[nodes.length - 1];
        newNodeArray[0] = combinedNode;
        System.arraycopy(nodes, 2, newNodeArray, 1, nodes.length - 2);
        
        return newNodeArray;
        
    }
    
    /**
     * Combines two {@link Node} objects via a parent node containing the sum of their frequencies.
     * 
     * @param zero  the {@link Node} to act as the zero child
     * @param one   the {@link Node} to act as the one child
     * @return      a single {@link Node} with the specified nodes as children
     */
    public static Node combine(Node zero, Node one) {
        return new Node(zero.getFrequency() + one.getFrequency(), zero, one);
    }
    
}
