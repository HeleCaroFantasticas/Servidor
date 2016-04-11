import java.net.*;
import java.io.*;

public class Socket {

	public static void main(String[] args){
		try{
	        Thread t = new ServidorHTTPSimple();
	        t.start();
	    } catch(Exception e){
	        e.printStackTrace();
	    }    
	}
}


class ServidorHTTPSimple extends Thread{
	private static java.net.Socket clientSocket;
	private static ServerSocket serverSocket;

	public void run(){

		while(true){
			try{
				serverSocket = null;
			    try {
			        serverSocket = new ServerSocket(1234);
					serverSocket.setSoTimeout(50000);
			    } catch (IOException e) {
			        System.err.println("No se pudo escuchar el puerto 1234");
			        System.exit(1);
			    }
				
				clientSocket = null;
			    try {
			        clientSocket = serverSocket.accept(); //Aceptando nueva conexion con cliente
			    } catch (IOException e) {
			        System.err.println("Accept failed.");
			    }
			    //System.out.println("Connected");
			    
			    InputStreamReader out = new InputStreamReader(clientSocket.getInputStream()); 
				BufferedReader in = new BufferedReader(out);

				String textFromClient;
				textFromClient = in.readLine(); //Lee texto del cliente
				//System.out.println(textFromClient); 
				
				//códigos de error
				String camino=textFromClient;
				camino=camino.replace("GET", "");
				camino=camino.replace("HTTP", "");
				camino=camino.replace(" ", "");
				camino=camino.replace("/1.1", "");
				System.out.println(camino);
				
				String textToClient = "";
				if (camino.length()==1){
					textToClient = "HTTP/1.1 200 OK\r\n\r\nCuerpo\n\n";
				} else {
					camino=camino.replace("/", "\\");
					File f = new File(System.getProperty("user.dir") + camino);
					if(f.exists() && !f.isDirectory()) { 
						textToClient = "HTTP/1.1 200 OK\r\n\r\nCuerpo\n\n";
					}
					else {
						
					}
				}
				
				
				String line = in.readLine();
				while (!line.isEmpty()) {
					System.out.println(line);
					line = in.readLine();
				}
				
				//GET y POST regresan los mismos headers. Lo que cambia es el cuerpo
				System.out.print(System.getProperty("user.dir"));
				if (System.getProperty("user.dir").contains(camino)){
					
				} else if (!System.getProperty("user.dir").contains(camino) && camino!="") {
					textToClient = "ERROR 404\n\n";
				}
				
				if (textFromClient.contains("GET")){
					//agregar el cuerpo del GET
				} else if (textFromClient.contains("POST")){
					//agregar el cuerpo del POST

			char[] inputBuffer = new char[clientSocket.getReceiveBufferSize()];

			int inputMessageLength = in.read(inputBuffer, 0,clientSocket.getReceiveBufferSize());
                     
			String inputMsg = new String(inputBuffer, 0, inputMessageLength);
			System.out.println(inputMsg);
				}
				
				//out.print(textToClient);
				clientSocket.getOutputStream().write(textToClient.getBytes("UTF-8")); //respuesta al cliente

				out.close();
				in.close();
				clientSocket.close();
				serverSocket.close();
			} catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch(IOException e) {
					e.printStackTrace();
					break;
			}
		}
	}
}