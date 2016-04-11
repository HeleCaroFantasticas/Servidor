import java.net.*;
import java.util.Scanner;
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
			        clientSocket = serverSocket.accept(); /**Aceptando nueva conexion con cliente*/
			    } catch (IOException e) {
			        System.err.println("Accept failed.");
			    }
			    //System.out.println("Connected");
			    
			    InputStreamReader out = new InputStreamReader(clientSocket.getInputStream()); 
				BufferedReader in = new BufferedReader(out);

				String textFromClient;
				textFromClient = in.readLine(); /**Lee texto del cliente*/
				
				
				
				
				
				/**códigos de error*/
				String camino=textFromClient;
				camino=camino.replace("GET", "");
				camino=camino.replace("POST", "");
				camino=camino.replace("HTTP", "");
				camino=camino.replace(" ", "");
				camino=camino.replace("/1.1", "");
				
				String textToClient = "";
				if (camino.length()==1){
					textToClient = "HTTP/1.1 200 OK\r\n";//Cuerpo\n\n
					/**Encabezados*/
					String line = in.readLine();
					while (!line.isEmpty()) {
						System.out.println(line);
						line = in.readLine();
					}
					
					textToClient+="\n\n";
				} else {
					camino=camino.replace("/", "\\");
					File f = new File(System.getProperty("user.dir") + camino);
					if(f.exists() && !f.isDirectory()) { 
						textToClient = "HTTP/1.1 200 OK\r\n";//Cuerpo\n\n
						/**Encabezados*/
						String line = in.readLine();
						while (!line.isEmpty()) {
							System.out.println(line);
							line = in.readLine();
						}
						
						/**Existe el archivo*/
						if (textFromClient.contains("GET")){
							//agregar el cuerpo del GET
							
							textToClient += readFile(System.getProperty("user.dir") + camino);
							textToClient += "\n\n";
						} else if (textFromClient.contains("POST")){
							//agregar el cuerpo del POST
						}
					} else if (f.exists() && f.isDirectory()){ 
						textToClient = "HTTP/1.1 200 OK\r\n\r\nCuerpo\n\n";
						/**Existe el directorio*/
					}
					else {
						textToClient = "ERROR 404\n\n";
					}
				}
				
				/**respuesta al cliente*/
				clientSocket.getOutputStream().write(textToClient.getBytes("UTF-8")); 

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
	
	private String readFile(String pathname) throws IOException {

	    File file = new File(pathname);
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = new Scanner(file);
	    String lineSeparator = "\n";

	    try {
	        while(scanner.hasNextLine()) {        
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        return fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}
}
