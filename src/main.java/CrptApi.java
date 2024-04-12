package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class CrptApi {

	private final TimeUnit timeUnit;
	private final int requestLimit;
	private int requestCount = 0;
	private long lastRequestTime = System.currentTimeMillis();

	public CrptApi(TimeUnit timeUnit, int requestLimit) {
		this.requestLimit = requestLimit;
		this.timeUnit = timeUnit;
	}

	public synchronized HttpResponse<String> createDocument(Document document, String signature) {
		long currentTime = System.currentTimeMillis();

		if (requestCount >= requestLimit && currentTime - lastRequestTime < 1000) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
				.POST(HttpRequest.BodyPublishers.ofString(toJson(document)))
				.header("Signature", signature)
				.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				requestCount++;
				lastRequestTime = System.currentTimeMillis();
			} else {
				System.out.println("Failed to create document: " + response.body());
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return response;

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

	@JsonFormat
	static
	class Document {
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

		@JsonCreator
		public Document(@JsonProperty("description") Description description,
						@JsonProperty("doc_id") String doc_id,
						@JsonProperty("doc_status") String doc_status,
						@JsonProperty("doc_type") String doc_type,
						@JsonProperty("importRequest") boolean importRequest,
						@JsonProperty("owner_inn") String owner_inn,
						@JsonProperty("participant_inn") String participant_inn,
						@JsonProperty("producer_inn") String producer_inn,
						@JsonProperty("production_date") String production_date,
						@JsonProperty("products") Product[] products,
						@JsonProperty("reg_date") String reg_date,
						@JsonProperty("reg_number") String reg_number) {

			this.description = description;
			this.doc_id = doc_id;
			this.doc_status = doc_status;
			this.doc_type = doc_type;
			this.importRequest = importRequest;
			this.owner_inn = owner_inn;
			this.participant_inn = participant_inn;
			this.producer_inn = producer_inn;
			this.production_date = production_date;
			this.products = products;
			this.reg_date = reg_date;
			this.reg_number = reg_number;
		}

		@Override
		public String toString() {
			return "Document{" +
					"description=" + description +
					", doc_id='" + doc_id + '\'' +
					", doc_status='" + doc_status + '\'' +
					", doc_type='" + doc_type + '\'' +
					", importRequest=" + importRequest +
					", owner_inn='" + owner_inn + '\'' +
					", participant_inn='" + participant_inn + '\'' +
					", producer_inn='" + producer_inn + '\'' +
					", production_date='" + production_date + '\'' +
					", products=" + Arrays.toString(products) +
					", reg_date='" + reg_date + '\'' +
					", reg_number='" + reg_number + '\'' +
					'}';
		}
	}

	@JsonFormat
	static
	class Description {
		private String participantInn;

		@JsonCreator
		public Description(@JsonProperty("participantInn") String participantInn) {
			this.participantInn = participantInn;
		}

		@Override
		public String toString() {
			return "Description{" +
					"participantInn='" + participantInn + '\'' +
					'}';
		}
	}

	@JsonFormat
	static
	class Product {
		private String certificate_document;
		private String certificate_document_date;
		private String certificate_document_number;
		private String owner_inn;
		private String producer_inn;
		private String production_date;
		private String tnved_code;
		private String uit_code;
		private String uitu_code;

		@JsonCreator
		public Product(@JsonProperty("certificate_document") String certificate_document,
					   @JsonProperty("certificate_document_date") String certificate_document_date,
					   @JsonProperty("certificate_document_number") String certificate_document_number,
					   @JsonProperty("owner_inn") String owner_inn,
					   @JsonProperty("producer_inn") String producer_inn,
					   @JsonProperty("production_date") String production_date,
					   @JsonProperty("tnved_code") String tnved_code,
					   @JsonProperty("uit_cod") String uit_code,
					   @JsonProperty("uitu_code") String uitu_code) {

			this.certificate_document = certificate_document;
			this.certificate_document_date = certificate_document_date;
			this.certificate_document_number = certificate_document_number;
			this.owner_inn = owner_inn;
			this.producer_inn = producer_inn;
			this.production_date = production_date;
			this.tnved_code = tnved_code;
			this.uit_code = uit_code;
			this.uitu_code = uitu_code;
		}

		@Override
		public String toString() {
			return "Product{" +
					"certificate_document='" + certificate_document + '\'' +
					", certificate_document_date='" + certificate_document_date + '\'' +
					", certificate_document_number='" + certificate_document_number + '\'' +
					", owner_inn='" + owner_inn + '\'' +
					", producer_inn='" + producer_inn + '\'' +
					", production_date='" + production_date + '\'' +
					", tnved_code='" + tnved_code + '\'' +
					", uit_code='" + uit_code + '\'' +
					", uitu_code='" + uitu_code + '\'' +
					'}';
		}
	}

	public enum TimeUnit {
		MILISECOND,
		SECOND,
		MINUTE,
		HOUR
	}
}
