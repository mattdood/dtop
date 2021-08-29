import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Dashboard {

    private Map<String, ContainerProps> dockerContainersMap;

    public Dashboard (Map<String, ContainerProps> dockerContainersMap) {
        this.dockerContainersMap = dockerContainersMap;
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

            Panel dockerContainerPanel = new Panel(new GridLayout(1));
            for (String key : this.dockerContainersMap.keySet()) {
                dockerContainerPanel.addComponent(new Label(this.dockerContainersMap.get(key).getNameOrId()));
            }

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
            row1Panel.addComponent(dockerContainerPanel.withBorder(Borders.singleLine("Containers: ")));
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
