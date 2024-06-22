package security;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi{
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;
    private final HttpClient httpClient;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        semaphore = new Semaphore(requestLimit);
        httpClient = HttpClient.newBuilder().build();

        scheduler = Executors.newScheduledThreadPool(1);
        long period = timeUnit.toMicros(1);
        scheduler.scheduleAtFixedRate(() -> semaphore.release(requestLimit - semaphore.availablePermits()), period, period, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();

        List<CrptApi.Product> products = new ArrayList<>();
        CrptApi.Product product = new CrptApi.Product();
        product.setCertificate_document("1279te712");
        product.setCertificate_document_date(localDate);
        product.setCertificate_document_number("1279te712");
        product.setOwner_inn("1279te712");
        product.setProduction_date(localDate);
        product.setTnved_code("1279te712");
        product.setUit_code("1279te712");
        product.setUitu_code("1279te712");
        products.add(product);

        Description description = new Description();
        description.setParticipantInn("Inn");

        CrptApi.Document document = new CrptApi.Document();
        document.setDescription(description);
        document.setDoc_id("131231ji312");
        document.setDoc_status("131231ji312");
        document.setDoc_type("131231ji312");
        document.setImportRequest(true);
        document.setOwner_inn("131231ji312");
        document.setParticipant_inn("131231ji312");
        document.setProducer_inn("131231ji312");
        document.setProduction_date(localDate);
        document.setProduction_type("131231ji312");
        document.setReg_datereg_date(localDate);
        document.setReg_number("131231ji312");
        document.setProducts(products);

        CrptApi crptApi = new CrptApi(TimeUnit.MILLISECONDS, 1);
        System.out.println(crptApi.tryCreateDocument(document, "signature"));

    }

    public boolean tryCreateDocument(Document document, String signature){
          try {
              semaphore.acquire();
              HttpRequest request = HttpRequest.newBuilder()
                       .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                       .header("Content-Type", "application/json")
                       .header("signature", signature)
                       .POST(HttpRequest.BodyPublishers.ofString(convertDocumentToJson(document)))
                       .build();

              HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
              System.out.println(response.body());
              //....
          } catch (IOException | InterruptedException e) {
              Thread.currentThread().interrupt();
              return false;
          } finally {
              semaphore.release();
          }
          return true;
    }

    private String convertDocumentToJson(Document document){
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create().toJson(document);
    }

    public static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private LocalDate production_date;
        private String production_type;
        private List<Product> products;
        private LocalDate reg_datereg_date;
        private String reg_number;

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public LocalDate getProduction_date() {
            return production_date;
        }

        public void setProduction_date(LocalDate production_date) {
            this.production_date = production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public LocalDate getReg_datereg_date() {
            return reg_datereg_date;
        }

        public void setReg_datereg_date(LocalDate reg_datereg_date) {
            this.reg_datereg_date = reg_datereg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }
    }

    public static class Product{
        private String certificate_document;
        private LocalDate certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private LocalDate production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        public String getCertificate_document() {
            return certificate_document;
        }

        public void setCertificate_document(String certificate_document) {
            this.certificate_document = certificate_document;
        }

        public LocalDate getCertificate_document_date() {
            return certificate_document_date;
        }

        public void setCertificate_document_date(LocalDate certificate_document_date) {
            this.certificate_document_date = certificate_document_date;
        }

        public String getCertificate_document_number() {
            return certificate_document_number;
        }

        public void setCertificate_document_number(String certificate_document_number) {
            this.certificate_document_number = certificate_document_number;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public LocalDate getProduction_date() {
            return production_date;
        }

        public void setProduction_date(LocalDate production_date) {
            this.production_date = production_date;
        }

        public String getTnved_code() {
            return tnved_code;
        }

        public void setTnved_code(String tnved_code) {
            this.tnved_code = tnved_code;
        }

        public String getUit_code() {
            return uit_code;
        }

        public void setUit_code(String uit_code) {
            this.uit_code = uit_code;
        }

        public String getUitu_code() {
            return uitu_code;
        }

        public void setUitu_code(String uitu_code) {
            this.uitu_code = uitu_code;
        }
    }

    public static class LocalDateAdapter extends TypeAdapter<LocalDate> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString(), formatter);
        }
    }

    public static class Description{
        private String participantInn;

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }
    }
}
