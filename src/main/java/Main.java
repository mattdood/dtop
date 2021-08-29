import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

import java.util.List;

public class Main {

    public static void main(String[] args) throws DockerException, InterruptedException {

        try {
            final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
            final List<Container> containers = docker.listContainers();
            for (Container container : containers) {
                System.out.println(container.id());
            }
            final String logs;
            try (LogStream stream = docker.logs(containers.get(0).id(), DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
                logs = stream.readFully().toString();
                System.out.println(logs);
            }
        }
        catch (DockerException ex) {
            System.out.println(ex.toString());
        }
        catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }
    }

}
