package com.example.exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Server
{
    //服务器端口
    private static final int SERVERPORT = 54321;
    private static final String DIVIDER="/　/";
    //客户端连接
    private static Map<Socket,String> mClientList=new LinkedHashMap<>();
    //线程池
    private ExecutorService mExecutorService;
    //ServerSocket对象
    private ServerSocket mServerSocket;
    //开启服务器
    public static void main(String[] args)
    {
        new Server();
    }
    public Server()
    {
        try
        {
            //设置服务器端口
            mServerSocket = new ServerSocket(SERVERPORT);
            //创建一个线程池
            mExecutorService = Executors.newCachedThreadPool();
            System.out.println("start...");
            //用来临时保存客户端连接的Socket对象
            Socket client = null;
            while (true)
            {
                //接收客户连接并添加到list中
                client = mServerSocket.accept();
                mClientList.put(client,"");
                //开启一个客户端线程
                mExecutorService.execute(new ThreadServer(client));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //每个客户端单独开启一个线程
    static class ThreadServer implements Runnable
    {
        private Socket			mSocket;
        private BufferedReader	mBufferedReader;
        private PrintWriter		mPrintWriter;
        private String			mStrMSG;
        private String username="";
        private Connection dbConn; //数据库连接

        public ThreadServer(Socket socket) throws IOException
        {
            this.mSocket = socket;
            mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"gb2312"));
            System.out.println("user: "+this.mSocket.getInetAddress()+" come, total:" + mClientList.size());
        }

        public boolean putInCache(String receiver,String sender,String content,String date,String time){
            String insertSQL="insert into info_cache values('"+sender+"','"
                    +receiver+"','"+content+"','"+date+"','"+time+"');";
            System.out.println(insertSQL);
            try {
                Statement stat=dbConn.createStatement();
                int a=stat.executeUpdate(insertSQL);
                if(a==1) return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return false;
        }

        public List<String> getCache(String username){
            List<String> cache=new ArrayList<>();
            String selectSQL="select * from info_cache where info_receiver='"+username+"' order by info_date asc,info_time asc;";
            String deletSQL="delete from info_cache where info_receiver='"+username+"';";
            try {
                Statement stat=dbConn.createStatement();
                ResultSet rs=stat.executeQuery(selectSQL);
                while (rs.next()){
                    StringBuffer sb=new StringBuffer();
                    sb.append("msg").append(DIVIDER)
                            .append(rs.getString("info_receiver")).append(DIVIDER)
                            .append(rs.getString("info_sender")).append(DIVIDER)
                            .append(rs.getString("cache_info")).append(DIVIDER)
                            .append(rs.getString("info_date")).append(DIVIDER)
                            .append(rs.getString("info_time"));
                    cache.add(sb.toString());
                }
                stat.executeUpdate(deletSQL);
            }catch (Exception e){
                e.printStackTrace();
            }
            return cache;
        }

        public boolean connect(){
            String connectString = "jdbc:mysql://172.18.59.33:3306/webdb"
                    + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&&useSSL=false";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                dbConn = DriverManager.getConnection(connectString, "wt", "1234");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public void run()
        {
            try
            {
                while ((mStrMSG = mBufferedReader.readLine()) != null)
                {
                    System.out.println("get: "+mStrMSG);
                    String[] arr=mStrMSG.split(DIVIDER);
                    System.out.println("instr: "+arr.length+" "+arr[0]);
                    switch (arr[0]){
                        case "exit":
                            mClientList.remove(mSocket);
                            mBufferedReader.close();
                            mSocket.close();
                            break;
                        case "initial":
                            username=arr[1];
                            mClientList.replace(mSocket,username);
                            List<String> cache;
                            if(connect()){
                                cache=getCache(username);      //读取数据库中的消息缓存
                                for(int i=0;i<cache.size();i++){
                                    mStrMSG=cache.get(i);
                                    sendMessage(username);
                                }
                            }
                            else
                                System.out.println("Error: fail to connect database!");

                            break;
                        case "msg":
                            if(!mClientList.containsValue(arr[1])){           //若该用户不在线则缓存入数据库
                                if(connect()){
                                    putInCache(arr[1],arr[2],arr[3],arr[4],arr[5]);
                                }else {
                                    System.out.println("Error: fail to connect database!");
                                }
                            }else {                 //若在线则发送消息
                                sendMessage(arr[1]);
                            }
                            break;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                mClientList.remove(mSocket);
            }
        }

        //发送消息给指定客户端
        private void sendMessage(String receiver) throws IOException
        {
            System.out.println("send to "+receiver+": "+mStrMSG);
            Socket client=null;
            Iterator<Map.Entry<Socket,String>> iter= mClientList.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry<Socket,String> element = iter.next();
                if(element.getValue().equals(receiver))
                    client=element.getKey();
            }
            if(client==null){
                System.out.println("Error: receiver not found!");
                return;
            }
            mPrintWriter = new PrintWriter(client.getOutputStream(), true);
            mPrintWriter.println(mStrMSG);
            mPrintWriter.close();
        }
    }

    public static String getTime(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");//可以方便地修改日期格式
        return dateFormat.format( now );
    }


}



