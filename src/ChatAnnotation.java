/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/chat")
public class ChatAnnotation {

    private static final String GUEST_PREFIX = "emmm";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation> connections =
            new CopyOnWriteArraySet<>();

    private String nickname;
    private Session session;

    public ChatAnnotation() {
        nickname = GUEST_PREFIX;
    }


    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
        System.out.println(message);
        if (message.startsWith("Connect//")){
            nickname=message.substring(9);
            System.out.println(nickname);
        }else broadcast(message);
    }




    @OnError
    public void onError(Throwable t) throws Throwable {
    }


    private void broadcast(String msg) {
        String from;
        String to;
        String content;
        String text=msg;
        if (msg.indexOf("//:") != -1){
            from=msg.substring(0,msg.indexOf("//:"));
            msg=msg.substring(msg.indexOf("//:")+3);
        }else{
            from="System";
        }
        if(msg.indexOf("\\\\:")!=-1){
            to=msg.substring(0,msg.indexOf("\\\\:"));
            msg=msg.substring(msg.indexOf("\\\\:")+3);
        }else{
            to="all";
        }
        content=msg;
        System.out.println("From:"+from+" To:"+to+" :"+content+"\n"+text);
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    if (from.equals(client.nickname)||to.equals(client.nickname)){
                        client.session.getBasicRemote().sendText(text);
                    }else if (from.equals("all")||to.equals("all")){
                        client.session.getBasicRemote().sendText(text);
                    }
                }
            } catch (IOException e) {
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}
