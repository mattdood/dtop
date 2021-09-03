import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws DockerException, InterruptedException {

        try {
            final DockerClient dockerClient = new DefaultDockerClient("unix:///var/run/docker.sock");
            final List<Container> dockerContainers = dockerClient.listContainers();

            Map<String, ContainerProps> dockerContainersMap = new HashMap<>();
            for (Container container : dockerContainers) {
                ContainerProps props = new ContainerProps(dockerClient, container);
                dockerContainersMap.put(props.getId(), props);
            }

            Dashboard dash = new Dashboard(dockerContainersMap);
        }
        catch (DockerException | InterruptedException | IOException ex) {
            System.out.println(ex.toString());
        }
    }

}
