package main.java;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CrptApi {
	private TimeUnit timeUnit;
	private int requestLimit;

	public CrptApi(TimeUnit timeUnit, int requestLimit){

	}

	public synchronized void createDocument(Document document) throws URISyntaxException, IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
				.POST(HttpRequest.BodyPublishers.ofString(toJson(document)))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private String toJson(Document document) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(document);
		} catch (IOException e) {
			System.out.println("Failed to convert document to JSON: " + e.getMessage());
			return "";
		}
	}


	private class Document {
		private Description description;
		private String doc_id;
		private String doc_status;
		private String doc_type;
		private boolean importRequest;
		private String owner_inn;
		private String participant_inn;
		private String producer_inn;
		private String production_date;
		private Product[] products;
		private String reg_date;
		private String reg_number;
	}

	private class Description {
		private String participantInn;
	}

	private class Product {
		private String certificate_document;
		private String certificate_document_date;
		private String certificate_document_number;
		private String owner_inn;
		private String producer_inn;
		private String production_date;
		private String tnved_code;
		private String uit_code;
		private String uitu_code;
	}
}
