Using the Java Client Library for Prometheus
In this lab, we will use the Prometheus Java client libraries to collect metrics for the /limesAvailable endpoint and expose a metrics endpoint that Prometheus can scrape. Then, test it by configuring Prometheus to scrape metrics from the app.

Add instrumentation to track the following metrics for the /limesAvailable endpoint:

total_requests (counter) — The total number of requests processed by the /limesAvailable endpoint during the lifetime of the application process.
inprogress_requests (gauge) — The number of requests currently being processed by the limesAvailable endpoint.
Some additional information you will need to be aware of:

The Java source code can be found on the Prometheus server at /home/cloud_user/content-prometheusdd-limedrop-svc.
From the project directory, you can run the application with the command ./gradlew clean bootRun.
While the application is running, you can access it using port 8080 on the Prometheus server.
Inside the Java project, the logic for the /limesAvailable endpoint can be found in src/main/java/com/limedrop/svc/LimeDropController.java.
The main Application class is src/main/java/com/limedrop/svc/App.java.
You can also find the application source code in GitHub at https://github.com/linuxacademy/content-prometheusdd-limedrop-svc. Check the example-solution branch for an example of the code changes needed to complete this lab.
Before We Begin
To get started, we need to log in to the Prometheus Server using the provided credentials.

Add Prometheus Client
Add Prometheus client library dependencies to the Java project.

Change to the root directory for the Java project:

cd /home/cloud_user/content-prometheusdd-limedrop-svc
Run the project to verify that it is able to compile and run before making any changes:

./gradlew clean bootRun
Once we see the text Started App in X seconds, the application is running. Use control + C to stop it.

Edit build.gradle:

vi build.gradle
Locate the dependencies block and add the following Prometheus client library dependencies:

dependencies {
  implementation 'io.prometheus:simpleclient:0.8.1'
  implementation 'io.prometheus:simpleclient_httpserver:0.8.1'

  ...

}
Run :wq to save and close the file.

If you want, you can run the project again to automatically download the dependencies and make sure it still works: ./gradlew clean bootRun

Add Instrumentation
Add instrumentation to the /limesAvailable endpoint.

Edit the controller class:
vi src/main/java/com/limedrop/svc/LimeDropController.java
Add the requested counter and gauge metrics so that the file matches the following:
package com.limedrop.svc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

@RestController
public class LimeDropController {

    static final Counter totalRequests = Counter.build()
      .name("total_requests").help("Total requests.").register();
    static final Gauge inprogressRequests = Gauge.build()
      .name("inprogress_requests").help("Inprogress requests.").register();

    @GetMapping(path="/limesAvailable", produces = "application/json")
    public String checkAvailability() {
        inprogressRequests.inc();
        totalRequests.inc();

        String response = "{\"warehouse_1\": \"58534\", \"warehouse_2\": \"72399\"}";

        inprogressRequests.dec();
        return response;
    }

}
Run the application again to make sure it compiles.
./gradlew clean bootRun
Add a Scrape Endpoint
Add a scrape endpoint to the application:

Edit the main application class.

vi src/main/java/com/limedrop/svc/App.java
Use the Prometheus HTTPServer to set up a scrape endpoint on port 8081 by editing the file to match the following:

package com.limedrop.svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

                try {
                    HTTPServer server = new HTTPServer(8081);
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

}
Run the application again to make sure it compiles:

./gradlew clean bootRun
While the application is still running, you should be able to access the /limesAvailable endpoint at http://<Prometheus Server Public IP>:8080. You can view the metrics at http://<Prometheus Server Public IP>:8081/metrics.

Access the /limesAvailable endpoint and see the total_requests counter increase each time you access the endpoint.

Test Your Setup
Test your setup by configuring Prometheus to scrape from your application.

Edit the Prometheus config:
sudo vi /etc/prometheus/prometheus.yml
Add a scrape config to scrape metrics for your app:
scrape_configs:

  ...

  - job_name: 'LimeDrop Java Svc'
    static_configs:
    - targets: ['localhost:8081']
Restart Prometheus to reload the config:
sudo systemctl restart prometheus
Run your Java app and leave it running to allow Prometheus to collect metrics:
./gradlew clean bootRun
Access Prometheus in a browser at http://<Prometheus Server Public IP>:9090. Run queries to see the metrics you are collecting for your Java app:
total_requests

inprogress_requests