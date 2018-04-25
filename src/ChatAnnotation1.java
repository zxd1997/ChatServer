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
//package websocket.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
class User{
	private String username;
	private String password;
	private boolean online=false;
	private boolean vip=true;
	private int tot=0;
	public User(String username,String password){
		this.username=username;
		this.password=password;
	}
	public String getUsername(){
		return username;
	}
	public String getPassword(){
		return password;
	}
	public boolean isOnline(){
		return online;
	}
	public void Online(){
		online=true;
	}
	public void Offline(){
		online=false;
	}
	public void normal(){
		vip=false;
	}
	public boolean isVIP(){
		return vip;
	}
	public int gettot(){
		return tot;
	}
	public void settot(int n){
		tot=n;
	}
}
class VIPS{
	private static ArrayList<User> VIP=new ArrayList();
	public static ArrayList<User> getVIPS(){
		//VIPS();
		VIP.add(new User("xzx","xzxmjj"));
		VIP.add(new User("zxd1997","19970226"));
		VIP.add(new User("Services1","123456"));
		VIP.add(new User("Services2","123456"));
		VIP.add(new User("Services3","123456"));
		VIP.add(new User("Services4","123456"));
		VIP.add(new User("Services5","123456"));
		VIP.add(new User("Services6","123456"));
		VIP.add(new User("Services7","123456"));
		VIP.add(new User("Services8","123456"));
		VIP.add(new User("Services9","123456"));
		VIP.add(new User("Services0","123456"));
		return VIP;
	}
}
@ServerEndpoint(value = "/websocket/chat")
public class ChatAnnotation1 {

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation1> connections =
            new CopyOnWriteArraySet<>();
    private static final ArrayList<User> users=VIPS.getVIPS();
    private String nickname;
    private String other;
    public String service="";
    private Session session;
    public ChatAnnotation1() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast("",message);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        for (User tmp:users){
        	if (tmp.getUsername().equals(nickname))
        		tmp.Offline();
        }
        broadcast("",message);
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
    	other="";
    	if (message.equals("ask for services")){
    		User t=null;
    		int k=32767;
    		for (User tmp:users){
    			if (tmp.isOnline()&&tmp.isVIP()){
    				if (tmp.gettot()<k){
    					k=tmp.gettot();
    					t=tmp;
    				}
    			}
    		}
    		if (k>5&&k!=32767){
    			broadcast(nickname,"Services* service full");
    			return;
    		}
   			if (t!=null){
   				service=t.getUsername();
   				t.settot(k+1);
   				broadcast(nickname,"Services* service:"+service+" serves for you");
   				broadcast(service,"Services* you need help "+nickname);
   				return;
   			}
    		broadcast(nickname,"ServicesSorry no services online now");
    		return;
    	}
    	if (message.equals("end of services")){
    		for (User tmp:users){
    			if (tmp.getUsername().equals(service)){
    				tmp.settot(tmp.gettot()-1);
    				break;
    			}
    		}
    		broadcast(nickname,"* Service over");
    		String tmp=service;
    		service="";
    		broadcast(tmp,"Services* "+nickname+" help finishied");
    		return;
    	}
    	if (message.startsWith("Service")){
    		if (message.indexOf("To(")!=-1){
        		other=message.substring(11,message.indexOf(")"));
        		message=message.substring(message.indexOf(")")+2);
        		broadcast(other,"ServicesServices:"+message);
        		broadcast(nickname,"Services"+nickname+":	"+message);
        		return;
    		}
    		for (User tmp:users){
    			if (tmp.getUsername().equals(service)){
    				if (!tmp.isOnline()){
        				tmp.settot(0);
    					User t=null;
    		    		int k=32767;
    		    		for (User tt:users){
    		    			if (tt.isOnline()&&tt.isVIP()){
    		    				if (tt.gettot()<k){
    		    					k=tt.gettot();
    		    					t=tt;
    		    				}
    		    			}
    		    		}
    		    		if (k>5){
    		    			broadcast(nickname,"Services* sorry, service offline, other service full");
    		    			return;
    		    		}
    		   			if (t!=null){
    		   				service=t.getUsername();
    		   				t.settot(k+1);
    		   				broadcast(nickname,"Services* service offline, transfer to:"+service);
    		   				broadcast(service,"Services* you need help "+nickname);
    		   				break;
    		   			}
    		    		broadcast(nickname,"ServicesSorry no services online now");
    				}
    				break;
    			}
    		}
    		message=message.substring(8);
    		broadcast(nickname,"Services"+nickname+":"+message);
    		broadcast(service,"Services"+nickname+":"+message);
    		return;
    	}
    	if (message.startsWith("login")){
    		String username=message.substring(13,message.indexOf("password"));
    		String password=message.substring(message.indexOf("password")+8);
    		for (User tmp:users){
    			if (tmp.getUsername().equals(username)){
    				if (tmp.getPassword().equals(password)){
    					if (tmp.isOnline()){
    						broadcast("","* "+username+" already Online");
    						return;
    					}
    					nickname=username;
    					tmp.Online();
    					broadcast(nickname,"* "+nickname+" has logged in");
    					if (tmp.isVIP()){
    						broadcast(nickname,"* you are VIP");
    					}
    					return;
    				}else {
    					broadcast(nickname,"* "+username+" username and password interupted");
    					return;
    				}
   				}
    		}
    		broadcast(nickname,"* "+username+" not exists");
    		return;
    	}
    	if (message.startsWith("register")){
    		String username=message.substring(16,message.indexOf("password"));
    		String password=message.substring(message.indexOf("password")+8);
    		for (User tmp:users){
    			if (tmp.getUsername().equals(username)){
    				broadcast(nickname,"* "+username+" already exists");
    				return;
    			}
    		}
    		nickname=username;
    		broadcast(nickname,"* "+nickname+" has registered");
    		User tmp=new User(username,password);
    		tmp.Online();tmp.normal();
    		users.add(tmp);
    		return;
    	}
    	if (message.startsWith("To(")){
    		other=message.substring(3,message.indexOf(")"));
    		message=message.substring(message.indexOf(")")+2);
       	}
        broadcast(other,"*private "+nickname+":"+message);
    }


    @OnError
    public void onError(Throwable t) throws Throwable {
    	broadcast("","Chat Error: " + t.toString());
    }


    private static void broadcast(String other,String msg) {
    	String onlinelist="";
    	String servicelist="";
    	boolean flag=false;
    	if ((msg.contains("logged"))||(msg.contains("registered"))||(msg.contains("disconnected"))||(msg.contains("joined"))) flag=true;
        for (ChatAnnotation1 client : connections) {
            try {
                synchronized (client) {
                	if (flag&&(!client.nickname.startsWith("Guest")))
                		onlinelist+=client.nickname+"<br />";
                	if ((!client.service.equals(""))&&(client.service.equals(other))){
                		servicelist+=client.nickname+" ";
                	}
                	if (other.equals("")||other.equals("-1")){
                		client.session.getBasicRemote().sendText(msg);
                	}else if((client.nickname.equals(other))||(msg.startsWith(client.nickname))){
                		client.session.getBasicRemote().sendText(msg);
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
                broadcast("",message);
            }
        }
        if (flag)
        	broadcast("-1","online"+onlinelist);
        if (msg.contains("Services"))
        	broadcast(other,"ServicelistCustomers in need: "+servicelist);
    }
}
