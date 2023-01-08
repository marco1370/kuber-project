Advanced_Prometheus_Queries
Introduction
The Prometheus Query Language (PromQL) provides a variety of tools that enable you to transform your raw metric data into useful and actionable information. In this lab, you will have the opportunity to explore some advanced features of Prometheus queries as you build queries to solve slightly complex problems.

Solution
Log in to the server using the credentials provided:

ssh cloud_user@<PUBLIC_IP_ADDRESS>
Write a Query to Determine Which Instances Have High CPU Utilization
Access the Prometheus expression browser in your web browser:

http://<PROMETHEUS_SERVER_PUBLIC_IP>:9090
Run a query to add the CPU usage in the system and user modes for each instance. Then, filter the results to only instances where the combined number of CPU seconds is more than 10000:

(node_cpu_seconds_total{mode="system"} + ignoring(mode) node_cpu_seconds_total{mode="user"}) > 10000
Click Execute.

On the Prometheus server, open the output file:

vi report.md
In the appropriate section, record the list of instance names from the results of the Prometheus query.

Get the Per-Second Rate of Increase in HTTP Request Duration for All Instances
Run a query from the Prometheus expression browser:

rate(http_request_duration_seconds_sum[5m])
Click Execute.

Copy all of the output, including the element data and values.

In the output file, paste in the data obtained using the query at the end of the file.

Save and exit the file by pressing Escape followed by :wq.

