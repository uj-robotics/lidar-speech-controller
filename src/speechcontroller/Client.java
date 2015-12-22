package speechcontroller;

import org.json.JSONObject;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
 
public class Client implements Runnable {
	
    JSONObject packet;
    public static final String SERVER_IP = "192.168.0.2";
    public static final int SERVER_PORT = 5678;
   
    private String mServerMessage;
 
    private boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    Thread backgroundThread;
 
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }
 
    public void stopClient() {
        mRun = false;
 
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
 
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    } 
   
    public Client(JSONObject packet) {
        this.packet = packet;
    }
    
    @Override
    public void run() {
 
        mRun = true;
 
        try {
 
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
 
            Socket socket = new Socket(serverAddr, SERVER_PORT);
            try {
 
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                sendMessage("!" + packet.toString().length() + "!" + packet.toString());
 
                Thread.sleep(500);
 
            } catch (Exception e) {
 
                e.printStackTrace();
 
            } finally {
 
                socket.close();
            }
 
        } catch (Exception e) {
 
           e.printStackTrace();
 
        }
    }
}

