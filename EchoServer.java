import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.io.PrintWriter;
  
public class EchoServer {  
  
    public static void main(String[] args) {  
          
        ServerSocket servSocket = null;   
        Socket socket = null;  
        BufferedReader in = null;  
        PrintWriter out = null;
        String msg;
                  
        try{  
              
            // create the server socket ps: only the server does it
            servSocket = new ServerSocket(8765);  
          
            // wait for a client conection to the server socket
            socket = servSocket.accept();  
              
            // io buffers
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            out = new PrintWriter(socket.getOutputStream(), true);
              
            // print the message in the socket and answer the client with a heyo
            while((msg = in.readLine()) != null) {
                System.out.println(msg);   
                out.println("heyo");
                try {
                    Thread.sleep(3000);     // sleep for one second
                } catch (InterruptedException e) {}
            }
              
        }catch(IOException e){  
            System.out.println("Algum problema ocorreu para criar ou receber o socket.");  
        }finally{  
            try{  
                // close the socket
                socket.close();  
                  
                // close the server socket
                servSocket.close();  
                  
            }catch(IOException e){}  
        }  
    }  
}  