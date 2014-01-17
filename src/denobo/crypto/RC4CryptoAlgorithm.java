package denobo.crypto;

/**
 * Provides RC4 stream cipher encryption/decryption.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RC4CryptoAlgorithm implements CryptoAlgorithm {

    /**
     * Holds the state used for the pseudo-random number generator.
     */
    private final int[] state;
    
    /**
     * Holds the key used to initialise the state.
     */
    private int[] key;

    /**
     * Holds the number of bytes to drop from the beginning of the key stream.
     */
    protected int drop;
    
    /**
     * Initialises a new instance of an RC4 encryption algorithm.
     */
    public RC4CryptoAlgorithm() {
        state = new int[256];
        drop = 0;
    }
    
    /**
     * Swaps the integers at two indices in the state permutation.
     * 
     * @param i the index of the first integer
     * @param j the index of the second integer
     */
    private void swap(int i, int j) {
        final int temp = state[i];
        state[i] = state[j];
        state[j] = temp;
    }
    
    /**
     * The key-scheduling algorithm initialises the state according to the key.
     */
    private void ksa() {
        
        // Initialise with bytes 0-255.
        for (int i = 0; i < state.length; i++) {
            state[i] = i;
        }
        
        // Permute the state, mixing in the key.
        int j = 0;
        for (int i = 0; i < state.length; i++) {
            j = (j + state[i] + key[i % key.length]) % 256;
            swap(i, j);
        }
        
    }
    
    /**
     * The pseudo-random generation algorithm fills a buffer array with a 
     * pseudo-random key stream.
     * 
     * @param buffer    the array to fill with the key stream
     */
    private void prga(int[] buffer) {
        
        // Fill buffer with pseudo-random key stream.
        int i = 0;
        int j = 0;
        for (int c = 0; c < buffer.length; c++) {
            i = (i + 1) % 256;
            j = (j + state[i]) % 256;
            swap(i, j);
            buffer[c] = state[(state[i] + state[j]) % 256];
        }
        
    }
    
    /**
     * Sets the key the algorithm will use to encrypt/decrypt data.
     * 
     * @param key   the key to be used to encrypt/decrypt data
     */
    public void setKey(int[] key) {
        this.key = key;
    }
   
    /**
     * Gets the key the algorithm is using to encrypt/decrypt data.
     * 
     * @return  the key being used to encrypt/decrypt data
     */
    public int[] getKey() {
        return key;
    }
    
    // TODO: This should maybe not be synchronized at this level
    @Override
    public synchronized byte[] encrypt(byte[] plaintext) {

        // Key scheduling algorithm
        ksa();
        
        // Drop bytes.
        final int[] dropArr = new int[drop];
        prga(dropArr);
        
        // Get key stream.
        final int[] keystream = new int[plaintext.length];
        prga(keystream);
        
        // Encrypt data.
        final byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ keystream[i]);
        }
        
        return ciphertext;     
        
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        
        // This encryption is invertible.
        return encrypt(ciphertext);
        
    }
    
}
