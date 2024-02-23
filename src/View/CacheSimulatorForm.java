package View;

import Model.Cache;
import Model.WritePolicy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import static Model.Instruction.*;
import static Model.TableGenerator.*;

public class CacheSimulatorForm extends JFrame {

    private JComboBox<String> typeComboBox;
    private JButton beginButton;

    public static Cache cache=new Cache();

    int currIndex=0;

    public JComboBox offsetComboBox;

    public JComboBox memorySizeComboBox;

    public static int nrCacheLines; //cache table size

    public static JTable cacheTable=new JTable();

    public static JTable cacheTable1=new JTable(); //cache table for 2-way set associative

    public static DefaultTableModel model1_1=(DefaultTableModel)  cacheTable1.getModel();; //cache table model for 2-way set associative

    public static JTable cacheTable2=new JTable(); //cache table for 4-way set associative

    public static DefaultTableModel model1_2=(DefaultTableModel)  cacheTable2.getModel();; //cache table model for 4-way set associative

    public static JTable cacheTable3=new JTable(); //cache table for 4-way set associative

    public static DefaultTableModel model1_3=(DefaultTableModel)  cacheTable3.getModel();; //cache table model for 4-way set associative

    public static JTable mainMemoryTable=new JTable();

    public static JTable instructionBreakdownTable=new JTable(2,3);

    public static DefaultTableModel model = (DefaultTableModel)  instructionBreakdownTable.getModel();

    public static DefaultTableModel model2=(DefaultTableModel)  mainMemoryTable.getModel();; //main memory table model

    public static DefaultTableModel model1=(DefaultTableModel)  cacheTable.getModel();; //cache table model

    public CacheSimulatorForm() {
        createView();

        setTitle("Cache Memory Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void createView() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Cache memory simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(20, 20, 20, 20);
        panel.add(titleLabel, c);

        JLabel typeLabel = new JLabel("Select type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel.add(typeLabel, c);

        typeComboBox = new JComboBox<>(new String[]{"Direct mapped", "N-Way set associative", "Replacement policy demo"});
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        c.gridx = 1;
        c.gridy = 1;
        panel.add(typeComboBox, c);

        beginButton = new JButton("Begin");
        beginButton.setFont(new Font("Arial", Font.BOLD, 18));
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        panel.add(beginButton, c);

        panel.setBackground(new Color(255, 165, 0));
        titleLabel.setForeground(Color.WHITE);

        beginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(typeComboBox.getSelectedItem().toString().equals("Direct mapped")) {
                    createCacheSimulationForm();
                    clearTableAndSetWhite(cacheTable);
                    clearTableAndSetWhite(mainMemoryTable);
                    setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                }
                else if(typeComboBox.getSelectedItem().toString().equals("Replacement policy demo")){
                    createReplacementFrame();
                }
                else if(typeComboBox.getSelectedItem().toString().equals("N-Way set associative")){
                    selectAssociativity();
                }
            }
        });
    }

    private void createCacheSimulationForm() {
        JFrame simulationFrame = new JFrame("Direct Mapped Cache Simulation");
        simulationFrame.setLayout(null);
        simulationFrame.setSize(1400, 800);

        JLabel cacheSizeLabel = new JLabel("Cache size:");
        cacheSizeLabel.setBounds(20, 20, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeLabel);

        JLabel memorySizeLabel = new JLabel("Memory size:");
        memorySizeLabel.setBounds(20, 50, 100, 20);
        simulationFrame.getContentPane().add(memorySizeLabel);

        JLabel offsetLabel = new JLabel("Offset bits:");
        offsetLabel.setBounds(20, 80, 100, 20);
        simulationFrame.getContentPane().add(offsetLabel);

        JLabel writePolicyLabel = new JLabel("Write policy:");
        writePolicyLabel.setBounds(30, 130, 150, 22);
        writePolicyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(writePolicyLabel);

        JRadioButton writeThroughCheckBox = new JRadioButton("Write through");
        writeThroughCheckBox.setBounds(20, 160, 150, 20);
        simulationFrame.getContentPane().add(writeThroughCheckBox);

        JRadioButton writeBackCheckBox = new JRadioButton("Write back");
        writeBackCheckBox.setBounds(20, 190, 100, 20);
        simulationFrame.getContentPane().add(writeBackCheckBox);

        JButton submitConfigButton = new JButton("Submit");
        submitConfigButton.setBounds(135, 190, 85, 30);
        simulationFrame.getContentPane().add(submitConfigButton);


        JComboBox cacheSizeComboBox = new JComboBox<>(new String[]{"2","4","8","16","32", "64"});
        cacheSizeComboBox.setBounds(120, 20, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeComboBox);

        cacheSizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nrCacheLines = Integer.parseInt(cacheSizeComboBox.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int logLines= (int) (nrCacheLines/Math.pow(2.0,offset));
                model1.setRowCount(logLines);
                for (int i=0;i<logLines;i++){
                    model1.setValueAt(i,i,0);
                    model1.setValueAt("0",i,1);
                    model1.setValueAt("0",i,4);
                }
            }
        });

        memorySizeComboBox = new JComboBox<>(new String[]{"256", "512", "1024", "2048", "4096", "8192"});
        memorySizeComboBox.setBounds(120, 50, 100, 20);
        simulationFrame.getContentPane().add(memorySizeComboBox);

        offsetComboBox = new JComboBox<>(new String[]{"0","1","2"});
        offsetComboBox.setBounds(120, 80, 100, 20);
        simulationFrame.getContentPane().add(offsetComboBox);

        memorySizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int memorySize = Integer.parseInt(memorySizeComboBox.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int row=0, col=0;
                String [][]memory=directMappedTableContent(memorySize,offset);
                col=(int)Math.pow(2,offset);
                row=memorySize/(int)Math.pow(2,offset);
                model2.setRowCount(row);
                model2.setColumnCount(col);
                for (int i=0;i<row;i++){
                    for (int j=0;j<col;j++){
                        model2.setValueAt(memory[i][j],i,j);
                    }
                }
            }
        });

        JLabel instructionLabel = new JLabel("Instruction:");
        instructionLabel.setBounds(40, 240, 140, 20);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionLabel);

        JComboBox instructionActionComboBox = new JComboBox<>(new String[]{"Load", "Store"});
        instructionActionComboBox.setBounds(20, 270, 100, 20);
        simulationFrame.getContentPane().add(instructionActionComboBox);

        JTextField instructionTextField = new JTextField();
        instructionTextField.setBounds(120, 270, 100, 20);
        simulationFrame.getContentPane().add(instructionTextField);

        JButton addInstructionButton = new JButton("Generate instruction");
        addInstructionButton.setBounds(20, 300, 200, 30);
        simulationFrame.getContentPane().add(addInstructionButton);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 340, 200, 30);
        simulationFrame.getContentPane().add(submitButton);

        submitConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(submitConfigButton, "Your selected configuration is: \n" +
                        "Cache size: " + cacheSizeComboBox.getSelectedItem().toString() + "\n" +
                        "Memory size: " + memorySizeComboBox.getSelectedItem().toString() + "\n" +
                        "Offset bits: " + offsetComboBox.getSelectedItem().toString() + "\n" +
                        "Write policy: " + (writeThroughCheckBox.isSelected() ? "Write through" : "Write back"));
                cache.setSize(Integer.parseInt(cacheSizeComboBox.getSelectedItem().toString()));
                cache.setOffsetBits(Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                cache.setPolicy(writeThroughCheckBox.isSelected() ? WritePolicy.WRITE_THROUGH : WritePolicy.WRITE_BACK);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable1, Color.WHITE);
                if(instructionActionComboBox.getSelectedItem().toString().equals("Load")) {
                    //JOptionPane.showMessageDialog(submitButton, "Please select a mama policy");
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 380, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString()) / Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString()))) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);


                        }
                    });

                    JButton checkValidIndexAndTagButton = new JButton("Check valid, index and tag");
                    checkValidIndexAndTagButton.setBounds(20, 420, 200, 30);
                    simulationFrame.getContentPane().add(checkValidIndexAndTagButton);


                    checkValidIndexAndTagButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String tag = model.getValueAt(1, 0).toString();
                            String index = model.getValueAt(1, 1).toString();
                            String blockOffset=tag+index;
                            int integerTag= Integer.parseInt(tag, 2);
                            int integerIndex= Integer.parseInt(index, 2);
                            int integerBlockOffset=Integer.parseInt(blockOffset,2);
                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                    "The index " + integerIndex  +" is searched in the cache");
                            highlightCell(cacheTable, integerIndex, 0, Color.BLUE);
                            highlightCell(instructionBreakdownTable, 1, 1, Color.BLUE);
                            if(model1.getValueAt(integerIndex,1).toString().equals("0")){
                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                        "The valid bit is 0, so the cache line is empty(cache miss)");
                                highlightCell(cacheTable, integerIndex, 1, Color.RED);
                            }

                            else if(model1.getValueAt(integerIndex,1).toString().equals("1")){
                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                        "The valid bit is 1, checking tag.");
                                if(model1.getValueAt(integerIndex,2).toString().equals(tag)){
                                    JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                            "The tag is correct, so the cache line is not empty(cache hit)");
                                    highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                }
                                else{
                                    JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                            "The tag is incorrect, so the cache line is empty(cache miss)");
                                    highlightCell(cacheTable, integerIndex, 2, Color.RED);
                                }
                            }

                            cacheTable.revalidate();
                            cacheTable.repaint();
                            instructionBreakdownTable.revalidate();
                            instructionBreakdownTable.repaint();
                        }
                    });

                    JButton updateCacheButton = new JButton("Update cache");
                    updateCacheButton.setBounds(20, 460, 200, 30);
                    simulationFrame.getContentPane().add(updateCacheButton);

                    updateCacheButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String tag = model.getValueAt(1, 0).toString();
                            String index = model.getValueAt(1, 1).toString();
                            String blockOffset=tag+index;
                            int integerTag= Integer.parseInt(tag, 2);
                            int integerIndex= Integer.parseInt(index, 2);
                            int integerBlockOffset=Integer.parseInt(blockOffset,2);
                            model1.setValueAt(tag, integerIndex, 2);
                            model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                            model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                            highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                            int nrCol=mainMemoryTable.getColumnCount();
                            for(int i=0;i<nrCol;i++)
                                highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                            mainMemoryTable.revalidate();
                            mainMemoryTable.repaint();
                            cacheTable.revalidate();
                            cacheTable.repaint();
                            JOptionPane.showMessageDialog(updateCacheButton,
                                    "The cache line is updated with the data from the main memory");
                        }
                    });

                    simulationFrame.revalidate();
                    simulationFrame.repaint();
                }
                if(instructionActionComboBox.getSelectedItem().toString().equals("Store")) {
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 380, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    JButton searchCacheButton = new JButton("Search address in cache");
                    searchCacheButton.setBounds(20, 420, 200, 30);
                    simulationFrame.getContentPane().add(searchCacheButton);

                    simulationFrame.revalidate();
                    simulationFrame.repaint();

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString()) / Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString()))) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);
                        }
                    });

                    searchCacheButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String tag = model.getValueAt(1, 0).toString();
                            String index = model.getValueAt(1, 1).toString();
                            String blockOffset=tag+index;
                            int integerTag= Integer.parseInt(tag, 2);
                            int integerIndex= Integer.parseInt(index, 2);
                            int integerBlockOffset=Integer.parseInt(blockOffset,2);
                            JOptionPane.showMessageDialog(searchCacheButton,
                                    "The index " + integerIndex  +" is searched in the cache");
                            highlightCell(cacheTable, integerIndex, 0, Color.BLUE);
                            highlightCell(instructionBreakdownTable, 1, 1, Color.BLUE);
                            if(writeThroughCheckBox.isSelected()){
                                if(model1.getValueAt(integerIndex,2)==null){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is different (data is loaded without dirty bit)=> cache miss");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else if(!model1.getValueAt(integerIndex, 2).equals(tag)){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is different (data is loaded without dirty bit)=> cache miss");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else {
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is the same (data is loaded without dirty bit)=> cache hit");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();

                                }
                            }
                            else if(writeBackCheckBox.isSelected()){
                                if(model1.getValueAt(integerIndex,2)==null){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is different (data is loaded without dirty bit)=> cache miss");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else if(!model1.getValueAt(integerIndex, 2).equals(tag) & model1.getValueAt(integerIndex, 4).equals("0")){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is different, data will be loaded without modifying the dirty bit");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else if(!model1.getValueAt(integerIndex, 2).equals(tag) & model1.getValueAt(integerIndex, 4).equals("1")){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is different, data will be loaded and the dirty bit will be modified");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    model1.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else if(model1.getValueAt(integerIndex, 2).equals(tag) & model1.getValueAt(integerIndex, 4).equals("0")){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is the same, dirty bit and cache will be modified, but not main memory");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    model1.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.WHITE);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else if(model1.getValueAt(integerIndex, 2).equals(tag) & model1.getValueAt(integerIndex, 4).equals("1")){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The tag is the same, and the dirty bit is 1, so cache hit"+"\n"+"Main memory is modified too");
                                    model1.setValueAt(tag, integerIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, integerIndex, 3);
                                    model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                    model1.setValueAt("0", integerIndex, 4); //dirty becomes 1
                                    highlightCell(cacheTable, integerIndex, 3, Color.RED);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.RED);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                            }
                            simulationFrame.revalidate();
                            simulationFrame.repaint();
                        }
                    });
                }
            }
        });

        addInstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                instructionTextField.setText(generateHexSmallerThan(Integer.parseInt(memorySizeComboBox.getSelectedItem().toString())));
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);

            }
        });

        //instruction breakdown table

        JLabel instructionBreakdownLabel = new JLabel("Address breakdown:");
        instructionBreakdownLabel.setBounds(300, 20, 200, 20);
        instructionBreakdownLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionBreakdownLabel);

        String []columnNames = {"Tag", "Index", "Offset"};
        instructionBreakdownTable.setBounds(300, 60, 320, 40);
        instructionBreakdownTable.setRowHeight(20);
        model.removeRow(0);
        model.insertRow(0,columnNames);
        instructionBreakdownTable.setEnabled(true);
        simulationFrame.getContentPane().add(instructionBreakdownTable);

        //cache table

        JLabel cacheTableLabel = new JLabel("Cache table:");
        cacheTableLabel.setBounds(300, 120, 200, 20);
        cacheTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(cacheTableLabel);

        JScrollPane scrollPane = new JScrollPane(cacheTable);
        scrollPane.setBounds(300, 160, 320, 200);
        //scrollPane.setVisible(true);
        cacheTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane);
        model1.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        //main memory table

        JLabel mainMemoryTableLabel = new JLabel("Main memory table:");
        mainMemoryTableLabel.setBounds(800, 20, 400, 20);
        mainMemoryTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(mainMemoryTableLabel);

        JScrollPane scrollPane2 = new JScrollPane(mainMemoryTable);
        scrollPane2.setBounds(800, 60, 460, 200);
        //scrollPane.setVisible(true);
        mainMemoryTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane2);



        simulationFrame.setVisible(true);
    }

    private void createFullyAssociativeForm() {
        JFrame simulationFrame = new JFrame("Fully associative Cache Simulation");
        simulationFrame.setLayout(null);
        simulationFrame.setSize(1400, 800);

        JLabel replacementPolicyLabel = new JLabel("Replacement policy:");
        replacementPolicyLabel.setBounds(20, 20, 200, 20);
        simulationFrame.getContentPane().add(replacementPolicyLabel);

        JRadioButton fifoCheckBox = new JRadioButton("FIFO");
        fifoCheckBox.setBounds(20, 50, 100, 20);
        simulationFrame.getContentPane().add(fifoCheckBox);

        JRadioButton lifoCheckBox = new JRadioButton("LIFO");
        lifoCheckBox.setBounds(20, 80, 100, 20);
        simulationFrame.getContentPane().add(lifoCheckBox);

        JRadioButton randomCheckBox = new JRadioButton("Random");
        randomCheckBox.setBounds(20, 110, 100, 20);
        simulationFrame.getContentPane().add(randomCheckBox);

        JLabel cacheSizeLabel = new JLabel("Cache size:");
        cacheSizeLabel.setBounds(20, 140, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeLabel);

        JLabel memorySizeLabel = new JLabel("Memory size:");
        memorySizeLabel.setBounds(20, 170, 100, 20);
        simulationFrame.getContentPane().add(memorySizeLabel);

        JLabel offsetLabel = new JLabel("Offset bits:");
        offsetLabel.setBounds(20, 200, 100, 20);
        simulationFrame.getContentPane().add(offsetLabel);

        JLabel writePolicyLabel = new JLabel("Write policy:");
        writePolicyLabel.setBounds(30, 230, 150, 22);
        writePolicyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(writePolicyLabel);

        JRadioButton writeThroughCheckBox = new JRadioButton("Write through");
        writeThroughCheckBox.setBounds(20, 260, 150, 20);
        simulationFrame.getContentPane().add(writeThroughCheckBox);

        JRadioButton writeBackCheckBox = new JRadioButton("Write back");
        writeBackCheckBox.setBounds(20, 290, 100, 20);
        simulationFrame.getContentPane().add(writeBackCheckBox);

        JButton submitConfigButton = new JButton("Submit");
        submitConfigButton.setBounds(135, 290, 85, 30);
        simulationFrame.getContentPane().add(submitConfigButton);


        JComboBox cacheSizeComboBox = new JComboBox<>(new String[]{"2","4","8","16","32", "64"});
        cacheSizeComboBox.setBounds(120, 140, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeComboBox);

        cacheSizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nrCacheLines = Integer.parseInt(cacheSizeComboBox.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int logLines= (int) (nrCacheLines/Math.pow(2.0,offset));
                model1.setRowCount(logLines);
                for (int i=0;i<logLines;i++){
                    model1.setValueAt(i,i,0);
                    model1.setValueAt("0",i,1);
                    model1.setValueAt("0",i,4);
                }
            }
        });

        memorySizeComboBox = new JComboBox<>(new String[]{"256", "512", "1024", "2048", "4096", "8192"});
        memorySizeComboBox.setBounds(120, 170, 100, 20);
        simulationFrame.getContentPane().add(memorySizeComboBox);

        offsetComboBox = new JComboBox<>(new String[]{"0","1","2"});
        offsetComboBox.setBounds(120, 200, 100, 20);
        simulationFrame.getContentPane().add(offsetComboBox);

        memorySizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int memorySize = Integer.parseInt(memorySizeComboBox.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int row=0, col=0;
                String [][]memory=directMappedTableContent(memorySize,offset);
                col=(int)Math.pow(2,offset);
                row=memorySize/(int)Math.pow(2,offset);
                model2.setRowCount(row);
                model2.setColumnCount(col);
                for (int i=0;i<row;i++){
                    for (int j=0;j<col;j++){
                        model2.setValueAt(memory[i][j],i,j);
                    }
                }
            }
        });

        JLabel instructionLabel = new JLabel("Instruction:");
        instructionLabel.setBounds(40, 320, 140, 20);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionLabel);

        JComboBox instructionActionComboBox = new JComboBox<>(new String[]{"Load", "Store"});
        instructionActionComboBox.setBounds(20, 350, 100, 20);
        simulationFrame.getContentPane().add(instructionActionComboBox);

        JTextField instructionTextField = new JTextField();
        instructionTextField.setBounds(120, 350, 100, 20);
        simulationFrame.getContentPane().add(instructionTextField);

        JButton addInstructionButton = new JButton("Generate instruction");
        addInstructionButton.setBounds(20, 380, 200, 30);
        simulationFrame.getContentPane().add(addInstructionButton);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 410, 200, 30);
        simulationFrame.getContentPane().add(submitButton);

        submitConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(submitConfigButton, "Your selected configuration is: \n" +
                        "Cache size: " + cacheSizeComboBox.getSelectedItem().toString() + "\n" +
                        "Memory size: " + memorySizeComboBox.getSelectedItem().toString() + "\n" +
                        "Offset bits: " + offsetComboBox.getSelectedItem().toString() + "\n" +
                        "Write policy: " + (writeThroughCheckBox.isSelected() ? "Write through" : "Write back"));
                cache.setSize(Integer.parseInt(cacheSizeComboBox.getSelectedItem().toString()));
                cache.setOffsetBits(Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                cache.setPolicy(writeThroughCheckBox.isSelected() ? WritePolicy.WRITE_THROUGH : WritePolicy.WRITE_BACK);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable1, Color.WHITE);
                if(instructionActionComboBox.getSelectedItem().toString().equals("Load")) {
                    //JOptionPane.showMessageDialog(submitButton, "Please select a mama policy");
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 440, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString()) / Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString()))) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);


                        }
                    });

                    JButton checkValidIndexAndTagButton = new JButton("Check valid and tag");
                    checkValidIndexAndTagButton.setBounds(20, 470, 200, 30);
                    simulationFrame.getContentPane().add(checkValidIndexAndTagButton);


                    checkValidIndexAndTagButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String tag = model.getValueAt(1, 0).toString();
                            String index = model.getValueAt(1, 1).toString();
                            String blockOffset=tag+index;
                            int integerTag= Integer.parseInt(tag, 2);
                            int integerIndex= Integer.parseInt(index, 2);
                            int integerBlockOffset=Integer.parseInt(blockOffset,2);
                            boolean full=true, found=false;

                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                    "Fully associative, so we do not check the index. Looking for the block " + blockOffset  +" in the whole cache");
                            for(int i=0;i<model1.getRowCount();i++){
                                if(model1.getValueAt(i,2)==null)
                                    full=false;
                                else if(model1.getValueAt(i,2).equals(tag)){
                                    found=true;
                                    break;
                                }
                            }
                            if(found){
                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                        "The block is found, so the cache line is not empty(cache hit). Loading data from cache");
                                for(int i=0;i<model1.getRowCount();i++){
                                    if(model1.getValueAt(i,2).equals(tag)){
                                        highlightCell(cacheTable, i, 2, Color.GREEN);
                                        break;
                                    }
                                }
                                highlightCell(instructionBreakdownTable, 1, 1, Color.GREEN);
                            }
                            if(!found && !full){
                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                        "The block is not found, but the cache is not full, so we add the tag");
                                for(int i=0;i<model1.getRowCount();i++){
                                    if(model1.getValueAt(i,2)==null){
                                        model1.setValueAt(tag, i, 2);
                                        model1.setValueAt("Block "+integerBlockOffset, i, 3);
                                        int nrCol=mainMemoryTable.getColumnCount();
                                        for(int j=0;j<nrCol;j++)
                                            highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                        model1.setValueAt("1", i, 1); //valid becomes 1
                                        highlightCell(cacheTable, i, 2, Color.GREEN);
                                        break;
                                    }
                                }
                                highlightCell(instructionBreakdownTable, 1, 0, Color.GREEN);


                            }
                            if(full && !found){
                                if(randomCheckBox.isSelected()){
                                    JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                            "The block is not found, so we need to replace a cache line. We use random replacement policy");
                                    int randomIndex=(int)(Math.random()*model1.getRowCount())-1;
                                    if(randomIndex==-1)
                                        randomIndex=0;
                                    else if(randomIndex==model1.getRowCount())
                                        randomIndex=model1.getRowCount()-1;
                                    model1.setValueAt(tag, randomIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, randomIndex, 3);
                                    model1.setValueAt("1", randomIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, randomIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else {
                                    JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                            "The block is not found, so we need to replace a cache line. We use " + (fifoCheckBox.isSelected() ? "FIFO" : "LIFO") + " replacement policy");
                                    int replaceIndex = fifoCheckBox.isSelected() ? currIndex : (currIndex == 0 ? model1.getRowCount() - 1 : currIndex - 1);
                                    model1.setValueAt(tag, replaceIndex, 2);
                                    model1.setValueAt("Block " + integerBlockOffset, replaceIndex, 3);
                                    model1.setValueAt("1", replaceIndex, 1); // valid becomes 1
                                    highlightCell(cacheTable, replaceIndex, 3, Color.GREEN);
                                    int nrCol = mainMemoryTable.getColumnCount();
                                    for (int i = 0; i < nrCol; i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                    currIndex = (currIndex + 1) % model1.getRowCount();
                                }

                            }

                            cacheTable.revalidate();
                            cacheTable.repaint();
                            instructionBreakdownTable.revalidate();
                            instructionBreakdownTable.repaint();
                        }
                    });

                    simulationFrame.revalidate();
                    simulationFrame.repaint();
                }
                if(instructionActionComboBox.getSelectedItem().toString().equals("Store")) {
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 440, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    JButton searchCacheButton = new JButton("Search address in cache");
                    searchCacheButton.setBounds(20, 470, 200, 30);
                    simulationFrame.getContentPane().add(searchCacheButton);

                    simulationFrame.revalidate();
                    simulationFrame.repaint();

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox.getSelectedItem().toString()) / Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString()))) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);


                        }
                    });

                    searchCacheButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String tag = model.getValueAt(1, 0).toString();
                            String index = model.getValueAt(1, 1).toString();
                            String blockOffset=tag+index;
                            int integerTag= Integer.parseInt(tag, 2);
                            int integerIndex= Integer.parseInt(index, 2);
                            int integerBlockOffset=Integer.parseInt(blockOffset,2);
                            boolean full=true, found=false;
                            JOptionPane.showMessageDialog(searchCacheButton,
                                    "Fully associative, so we do not check the index. Looking for the block " + blockOffset  +" in the whole cache");
                            for(int i=0;i<model1.getRowCount();i++){
                                if(model1.getValueAt(i,2)==null)
                                    full=false;
                                else if(model1.getValueAt(i,2).equals(tag)){
                                    found=true;
                                    break;
                                }
                            }
                            if(found) {
                                if (writeBackCheckBox.isSelected()) {
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The block is found, check the dirty bit");
                                    for(int x=0;x<model1.getRowCount();x++){
                                        if(model1.getValueAt(x,2).equals(tag)){
                                            integerIndex=x;
                                            break;
                                        }
                                    }
                                    if (model1.getValueAt(integerIndex, 4).equals("0")) {
                                        JOptionPane.showMessageDialog(searchCacheButton,
                                                "The dirty bit is 0, updating the cache line and the dirty bit, but not main memory");
                                        for(int i=0;i<model1.getRowCount();i++){
                                            if(model1.getValueAt(i,2).equals(tag)){
                                                highlightCell(cacheTable, i, 2, Color.GREEN);
                                                model1.setValueAt("1", i, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                                break;
                                            }
                                        }
                                    }
                                    else if (model1.getValueAt(integerIndex, 4).equals("1")) {
                                        JOptionPane.showMessageDialog(searchCacheButton,
                                                "The dirty bit is 1, updating the cache line and the dirty bit, as well as the main memory");
                                        for(int i=0;i<model1.getRowCount();i++){
                                            if(model1.getValueAt(i,2).equals(tag)){
                                                highlightCell(cacheTable, i, 2, Color.GREEN);
                                                model1.setValueAt("0", i, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                                break;
                                            }
                                        }
                                    }
                                    highlightCell(instructionBreakdownTable, 1, 1, Color.GREEN);
                                }
                                else if(writeThroughCheckBox.isSelected()){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The block is found, so the cache line is not empty(cache hit). Updating data in cache and main memory");
                                    for(int i=0;i<model1.getRowCount();i++){
                                        if(model1.getValueAt(i,2).equals(tag)){
                                            highlightCell(cacheTable, i, 2, Color.GREEN);
                                            model1.setValueAt("0", i, 4); //dirty becomes 1
                                            for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                            break;
                                        }
                                    }
                                    highlightCell(instructionBreakdownTable, 1, 1, Color.GREEN);
                                }
                            }
                            if(!found && !full){
                                JOptionPane.showMessageDialog(searchCacheButton,
                                        "The block is not found, and the cache is not full, so we add the block");
                                for(int i=0;i<model1.getRowCount();i++){
                                    if(model1.getValueAt(i,2)==null){
                                        model1.setValueAt(tag, i, 2);
                                        model1.setValueAt("Block "+integerBlockOffset, i, 3);
                                        int nrCol=mainMemoryTable.getColumnCount();
                                        for(int j=0;j<nrCol;j++)
                                            highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                        model1.setValueAt("1", i, 1); //valid becomes 1
                                        highlightCell(cacheTable, i, 2, Color.GREEN);
                                        mainMemoryTable.revalidate();
                                        mainMemoryTable.repaint();
                                        break;
                                    }
                                }
                                highlightCell(instructionBreakdownTable, 1, 1, Color.GREEN);


                            }
                            if(full && !found){
//                                currIndex=0;
                                if(randomCheckBox.isSelected()){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The block is not found, so we need to replace a cache line. We use random replacement policy");
                                    int randomIndex=(int)(Math.random()*model1.getRowCount())-1;
                                    if(randomIndex==-1)
                                        randomIndex=0;
                                    else if(randomIndex==model1.getRowCount())
                                        randomIndex=model1.getRowCount()-1;
                                    model1.setValueAt(tag, randomIndex, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, randomIndex, 3);
                                    model1.setValueAt("1", randomIndex, 1); //valid becomes 1
                                    highlightCell(cacheTable, randomIndex, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }
                                else if(fifoCheckBox.isSelected()){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The block is not found, so we need to replace a cache line. We use FIFO replacement policy");
                                    int index1=currIndex;
                                    currIndex++;
                                    currIndex=currIndex%model1.getRowCount();
                                    model1.setValueAt(tag, index1, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, index1, 3);
                                    model1.setValueAt("1", index1, 1); //valid becomes 1
                                    highlightCell(cacheTable, index1, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();

                                }
                                else if(lifoCheckBox.isSelected()){
                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "The block is not found, so we need to replace a cache line. We use LIFO replacement policy");
                                    int index1=model1.getRowCount()-currIndex-1;
                                    currIndex++;
                                    currIndex=currIndex%model1.getRowCount();
                                    model1.setValueAt(tag, index1, 2);
                                    model1.setValueAt("Block "+integerBlockOffset, index1, 3);
                                    model1.setValueAt("1", index1, 1); //valid becomes 1
                                    highlightCell(cacheTable, index1, 3, Color.GREEN);
                                    int nrCol=mainMemoryTable.getColumnCount();
                                    for(int i=0;i<nrCol;i++)
                                        highlightCell(mainMemoryTable, integerBlockOffset, i, Color.GREEN);
                                    mainMemoryTable.revalidate();
                                    mainMemoryTable.repaint();
                                }

                            }

                            cacheTable.revalidate();
                            cacheTable.repaint();
                            instructionBreakdownTable.revalidate();
                            instructionBreakdownTable.repaint();
                        }
                    });
                }
            }
        });

        addInstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                instructionTextField.setText(generateHexSmallerThan(Integer.parseInt(memorySizeComboBox.getSelectedItem().toString())));
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);

            }
        });

        //instruction breakdown table

        JLabel instructionBreakdownLabel = new JLabel("Address breakdown:");
        instructionBreakdownLabel.setBounds(300, 20, 200, 20);
        instructionBreakdownLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionBreakdownLabel);

        String []columnNames = {"Tag", "Index", "Offset"};
        instructionBreakdownTable.setBounds(300, 60, 320, 40);
        instructionBreakdownTable.setRowHeight(20);
        model.removeRow(0);
        model.insertRow(0,columnNames);
        instructionBreakdownTable.setEnabled(true);
        simulationFrame.getContentPane().add(instructionBreakdownTable);

        //cache table

        JLabel cacheTableLabel = new JLabel("Cache table:");
        cacheTableLabel.setBounds(300, 120, 200, 20);
        cacheTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(cacheTableLabel);

        JScrollPane scrollPane = new JScrollPane(cacheTable);
        scrollPane.setBounds(300, 160, 320, 200);
        //scrollPane.setVisible(true);
        cacheTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane);
        model1.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        //main memory table

        JLabel mainMemoryTableLabel = new JLabel("Main memory table:");
        mainMemoryTableLabel.setBounds(800, 20, 400, 20);
        mainMemoryTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(mainMemoryTableLabel);

        JScrollPane scrollPane2 = new JScrollPane(mainMemoryTable);
        scrollPane2.setBounds(800, 60, 460, 200);
        //scrollPane.setVisible(true);
        mainMemoryTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane2);



        simulationFrame.setVisible(true);
    }

    private void create2WayCacheSimulationForm() {
        JFrame simulationFrame = new JFrame("2-Way Mapped Cache Simulation");
        simulationFrame.setLayout(null);
        simulationFrame.setSize(1400, 800);

        JLabel replacementPolicyLabel = new JLabel("Replacement policy:");
        replacementPolicyLabel.setBounds(20, 20, 200, 20);
        simulationFrame.getContentPane().add(replacementPolicyLabel);

        JRadioButton fifoCheckBox = new JRadioButton("FIFO");
        fifoCheckBox.setBounds(20, 50, 100, 20);
        simulationFrame.getContentPane().add(fifoCheckBox);

        JRadioButton lifoCheckBox = new JRadioButton("LIFO");
        lifoCheckBox.setBounds(20, 80, 100, 20);
        simulationFrame.getContentPane().add(lifoCheckBox);

        JRadioButton randomCheckBox = new JRadioButton("Random");
        randomCheckBox.setBounds(20, 110, 100, 20);
        simulationFrame.getContentPane().add(randomCheckBox);

        JLabel cacheSizeLabel = new JLabel("Cache size:");
        cacheSizeLabel.setBounds(20, 140, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeLabel);

        JLabel memorySizeLabel = new JLabel("Memory size:");
        memorySizeLabel.setBounds(20, 170, 100, 20);
        simulationFrame.getContentPane().add(memorySizeLabel);

        JLabel offsetLabel = new JLabel("Offset bits:");
        offsetLabel.setBounds(20, 200, 100, 20);
        simulationFrame.getContentPane().add(offsetLabel);

        JLabel writePolicyLabel = new JLabel("Write policy:");
        writePolicyLabel.setBounds(30, 230, 150, 22);
        writePolicyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(writePolicyLabel);

        JRadioButton writeThroughCheckBox = new JRadioButton("Write through");
        writeThroughCheckBox.setBounds(20, 260, 150, 20);
        simulationFrame.getContentPane().add(writeThroughCheckBox);

        JRadioButton writeBackCheckBox = new JRadioButton("Write back");
        writeBackCheckBox.setBounds(20, 290, 100, 20);
        simulationFrame.getContentPane().add(writeBackCheckBox);

        JButton submitConfigButton = new JButton("Submit");
        submitConfigButton.setBounds(135, 290, 85, 30);
        simulationFrame.getContentPane().add(submitConfigButton);

        JComboBox offsetComboBox = new JComboBox<>(new String[]{"0","1","2"});
        offsetComboBox.setBounds(120, 200, 100, 20);
        simulationFrame.getContentPane().add(offsetComboBox);

        JComboBox cacheSizeComboBox1 = new JComboBox<>(new String[]{"2","4","8","16","32", "64"});
        cacheSizeComboBox1.setBounds(120, 140, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeComboBox1);

        cacheSizeComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nrCacheLines = Integer.parseInt(cacheSizeComboBox1.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int logLines= (int) (nrCacheLines/Math.pow(2.0,offset));
                logLines=logLines/2+logLines%2;
                model1.setRowCount(logLines);
                model1_1.setRowCount(logLines);
                for (int i=0;i<logLines;i++){
                    model1.setValueAt(i,i,0);
                    model1.setValueAt("0",i,1);
                    model1.setValueAt("0",i,4);

                    model1_1.setValueAt(i,i,0);
                    model1_1.setValueAt("0",i,1);
                    model1_1.setValueAt("0",i,4);
                }
            }
        });

        JComboBox memorySizeComboBox = new JComboBox<>(new String[]{"256", "512", "1024", "2048", "4096", "8192"});
        memorySizeComboBox.setBounds(120, 170, 100, 20);
        simulationFrame.getContentPane().add(memorySizeComboBox);

        memorySizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int memorySize = Integer.parseInt(memorySizeComboBox.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int row=0, col=0;
                String [][]memory=directMappedTableContent(memorySize,offset);
                col=(int)Math.pow(2,offset);
                row=memorySize/(int)Math.pow(2,offset);
                model2.setRowCount(row);
                model2.setColumnCount(col);
                for (int i=0;i<row;i++){
                    for (int j=0;j<col;j++){
                        model2.setValueAt(memory[i][j],i,j);
                    }
                }
            }
        });

        JLabel instructionLabel = new JLabel("Instruction:");
        instructionLabel.setBounds(40, 320, 140, 20);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionLabel);

        JComboBox instructionActionComboBox = new JComboBox<>(new String[]{"Load", "Store"});
        instructionActionComboBox.setBounds(20, 350, 100, 20);
        simulationFrame.getContentPane().add(instructionActionComboBox);

        JTextField instructionTextField = new JTextField();
        instructionTextField.setBounds(120, 350, 100, 20);
        simulationFrame.getContentPane().add(instructionTextField);

        JButton addInstructionButton = new JButton("Generate instruction");
        addInstructionButton.setBounds(20, 380, 200, 30);
        simulationFrame.getContentPane().add(addInstructionButton);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 410, 200, 30);
        simulationFrame.getContentPane().add(submitButton);

        submitConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cacheSize = Integer.parseInt(cacheSizeComboBox1.getSelectedItem().toString());
                int offsetBits = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int blockSize = (int) Math.pow(2, offsetBits);

                if (cacheSize <= 2 * blockSize) {
                    JOptionPane.showMessageDialog(submitConfigButton, "The selected configuration is not compatible to build a 4-way set associative cache. Please select a larger cache size or a smaller offset.");
                } else {
                    JOptionPane.showMessageDialog(submitConfigButton, "Your selected configuration is: \n" +
                            "Cache size: " + cacheSizeComboBox1.getSelectedItem().toString() + "\n" +
                            "Memory size: " + memorySizeComboBox.getSelectedItem().toString() + "\n" +
                            "Offset bits: " + offsetComboBox.getSelectedItem().toString() + "\n" +
                            "Write policy: " + (writeThroughCheckBox.isSelected() ? "Write through" : "Write back"));
                    cache.setSize(Integer.parseInt(cacheSizeComboBox1.getSelectedItem().toString()));
                    cache.setOffsetBits(Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                    cache.setPolicy(writeThroughCheckBox.isSelected() ? WritePolicy.WRITE_THROUGH : WritePolicy.WRITE_BACK);
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable1, Color.WHITE);
                if(instructionActionComboBox.getSelectedItem().toString().equals("Load")) {
                    //JOptionPane.showMessageDialog(submitButton, "Please select a mama policy");
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 440, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString()) / (Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString())) * 2)) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);
                        }
                    });

                    JButton checkValidIndexAndTagButton = new JButton("Check valid, index and tag");
                    checkValidIndexAndTagButton.setBounds(20, 470, 200, 30);
                    simulationFrame.getContentPane().add(checkValidIndexAndTagButton);

                    checkValidIndexAndTagButton.addActionListener(
                            new ActionListener() {
                                boolean firstUsed=false;
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String tag = model.getValueAt(1, 0).toString();
                                    String index = model.getValueAt(1, 1).toString();
                                    String blockOffset=tag+index;
                                    int integerTag= Integer.parseInt(tag, 2);
                                    int integerIndex= Integer.parseInt(index, 2);
                                    int integerBlockOffset=Integer.parseInt(blockOffset,2);

                                    JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                            "2-way mapped, so we check the index and the tag. Looking for the tag " + tag  +" in the cache line with index " + index);
                                    if((model1.getValueAt(integerIndex,2)!=null && model1.getValueAt(integerIndex,2).equals(tag)) || (model1_1.getValueAt(integerIndex,2)!=null && model1_1.getValueAt(integerIndex,2).equals(tag))){
                                        JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                "The tag is found, so the cache line is not empty(cache hit). Loading data from cache");
                                        if(model1.getValueAt(integerIndex,2).equals(tag)){
                                            highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                            cacheTable.revalidate();
                                            cacheTable.repaint();
                                        }
                                        else{
                                            highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                            cacheTable1.revalidate();
                                            cacheTable1.repaint();
                                        }
                                    }else {
                                        if (model1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                    "The cache line is empty, so we add it to the first set");
                                            model1.setValueAt(tag, integerIndex, 2);
                                            model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                            firstUsed = true;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        } else if (model1_1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                    "The cache line is empty, so we add it to the second set");
                                            model1_1.setValueAt(tag, integerIndex, 2);
                                            model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                            firstUsed = false;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        } else if (model1.getValueAt(integerIndex, 2) != null && model1_1.getValueAt(integerIndex, 2) != null) {
                                            if (firstUsed) {
                                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                firstUsed = false;
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            } else {
                                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                firstUsed = true;
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                        }
                                    }
                                }
                            }
                    );

                    simulationFrame.revalidate();
                    simulationFrame.repaint();
                }
                if(instructionActionComboBox.getSelectedItem().toString().equals("Store")) {
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 440, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    JButton searchCacheButton = new JButton("Store address in cache");
                    searchCacheButton.setBounds(20, 470, 200, 30);
                    simulationFrame.getContentPane().add(searchCacheButton);

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString()) / (Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString())) * 2)) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);
                        }
                    });

                    searchCacheButton.addActionListener(
                            new ActionListener() {
                                int firstUsed=0;
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String tag = model.getValueAt(1, 0).toString();
                                    String index = model.getValueAt(1, 1).toString();
                                    String blockOffset=tag+index;
                                    int integerTag= Integer.parseInt(tag, 2);
                                    int integerIndex= Integer.parseInt(index, 2);
                                    int integerBlockOffset=Integer.parseInt(blockOffset,2);

                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "2-way mapped, so we check the index and the tag. Looking for the tag " + tag  +" in the cache line with index " + index);
                                    if((model1.getValueAt(integerIndex,2)!=null && model1.getValueAt(integerIndex,2).equals(tag)) || (model1_1.getValueAt(integerIndex,2)!=null && model1_1.getValueAt(integerIndex,2).equals(tag))){
                                        JOptionPane.showMessageDialog(searchCacheButton,
                                                "The tag is found, so the cache line is not empty(cache hit).");
                                        if(writeBackCheckBox.isSelected() && (model1.getValueAt(integerIndex, 4).equals("1") || model1_1.getValueAt(integerIndex, 4).equals("1"))){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The write policy is write back, so we use the dirty bit. Dirty bit is 1. Updating the cache and the main memory");
                                            if(model1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                model1.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable.revalidate();
                                                cacheTable.repaint();
                                            }
                                            else if (model1_1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                model1_1.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable1.revalidate();
                                                cacheTable1.repaint();
                                            }
                                        }
                                        else if(writeBackCheckBox.isSelected() && (model1.getValueAt(integerIndex, 4).equals("0") || model1_1.getValueAt(integerIndex, 4).equals("0"))){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The write policy is write back, so we use the dirty bit. Updating the cache but not the main memory");
                                            if(model1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                model1.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                cacheTable.revalidate();
                                                cacheTable.repaint();
                                            }
                                            else if(model1_1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                model1_1.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                cacheTable1.revalidate();
                                                cacheTable1.repaint();
                                            }
                                        }
                                        else{
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The write policy is write through, so we update the cache and the main memory");
                                            if(model1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable.revalidate();
                                                cacheTable.repaint();
                                            }
                                            else if(model1_1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable1.revalidate();
                                                cacheTable1.repaint();
                                            }
                                        }
                                    }else {
                                        if (model1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The cache line is empty, so we store it in the first set");
                                            model1.setValueAt(tag, integerIndex, 2);
                                            model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                            firstUsed = 0;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        } else if (model1_1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The cache line is empty, so we store it in the second set");
                                            model1_1.setValueAt(tag, integerIndex, 2);
                                            model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                            firstUsed = 1;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if (model1.getValueAt(integerIndex, 2) != null && model1_1.getValueAt(integerIndex, 2) != null) {
                                            if (firstUsed%2==0) {
                                                JOptionPane.showMessageDialog(searchCacheButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 1 : lifoCheckBox.isSelected() ? 0 : (int) (Math.random() * 2);                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            } else if(firstUsed%2==1){
                                                JOptionPane.showMessageDialog(searchCacheButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 0 : lifoCheckBox.isSelected() ? 1 : (int) (Math.random() * 4);                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                        }
                                    }
                                }
                            }
                    );

                    simulationFrame.revalidate();
                    simulationFrame.repaint();
                }
            }
        });

        addInstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                instructionTextField.setText(generateHexSmallerThan(Integer.parseInt(memorySizeComboBox.getSelectedItem().toString())));
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable1, Color.WHITE);

            }
        });

        //instruction breakdown table

        JLabel instructionBreakdownLabel = new JLabel("Address breakdown:");
        instructionBreakdownLabel.setBounds(300, 20, 200, 20);
        instructionBreakdownLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionBreakdownLabel);

        String []columnNames = {"Tag", "Index", "Offset"};
        instructionBreakdownTable.setBounds(300, 60, 260, 40);
        instructionBreakdownTable.setRowHeight(20);
        model.removeRow(0);
        model.insertRow(0,columnNames);
        instructionBreakdownTable.setEnabled(true);
        simulationFrame.getContentPane().add(instructionBreakdownTable);

        //cache tables 2 way

        JLabel cacheTableLabel = new JLabel("Cache tables:");
        cacheTableLabel.setBounds(300, 120, 200, 20);
        cacheTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(cacheTableLabel);

        JScrollPane scrollPane = new JScrollPane(cacheTable);
        scrollPane.setBounds(300, 160, 300, 200);
        //scrollPane.setVisible(true);
        cacheTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane);
        model1.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        JScrollPane scrollPane1 = new JScrollPane(cacheTable1);
        scrollPane1.setBounds(300, 400, 300, 200);
        //scrollPane.setVisible(true);
        cacheTable1.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane1);
        model1_1.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        //main memory table

        JLabel mainMemoryTableLabel = new JLabel("Main memory table:");
        mainMemoryTableLabel.setBounds(800, 20, 400, 20);
        mainMemoryTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(mainMemoryTableLabel);

        JScrollPane scrollPane2 = new JScrollPane(mainMemoryTable);
        scrollPane2.setBounds(800, 60, 460, 200);
        //scrollPane.setVisible(true);
        mainMemoryTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane2);



        simulationFrame.setVisible(true);
    }

    private void create4WayCacheSimulationForm() {
        JFrame simulationFrame = new JFrame("4-Way Mapped Cache Simulation");
        simulationFrame.setLayout(null);
        simulationFrame.setSize(1450, 800);

        JLabel replacementPolicyLabel = new JLabel("Replacement policy:");
        replacementPolicyLabel.setBounds(20, 20, 200, 20);
        simulationFrame.getContentPane().add(replacementPolicyLabel);

        JRadioButton fifoCheckBox = new JRadioButton("FIFO");
        fifoCheckBox.setBounds(20, 50, 100, 20);
        simulationFrame.getContentPane().add(fifoCheckBox);

        JRadioButton lifoCheckBox = new JRadioButton("LIFO");
        lifoCheckBox.setBounds(20, 80, 100, 20);
        simulationFrame.getContentPane().add(lifoCheckBox);

        JRadioButton randomCheckBox = new JRadioButton("Random");
        randomCheckBox.setBounds(20, 110, 100, 20);
        simulationFrame.getContentPane().add(randomCheckBox);

        JLabel cacheSizeLabel = new JLabel("Cache size:");
        cacheSizeLabel.setBounds(20, 140, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeLabel);

        JLabel memorySizeLabel = new JLabel("Memory size:");
        memorySizeLabel.setBounds(20, 170, 100, 20);
        simulationFrame.getContentPane().add(memorySizeLabel);

        JLabel offsetLabel = new JLabel("Offset bits:");
        offsetLabel.setBounds(20, 200, 100, 20);
        simulationFrame.getContentPane().add(offsetLabel);

        JLabel writePolicyLabel = new JLabel("Write policy:");
        writePolicyLabel.setBounds(30, 230, 150, 22);
        writePolicyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(writePolicyLabel);

        JRadioButton writeThroughCheckBox = new JRadioButton("Write through");
        writeThroughCheckBox.setBounds(20, 260, 150, 20);
        simulationFrame.getContentPane().add(writeThroughCheckBox);

        JRadioButton writeBackCheckBox = new JRadioButton("Write back");
        writeBackCheckBox.setBounds(20, 290, 100, 20);
        simulationFrame.getContentPane().add(writeBackCheckBox);

        JButton submitConfigButton = new JButton("Submit");
        submitConfigButton.setBounds(135, 290, 85, 30);
        simulationFrame.getContentPane().add(submitConfigButton);

        JComboBox offsetComboBox = new JComboBox<>(new String[]{"0","1","2"});
        offsetComboBox.setBounds(120, 200, 100, 20);
        simulationFrame.getContentPane().add(offsetComboBox);

        JComboBox cacheSizeComboBox1 = new JComboBox<>(new String[]{"2","4","8","16","32", "64"});
        cacheSizeComboBox1.setBounds(120, 140, 100, 20);
        simulationFrame.getContentPane().add(cacheSizeComboBox1);

        cacheSizeComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nrCacheLines = Integer.parseInt(cacheSizeComboBox1.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int logLines= (int) (nrCacheLines/Math.pow(2.0,offset));
                logLines=logLines/4+logLines%2;
                model1.setRowCount(logLines);
                model1_1.setRowCount(logLines);
                model1_2.setRowCount(logLines);
                model1_3.setRowCount(logLines);
                for (int i=0;i<logLines;i++){
                    model1.setValueAt(i,i,0);
                    model1.setValueAt("0",i,1);
                    model1.setValueAt("0",i,4);

                    model1_1.setValueAt(i,i,0);
                    model1_1.setValueAt("0",i,1);
                    model1_1.setValueAt("0",i,4);

                    model1_2.setValueAt(i,i,0);
                    model1_2.setValueAt("0",i,1);
                    model1_2.setValueAt("0",i,4);

                    model1_3.setValueAt(i,i,0);
                    model1_3.setValueAt("0",i,1);
                    model1_3.setValueAt("0",i,4);
                }
            }
        });

        JComboBox memorySizeComboBox = new JComboBox<>(new String[]{"256", "512", "1024", "2048", "4096", "8192"});
        memorySizeComboBox.setBounds(120, 170, 100, 20);
        simulationFrame.getContentPane().add(memorySizeComboBox);

        memorySizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int memorySize = Integer.parseInt(memorySizeComboBox.getSelectedItem().toString());
                int offset = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int row=0, col=0;
                String [][]memory=directMappedTableContent(memorySize,offset);
                col=(int)Math.pow(2,offset);
                row=memorySize/(int)Math.pow(2,offset);
                model2.setRowCount(row);
                model2.setColumnCount(col);
                for (int i=0;i<row;i++){
                    for (int j=0;j<col;j++){
                        model2.setValueAt(memory[i][j],i,j);
                    }
                }
            }
        });

        JLabel instructionLabel = new JLabel("Instruction:");
        instructionLabel.setBounds(40, 320, 140, 20);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionLabel);

        JComboBox instructionActionComboBox = new JComboBox<>(new String[]{"Load", "Store"});
        instructionActionComboBox.setBounds(20, 350, 100, 20);
        simulationFrame.getContentPane().add(instructionActionComboBox);

        JTextField instructionTextField = new JTextField();
        instructionTextField.setBounds(120, 350, 100, 20);
        simulationFrame.getContentPane().add(instructionTextField);

        JButton addInstructionButton = new JButton("Generate instruction");
        addInstructionButton.setBounds(20, 380, 200, 30);
        simulationFrame.getContentPane().add(addInstructionButton);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 410, 200, 30);
        simulationFrame.getContentPane().add(submitButton);

        submitConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cacheSize = Integer.parseInt(cacheSizeComboBox1.getSelectedItem().toString());
                int offsetBits = Integer.parseInt(offsetComboBox.getSelectedItem().toString());
                int blockSize = (int) Math.pow(2, offsetBits);

                if (cacheSize <= 4 * blockSize) {
                    JOptionPane.showMessageDialog(submitConfigButton, "The selected configuration is not compatible to build a 4-way set associative cache. Please select a larger cache size or a smaller offset.");
                } else {
                    JOptionPane.showMessageDialog(submitConfigButton, "Your selected configuration is: \n" +
                            "Cache size: " + cacheSizeComboBox1.getSelectedItem().toString() + "\n" +
                            "Memory size: " + memorySizeComboBox.getSelectedItem().toString() + "\n" +
                            "Offset bits: " + offsetComboBox.getSelectedItem().toString() + "\n" +
                            "Write policy: " + (writeThroughCheckBox.isSelected() ? "Write through" : "Write back"));
                    cache.setSize(Integer.parseInt(cacheSizeComboBox1.getSelectedItem().toString()));
                    cache.setOffsetBits(Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                    cache.setPolicy(writeThroughCheckBox.isSelected() ? WritePolicy.WRITE_THROUGH : WritePolicy.WRITE_BACK);
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable1, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable2, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable3, Color.WHITE);
                if(instructionActionComboBox.getSelectedItem().toString().equals("Load")) {
                    //JOptionPane.showMessageDialog(submitButton, "Please select a mama policy");
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 440, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString()) / (Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString())) * 2)) / Math.log(2.0));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString()) / (Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString())) * 4)) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);
                        }
                    });

                    JButton checkValidIndexAndTagButton = new JButton("Check valid, index and tag");
                    checkValidIndexAndTagButton.setBounds(20, 470, 200, 30);
                    simulationFrame.getContentPane().add(checkValidIndexAndTagButton);

                    checkValidIndexAndTagButton.addActionListener(
                            new ActionListener() {
                                int firstUsed=0;
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String tag = model.getValueAt(1, 0).toString();
                                    String index = model.getValueAt(1, 1).toString();
                                    String blockOffset=tag+index;
                                    int integerTag= Integer.parseInt(tag, 2);
                                    int integerIndex= Integer.parseInt(index, 2);
                                    int integerBlockOffset=Integer.parseInt(blockOffset,2);

                                    JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                            "4-way mapped, so we check the index and the tag. Looking for the tag " + tag  +" in the cache line with index " + index);
                                    if((model1.getValueAt(integerIndex,2)!=null && model1.getValueAt(integerIndex,2).equals(tag)) || (model1_1.getValueAt(integerIndex,2)!=null && model1_1.getValueAt(integerIndex,2).equals(tag)) || (model1_2.getValueAt(integerIndex,2)!=null && model1_2.getValueAt(integerIndex,2).equals(tag)) || (model1_3.getValueAt(integerIndex,2)!=null && model1_3.getValueAt(integerIndex,2).equals(tag))){
                                        JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                "The tag is found, so the cache line is not empty(cache hit). Loading data from cache");
                                        if(model1.getValueAt(integerIndex,2).equals(tag)){
                                            highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                            cacheTable.revalidate();
                                            cacheTable.repaint();
                                        }
                                        else if (model1_1.getValueAt(integerIndex,2).equals(tag)){
                                            highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                            cacheTable1.revalidate();
                                            cacheTable1.repaint();
                                        }
                                        else if (model1_2.getValueAt(integerIndex,2).equals(tag)){
                                            highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                            cacheTable2.revalidate();
                                            cacheTable2.repaint();
                                        }
                                        else if (model1_3.getValueAt(integerIndex,2).equals(tag)){
                                            highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                            cacheTable3.revalidate();
                                            cacheTable3.repaint();
                                        }
                                    }else {
                                        if (model1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                    "The cache line is empty, so we add it to the first set");
                                            model1.setValueAt(tag, integerIndex, 2);
                                            model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                            firstUsed = 0;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        } else if (model1_1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                    "The cache line is empty, so we add it to the second set");
                                            model1_1.setValueAt(tag, integerIndex, 2);
                                            model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                            firstUsed = 1;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if(model1_2.getValueAt(integerIndex,2)==null){
                                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                    "The cache line is empty, so we add it to the third set");
                                            model1_2.setValueAt(tag, integerIndex, 2);
                                            model1_2.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_2.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                            firstUsed = 2;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if(model1_3.getValueAt(integerIndex,2)==null){
                                            JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                    "The cache line is empty, so we add it to the fourth set");
                                            model1_3.setValueAt(tag, integerIndex, 2);
                                            model1_3.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_3.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                            firstUsed = 3;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if (model1.getValueAt(integerIndex, 2) != null && model1_1.getValueAt(integerIndex, 2) != null && model1_2.getValueAt(integerIndex, 2) != null && model1_3.getValueAt(integerIndex, 2) != null) {
                                            if (firstUsed%4==0) {
                                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 1 : lifoCheckBox.isSelected() ? 3 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            } else if (firstUsed%4==1) {
                                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 2 : lifoCheckBox.isSelected() ? 3 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                            else if(firstUsed%4==2){
                                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_3.setValueAt(tag, integerIndex, 2);
                                                model1_3.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_3.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 3 : lifoCheckBox.isSelected() ? 2 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                            else if(firstUsed%4==3){
                                                JOptionPane.showMessageDialog(checkValidIndexAndTagButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_2.setValueAt(tag, integerIndex, 2);
                                                model1_2.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_2.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 0 : lifoCheckBox.isSelected() ? 1 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                        }
                                    }
                                }
                            }
                    );

                    simulationFrame.revalidate();
                    simulationFrame.repaint();
                }
                if(instructionActionComboBox.getSelectedItem().toString().equals("Store")) {
                    JButton convertInstructionButton = new JButton("Convert instruction");
                    convertInstructionButton.setBounds(20, 440, 200, 30);
                    simulationFrame.getContentPane().add(convertInstructionButton);

                    JButton searchCacheButton = new JButton("Store address in cache");
                    searchCacheButton.setBounds(20, 470, 200, 30);
                    simulationFrame.getContentPane().add(searchCacheButton);

                    convertInstructionButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString())) / Math.log(Math.pow(Double.parseDouble(offsetComboBox.getSelectedItem().toString()), 2)));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
//                            String []line={tag,index,offset};
//                            model.removeRow(1);
//                            model.insertRow(1,line);
//                            String hexInstruction = instructionTextField.getText();
//                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
//                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
//                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
//                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString()) / (Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString())) * 2)) / Math.log(2.0));
//                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
//                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String hexInstruction = instructionTextField.getText();
                            double instructionLength = Math.log(Double.parseDouble(memorySizeComboBox.getSelectedItem().toString()))/Math.log(2.0);
                            String binaryInstruction = generateBinaryInstruction(hexInstruction, (int) instructionLength);
                            String offset = binaryInstruction.substring(binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            String instrWithoutOffset = binaryInstruction.substring(0, binaryInstruction.length() - Integer.parseInt(offsetComboBox.getSelectedItem().toString()));
                            int nrOfIndexBits = (int) (Math.log(Double.parseDouble(cacheSizeComboBox1.getSelectedItem().toString()) / (Math.pow(2.0, Double.parseDouble(offsetComboBox.getSelectedItem().toString())) * 4)) / Math.log(2.0));
                            String index = instrWithoutOffset.substring(instrWithoutOffset.length() - nrOfIndexBits);
                            String tag = instrWithoutOffset.substring(0, instrWithoutOffset.length() - nrOfIndexBits);
                            String []line={tag,index,offset};
                            model.removeRow(1);
                            model.insertRow(1,line);
                        }
                    });

                    searchCacheButton.addActionListener(
                            new ActionListener() {
                                int firstUsed=0;
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String tag = model.getValueAt(1, 0).toString();
                                    String index = model.getValueAt(1, 1).toString();
                                    String blockOffset=tag+index;
                                    int integerTag= Integer.parseInt(tag, 2);
                                    int integerIndex= Integer.parseInt(index, 2);
                                    int integerBlockOffset=Integer.parseInt(blockOffset,2);

                                    JOptionPane.showMessageDialog(searchCacheButton,
                                            "4-way mapped, so we check the index and the tag. Looking for the tag " + tag  +" in the cache line with index " + index);
                                    if((model1.getValueAt(integerIndex,2)!=null && model1.getValueAt(integerIndex,2).equals(tag)) || (model1_1.getValueAt(integerIndex,2)!=null && model1_1.getValueAt(integerIndex,2).equals(tag)) || (model1_2.getValueAt(integerIndex,2)!=null && model1_2.getValueAt(integerIndex,2).equals(tag)) || (model1_3.getValueAt(integerIndex,2)!=null && model1_3.getValueAt(integerIndex,2).equals(tag))){
                                        JOptionPane.showMessageDialog(searchCacheButton,
                                                "The tag is found, so the cache line is not empty(cache hit).");
                                        if(writeBackCheckBox.isSelected() && (model1.getValueAt(integerIndex, 4).equals("1") || model1_1.getValueAt(integerIndex, 4).equals("1") || model1_2.getValueAt(integerIndex, 4).equals("1") || model1_3.getValueAt(integerIndex, 4).equals("1"))){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The write policy is write back, so we use the dirty bit. Dirty bit is 1. Updating the cache and the main memory");
                                            if(model1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                model1.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable.revalidate();
                                                cacheTable.repaint();
                                            }
                                            else if (model1_1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                model1_1.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable1.revalidate();
                                                cacheTable1.repaint();
                                            }
                                            else if (model1_2.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                                model1_2.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable2.revalidate();
                                                cacheTable2.repaint();
                                            }
                                            else if (model1_3.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                                model1_3.setValueAt("0", integerIndex, 4); //dirty becomes 0
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable3.revalidate();
                                                cacheTable3.repaint();
                                            }
                                        }
                                        if(writeThroughCheckBox.isSelected()){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The write policy is write through, so we update the cache and the main memory");
                                            if(model1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable.revalidate();
                                                cacheTable.repaint();
                                            }
                                            else if(model1_1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable1.revalidate();
                                                cacheTable1.repaint();
                                            }
                                            else if(model1_2.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable2.revalidate();
                                                cacheTable2.repaint();
                                            }
                                            else if(model1_3.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                cacheTable3.revalidate();
                                                cacheTable3.repaint();
                                            }
                                        }
                                        else if(writeBackCheckBox.isSelected() && (model1.getValueAt(integerIndex, 4).equals("0") || model1_1.getValueAt(integerIndex, 4).equals("0"))|| model1_2.getValueAt(integerIndex, 4).equals("0") || model1_3.getValueAt(integerIndex, 4).equals("0")){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The write policy is write back, so we use the dirty bit. Updating the cache but not the main memory");
                                            if(model1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                model1.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                cacheTable.revalidate();
                                                cacheTable.repaint();
                                            }
                                            else if (model1_1.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                model1_1.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                cacheTable1.revalidate();
                                                cacheTable1.repaint();
                                            }
                                            else if(model1_2.getValueAt(integerIndex,2).equals(tag)){
                                                highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                                model1_2.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                cacheTable2.revalidate();
                                                cacheTable2.repaint();
                                            }
                                            else{
                                                highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                                model1_2.setValueAt("1", integerIndex, 4); //dirty becomes 1
                                                for(int j=0;j<mainMemoryTable.getColumnCount();j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.WHITE);
                                                cacheTable3.revalidate();
                                                cacheTable3.repaint();
                                            }
                                        }
                                    }else {
                                        if (model1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The cache line is empty, so we store it in the first set");
                                            model1.setValueAt(tag, integerIndex, 2);
                                            model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                            firstUsed = 0;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        } else if (model1_1.getValueAt(integerIndex, 2) == null) {
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The cache line is empty, so we store it in the second set");
                                            model1_1.setValueAt(tag, integerIndex, 2);
                                            model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                            firstUsed = 1;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if(model1_2.getValueAt(integerIndex,2)==null){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The cache line is empty, so we store it in the third set");
                                            model1_2.setValueAt(tag, integerIndex, 2);
                                            model1_2.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_2.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                            firstUsed = 2;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if(model1_3.getValueAt(integerIndex,2)==null){
                                            JOptionPane.showMessageDialog(searchCacheButton,
                                                    "The cache line is empty, so we store it in the fourth set");
                                            model1_3.setValueAt(tag, integerIndex, 2);
                                            model1_3.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                            int nrCol = mainMemoryTable.getColumnCount();
                                            for (int j = 0; j < nrCol; j++)
                                                highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                            model1_3.setValueAt("1", integerIndex, 1); //valid becomes 1
                                            highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                            firstUsed = 3;
                                            mainMemoryTable.revalidate();
                                            mainMemoryTable.repaint();
                                        }
                                        else if (model1.getValueAt(integerIndex, 2) != null && model1_1.getValueAt(integerIndex, 2) != null && model1_2.getValueAt(integerIndex, 2) != null && model1_3.getValueAt(integerIndex, 2) != null) {
                                            if (firstUsed%4==0) {
                                                JOptionPane.showMessageDialog(searchCacheButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt(tag, integerIndex, 2);
                                                model1_1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable1, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 1 : lifoCheckBox.isSelected() ? 3 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            } else if (firstUsed%4==1) {
                                                JOptionPane.showMessageDialog(searchCacheButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 2 : lifoCheckBox.isSelected() ? 3 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                            else if(firstUsed%4==2){
                                                JOptionPane.showMessageDialog(searchCacheButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_3.setValueAt(tag, integerIndex, 2);
                                                model1_3.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_3.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable3, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 3 : lifoCheckBox.isSelected() ? 2 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                            else if(firstUsed%4==3){
                                                JOptionPane.showMessageDialog(searchCacheButton,
                                                        "The cache line is full, adopting " + (fifoCheckBox.isSelected() ? "fifo" : (lifoCheckBox.isSelected() ? "lifo" : "random")) + " replacement policy");                                                model1.setValueAt(tag, integerIndex, 2);
                                                model1_2.setValueAt(tag, integerIndex, 2);
                                                model1_2.setValueAt("Block " + integerBlockOffset, integerIndex, 3);
                                                int nrCol = mainMemoryTable.getColumnCount();
                                                for (int j = 0; j < nrCol; j++)
                                                    highlightCell(mainMemoryTable, integerBlockOffset, j, Color.GREEN);
                                                model1_2.setValueAt("1", integerIndex, 1); //valid becomes 1
                                                highlightCell(cacheTable2, integerIndex, 2, Color.GREEN);
                                                firstUsed = fifoCheckBox.isSelected() ? 3-firstUsed : lifoCheckBox.isSelected() ? 1 : (int) (Math.random() * 4);
                                                mainMemoryTable.revalidate();
                                                mainMemoryTable.repaint();
                                            }
                                        }
                                    }
                                }
                            }
                    );

                    simulationFrame.revalidate();
                    simulationFrame.repaint();
                }
            }
        });

        addInstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                instructionTextField.setText(generateHexSmallerThan(Integer.parseInt(memorySizeComboBox.getSelectedItem().toString())));
                setAllCellsBackgroundColor(cacheTable, Color.WHITE);
                setAllCellsBackgroundColor(mainMemoryTable, Color.WHITE);
                setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                setAllCellsBackgroundColor(cacheTable1, Color.WHITE);

            }
        });

        //instruction breakdown table

        JLabel instructionBreakdownLabel = new JLabel("Address breakdown:");
        instructionBreakdownLabel.setBounds(300, 20, 200, 20);
        instructionBreakdownLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(instructionBreakdownLabel);

        String []columnNames = {"Tag", "Index", "Offset"};
        instructionBreakdownTable.setBounds(300, 60, 260, 40);
        instructionBreakdownTable.setRowHeight(20);
        model.removeRow(0);
        model.insertRow(0,columnNames);
        instructionBreakdownTable.setEnabled(true);
        simulationFrame.getContentPane().add(instructionBreakdownTable);

        //cache tables 4 way

        JLabel cacheTableLabel = new JLabel("Cache tables:");
        cacheTableLabel.setBounds(300, 120, 200, 20);
        cacheTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(cacheTableLabel);

        JScrollPane scrollPane = new JScrollPane(cacheTable);
        scrollPane.setBounds(270, 160, 300, 200);
        //scrollPane.setVisible(true);
        cacheTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane);
        model1.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        JScrollPane scrollPane1 = new JScrollPane(cacheTable1);
        scrollPane1.setBounds(270, 400, 300, 200);
        //scrollPane.setVisible(true);
        cacheTable1.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane1);
        model1_1.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        JScrollPane scrollPane2 = new JScrollPane(cacheTable2);
        scrollPane2.setBounds(580, 400, 300, 200);
        //scrollPane.setVisible(true);
        cacheTable2.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane2);
        model1_2.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});

        JScrollPane scrollPane3 = new JScrollPane(cacheTable3);
        scrollPane3.setBounds(580, 160, 300, 200);
        //scrollPane.setVisible(true);
        cacheTable3.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane3);
        model1_3.setColumnIdentifiers(new String[]{"index", "Valid", "Tag", "Data", "Dirty"});


        //main memory table

        JLabel mainMemoryTableLabel = new JLabel("Main memory table:");
        mainMemoryTableLabel.setBounds(900, 20, 400, 20);
        mainMemoryTableLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        simulationFrame.getContentPane().add(mainMemoryTableLabel);

        JScrollPane scrollPane4 = new JScrollPane(mainMemoryTable);
        scrollPane4.setBounds(900, 60, 460, 200);
        //scrollPane.setVisible(true);
        mainMemoryTable.setRowHeight(20);
        simulationFrame.getContentPane().add(scrollPane4);



        simulationFrame.setVisible(true);
    }

    private void createReplacementFrame(){
        JFrame policyFrame=new JFrame("Replacement policy demo");
        policyFrame.setSize(650,500);
        policyFrame.setLayout(null);

        JLabel replacementType=new JLabel("Replacement policy");
        replacementType.setBounds(20,20,80,20);
        //replacementType.setLocation(20,20);
        policyFrame.getContentPane().add(replacementType);

        JComboBox replacementBox = new JComboBox<>(new String[]{"FIFO", "LIFO", "Random"});
        replacementBox.setBounds(100,20,80,20);
        policyFrame.getContentPane().add(replacementBox);

        JButton addRandomInstruction= new JButton("Add instruction");
        addRandomInstruction.setBounds(60,50,100,30);
        policyFrame.getContentPane().add(addRandomInstruction);

        JTable instrTable=new JTable(0,1);
        instrTable.setBounds(200,20,400,300);
        policyFrame.getContentPane().add(instrTable);

        DefaultTableModel modelT = (DefaultTableModel) instrTable.getModel();

        //add row to table
        addRandomInstruction.addActionListener(new ActionListener() {
            int k=0;
            public void actionPerformed(ActionEvent e) {
                modelT.addRow(new Object[]{generateHexInstruction()});
                k++;
                if(k%19==0 && replacementBox.getSelectedItem().toString().equals("FIFO")) {
                    JOptionPane.showMessageDialog(addRandomInstruction, "Memory is full. Replacing the first instruction");
                    modelT.removeRow(0);
                    modelT.insertRow(0, new Object[]{generateHexInstruction()});
                    highlightCell(instrTable,0,0,Color.YELLOW);
                }
                else if(k%19==0 && replacementBox.getSelectedItem().toString().equals("LIFO")) {
                    JOptionPane.showMessageDialog(addRandomInstruction, "Memory is full. Replacing the last instruction");
                    modelT.removeRow(18);
                    modelT.insertRow(18, new Object[]{generateHexInstruction()});
                    highlightCell(instrTable,18,0,Color.YELLOW);
                }
                else if(k%19==0 && replacementBox.getSelectedItem().toString().equals("Random")) {
                    JOptionPane.showMessageDialog(addRandomInstruction, "Memory is full. Replacing a random instruction");
                    int random=(int)(Math.random()*19);
                    modelT.removeRow(random);
                    modelT.insertRow(random, new Object[]{generateHexInstruction()});
                    highlightCell(instrTable,random,0,Color.YELLOW);
                }
            }
        });

        JButton clearTableButton=new JButton("Reset");
        clearTableButton.setBounds(60,100,100,30);
        policyFrame.getContentPane().add(clearTableButton);

        clearTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAllCellsBackgroundColor(instrTable,Color.WHITE);
                modelT.setRowCount(0);
                instrTable.revalidate();
                instrTable.repaint();
            }
        });

        policyFrame.setVisible(true);
    }

    private void selectAssociativity() {
        JFrame associativityFrame = new JFrame("N-Way Set Associative");
        associativityFrame.setLayout(null);
        associativityFrame.setSize(400, 300);
        associativityFrame.setBackground(new Color(255, 165, 0));

        JLabel associativityLabel = new JLabel("Select associativity:");
        associativityLabel.setBounds(20, 20, 150, 20);
        associativityFrame.getContentPane().add(associativityLabel);

        JComboBox associativityComboBox = new JComboBox<>(new String[]{"Fully associative", "2", "4"});
        associativityComboBox.setBounds(180, 20, 140, 20);
        associativityFrame.getContentPane().add(associativityComboBox);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 50, 100, 30);
        associativityFrame.getContentPane().add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(associativityComboBox.getSelectedItem().toString().equals("2")){
                    create2WayCacheSimulationForm();
                    clearTableAndSetWhite(cacheTable);
                    clearTableAndSetWhite(cacheTable1);
                    clearTableAndSetWhite(mainMemoryTable);
                    setAllCellsBackgroundColor(instructionBreakdownTable, Color.WHITE);
                }
                else if(associativityComboBox.getSelectedItem().toString().equals("4") ) {
                    create4WayCacheSimulationForm();
                    clearTableAndSetWhite(cacheTable);
                    clearTableAndSetWhite(cacheTable1);
                    clearTableAndSetWhite(cacheTable2);
                    clearTableAndSetWhite(cacheTable3);
                    clearTableAndSetWhite(mainMemoryTable);
                }
                else if(associativityComboBox.getSelectedItem().toString().equals("Fully associative")) {
                    createFullyAssociativeForm();
                    clearTableAndSetWhite(cacheTable);
                    clearTableAndSetWhite(cacheTable1);
                    clearTableAndSetWhite(cacheTable2);
                    clearTableAndSetWhite(cacheTable3);
                    clearTableAndSetWhite(mainMemoryTable);
                }
            }
        });

        associativityFrame.setVisible(true);
    }

    public void clearTableAndSetWhite(JTable table) {
        // Get the table model
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Clear all rows
        model.setRowCount(0);

        // Set row count to initial size
        model.setRowCount(table.getRowCount());

        // Set all cells background color to white
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                table.getCellRenderer(row, col).getTableCellRendererComponent(table, null, false, false, row, col).setBackground(Color.WHITE);
            }
        }

        // Repaint the table for changes to take effect
        table.repaint();
    }
}