package denobo.compression.huffman;

/**
 * Represents a table of prefix-codes used in Huffman compression.
 * 
 * @author Saul Johnson
 */
public class PrefixCodeTable {
    
    /**
     * Holds the prefix codes as {@link BitSequence} objects.
     */
    private final BitSequence[] codes;
    
    /**
     * Holds the symbols as integers.
     */
    private final int[] symbols;
    
    /**
     * Initialises a new instance of a prefix code table.
     * 
     * @param tree  the Huffman frequency tree to initialise from
     */
    public PrefixCodeTable(FrequencyTree tree) {
        
        // Get tree leaves.
        final Node[] leaves = tree.getLeaves();
        
        // Initialise mapping arrays.
        codes = new BitSequence[leaves.length];
        symbols = new int[leaves.length];
                
        // Backtrace all leaves to root, calculating prefix codes.
        for (int i = 0; i < leaves.length; i++) {
            codes[i] = leaves[i].buildBitSequence();
            symbols[i] = leaves[i].getData();
        }  
        
        // Sort codes, shortest first.
        sortByCodeLength();
                
    }
    
    /**
     * Initialises a new instance of a prefix code table.
     * 
     * @param symbols   the symbols in the table  
     * @param codes     the prefix codes in the table
     */
    public PrefixCodeTable(int[] symbols, BitSequence[] codes) {
        
        if (symbols.length != codes.length) {
            throw new RuntimeException("Symbol and code arrays must be the same length.");
        }
        
        this.symbols = symbols;
        this.codes = codes;
        
    }
    
    /**
     * Sorts the table by code length, shortest codes first.
     */
    private void sortByCodeLength() {
        
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < symbols.length - 1; i++) {
                if (codes[i].getLength() > codes[i + 1].getLength()) {
                    
                    final BitSequence tempCode = codes[i];
                    codes[i] = codes[i + 1];
                    codes[i + 1] = tempCode;
                    
                    final int tempSymbol = symbols[i];
                    symbols[i] = symbols[i + 1];
                    symbols[i + 1] = tempSymbol;
                    
                    swapped = true;
                }
            }
        } while(swapped);
        
    }
    
    /**
     * Gets the array of codes for the table.
     * 
     * @return  the array of codes
     */
    public BitSequence[] getCodes() {
        return codes;
    }

    /**
     * Gets the array of symbols for the table.
     * 
     * @return  the array of symbols
     */
    public int[] getSymbols() {
        return symbols;
    }
    
    /**
     * Returns the {@link BitSequence} for the given symbol.
     * 
     * @param symbol    the symbol to translate
     * @return          a {@link BitSequence} object for the given symbol
     */
    public BitSequence translateSymbol(int symbol) {
        
        for (int i = 0; i < symbols.length; i++) {
           if (symbols[i] == symbol) {
               return codes[i];
           }
        }       
        return null;
        
    }
  
    /**
     * Returns the symbol for the given prefix code.
     * 
     * @param code  the prefix code to translate
     * @return      a symbol for the given prefix code
     */
    public int translateCode(BitSequence code) {
        
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(code)) {
                return symbols[i];
            }
        }       
        return Integer.MIN_VALUE;
        
    }
    
    /**
     * Gets whether or not this table contains the specified {@link BitSequence}.
     * 
     * @param code  the {@link BitSequence} to seek
     * @return      true if the {@link BitSequence} was found, otherwise false
     */
    public boolean hasCode(BitSequence code) {
        
        for (BitSequence seq : codes) {
            if (seq.equals(code)) {
                return true;
            }
        }   
        return false;
        
    }
    
    /**
     * Appends space characters to a string until it reaches the specified length.
     * 
     * @param str   the string to pad
     * @param len   the length to pad to
     * @return      the string padded to the specified length
     */
    private String padRight(String str, int len) {
        
        final StringBuilder sb = new StringBuilder(str);
        while (sb.length() < len) {
            sb.append(" ");
        }
        return sb.toString();
        
    }
    
    @Override
    public String toString() {
        
        // Return ASCII table.
        final StringBuilder sb = new StringBuilder("Symbol    |Code      \n"
                + "---------------------\n");
        for (int i = 0; i < codes.length; i++) {
            sb.append(padRight(Integer.toString(symbols[i]), 11))
                    .append(codes[i].toBitString())
                    .append(";").append("\n");
        }
        
        return sb.toString();
        
    }

}
