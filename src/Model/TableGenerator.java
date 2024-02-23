package Model;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TableGenerator {

        public static JTable createTable(JFrame existingFrame, int rows, int columns) {

            DefaultTableModel model = new DefaultTableModel(rows, columns);

            JTable table = new JTable(model);

            JScrollPane scrollPane = new JScrollPane(table);

            existingFrame.getContentPane().add(scrollPane);

            existingFrame.revalidate();
            existingFrame.repaint();
            return table;
        }

    public static void highlightCell(JTable table, int row, int column, Color highlightColor) {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row1, int column1) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row1, column1);

                if (row1 == row && column1 == column) {
                    cellComponent.setBackground(highlightColor);
                } else {
                    cellComponent.setBackground(table.getBackground());
                }

                return cellComponent;
            }
        };
        table.getColumnModel().getColumn(column).setCellRenderer(cellRenderer);
    }

    public static void setAllCellsBackgroundColor(JTable table, Color backgroundColor) {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                cellComponent.setBackground(backgroundColor);

                return cellComponent;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }
    public static void main(String []args){
            JFrame f1=new JFrame();
            f1.setSize(600,400);
            TableGenerator t=new TableGenerator();
            JTable  t1=createTable(f1,3,4);
            highlightCell(t1,1,1,Color.BLUE);
            f1.setVisible(true);
    }

}
