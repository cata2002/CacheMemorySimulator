import View.CacheSimulatorForm;

import javax.swing.*;

public class  Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CacheSimulatorForm().setVisible(true));
    }
}
