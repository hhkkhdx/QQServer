package service;

import common.Message;
import common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  19:46
 * @Version: 1.0
 */
public class MessageClientService {

    public void sendMessageToOne(String sendId, String receiverId, String content) {
        // 构建message 
        Message message = new Message();
        message.setSender(sendId);
        message.setReceiver(receiverId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        message.setMesType(MessageType.MESSAGE_COMMON_MESSAGE);
        System.out.println(sendId + " 对 " + receiverId + " 说 " + content);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(sendId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void sendMessageToAll(String sendId, String content) {
        // 构建message
        Message message = new Message();
        message.setSender(sendId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        message.setMesType(MessageType.MESSAGE_MESSAGE_TO_ALL);
        System.out.println(sendId + " 对 所有人 说 " + content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(sendId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
