/**
 * Echo client for DAS4 warmup exercises
 *
 * This client first attempts to connect to port 8765 on localhost
 *
 * If successful, it then reads each line from standard input, writes
 * that line to the server to which it connected, receives the echoed
 * line from that server, and then displays the line received on standard out
 *
 * The program expects to receive a string as args[0] that is used when
 * displaying the echoed line - if invoked without an argument, it uses the
 * string "0"
 */

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class EchoClient {
    public static void main(String[] args) throws IOException {

       Socket echoSocket = null;
       PrintWriter out = null;
       BufferedReader in = null;
	   String x = "0";

        try {
            echoSocket = new Socket("localhost", 8765);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(
                 new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: localhost.");
            System.exit(1);
        }

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	String userInput;

	if (args.length != 0)
	    x = args[0];
	while ((userInput = stdIn.readLine()) != null) {
            try {
                Thread.sleep(1000);		// sleep for one second
            } catch (InterruptedException e) {}
	    out.println(userInput);
	    System.out.format("EchoClient%s:%s\n", x, in.readLine());
	}

	out.close();
	in.close();
	stdIn.close();
	echoSocket.close();
    }
}

