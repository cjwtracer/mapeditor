package tool.account;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;

public class Login {
	static final String ACCOUNT_FILE = System.getProperty("user.home") + File.separator + ".MapEditor" + File.separator + ".account";
	
	public Login(){
	}
	
	public boolean verify() throws IOException{
		FileInputStream fis = null;
		String name = null;
		String password = null;
		try{
			fis = new FileInputStream(ACCOUNT_FILE);
			DataInputStream dis = new DataInputStream(fis);
			name = dis.readUTF();
			password = dis.readUTF();
			dis.close();
		}catch(IOException ex){
			System.err.println("Loading account failure!\n");
			throw ex;
		}finally{
			try{if(fis != null)fis.close();}catch(IOException e){}
		}
		boolean permit = false;
		String path = "http://192.168.2.124:8090/webdata/webdata!getData.action";
		URL url = new URL(path + "?name=" + name + "&password=" + password);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(true);
		String content = "";
		try{
			urlConnection.setReadTimeout(5000);
			InputStream is = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String line = bufferedReader.readLine();
			while(line != null){
				sb.append(line);
			}
			bufferedReader.close();
			
			String ecod = urlConnection.getContentEncoding();
			if (ecod == null)
				ecod = Charset.defaultCharset().name();
			content = new String(sb.toString().getBytes(), ecod);
		}catch(SocketTimeoutException ex1){
			System.out.println("Response timeout\n");
			throw ex1;
		}catch(IOException ex){
			System.err.println("Get verification failure!\n");
			throw ex;
		}
		permit = content.equals("0");
		return permit;
	}

}
