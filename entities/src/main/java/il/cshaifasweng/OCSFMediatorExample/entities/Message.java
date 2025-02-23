package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Simple but yet effective way to handle messages between the client side and the server side, without any convertors or any necessary additional code
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String command;   // e.g., "login", "logout", "getMenu", "UpdatePrice"...
    private Object[] payload; // flexible data array

    public Message(String command, Object[] payload) {
        this.command = command;
        this.payload = payload;
    }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public Object[] getPayload() { return payload; }
    public void setPayload(Object[] payload) { this.payload = payload; }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
