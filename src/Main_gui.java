import gui.CompilerFrame;

import javax.swing.SwingUtilities;

//MAIN ENTRY POINT
public class Main_gui {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompilerFrame frame = new CompilerFrame();
            frame.setVisible(true);
        });
    }
}

