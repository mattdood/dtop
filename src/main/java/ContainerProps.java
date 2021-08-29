import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

public class ContainerProps {
    private DockerClient dockerClient = new DefaultDockerClient("unix:///var/run/docker.sock");
    private String id;
    private ImmutableList<String> names;
    private String logs;
    private String image;
    private String status;
    private Long created;
    private String ports;

    public ContainerProps(DockerClient dockerClient, Container container) {
        this.id = container.id();
        this.names = container.names();

        try (LogStream stream = dockerClient.logs(this.id, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
            this.logs = stream.readFully().toString();
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        this.image = container.image();
        this.status = container.status();
        this.created = container.created();
        this.ports = container.portsAsString();

    }

    private void refreshLogs() {
        try (LogStream stream = this.dockerClient.logs(this.id, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
            this.logs = stream.readFully().toString();
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
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
