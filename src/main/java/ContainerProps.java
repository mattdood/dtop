import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

public class ContainerProps {
    private DockerClient dockerClient = new DefaultDockerClient("unix:///var/run/docker.sock");
    private Container dockerContainer;
    private String id;
    private ImmutableList<String> names;
    private String logs;
    private String image;
    private String status;
    private Long created;
    private String ports;

    public ContainerProps(DockerClient dockerClient, Container container) {
        this.dockerContainer = container;
        this.id = container.id();
        this.names = container.names();
        this.image = container.image();
        this.status = container.status();
        this.created = container.created();
        this.ports = container.portsAsString();
        this.logs = pullLogs();
    }

    private String pullLogs() {
        String logs = "";
        try (LogStream stream = dockerClient.logs(this.id, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
            logs = stream.readFully().toString();
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public void refresh() {
        this.id = dockerContainer.id();
        this.names = dockerContainer.names();

        try (LogStream stream = dockerClient.logs(this.id, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
            this.logs = stream.readFully().toString();
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        this.image = dockerContainer.image();
        this.status = dockerContainer.status();
        this.created = dockerContainer.created();
        this.ports = dockerContainer.portsAsString();
    }

    public String getId() {
        return id;
    }

    public String getNameOrId() {
        if (this.names.isEmpty()) {
            return id;
        }
        return names.toString();
    }

    public String getLogs() {
        this.logs = pullLogs();
        return logs;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public Long getCreated() {
        return created;
    }

    public String getPorts() {
        return ports;
    }
}
