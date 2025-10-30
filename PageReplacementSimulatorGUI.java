import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Interface Swing para o PageReplacementSimulator
 * Permite inserir a sequência, número de frames, escolher algoritmo
 * e visualizar passo a passo em uma tabela. Também integra o gráfico
 * já existente em PageReplacementSimulator.showBarChart.
 */
public class PageReplacementSimulatorGUI {

    private JFrame frame;
    private JTextField seqField;
    private JSpinner framesSpinner;
    private JComboBox<String> algoCombo;
    private JButton runBtn, prevBtn, nextBtn, chartBtn;
    private JTable table;
    private JPanel resultsPanel; // Container para múltiplas tabelas
    private JScrollPane resultsScrollPane;
    private JLabel summaryLabel;

    private PageReplacementSimulator.Result currentResult;
    private List<PageReplacementSimulator.Result> allResults; // para mode "Todos"
    private int currentStepIndex = 0;
    private boolean isAllMode = false; // indica se está em modo "Todos"

    // Cores customizadas
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(230, 126, 34);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public PageReplacementSimulatorGUI() {
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Page Replacement Simulator - GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 700);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_COLOR);

        // Mostrar tutorial primeiro
        showTutorial();

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(BG_COLOR);
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        // Título
        JLabel titleLabel = new JLabel("📊 Simulador de Substituição de Páginas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        top.add(titleLabel, c);

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        JLabel seqLabel = new JLabel("Sequência (ex: 7,0,1,2,0,3...):");
        seqLabel.setFont(new Font("Arial", Font.BOLD, 12));
        seqLabel.setForeground(TEXT_COLOR);
        top.add(seqLabel, c);
        seqField = new JTextField(40);
        seqField.setText("7,0,1,2,0,3,0,4,2,3,0,3,2");
        seqField.setFont(new Font("Arial", Font.PLAIN, 11));
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 3;
        top.add(seqField, c);

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        JLabel framesLabel = new JLabel("Frames:");
        framesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        framesLabel.setForeground(TEXT_COLOR);
        top.add(framesLabel, c);
        framesSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 100, 1));
        ((JSpinner.DefaultEditor) framesSpinner.getEditor()).getTextField().setFont(new Font("Arial", Font.PLAIN, 11));
        c.gridx = 1;
        c.gridy = 2;
        top.add(framesSpinner, c);

        c.gridx = 2;
        c.gridy = 2;
        JLabel algoLabel = new JLabel("Algoritmo:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        algoLabel.setForeground(TEXT_COLOR);
        top.add(algoLabel, c);
        algoCombo = new JComboBox<>(new String[] { "Todos", "FIFO", "LRU", "Relógio", "Ótimo" });
        algoCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        c.gridx = 3;
        c.gridy = 2;
        top.add(algoCombo, c);

        runBtn = new JButton("▶ Executar");
        runBtn.setFont(new Font("Arial", Font.BOLD, 12));
        runBtn.setBackground(SUCCESS_COLOR);
        runBtn.setForeground(Color.WHITE);
        runBtn.setFocusPainted(false);
        runBtn.setBorderPainted(false);
        runBtn.addActionListener(this::onRun);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        top.add(runBtn, c);

        prevBtn = new JButton("⬅ Anterior");
        prevBtn.setFont(new Font("Arial", Font.BOLD, 12));
        prevBtn.setBackground(PRIMARY_COLOR);
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setFocusPainted(false);
        prevBtn.setBorderPainted(false);
        prevBtn.addActionListener((_) -> showStep(currentStepIndex - 1));
        prevBtn.setEnabled(false);
        c.gridx = 1;
        c.gridy = 3;
        top.add(prevBtn, c);

        nextBtn = new JButton("Próximo ➡");
        nextBtn.setFont(new Font("Arial", Font.BOLD, 12));
        nextBtn.setBackground(PRIMARY_COLOR);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorderPainted(false);
        nextBtn.addActionListener((_) -> {
            showStep(currentStepIndex + 1);
            checkIfLastStep();
        });
        nextBtn.setEnabled(false);
        c.gridx = 2;
        c.gridy = 3;
        top.add(nextBtn, c);

        chartBtn = new JButton("📈 Gráfico");
        chartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        chartBtn.setBackground(WARNING_COLOR);
        chartBtn.setForeground(Color.WHITE);
        chartBtn.setFocusPainted(false);
        chartBtn.setBorderPainted(false);
        chartBtn.addActionListener((_) -> onShowChart());
        c.gridx = 3;
        c.gridy = 3;
        top.add(chartBtn, c);

        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 13));
        summaryLabel.setForeground(SECONDARY_COLOR);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        top.add(summaryLabel, c);

        frame.getContentPane().add(top, BorderLayout.NORTH);

        // Tabela única para modo single
        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        // Panel para múltiplas tabelas (modo "Todos")
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(BG_COLOR);

        // ScrollPane que será usado para ambos os modos
        resultsScrollPane = new JScrollPane(table);
        frame.getContentPane().add(resultsScrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void showTutorial() {
        JDialog tutorialDialog = new JDialog(frame, "📖 Explicação dos Algoritmos", true);
        tutorialDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        tutorialDialog.setSize(800, 700);
        tutorialDialog.setLocationRelativeTo(frame);
        tutorialDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titleLabel = new JLabel("📚 Bem-vindo ao Simulador de Substituição de Páginas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Área de texto com scroll
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setMargin(new Insets(15, 15, 15, 15));
        textArea.setBackground(BG_COLOR);
        textArea.setForeground(TEXT_COLOR);

        String tutorialText = "BEM-VINDO AO SIMULADOR DE SUBSTITUICAO DE PAGINAS!" +
                "\n\nELEMENTOS DA INTERFACE:" +
                "\n\n1. SEQUENCIA (ex: 7,0,1,2,0,3...)" +
                "\n   - Insira a sequencia de referencias de pagina separadas por virgula." +
                "\n   - Esta eh a ordem em que as paginas serao acessadas na memoria." +
                "\n\n2. FRAMES" +
                "\n   - Numero de espacos disponiveis na memoria." +
                "\n   - Quanto menor o valor, mais faltas de pagina." +
                "\n\n3. ALGORITMO" +
                "\n   - TODOS: Executa os 4 algoritmos simultaneamente" +
                "\n   - FIFO: First In, First Out (fila simples)" +
                "\n   - LRU: Least Recently Used (pagina menos usada recentemente)" +
                "\n   - RELOGIO: Variacao do LRU com bit de uso" +
                "\n   - OTIMO: Algoritmo otimo (remove pagina usada mais tarde)" +
                "\n\nCOMO USAR:" +
                "\n\n1. Insira a sequencia de paginas" +
                "\n2. Defina o numero de frames disponiveis" +
                "\n3. Escolha um algoritmo (recomendado: 'Todos' para comparar)" +
                "\n4. Clique em 'Executar'" +
                "\n5. Use 'Anterior' e 'Proximo' para navegar passo a passo" +
                "\n6. Um grafico sera exibido automaticamente com os resultados" +
                "\n\nINTERPRETANDO OS RESULTADOS:" +
                "\n\n- Algoritmo: Nome do algoritmo executado" +
                "\n- F0, F1, F2...: Estado dos frames apos a operacao" +
                "\n- Falta: '*' indica falta de pagina (miss), espaco indica acerto (hit)" +
                "\n- O objetivo eh minimizar o numero de faltas de pagina!";

        textArea.setText(tutorialText);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(BG_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton okBtn = new JButton("OK, Entendi!");
        okBtn.setFont(new Font("Arial", Font.BOLD, 13));
        okBtn.setBackground(SUCCESS_COLOR);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorderPainted(false);
        okBtn.setPreferredSize(new Dimension(150, 35));
        okBtn.addActionListener((_) -> tutorialDialog.dispose());
        buttonPanel.add(okBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        tutorialDialog.add(mainPanel);
        tutorialDialog.setVisible(true);
    }

    private void onRun(ActionEvent ev) {
        String seq = seqField.getText().trim();
        int frames = (Integer) framesSpinner.getValue();
        List<Integer> refs;
        try {
            refs = PageReplacementSimulator.parseRefs(seq);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Sequência inválida: " + ex.getMessage());
            return;
        }

        String choice = (String) algoCombo.getSelectedItem();
        allResults = new ArrayList<>();

        if ("Todos".equals(choice)) {
            isAllMode = true;
            allResults.add(PageReplacementSimulator.simulateFIFO(refs, frames));
            allResults.add(PageReplacementSimulator.simulateLRU(refs, frames));
            allResults.add(PageReplacementSimulator.simulateClock(refs, frames));
            allResults.add(PageReplacementSimulator.simulateOptimal(refs, frames));
            summaryLabel.setText(buildSummary(allResults));
            currentStepIndex = 0;
            updateTableForAllAlgorithms(frames);
            showStep(0);
            prevBtn.setEnabled(true);
            nextBtn.setEnabled(true);
        } else {
            isAllMode = false;
            switch (choice) {
                case "FIFO":
                    currentResult = PageReplacementSimulator.simulateFIFO(refs, frames);
                    break;
                case "LRU":
                    currentResult = PageReplacementSimulator.simulateLRU(refs, frames);
                    break;
                case "Relógio":
                    currentResult = PageReplacementSimulator.simulateClock(refs, frames);
                    break;
                case "Ótimo":
                    currentResult = PageReplacementSimulator.simulateOptimal(refs, frames);
                    break;
                default:
                    currentResult = PageReplacementSimulator.simulateFIFO(refs, frames);
            }
            summaryLabel.setText(String.format("%s - %d faltas", currentResult.name, currentResult.faults));
            currentStepIndex = 0;
            updateTableForFrames(frames);
            showStep(0);
            prevBtn.setEnabled(true);
            nextBtn.setEnabled(true);
        }
    }

    private void checkIfLastStep() {
        if (isAllMode) {
            int maxSteps = allResults.get(0).steps.size();
            if (currentStepIndex >= maxSteps - 1) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(1000); // 1 segundo de espera
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    PageReplacementSimulator.showBarChart(allResults);
                });
            }
        } else {
            if (currentResult != null && currentStepIndex >= currentResult.steps.size() - 1) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(1000); // 1 segundo de espera
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    List<PageReplacementSimulator.Result> singleResult = new ArrayList<>();
                    singleResult.add(currentResult);
                    PageReplacementSimulator.showBarChart(singleResult);
                });
            }
        }
    }

    private void updateTableForAllAlgorithms(int frames) {
        // Cria colunas: Ref + F0..Fn-1 + Falta
        String[] cols = new String[2 + frames];
        cols[0] = "Ref";
        for (int i = 0; i < frames; i++) {
            cols[1 + i] = "F" + i;
        }
        cols[cols.length - 1] = "Falta";

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
    }

    private void updateTableForFrames(int frames) {
        // coluna Ref + F0..Fn-1 + Falta
        String[] cols = new String[2 + frames];
        cols[0] = "Ref";
        for (int i = 0; i < frames; i++)
            cols[1 + i] = "F" + i;
        cols[cols.length - 1] = "Falta";
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
    }

    private void showStep(int idx) {
        if (isAllMode) {
            showStepAllAlgorithms(idx);
        } else {
            showStepSingleAlgorithm(idx);
        }
    }

    private void showStepSingleAlgorithm(int idx) {
        if (currentResult == null)
            return;
        if (idx < 0)
            idx = 0;
        if (idx >= currentResult.steps.size())
            idx = currentResult.steps.size() - 1;
        currentStepIndex = idx;
        PageReplacementSimulator.Step st = currentResult.steps.get(idx);

        // Trocar para visualização de tabela única
        resultsScrollPane.setViewportView(table);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // TABELA - SOMENTE DADOS (sem título)
        Object[] dataRow = new Object[model.getColumnCount()];
        dataRow[0] = st.ref;
        for (int i = 0; i < st.framesSnapshot.size() && i + 1 < dataRow.length; i++) {
            Integer v = st.framesSnapshot.get(i);
            dataRow[1 + i] = v == null ? "-" : v.toString();
        }
        dataRow[dataRow.length - 1] = st.fault ? "*" : " ";
        model.addRow(dataRow);

        summaryLabel.setText(String.format("%s - Passo %d/%d - %d faltas", currentResult.name, currentStepIndex + 1,
                currentResult.steps.size(), currentResult.faults));
    }

    private void showStepAllAlgorithms(int idx) {
        if (allResults == null || allResults.isEmpty())
            return;

        // Validar índice
        int maxSteps = allResults.get(0).steps.size();
        if (idx < 0)
            idx = 0;
        if (idx >= maxSteps)
            idx = maxSteps - 1;
        currentStepIndex = idx;

        // Limpar o panel de resultados
        resultsPanel.removeAll();

        // Pegar número de frames
        int frames = allResults.get(0).steps.get(0).framesSnapshot.size();

        // Nomes dos algoritmos
        String[] algoNames = { "FIFO", "LRU", "Relógio", "Ótimo" };

        // Para cada algoritmo: criar TÍTULO (JLabel) + TABELA (JTable)
        for (int algoIdx = 0; algoIdx < allResults.size(); algoIdx++) {
            PageReplacementSimulator.Result result = allResults.get(algoIdx);
            PageReplacementSimulator.Step st = result.steps.get(idx);

            // ===== TÍTULO FORA DA TABELA =====
            JLabel titleLabel = new JLabel(">>> " + algoNames[algoIdx] + " <<<");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(PRIMARY_COLOR);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
            resultsPanel.add(titleLabel);

            // ===== TABELA SOMENTE COM DADOS =====
            DefaultTableModel model = new DefaultTableModel();

            // Colunas
            model.addColumn("Ref");
            for (int i = 0; i < frames; i++) {
                model.addColumn("F" + i);
            }
            model.addColumn("Falta");

            // Dados
            Object[] dataRow = new Object[1 + frames + 1];
            dataRow[0] = st.ref;
            for (int i = 0; i < st.framesSnapshot.size(); i++) {
                Integer v = st.framesSnapshot.get(i);
                dataRow[1 + i] = v == null ? "-" : v.toString();
            }
            dataRow[dataRow.length - 1] = st.fault ? "*" : " ";
            model.addRow(dataRow);

            JTable algoTable = new JTable(model) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            algoTable.setFont(new Font("Arial", Font.PLAIN, 11));
            algoTable.setRowHeight(25);
            algoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            algoTable.getTableHeader().setBackground(PRIMARY_COLOR);
            algoTable.getTableHeader().setForeground(Color.WHITE);
            algoTable.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Wrapper para a tabela com altura fixa
            JPanel tableWrapper = new JPanel(new BorderLayout());
            tableWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            tableWrapper.add(algoTable.getTableHeader(), BorderLayout.NORTH);
            tableWrapper.add(algoTable, BorderLayout.CENTER);
            tableWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            tableWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            resultsPanel.add(tableWrapper);

            // ===== ESPAÇO VAZIO =====
            if (algoIdx < allResults.size() - 1) {
                resultsPanel.add(Box.createVerticalStrut(20));
            }
        }

        // Atualizar o scroll para mostrar o resultsPanel
        resultsScrollPane.setViewportView(resultsPanel);
        resultsScrollPane.revalidate();
        resultsScrollPane.repaint();

        // Atualizar label com resumo
        StringBuilder summary = new StringBuilder("Passo " + (currentStepIndex + 1) + "/" + maxSteps + " | Referência: "
                + allResults.get(0).steps.get(idx).ref + " | ");
        for (int i = 0; i < allResults.size(); i++) {
            PageReplacementSimulator.Result r = allResults.get(i);
            summary.append(algoNames[i]).append("=").append(r.faults).append(" faltas  ");
        }
        summaryLabel.setText(summary.toString());
    }

    private String buildSummary(List<PageReplacementSimulator.Result> results) {
        StringBuilder sb = new StringBuilder("Resumo: ");
        for (PageReplacementSimulator.Result r : results) {
            sb.append(String.format("%s=%d faltas | ", r.name, r.faults));
        }
        return sb.toString();
    }

    private void onShowChart() {
        // se o usuário selecionou "Todos", gera os quatro resultados e chama
        // showBarChart
        String choice = (String) algoCombo.getSelectedItem();
        String seq = seqField.getText().trim();
        int frames = (Integer) framesSpinner.getValue();
        List<Integer> refs;
        try {
            refs = PageReplacementSimulator.parseRefs(seq);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Sequência inválida: " + ex.getMessage());
            return;
        }

        if ("Todos".equals(choice)) {
            List<PageReplacementSimulator.Result> results = new ArrayList<>();
            results.add(PageReplacementSimulator.simulateFIFO(refs, frames));
            results.add(PageReplacementSimulator.simulateLRU(refs, frames));
            results.add(PageReplacementSimulator.simulateClock(refs, frames));
            results.add(PageReplacementSimulator.simulateOptimal(refs, frames));
            PageReplacementSimulator.showBarChart(results);
        } else {
            // mostrar gráfico com apenas um algoritmo (único bar)
            List<PageReplacementSimulator.Result> results = new ArrayList<>();
            switch (choice) {
                case "FIFO":
                    results.add(PageReplacementSimulator.simulateFIFO(refs, frames));
                    break;
                case "LRU":
                    results.add(PageReplacementSimulator.simulateLRU(refs, frames));
                    break;
                case "Relógio":
                    results.add(PageReplacementSimulator.simulateClock(refs, frames));
                    break;
                case "Ótimo":
                    results.add(PageReplacementSimulator.simulateOptimal(refs, frames));
                    break;
                default:
                    results.add(PageReplacementSimulator.simulateFIFO(refs, frames));
            }
            PageReplacementSimulator.showBarChart(results);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PageReplacementSimulatorGUI());
    }
}
