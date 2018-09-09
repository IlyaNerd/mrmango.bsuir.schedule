package mrmango.bsuir.schedule.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Configuration
@ComponentScan("mrmango.bsuir.schedule")
@PropertySource("file:application.properties")
@Import({EmailConfig.class, ScheduleConfig.class})
public class AppConfig {

    @Bean
    public JsonFactory jsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    @Bean
    public List<String> googleScopes() {
        return Collections.singletonList(CalendarScopes.CALENDAR);
    }

    @Bean
    @SneakyThrows
    public Credential credential(NetHttpTransport netHttpTransport,
                                 JsonFactory jsonFactory,
                                 List<String> googleScopes,
                                 @Value("${credentials.client.key}") String clientKey,
                                 @Value("${credentials.folder}") String credentialsFolder) {
        // Load client secrets.
        InputStream in = new FileInputStream(clientKey);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                netHttpTransport, jsonFactory, clientSecrets, googleScopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File(credentialsFolder)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    @Bean
    @SneakyThrows
    public NetHttpTransport netHttpTransport() {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    public Calendar googleCalendarService(NetHttpTransport netHttpTransport,
                                          Credential credential,
                                          JsonFactory jsonFactory,
                                          @Value("${application.name}") String applicationName) {
        return new Calendar.Builder(netHttpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }

    @Bean
    @SneakyThrows
    public boolean initDirs(@Value("${download.dir}") String dir) {
        try {
            Files.createDirectories(Paths.get(dir));
            return true;
        } catch (FileAlreadyExistsException ignored) {
            return false;
        }
    }
}
