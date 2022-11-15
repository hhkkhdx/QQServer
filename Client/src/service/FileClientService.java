package service;

import common.Message;
import common.MessageType;

import java.io.*;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  20:44
 * @Version: 1.0
 */
public class FileClientService {

    public void sentToOne(String src, String dest, String sender, String receiver) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMesType(MessageType.MESSAGE_FILE_MESSAGE);
        FileInputStream fis = null;
        File file = new File(src);
        byte[] bytes = new byte[(int) file.length()];
        // 读取文件
        try {
            fis = new FileInputStream(file);
            fis.read(bytes);
            message.setSrc(src);
            message.setDest(dest);
            message.setFileBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println(message.getSender() + " 给 " + message.getReceiver() + " 发送文件 " + src +
                " 到对方电脑的目录 " + dest);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream((ManageClientConnectServerThread.getClientConnectServerThread(message.getSender()).getSocket().getOutputStream()));
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


}
