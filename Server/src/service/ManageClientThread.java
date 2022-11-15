package service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  14:41
 * @Version: 1.0
 */
public class ManageClientThread {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    public static ServerConnectClientThread getClientThread(String userId) {
        return hm.get(userId);
    }

    public static List<String> getOnlineUsers() {
        return new Vector<>(hm.keySet());
    }

    public static boolean removeClientThread(String userId) {
        return hm.remove(userId) != null;
    }


}
