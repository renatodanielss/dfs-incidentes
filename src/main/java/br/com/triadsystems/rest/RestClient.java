package br.com.triadsystems.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class RestClient {
	private String propertiesFile;
	private String restURL;
	private Set<String> keys;

	public RestClient(String propertiesFile, String restURL) {
		super();
		this.propertiesFile = propertiesFile;
		this.restURL = restURL;
	}

	public void callRest() throws ClientProtocolException, IOException {
		HttpClient restClient = HttpClientBuilder.create().build();
		HttpGet restRequest = new HttpGet(this.restURL);
		
		if (this.propertiesFile != null) {
			Properties headersProperties = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = loader.getResourceAsStream(this.propertiesFile);
			headersProperties.load(inputStream);
			
			this.keys = headersProperties.stringPropertyNames();
			for (String key : keys) {
				//System.out.println(key + ": " + headersProperties.getProperty(key));
				restRequest.setHeader(key, headersProperties.getProperty(key));
			}
		}
		
		for(Header header : restRequest.getAllHeaders()) {
			System.out.println(header.getName() + ": " + header.getValue());
		}

		HttpResponse restResponse = restClient.execute(restRequest);
		BufferedReader restContent = new BufferedReader(new InputStreamReader(restResponse.getEntity().getContent()));
		String strContent = "";
		while ((strContent = restContent.readLine()) != null) {
			System.out.println(strContent);
		}
	}
}