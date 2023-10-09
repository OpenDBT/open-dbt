package com.highgo.opendbt.experiment.terminal.entity;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author highgo
 */
public class ConnectInfo {

    private WebSocketSession webSocketSession;
    private JSch jsch;
    private Channel channel;

    public ConnectInfo() {}

    public ConnectInfo(WebSocketSession webSocketSession, JSch jsch, Channel channel) {
        this.webSocketSession = webSocketSession;
        this.jsch = jsch;
        this.channel = channel;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public JSch getJsch() {
        return jsch;
    }

    public void setJsch(JSch jsch) {
        this.jsch = jsch;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "ConnectInfo{" +
                "webSocketSession=" + webSocketSession +
                ", jsch=" + jsch +
                ", channel=" + channel +
                '}';
    }
}
