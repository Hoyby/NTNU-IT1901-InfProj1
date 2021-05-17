package ajour.ui;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import ajour.json.TrackerModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TrackerContainerAccess} that sends and receives data in the
 * form of serialized {@link TrackerContainer} and {@link Tracker} objects to a REST-server.
 */

public class RemoteTrackerContainerAccess implements TrackerContainerAccess {

    private final URI serverUri;
    private final ObjectMapper objectMapper;
    private TrackerContainer trackerContainer;

    /**
     * Constructor initializing the objectmapper used for serializing as well as registering
     * the required modules.
     *
     * @param serverUri URL for the server and service that the ContainerAccess will use
     *                  to get and put data.
     */
    public RemoteTrackerContainerAccess(URI serverUri) {
        this.serverUri = serverUri;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new TrackerModule());
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Constructsa a GET-request to fetch the full {@link TrackerContainer} from the server,
     * with all the {@link Tracker} objects contained within, as well as any nested {@link ajour.core.TrackerEntry}
     * objects.
     *
     * @return The {@link TrackerContainer} currently in use by the server.
     */
    private TrackerContainer getTrackerContainer() {
        HttpRequest httpRequest = HttpRequest.newBuilder(serverUri)
            .header("Accept", "application/json")
            .GET()
            .build();
        try {
            HttpResponse<String> httpResponse =
                HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            trackerContainer = objectMapper.readValue(httpResponse.body(), TrackerContainer.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return trackerContainer;
    }

    /**
     * Constructs a PUT-request and sends the new {@link Tracker} to the server.
     *
     * @param tracker the {@link Tracker} to be added.
     */
    @Override
    public void addTracker(Tracker tracker) {
        putTracker(tracker);
    }

    /**
     * Constructs a DELETE-request with the name of the {@link Tracker} to removed in the URI,
     * and sends it to the server.
     *
     * @param name the name of the {@link Tracker} to be removed.
     */
    @Override
    public void removeTracker(String name) {
        HttpRequest httpRequest = HttpRequest
            .newBuilder(trackerUri(name))
            .header("Accept", "application/json")
            .DELETE()
            .build();
        try {
            HttpResponse<String> response =
                HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (objectMapper.readValue(response.body(), boolean.class)) {
                trackerContainer.removeTracker(name);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the name for all {@link Tracker} objects in the {@link TrackerContainer}. By keeping track of the names by
     * keeping a local TrackerContainer, this method minimizes the amount of requests sent to the server in use.
     *
     * @return a List of strings containing names for all {@link Tracker} objects on the server.
     */
    @Override
    public List<String> getTrackerNames() {
        if (trackerContainer == null) {
            trackerContainer = getTrackerContainer();
        }
        return trackerContainer
            .getTrackers()
            .stream()
            .map(Tracker::getName)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Constructs a GET-request to fetch the {@link Tracker} with the corresponding name from the
     * server.
     *
     * @param name the name of the {@link Tracker} object to be returned.
     * @return the {@link Tracker} object with the given name.
     */
    @Override
    public Tracker getTracker(String name) {
        if (name != null) {
            HttpRequest httpRequest = HttpRequest.newBuilder(trackerUri(name))
                .header("Accept", "application/json")
                .GET()
                .build();
            try {
                HttpResponse<String> httpResponse = HttpClient.newBuilder()
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return objectMapper.readValue(httpResponse.body(), Tracker.class);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Checks if the server has a {@link Tracker} object with the given name.
     *
     * @param name the name of the {@link Tracker} to be checked.
     * @return true if a {@link Tracker} with the given name exists on the server otherwise false.
     */
    @Override
    public boolean hasTracker(String name) {
        return Objects.requireNonNull(getTrackerContainer()).containsTracker(name);
    }

    /**
     * Constructs a PUT-request and sends the {@link Tracker}r that has changed to the server.
     *
     * @param tracker the changed {@link Tracker} object.
     */
    @Override
    public void trackerChanged(Tracker tracker) {
        putTracker(tracker);
    }

    /**
     * Constructs and sends a PUT-request with a serialized {@link Tracker} in the body. This
     * method is used both for sending new {@link Tracker}s as well as changed ones, so
     * that the server will either add or replace the {@link Tracker}.
     *
     * @param tracker the new or changed {@link Tracker} object to be added or replaced on the
     *                server.
     */
    private void putTracker(Tracker tracker) {
        try {
            String data = objectMapper.writeValueAsString(tracker);
            HttpRequest httpRequest = HttpRequest.newBuilder(trackerUri(tracker.getName()))
                .header("Accept", "application/json")
                .header("content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(data))
                .build();
            HttpResponse<String> httpResponse = HttpClient.newBuilder()
                .build()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (objectMapper.readValue(httpResponse.body(), boolean.class)) {
                if (trackerContainer.containsTracker(tracker.getName())) {
                    trackerContainer.removeTracker(tracker.getName());
                }
                trackerContainer.addTracker(tracker);
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error while parsing JSON");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a URI by resolving the input string against the serverUri for fetching
     * specific {@link Tracker}s from the server.
     *
     * @param name the name of the {@link Tracker} to be fetched.
     * @return the URI which corresponds to a {@link Tracker} on the server with the given name.
     */
    private URI trackerUri(String name) {
        return serverUri.resolve(URLEncoder.encode(name, StandardCharsets.UTF_8));
    }
}
