package com.highgo.opendbt.experiment.terminal.utils;


import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;

/**
 * @author highgo
 * @date 2021/12/2 15:15
 */
public class DockerUtil {

    public static void execute(String ip,DockerAction dockerAction)throws Exception{
        DockerClient docker = DefaultDockerClient.builder().uri("http://".concat(ip).concat(":2375")).apiVersion("v1.35").build();
        dockerAction.action(docker);
        docker.close();
    }

    public static <T> T query(String ip,DockerQuery<T> dockerQuery)throws Exception{
        DockerClient docker = DefaultDockerClient.builder().uri("http://".concat(ip).concat(":2375")).apiVersion("v1.35").build();
        T result=dockerQuery.action(docker);
        docker.close();
        return result;
    }

    public interface DockerAction {
        void action(DockerClient docker) throws Exception;
    }

    public interface DockerQuery<T> {
        T action(DockerClient docker) throws Exception;
    }
}
