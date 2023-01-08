Advanced Configuration for Prometheus Alerts
Introduction
Prometheus Alertmanager provides some additional useful features around the management of alerts. These features allow you to customize and tweak your alerts so they are more useful in real-world situations. In this lab, you will have the opportunity to practice using some of these Alertmanager features, including alert grouping, inhibitions, and silences.

Solution
Log in to the Prometheus server using the credentials provided:

ssh cloud_user@<PROMETHEUS_SERVER_PUBLIC_IP>
Combine the Web Server Down Alerts into a Single Group
Check Prometheus in a web browser at http://<PROMETHEUS_SERVER_PUBLIC_IP>:9090.

Click the Alerts tab. We should see the WebBadGateway alert as well as WebServer1Down and WebServer2Down.

In the terminal, open the Alertmanager configuration file:

sudo vi /etc/alertmanager/alertmanager.yml
Add a new node to routing tree to combine the WebServer.*Down alerts:

route:

  ...

  routes:
  - receiver: 'web.hook'
    group_by: ['service']
    match_re:
      alertname: 'WebServer.*Down'
Save and exit the file by pressing Escape followed by :wq.

Load the new configuration:

sudo killall -HUP alertmanager
Check Alertmanager in a web browser at http://<PROMETHEUS_SERVER_PUBLIC_IP>:9093. You should see the Web Server alerts grouped together under the group service="webserver".

Create an Inhibition to Stop the WebBadGateway Alert When a WebServerDown Alert Is Already Firing
In the terminal, edit the Alertmanager configuration file:

sudo vi /etc/alertmanager/alertmanager.yml
Add a new inhibit rule:

inhibit_rules:

  ...

  - source_match_re:
      alertname: 'WebServer.*Down'
    target_match:
      alertname: 'WebBadGateway'
Save and exit the file by pressing Escape followed by :wq.

Load the new configuration:

sudo killall -HUP alertmanager
Refresh Alertmanager in the browser. The WebBadGateway should no longer appear. You can click the Inhibited box to make it appear again.

Silence the WebServer1Down Alert
Expand the service="webserver" group.

Locate the alert with alertname="WebServer1Down", and click the Silence button for that alert.

Enter your name for Creator.

Enter "silence WebServerDown" for Comment.

Click Create.

Navigate back to the main Alertmanager page, where we should see WebServer1Down is no longer ther