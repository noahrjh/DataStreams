import javafx.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamFrame extends JFrame
{
    JPanel mainPnl;
    JPanel buttonPnl;
    JPanel displayPnl;
    JPanel searchPnl;

    JTextArea leftArea;
    JTextArea rightArea;

    JScrollPane leftPane;
    JScrollPane rightPane;

    JButton loadBtn;
    JButton filterBtn;
    JButton quitBtn;

    JLabel label;

    JTextField search;

    private File selectedFile;
    private Path filePath;

    private Set set = new HashSet();

    public DataStreamFrame()
    {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createDisplayPnl();
        createButtonPnl();
        createSearchPanel();

        mainPnl.add(searchPnl, BorderLayout.NORTH);
        mainPnl.add(displayPnl, BorderLayout.CENTER);
        mainPnl.add(buttonPnl, BorderLayout.SOUTH);

        add(mainPnl);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createSearchPanel()
    {
        searchPnl = new JPanel();
        searchPnl.setLayout(new GridLayout(1,2));

        search = new JTextField();
        search.setToolTipText("Enter a search string here");
        search.setBackground(new Color(100, 100, 100));

        label = new JLabel("Search String: ");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setHorizontalAlignment(JLabel.RIGHT);

        searchPnl.add(label);
        searchPnl.add(search);
    }

    public void createDisplayPnl()
    {
        displayPnl = new JPanel();
        displayPnl.setLayout(new GridLayout(1,2));
        displayPnl.setBorder(new TitledBorder(new EtchedBorder(), ""));

        leftArea = new JTextArea();
        rightArea = new JTextArea();

        leftArea.setEditable(false);
        rightArea.setEditable(false);

        leftArea.setBackground(new Color(255, 255, 255));
        rightArea.setBackground(new Color(255, 255, 255));

        leftArea.setFont(new Font("Arial", Font.PLAIN, 18));
        rightArea.setFont(new Font("Arial", Font.PLAIN, 18));

        leftPane = new JScrollPane(leftArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPane = new JScrollPane(rightArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftPane.setToolTipText("Original file");
        rightPane.setToolTipText("Filtered File");

        displayPnl.add(leftPane);
        displayPnl.add(rightPane);
    }

    public void createButtonPnl()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1,3));
        buttonPnl.setBorder(new TitledBorder(new EtchedBorder(), ""));

        loadBtn = new JButton("Load");
        filterBtn = new JButton("Filter");
        quitBtn = new JButton("Quit");

        filterBtn.setEnabled(false);
        filterBtn.setBackground(new Color(255, 255, 255));

        loadBtn.setFont(new Font("Arial", Font.BOLD, 20));
        filterBtn.setFont(new Font("Arial", Font.BOLD, 20));
        quitBtn.setFont(new Font("Arial", Font.BOLD, 20));

        loadBtn.addActionListener(e -> {load();});
        filterBtn.addActionListener((e) -> {filter();});
        quitBtn.addActionListener((e) -> {System.exit(0);});

        buttonPnl.add(loadBtn);
        buttonPnl.add(filterBtn);
        buttonPnl.add(quitBtn);
    }

    public void load()
    {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
            filePath = selectedFile.toPath();
        }
        filterBtn.setEnabled(true);
        filterBtn.setBackground(null);
        JOptionPane.showMessageDialog(mainPnl, "File Loaded", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void filter() {
        leftArea.setText("");
        rightArea.setText("");
        String wordFilter = search.getText();
        String rec = "";
        try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath())))
        {
            Set<String> set = lines.filter(w -> w.contains(wordFilter)).collect(Collectors.toSet());
            set.forEach(w -> rightArea.append(w + "\n"));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            InputStream in =
                    new BufferedInputStream(Files.newInputStream(filePath, CREATE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
            int line = 0;
            while(reader.ready())
            {
                rec = reader.readLine();
                leftArea.append(rec + "\n");
                line++;
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
