Collecting_Linux_Server_Metrics_with_Prometheus
Introduction
Prometheus uses a pull model to periodically scrape metric data from targets. In order to collect data, you will need to set up an entity that is able to provide metric data, and then configure the Prometheus server to scrape it. In this lab, you will walk through the process of configuring Prometheus monitoring for a Linux server.

Solution
Log in to the LimeDrop web server using the credentials provided on the lab page:

ssh cloud_user@<LIMEDROP_PUBLIC_IP_ADDRESS>
Note: When copying and pasting code into Vi from the lab guide, first enter :set paste (and then i to enter insert mode) to avoid adding unnecessary spaces and hashes.

Install and Configure Node Exporter on the LimeDrop Web Server
Create a user and group that will be used to run Node Exporter:

[cloud_user@limedrop]$ sudo useradd -M -r -s /bin/false node_exporter
Download the Node Exporter binary:

[cloud_user@limedrop]$ wget https://github.com/prometheus/node_exporter/releases/download/v0.18.1/node_exporter-0.18.1.linux-amd64.tar.gz
Extract the Node Exporter binary:

[cloud_user@limedrop]$ tar xvfz node_exporter-0.18.1.linux-amd64.tar.gz
Copy the Node Exporter binary to the appropriate location:

[cloud_user@limedrop]$ sudo cp node_exporter-0.18.1.linux-amd64/node_exporter /usr/local/bin/
Set ownership on the Node Exporter binary:

[cloud_user@limedrop]$ sudo chown node_exporter:node_exporter /usr/local/bin/node_exporter
Create a systemd unit file for Node Exporter:

[cloud_user@limedrop]$ sudo vi /etc/systemd/system/node_exporter.service
Define the Node Exporter service in the unit file:

[Unit]
Description=Prometheus Node Exporter
Wants=network-online.target
After=network-online.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target
Save and exit the file by pressing Escape followed by :wq!.

Make sure systemd picks up the changes we made:

[cloud_user@limedrop]$ sudo systemctl daemon-reload
Start the node_exporter service:

[cloud_user@limedrop]$ sudo systemctl start node_exporter
Enable the node_exporter service so it will automatically start at boot:

[cloud_user@limedrop_web]$ sudo systemctl enable node_exporter
Test that your Node Exporter is working by making a request to it from localhost:

[cloud_user@limedrop]$ curl localhost:9100/metrics
You should see some metric data in response to this request.

Configure Prometheus to Scrape Metrics from the LimeDrop Web Server
Open a new terminal session.

Log in to the Prometheus server:

ssh cloud_user@<PROMETHEUS_PUBLIC_IP_ADDRESS>
Edit the Prometheus config file:

[cloud_user@prometheus]$ sudo vi /etc/prometheus/prometheus.yml
Locate the scrape_configs section and add the following beneath it (ensuring it's indented to align with the existing job_name section):

...

  - job_name: 'LimeDrop Web Server'
    static_configs:
    - targets: ['10.0.1.102:9100']

...
Save and exit the file by pressing Escape followed by :wq!.

Restart Prometheus to load the new config:

[cloud_user@prometheus]$ sudo systemctl restart prometheus
Navigate to the Prometheus expression browser in your web browser using the public IP address of your Prometheus server: <PROMETHEUS_SERVER_PUBLIC_IP>:9090.

In the expression field (the box at the top of the page), paste in the following query to verify you are able to get some metric data from the LimeDrop web server:

node_filesystem_avail_bytes{job="LimeDrop Web Server"}
Click Execute. We should then see several results.

