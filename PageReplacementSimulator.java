import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simulador de Algoritmos de Substituição de Páginas
 * Algoritmos: FIFO, LRU, Relógio (Clock), Ótimo (OPT)
 *
 * Uso (console):
 *  java PageReplacementSimulator "7,0,1,2,0,3,0,4,2,3,0,3,2" 3 --verbose --gui
 *  (sequência, número de molduras, flags opcionais: --verbose para tabela passo a passo; --gui para gráfico Swing)
 *
 * Sem argumentos, o programa pergunta interativamente.
 */
public class PageReplacementSimulator {
    public static class Result {
        public final String name;
        public final int faults;
        public final List<Step> steps;
        public Result(String name, int faults, List<Step> steps) {
            this.name = name;
            this.faults = faults;
            this.steps = steps;
        }
    }

    public static class Step {
        public final int ref;
        public final List<Integer> framesSnapshot; // tamanho = frames
        public final boolean fault;
        public Step(int ref, List<Integer> framesSnapshot, boolean fault) {
            this.ref = ref;
            this.framesSnapshot = framesSnapshot;
            this.fault = fault;
        }
    }

    // ---------- FIFO ----------
    public static Result simulateFIFO(List<Integer> refs, int frames) {
        Queue<Integer> queue = new ArrayDeque<>();
        Set<Integer> inFrames = new HashSet<>();
        int faults = 0;
        List<Step> steps = new ArrayList<>();
        for (int r : refs) {
            boolean fault = false;
            if (!inFrames.contains(r)) {
                faults++; fault = true;
                if (queue.size() == frames) {
                    Integer victim = queue.poll();
                    if (victim != null) inFrames.remove(victim);
                }
                if (frames > 0) {
                    queue.offer(r);
                    inFrames.add(r);
                }
            }
            steps.add(new Step(r, snapshot(queue, frames), fault));
        }
        return new Result("FIFO", faults, steps);
    }

    private static List<Integer> snapshot(Queue<Integer> q, int frames) {
        List<Integer> list = new ArrayList<>(q);
        // Preencher com nulls até frames
        while (list.size() < frames) list.add(null);
        return list;
    }

    // ---------- LRU ----------
    public static Result simulateLRU(List<Integer> refs, int frames) {
        // LinkedHashMap com accessOrder=true mantém a ordem do mais antigo -> mais recente
        LinkedHashMap<Integer, Integer> lru = new LinkedHashMap<>(16, 0.75f, true);
        int faults = 0;
        List<Step> steps = new ArrayList<>();
        for (int r : refs) {
            boolean fault = false;
            if (!lru.containsKey(r)) {
                faults++; fault = true;
                if (lru.size() == frames && frames > 0) {
                    Integer victim = lru.keySet().iterator().next(); // menos recentemente usado
                    lru.remove(victim);
                }
            }
            if (frames > 0) lru.put(r, 1); // atualiza acesso
            steps.add(new Step(r, snapshotFromKeys(lru, frames), fault));
        }
        return new Result("LRU", faults, steps);
    }

    private static List<Integer> snapshotFromKeys(LinkedHashMap<Integer, Integer> lru, int frames) {
        // Mostrar do mais antigo ao mais recente
        List<Integer> list = new ArrayList<>(lru.keySet());
        while (list.size() < frames) list.add(null);
        return list;
    }

    // ---------- Relógio (Clock) ----------
    public static Result simulateClock(List<Integer> refs, int frames) {
        int[] frameArr = new int[Math.max(frames, 1)];
        boolean[] used = new boolean[Math.max(frames, 1)];
        Arrays.fill(frameArr, Integer.MIN_VALUE); // sentinel para "vazio"
        int ptr = 0;
        int faults = 0;
        List<Step> steps = new ArrayList<>();
        for (int r : refs) {
            boolean fault = false;
            int idx = indexOf(frameArr, r);
            if (idx >= 0) {
                used[idx] = true; // acerto: marca bit de uso
            } else {
                faults++; fault = true;
                if (frames > 0) {
                    while (used[ptr]) { // segunda chance
                        used[ptr] = false;
                        ptr = (ptr + 1) % frames;
                    }
                    frameArr[ptr] = r;
                    used[ptr] = true;
                    ptr = (ptr + 1) % frames;
                }
            }
            steps.add(new Step(r, toList(frameArr, frames), fault));
        }
        return new Result("Relógio", faults, steps);
    }

    private static int indexOf(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }

    private static List<Integer> toList(int[] arr, int frames) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < frames; i++) list.add(arr[i] == Integer.MIN_VALUE ? null : arr[i]);
        return list;
    }

    // ---------- Ótimo (OPT) ----------
    public static Result simulateOptimal(List<Integer> refs, int frames) {
        List<Integer> memory = new ArrayList<>(frames);
        int faults = 0;
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < refs.size(); i++) {
            int r = refs.get(i);
            boolean fault = false;
            if (!memory.contains(r)) {
                faults++; fault = true;
                if (memory.size() < frames) {
                    if (frames > 0) memory.add(r);
                } else if (frames > 0) {
                    int victimIndex = chooseVictimOPT(memory, refs, i + 1);
                    memory.set(victimIndex, r);
                }
            } else {
                // acerto: nada a fazer
            }
            steps.add(new Step(r, padded(memory, frames), fault));
        }
        return new Result("Ótimo", faults, steps);
    }

    private static int chooseVictimOPT(List<Integer> memory, List<Integer> refs, int start) {
        int victim = -1, farthest = -1;
        for (int m = 0; m < memory.size(); m++) {
            int page = memory.get(m);
            int nextUse = Integer.MAX_VALUE;
            for (int j = start; j < refs.size(); j++) {
                if (refs.get(j) == page) { nextUse = j; break; }
            }
            if (nextUse == Integer.MAX_VALUE) {
                return m; // nunca mais usado -> melhor vítima
            }
            if (nextUse > farthest) {
                farthest = nextUse; victim = m;
            }
        }
        return victim;
    }

    private static List<Integer> padded(List<Integer> memory, int frames) {
        List<Integer> list = new ArrayList<>(memory);
        while (list.size() < frames) list.add(null);
        return list;
    }

    // ---------- Utilidades ----------
    public static List<Integer> parseRefs(String s) {
        String[] parts = s.trim().split("[ ,;]+");
        List<Integer> res = new ArrayList<>();
        for (String p : parts) if (!p.isBlank()) res.add(Integer.parseInt(p));
        return res;
    }

    private static void printSummary(List<Result> results) {
        for (Result r : results) {
            System.out.printf("- %s - %d faltas de página%n", r.name, r.faults);
        }
    }

    private static void printVerbose(List<Result> results, List<Integer> refs, int frames) {
        int width = Math.max(5, frames);
        System.out.println("\nTabela (passo a passo):");
        for (Result r : results) {
            System.out.println("\n== " + r.name + " ==");
            System.out.println("Ref | " + java.util.stream.IntStream.range(0, frames).mapToObj(i -> "F"+i).collect(Collectors.joining(" ")) + " | Falta");
            for (Step st : r.steps) {
                String frameStr = st.framesSnapshot.stream()
                        .map(x -> x == null ? "-" : x.toString())
                        .collect(Collectors.joining(" "));
                System.out.printf("%3d | %s | %s%n", st.ref, frameStr, st.fault ? "*" : " ");
            }
            System.out.printf("Total de faltas: %d%n", r.faults);
        }
    }

    // ---------- GUI simples (extra) ----------
    public static void showBarChart(List<Result> results) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Comparativo de Faltas de Página");
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setSize(720, 480);
            f.setLocationRelativeTo(null);
            f.setLayout(new BorderLayout());

            JPanel chart = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int margin = 40;
                    int n = results.size();
                    int max = results.stream().mapToInt(r -> r.faults).max().orElse(1);
                    int barW = Math.max(30, (w - 2*margin) / (n * 2));
                    int gap = barW;
                    int x = margin;
                    // eixo y
                    g2.drawLine(margin, margin, margin, h - margin);
                    g2.drawLine(margin, h - margin, w - margin, h - margin);
                    // marcações simples
                    for (int i = 0; i <= 5; i++) {
                        int val = (int) Math.round(i * (max / 5.0));
                        int y = h - margin - (int) ((h - 2*margin) * (val / (double) max));
                        g2.drawLine(margin - 5, y, margin, y);
                        g2.drawString(String.valueOf(val), 5, y + 5);
                    }
                    for (Result r : results) {
                        int barH = (int) ((h - 2*margin) * (r.faults / (double) max));
                        int y = h - margin - barH;
                        g2.fillRect(x, y, barW, barH);
                        g2.drawRect(x, y, barW, barH);
                        // rótulos
                        String label = r.name;
                        int strW = g2.getFontMetrics().stringWidth(label);
                        g2.drawString(label, x + (barW - strW)/2, h - margin + 15);
                        g2.drawString(String.valueOf(r.faults), x + (barW/2) - 5, y - 5);
                        x += barW + gap;
                    }
                }
            };

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton savePng = new JButton(new AbstractAction("Salvar PNG…") {
                @Override public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    if (fc.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
                        FileDialogUtil.savePanelAsPNG(chart, fc.getSelectedFile().getAbsolutePath());
                    }
                }
            });
            top.add(savePng);

            f.add(top, BorderLayout.NORTH);
            f.add(chart, BorderLayout.CENTER);
            f.setVisible(true);
        });
    }

    static class FileDialogUtil {
        static void savePanelAsPNG(JPanel panel, String path) {
            try {
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(panel.getWidth(), panel.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = img.createGraphics();
                panel.paint(g2);
                g2.dispose();
                if (!path.toLowerCase().endsWith(".png")) path += ".png";
                javax.imageio.ImageIO.write(img, "png", new java.io.File(path));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Erro ao salvar: " + ex.getMessage());
            }
        }
    }

    // ---------- main ----------
    public static void main(String[] args) {
        String seqStr = null; int frames = -1; boolean verbose = false; boolean gui = false;
        if (args.length >= 2) {
            seqStr = args[0];
            frames = Integer.parseInt(args[1]);
            for (int i = 2; i < args.length; i++) {
                if ("--verbose".equalsIgnoreCase(args[i])) verbose = true;
                if ("--gui".equalsIgnoreCase(args[i])) gui = true;
            }
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Sequência de páginas (ex: 7,0,1,2,0,3,0,4,2,3,0,3,2): ");
            seqStr = sc.nextLine();
            System.out.print("Número de molduras (frames): ");
            frames = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Mostrar passo a passo? (s/n): ");
            verbose = sc.nextLine().trim().toLowerCase().startsWith("s");
            System.out.print("Abrir gráfico (GUI)? (s/n): ");
            gui = sc.nextLine().trim().toLowerCase().startsWith("s");
        }

        List<Integer> refs = parseRefs(seqStr);
        List<Result> results = new ArrayList<>();
        results.add(simulateFIFO(refs, frames));
        results.add(simulateLRU(refs, frames));
        results.add(simulateClock(refs, frames));
        results.add(simulateOptimal(refs, frames));

        System.out.println("\nResumo (faltas por algoritmo):");
        printSummary(results);
        if (verbose) printVerbose(results, refs, frames);
        if (gui) showBarChart(results);
    }
}
