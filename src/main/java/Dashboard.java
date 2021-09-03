import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.spotify.docker.client.exceptions.DockerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dashboard {

    private Map<String, ContainerProps> dockerContainersMap;
    // terminal
    // window
    private WindowBasedTextGUI textGUI;
    private Window window;
    private Panel infoPanel;
    private Label logPanel;
    private Label memoryPanel;
    private Label networkPanel;
    private Label processesPanel;

    public Dashboard (Map<String, ContainerProps> dockerContainersMap) throws DockerException, InterruptedException, IOException {
        this.dockerContainersMap = dockerContainersMap;
        this.window = initializeWindow();
        this.infoPanel = initializeInfoPanel();

        // these are containing panels
        this.logPanel = initializeLabel("");
        this.memoryPanel = initializeLabel("");
        this.networkPanel = initializeLabel("");
        this.processesPanel = initializeLabel("");
        this.textGUI = initializeTerminal();
    }

    private Panel initializePanel(int columnCount, int columnsWidth, int rowsHeight, Label content) {
        Panel initializedPanel = new Panel(new GridLayout(columnCount));
        initializedPanel.setPreferredSize(new TerminalSize(columnsWidth, rowsHeight));
        initializedPanel.addComponent(content);
        return initializedPanel;
    }

    private Label initializeLabel(String content) {
        Label initializedLabel = new Label(content);
        return initializedLabel;
    }

    private void updateLogLabel(Label label, String dockerContainerId) {
        RadioBoxList<String> dockerContainerRadio = new RadioBoxList<>();
        for (String key : this.dockerContainersMap.keySet()) {
            dockerContainerRadio.addItem(this.dockerContainersMap.get(key).getNameOrId());
        }
        ContainerProps containerProperties = this.dockerContainersMap.get(dockerContainerId);
        this.logPanel.setText(containerProperties.getLogs());
    }

    public void drawScreen() throws DockerException, InterruptedException {

        System.out.print("Here");

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;

        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.WHITE));
            final Window window = new BasicWindow("dtop");

            // screen container
            Panel containerPanel = new Panel(new GridLayout(1));
            containerPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            // start of row 1
            Panel row1Panel = new Panel(new GridLayout(3));
            row1Panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

//            Panel dockerContainerPanel = new Panel(new GridLayout(1));
//            for (String key : this.dockerContainersMap.keySet()) {
//                dockerContainerPanel.addComponent(new Label(this.dockerContainersMap.get(key).getNameOrId()));
//            }

            RadioBoxList<String> dockerContainerRadio = new RadioBoxList<>();
            for (String key : this.dockerContainersMap.keySet()) {
                dockerContainerRadio.addItem(this.dockerContainersMap.get(key).getNameOrId());
            }
            dockerContainerRadio.addListener(new RadioBoxList.Listener() {
                @Override
                public void onSelectionChanged(int selectedIndex, int previousSelection) {

                }
            });

            Panel infoPanel = new Panel(new GridLayout(2));
            infoPanel.addComponent(new Label("Id: "));
            infoPanel.addComponent(new Label("some-example-id-here"));
            infoPanel.addComponent(new Label("Names: "));
            infoPanel.addComponent(new Label("example_container_name"));
            infoPanel.addComponent(new Label("Image: "));
            infoPanel.addComponent(new Label("some_ubuntu_image"));
            infoPanel.addComponent(new Label("Created: "));
            infoPanel.addComponent(new Label("2020-07-19T12:00.00Z01"));
            infoPanel.addComponent(new Label("Status: "));
            infoPanel.addComponent(new Label("Running"));
            infoPanel.addComponent(new Label("Ports: "));
            infoPanel.addComponent(new Label("8080:8080"));

            Panel commandsPanel = new Panel(new GridLayout(2));
            commandsPanel.addComponent(new Label("Stop"));
            commandsPanel.addComponent(new Label("Restart"));
            commandsPanel.addComponent(new Label("Kill"));
            commandsPanel.addComponent(new Label("Pause"));
            commandsPanel.addComponent(new Label("Down"));

            // start of row 2
            Panel row2Panel = new Panel(new GridLayout(2));
            row2Panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            Panel logsPanel = new Panel(new GridLayout(1));
            logsPanel.setPreferredSize(new TerminalSize(50, 25));

            Panel row2Column2Panel = new Panel(new GridLayout(1));
            row2Column2Panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            Panel processesPanel = new Panel(new GridLayout(1));
            processesPanel.setPreferredSize(new TerminalSize(50, 2));

            Panel memoryUsagePanel = new Panel(new GridLayout(1));
            memoryUsagePanel.setPreferredSize(new TerminalSize(50, 2));

            Panel networkPanel = new Panel(new GridLayout(1));
            networkPanel.setPreferredSize(new TerminalSize(50, 2));

            // start of row 3
            Panel row3Panel = new Panel(new GridLayout(1));
            row3Panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            Panel managePanel = new Panel(new GridLayout(2));
            managePanel.addComponent(new Label("$ "));
            final TextBox managementCommand = new TextBox().addTo(managePanel);
            managePanel.setPreferredSize(new TerminalSize(50, 2));

            // add components to row parent
            row1Panel.addComponent(dockerContainerRadio.withBorder(Borders.singleLine("Containers: ")));
            row1Panel.addComponent(infoPanel.withBorder(Borders.singleLine("Info: ")));
            row1Panel.addComponent(commandsPanel.withBorder(Borders.singleLine("Commands: ")));

            row2Column2Panel.addComponent(processesPanel.withBorder(Borders.singleLine("Processes: ")));
            row2Column2Panel.addComponent(memoryUsagePanel.withBorder(Borders.singleLine("Memory Usage: ")));
            row2Column2Panel.addComponent(networkPanel.withBorder(Borders.singleLine("Network: ")));
            row2Panel.addComponent(logsPanel.withBorder(Borders.singleLine("Logs: ")));
            row2Panel.addComponent(row2Column2Panel);

            row3Panel.addComponent(managePanel.withBorder(Borders.singleLine("Manage: ")));

            // add rows to container parent
            containerPanel.addComponent(row1Panel);
            containerPanel.addComponent(row2Panel);
            containerPanel.addComponent(row3Panel);

            window.setComponent(containerPanel);
            textGUI.addWindowAndWait(window);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

    }

}
