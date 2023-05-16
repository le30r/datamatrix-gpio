import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

public class SortingServiceHttpClient {
    HttpClient client = HttpClient.newHttpClient();
    private String address;

    public SortingServiceHttpClient(String address) {
        this.address = address;
    }

    public int getSortingLineParameter(int id) throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(address + "/line?delivery=" + id + "&sortingCentre=1"))
                .header("Accept", "text/plain")
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 409) {
            return -1;
        }
        if (response.statusCode() == 400) {
            return -2;
        }
        return Integer.parseInt(response.body());
    }


}
