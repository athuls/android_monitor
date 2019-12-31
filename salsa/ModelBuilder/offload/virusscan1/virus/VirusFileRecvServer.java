package virus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import java.net.ServerSocket;

class VirusFileRecvServer {
	
	private ServerSocket m_serverSocket;

	private class DataReceiver implements Runnable {
		private Socket m_socket;
		private int m_fileCounter;

		public DataReceiver(Socket socket, int fileCounter) {
			this.m_socket = socket;
			this.m_fileCounter = fileCounter;
		}
	
		public void run() {
			try {
				// FileOutputStream fos = new FileOutputStream("savedFile" + m_fileCounter + ".txt");
                        	DataInputStream in = new DataInputStream(new BufferedInputStream(m_socket.getInputStream()));
                        	byte[] buffer = new byte[4096];

                        	int read = 0, totalRead = 0;
                        	while((read = in.read(buffer)) > 0) {
                        		// fos.write(buffer, 0, read);					
                        	        totalRead += read;
                        	}
                        	
				// fos.close();
                        	in.close();	
				if(m_fileCounter % 4 == 0) {	
					System.out.println("File counter: " + m_fileCounter + " total bytes: " + totalRead);
				}
			} catch (IOException ex) {	
				System.err.println("Failed to write client data to file " + ex.toString());
			} catch (Exception ex) {
				System.err.println("Failed to write client data to file with general ex " + ex.toString());
			}
		}
	}
	
	private void setupAndWaitForConnections() {
	    	m_serverSocket = null;
		try {	
		    	m_serverSocket = new ServerSocket(4445);
			System.out.println("Server running on socket 4445");
		} catch (IOException ex) {
			System.err.println("Couldn't create socket " + ex.toString());
		}
	
		int fileCounter = 0;	
		while(true) {
			try {
				Socket socket = null;
				// System.out.println("Going to create socket");
				socket = m_serverSocket.accept();
				
				DataReceiver receiver = new DataReceiver(socket, fileCounter);
				new Thread(receiver).start();
				
				/*FileOutputStream fos = new FileOutputStream("savedFile" + fileCounter + ".txt");
				DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				byte[] buffer = new byte[4096];
				
				int read = 0, totalRead = 0;
				while((read = in.read(buffer)) > 0) {
					fos.write(buffer, 0, read);
					totalRead += read;
				}	
				fos.close();
				in.close();
				*/
				fileCounter++;
				
				// FileOutputStream fos = new FileOutputStream("testfile.txt");
				// fos.write(bytes);
			} catch (IOException ex) {	
				System.err.println("IOException reading stream " + ex.toString());
			}

		}
	}
	
	public static void main(String[] args) {
		VirusFileRecvServer virusFileRecvServer = new VirusFileRecvServer();
		virusFileRecvServer.setupAndWaitForConnections();
	}
}
