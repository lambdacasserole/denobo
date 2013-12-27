package denobo.centralcommand;

import denobo.compression.BasicCompressor;
import denobo.compression.Compressor;
import denobo.compression.FileUtils;
import denobo.compression.huffman.ByteFrequencySet;
import denobo.compression.huffman.FrequencyTree;
import denobo.compression.huffman.PrefixCodeTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Saul Johnson
 */
public class HuffmanTestTab extends JPanel implements ActionListener {
    
    private final JButton openFileButton;
    private final JTextField openFilePath;
    
    private final JButton encodeButton;
    private final JButton decodeButton;
    
    private final FrequencyTableModel tableModel;
    
    private final ByteFrequencyGraphPanel beforeCompressionGraph;
    private final ByteFrequencyGraphPanel afterCompressionGraph;
    
    private static int tabNumber = 0;
        
    public static int nextTabNumber() {
        return tabNumber++;
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public HuffmanTestTab() {
        
        super();
        
        this.setLayout(new BorderLayout());
        
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setMinimumSize(new Dimension(800, 600));
        
        final JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        contentPanel.add(westPanel, BorderLayout.WEST);
        
        final JPanel frequencyGraphHolder = new JPanel();
        frequencyGraphHolder.setBorder(new TitledBorder("Byte Frequencies"));
        frequencyGraphHolder.setLayout(new BorderLayout());
        frequencyGraphHolder.setBackground(Color.WHITE);
        westPanel.add(frequencyGraphHolder, BorderLayout.NORTH);
        
        final JPanel frequencyTablePanel = new JPanel();
        frequencyTablePanel.setBorder(new TitledBorder("Prefix Code Table"));
        frequencyTablePanel.setLayout(new BorderLayout());
        frequencyTablePanel.setBackground(Color.WHITE);
        westPanel.add(frequencyTablePanel, BorderLayout.CENTER);

        final JPanel testButtonPanel = new JPanel();
        testButtonPanel.setLayout(null);
        testButtonPanel.setBorder(new TitledBorder("Test Controls"));
        testButtonPanel.setPreferredSize(new Dimension(0, 100));
        testButtonPanel.setBackground(Color.WHITE);
        westPanel.add(testButtonPanel, BorderLayout.SOUTH);
        
        final JLabel openFileLabel = new JLabel("File:");
        openFileLabel.setBounds(25, 25, 40, 25);
        testButtonPanel.add(openFileLabel);
        
        openFilePath = new JTextField();
        openFilePath.setBounds(65, 25, 320, 25);
        testButtonPanel.add(openFilePath);
        
        openFileButton = new JButton("Browse...");
        openFileButton.setBounds(400, 25, 100, 25);
        openFileButton.addActionListener(this);
        testButtonPanel.add(openFileButton);
        
        encodeButton = new JButton("Encode...");
        encodeButton.setBounds(290, 60, 100, 25);
        encodeButton.addActionListener(this);
        testButtonPanel.add(encodeButton);
        
        decodeButton = new JButton("Decode...");
        decodeButton.setBounds(400, 60, 100, 25);
        decodeButton.addActionListener(this);
        testButtonPanel.add(decodeButton);
        
        tableModel = new FrequencyTableModel();
        final JTable frequencyTableView = new JTable(tableModel);
        final JScrollPane tableScroller = new JScrollPane(frequencyTableView);
        frequencyTableView.setFillsViewportHeight(true);
        frequencyTablePanel.add(tableScroller, BorderLayout.CENTER);
        
        beforeCompressionGraph = new ByteFrequencyGraphPanel();
        beforeCompressionGraph.setVerticalLayout(true);
        beforeCompressionGraph.setPreferredSize(new Dimension(256, 256));
        frequencyGraphHolder.add(beforeCompressionGraph, BorderLayout.WEST);
        
        afterCompressionGraph = new ByteFrequencyGraphPanel();
        afterCompressionGraph.setVerticalLayout(true);
        afterCompressionGraph.setForegroundColor(Color.RED);
        afterCompressionGraph.setPreferredSize(new Dimension(256, 256));
        frequencyGraphHolder.add(afterCompressionGraph, BorderLayout.CENTER);
        
        contentPanel.add(new JPanel(), BorderLayout.CENTER);
        
        final JScrollPane contentScroller = new JScrollPane(contentPanel);
        this.add(contentScroller, BorderLayout.CENTER);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == openFileButton) {
            
            // Browse for file.
            final JFileChooser openBox = new JFileChooser();
            final int result = openBox.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                
                // Put file path into text box.
                final File file = openBox.getSelectedFile();
                openFilePath.setText(file.getAbsolutePath());
                
            } 
            
        } else if (e.getSource() == encodeButton) {
            
            // Browse for file.
            final JFileChooser saveBox = new JFileChooser();
            final int result = saveBox.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                
                final File destFile = saveBox.getSelectedFile();
                final File sourceFile = new File(openFilePath.getText());
                
                beforeCompressionGraph.setFile(sourceFile);
                
                final ByteFrequencySet frequencies = ByteFrequencySet.fromFile(sourceFile);
                final FrequencyTree tree = FrequencyTree.fromFrequencySet(frequencies);
                final PrefixCodeTable table = new PrefixCodeTable(tree);
                
                // Clear table.
                while (tableModel.getRowCount() > 0) { tableModel.removeRow(0); }
                
                // Populate table.
                for (int i = 0; i < 256; i++) {
                    tableModel.addRow(new Object[] {i, frequencies.getUnsignedByteFrequency(i), table.translateSymbol(i).toBitString()});
                }
                
                // Compress data.
                final Compressor compressor = new BasicCompressor();
                final byte[] input = FileUtils.getFileBytes(sourceFile);
                final byte[] output = compressor.compress(input);
                
                // Save compressed file.
                FileUtils.setFileBytes(destFile, output);
                
                // Visualise frequency change.
                afterCompressionGraph.setFile(destFile);
                
            } 
            
        } else if (e.getSource() == decodeButton) {
                
            // Browse for file.
            final JFileChooser saveBox = new JFileChooser();
            final int result = saveBox.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                
                final File destFile = saveBox.getSelectedFile();
                final File sourceFile = new File(openFilePath.getText());
                
                beforeCompressionGraph.setFile(sourceFile);
                
                final byte[] sourceFileBytes = FileUtils.getFileBytes(sourceFile);
//                final PrefixCodeTable table = PrefixCodeTable.deserialize(sourceFileBytes, 8);
                
                // Clear table.
                while (tableModel.getRowCount() > 0) { tableModel.removeRow(0); }
                
                // Populate table.
//                for (int i = 0; i < 256; i++) {
//                    tableModel.addRow(new Object[] {i, "-", table.translateSymbol(i).toBitString()});
//                }
                
                // Compress data.
                final Compressor compressor = new BasicCompressor();
                final byte[] input = FileUtils.getFileBytes(sourceFile);
                final byte[] output = compressor.decompress(input);
                
                // Save compressed file.
                FileUtils.setFileBytes(destFile, output);
                
                // Visualise frequency change.
                afterCompressionGraph.setFile(destFile);
                
            }
        }
    }
}
