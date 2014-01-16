package denobo.compression.huffman;

/**
 * Represents a Huffman tree of frequencies.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class FrequencyTree extends Node {

    /**
     * Keep references to tree leaves for quick access. We won't have to search
     * for them again if we keep them here.
     */
    private final Node[] leaves;
    
    /**
     * Initialises a new instance of a Huffman tree.
     * 
     * @param leaves    an array of all leaf (data) nodes on the tree
     * @param zero      the tree's zero child
     * @param one       the tree's one child
     */
    private FrequencyTree(Node[] leaves, Node zero, Node one) {
        
        // This node is the root, it has a frequency of 1.
        super(1, zero, one);
        
        // Keep references to leaves for quick access.
        this.leaves = leaves;
        
    }
    
    /**
     * Gets an array of the leaf (data) nodes in the tree.
     * 
     * @return  an array of the leaf (data) nodes in the tree.
     */
    public Node[] getLeaves() {
        return leaves;
    }
    
    /**
     * Generates a new Huffman tree from a set of frequencies.
     * 
     * @param freqs the set of frequencies
     * @return      a Huffman tree optimally encoding the specified frequencies
     */
    public static FrequencyTree fromFrequencySet(ByteFrequencySet freqs) {
       
        // Initialise data nodes.
        final Node[] leaves = new Node[freqs.getSize()];
        for (int i = 0; i < leaves.length; i++) {
            leaves[i] = new Node(freqs.getUnsignedByteFrequency(i), i);
        }
        
        // Combine most frequent until we only have two nodes left.
        Node[] nodes = new Node[leaves.length];
        System.arraycopy(leaves, 0, nodes, 0, nodes.length);
        while (nodes.length > 2) { 
            nodes = Node.combineLeastFrequent(nodes); 
        }
        
        // Add those two nodes to root.
        return new FrequencyTree(leaves, nodes[0], nodes[1]);
        
    }
    
}
