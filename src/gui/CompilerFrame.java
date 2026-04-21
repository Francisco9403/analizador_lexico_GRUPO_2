package gui;

import service.CompilerService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;


public class CompilerFrame extends JFrame {

    private static final Path DEFAULT_INPUT_PATH = Path.of("src", "test/test.txt");

    private final JTextArea sourceArea;
    private final JTextArea outputArea;
    private final CompilerService compilerService;
    private final JLabel cursorStatusLabel;

    public CompilerFrame() {
        super("Compilador - Analizador Lexico/Sintactico");

        this.compilerService = new CompilerService();
        this.sourceArea = new JTextArea();
        this.outputArea = new JTextArea();
        this.cursorStatusLabel = new JLabel("Linea: 1, Columna: 1");

        configureUi();
        setDefaultExample();
    }

    private void configureUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Interfaz de Compilador", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        sourceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setEditable(false);

        sourceArea.addCaretListener(e -> updateCursorStatus());
        sourceArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCursorStatus();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCursorStatus();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCursorStatus();
            }
        });

        JScrollPane sourceScroll = new JScrollPane(sourceArea);
        sourceScroll.setBorder(javax.swing.BorderFactory.createTitledBorder("Codigo fuente"));

        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(javax.swing.BorderFactory.createTitledBorder("Salida del compilador"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sourceScroll, outputScroll);
        splitPane.setResizeWeight(0.55);
        add(splitPane, BorderLayout.CENTER);

        JPanel actions = getJPanel();

        JPanel footer = new JPanel(new BorderLayout());
        footer.add(actions, BorderLayout.WEST);
        footer.add(cursorStatusLabel, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel getJPanel() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Cargar archivo");
        JButton saveBtn = new JButton("Guardar archivo");
        JButton compileBtn = new JButton("Compilar");
        JButton clearBtn = new JButton("Limpiar salida");

        loadBtn.addActionListener(e -> loadFile());
        saveBtn.addActionListener(e -> saveFile());
        compileBtn.addActionListener(e -> compileCurrentSource());
        clearBtn.addActionListener(e -> outputArea.setText(""));

        actions.add(loadBtn);
        actions.add(saveBtn);
        actions.add(compileBtn);
        actions.add(clearBtn);
        return actions;
    }

    private void setDefaultExample() {
        try {
            sourceArea.setText(Files.readString(DEFAULT_INPUT_PATH, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            outputArea.setText("No se pudo cargar src/input_1.txt: " + ex.getMessage());
        }
        sourceArea.setCaretPosition(0);
        updateCursorStatus();
    }

    private void updateCursorStatus() {
        try {
            int caretPosition = sourceArea.getCaretPosition();
            int line = sourceArea.getLineOfOffset(caretPosition);
            int column = caretPosition - sourceArea.getLineStartOffset(line);
            cursorStatusLabel.setText("Linea: " + (line + 1) + ", Columna: " + (column + 1));
        } catch (BadLocationException ex) {
            cursorStatusLabel.setText("Linea: -, Columna: -");
        }
    }

    private void compileCurrentSource() {
        String sourceCode = sourceArea.getText();
        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay codigo para compilar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String result = compilerService.compileSource(sourceCode);
        outputArea.setText(result);
        outputArea.setCaretPosition(0);
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar codigo fuente");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt", "src", "code"));

        int option = chooser.showOpenDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path path = chooser.getSelectedFile().toPath();
        try {
            sourceArea.setText(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el archivo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar codigo fuente");

        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path path = chooser.getSelectedFile().toPath();
        try {
            Files.writeString(path, sourceArea.getText(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el archivo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

