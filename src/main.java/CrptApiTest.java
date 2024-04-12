package main.java;

import java.io.IOException;
import java.net.URISyntaxException;

public class CrptApiTest {
		public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

			CrptApi crptApi = new CrptApi(CrptApi.TimeUnit.SECOND, 10);

			CrptApi.Document document = new CrptApi.Document(
					new CrptApi.Description("participantInn"),
					"doc_id",
					"doc_status",
					"doc_type",
					true,
					"owner_inn",
					"participant_inn",
					"producer_inn",
					"production_date",
					new CrptApi.Product[]{new CrptApi.Product("certificate_document",
							"certificate_document_date",
							"certificate_document_number",
							"owner_inn", "producer_inn",
							"production_date",
							"tnved_code",
							"uit_code",
							"uitu_code")},
					"reg_date",
					"reg_number");

			print(document);

			crptApi.createDocument(document,  "signature");
		}

		public static void print(CrptApi.Document document){
			System.out.println(document);
		}
	}

