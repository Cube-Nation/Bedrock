package de.cubenation.bedrock.bungee.plugin.io;

import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class OutgoingPluginMessage {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final DataOutputStream dout = new DataOutputStream(out);

    private ServerInfo server;

    public OutgoingPluginMessage(byte subChannel, ServerInfo server) {
        this.server = server;
        write(subChannel);
    }

    public OutgoingPluginMessage write(String msg) {
        try {
            dout.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public OutgoingPluginMessage write(byte msg) {
        try {
            dout.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public OutgoingPluginMessage write(float msg) {
        try {
            dout.writeFloat(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public OutgoingPluginMessage write(double msg) {
        try {
            dout.writeDouble(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public OutgoingPluginMessage write(int msg) {
        try {
            dout.writeInt(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public byte[] getMessage() {
        return out.toByteArray();
    }

    public void send() {
        server.sendData(IOVerbs.CHANNEL, getMessage());
    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        if (server != null) {
            this.server = server;
        }
    }

}
