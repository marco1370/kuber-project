Using HTTP Rewrites with HAProxy
Introduction
Wouldn’t it be great if we could manage all our HTTP requests, all in one place? HAProxy has the power to implement HTTP rewrites, changing the request as it moves between the client and the backend servers, transparently. In this hands-on lab, we’re going to get hands-on with HAProxy, using it to configure and test a basic HTTP rewrite. We’re also going to consolidate 2 HTTP frontends into 1, using access control lists (ACLs) to direct traffic to the correct backend, based on request hostname. Upon completion of this lab, you will be able to configure an HAProxy installation to implement a basic HTTP rewrite as well as implement HAProxy ACLs.

Solution
When the lab starts, open an SSH connection to your lab instance(s):

ssh cloud_user@PUBLIC_IP_ADDRESS
Replace PUBLIC_IP_ADDRESS with either the public IP or DNS of the instance(s). The cloud_user password has been provided with the instance information.

Create a Rewrite and Some ACLs
Relocate the /test.txt File
For this objective, you'll start by creating a new subdirectory on the site, /textfiles, and move the test.txt file there. You will handle requests for the file in its original location, /test.txt, using an HTTP rewrite in HAProxy.

Create the new /textfiles subdirectory:

for site in `seq 1 2` ; do for server in `seq 1 3` ; do podman exec site$site\_server$server mkdir /usr/share/nginx/html/textfiles ; done ; done
Move the test.txt file to the /textfiles subdirectory:

for site in `seq 1 2` ; do for server in `seq 1 3` ; do podman exec site$site\_server$server mv -v /usr/share/nginx/html/test.txt /usr/share/nginx/html/textfiles ; done ; done
Check your work on the site1_server1 container. Run the following commands:

podman exec -it site1_server1 /bin/bash
ls -la /usr/share/nginx/html
Note the test.txt file is no longer in the root of the site.

Check the /textfiles directory:

ls -la /usr/share/nginx/html/textfiles
You should see the file listed here.

Exit the container:

exit
Check the original location (/test.txt), using curl:

curl -s http://127.0.0.1:8000/test.txt
You should receive a 404, Not Found.

Test again on 8100 (site2):

curl -s http://127.0.0.1:8100/test.txt
You should see the test.txt file is no longer available in the original location.

Check the new location (/textfiles/test.txt), using curl:

curl -s http://127.0.0.1:8000/textfiles/test.txt
Check again for site2Using HTTP Rewrites with HAProxy
Introduction
Wouldn’t it be great if we could manage all our HTTP requests, all in one place? HAProxy has the power to implement HTTP rewrites, changing the request as it moves between the client and the backend servers, transparently. In this hands-on lab, we’re going to get hands-on with HAProxy, using it to configure and test a basic HTTP rewrite. We’re also going to consolidate 2 HTTP frontends into 1, using access control lists (ACLs) to direct traffic to the correct backend, based on request hostname. Upon completion of this lab, you will be able to configure an HAProxy installation to implement a basic HTTP rewrite as well as implement HAProxy ACLs.

Solution
When the lab starts, open an SSH connection to your lab instance(s):

ssh cloud_user@PUBLIC_IP_ADDRESS
Replace PUBLIC_IP_ADDRESS with either the public IP or DNS of the instance(s). The cloud_user password has been provided with the instance information.

Create a Rewrite and Some ACLs
Relocate the /test.txt File
For this objective, you'll start by creating a new subdirectory on the site, /textfiles, and move the test.txt file there. You will handle requests for the file in its original location, /test.txt, using an HTTP rewrite in HAProxy.

Create the new /textfiles subdirectory:

for site in `seq 1 2` ; do for server in `seq 1 3` ; do podman exec site$site\_server$server mkdir /usr/share/nginx/html/textfiles ; done ; done
Move the test.txt file to the /textfiles subdirectory:

for site in `seq 1 2` ; do for server in `seq 1 3` ; do podman exec site$site\_server$server mv -v /usr/share/nginx/html/test.txt /usr/share/nginx/html/textfiles ; done ; done
Check your work on the site1_server1 container. Run the following commands:

podman exec -it site1_server1 /bin/bash
ls -la /usr/share/nginx/html
Note the test.txt file is no longer in the root of the site.

Check the /textfiles directory:

ls -la /usr/share/nginx/html/textfiles
You should see the file listed here.

Exit the container:

exit
Check the original location (/test.txt), using curl:

curl -s http://127.0.0.1:8000/test.txt
You should receive a 404, Not Found.

Test again on 8100 (site2):

curl -s http://127.0.0.1:8100/test.txt
You should see the test.txt file is no longer available in the original location.

Check the new location (/textfiles/test.txt), using curl:

curl -s http://127.0.0.1:8000/textfiles/test.txt
Check again for site2:

curl -s http://127.0.0.1:8100/textfiles/test.txt
The test.txt file is available in the new location. How can you make this available via the original URL? You can use HAProxy to accomplish that!

Create an HTTP Rewrite
Fortunately, you can handle changes in your HTTP backend using HAProxy's ability to rewrite HTTP traffic that it is proxying. HAProxy works at the HTTP application layers, 5 through 7, so it can inspect and manipulate packets in those layers.

Open the /etc/haproxy/haproxy.cfg file:

sudo vi /etc/haproxy/haproxy.cfg
Scroll down to the Site Frontends section. Add the following rewrite to this section for frontend site1 and frontend site2:

Note: When copying and pasting code into Vim from the lab guide, first enter :set paste (and then i to enter insert mode) to avoid adding unnecessary spaces and hashes. To save and quit the file, press Escape followed by :wq. To exit the file without saving, press Escape followed by :q!.

```
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
```
The Site Frontends section of the file should now look like this:

# Site Frontends
frontend site1
    bind *:8000
    mode http
    default_backend site1
    acl p_ext_txt path_end -i .txt
    acl p_folder_textUsing HTTP Rewrites with HAProxy
Introduction
Wouldn’t it be great if we could manage all our HTTP requests, all in one place? HAProxy has the power to implement HTTP rewrites, changing the request as it moves between the client and the backend servers, transparently. In this hands-on lab, we’re going to get hands-on with HAProxy, using it to configure and test a basic HTTP rewrite. We’re also going to consolidate 2 HTTP frontends into 1, using access control lists (ACLs) to direct traffic to the correct backend, based on request hostname. Upon completion of this lab, you will be able to configure an HAProxy installation to implement a basic HTTP rewrite as well as implement HAProxy ACLs.

Solution
When the lab starts, open an SSH connection to your lab instance(s):

ssh cloud_user@PUBLIC_IP_ADDRESS
Replace PUBLIC_IP_ADDRESS with either the public IP or DNS of the instance(s). The cloud_user password has been provided with the instance information.

Create a Rewrite and Some ACLs
Relocate the /test.txt File
For this objective, you'll start by creating a new subdirectory on the site, /textfiles, and move the test.txt file there. You will handle requests for the file in its original location, /test.txt, using an HTTP rewrite in HAProxy.

Create the new /textfiles subdirectory:

for site in `seq 1 2` ; do for server in `seq 1 3` ; do podman exec site$site\_server$server mkdir /usr/share/nginx/html/textfiles ; done ; done
Move the test.txt file to the /textfiles subdirectory:

for site in `seq 1 2` ; do for server in `seq 1 3` ; do podman exec site$site\_server$server mv -v /usr/share/nginx/html/test.txt /usr/share/nginx/html/textfiles ; done ; done
Check your work on the site1_server1 container. Run the following commands:

podman exec -it site1_server1 /bin/bash
ls -la /usr/share/nginx/html
Note the test.txt file is no longer in the root of the site.

Check the /textfiles directory:

ls -la /usr/share/nginx/html/textfiles
You should see the file listed here.

Exit the container:

exit
Check the original location (/test.txt), using curl:

curl -s http://127.0.0.1:8000/test.txt
You should receive a 404, Not Found.

Test again on 8100 (site2):

curl -s http://127.0.0.1:8100/test.txt
You should see the test.txt file is no longer available in the original location.

Check the new location (/textfiles/test.txt), using curl:

curl -s http://127.0.0.1:8000/textfiles/test.txt
Check again for site2:

curl -s http://127.0.0.1:8100/textfiles/test.txt
The test.txt file is available in the new location. How can you make this available via the original URL? You can use HAProxy to accomplish that!

Create an HTTP Rewrite
Fortunately, you can handle changes in your HTTP backend using HAProxy's ability to rewrite HTTP traffic that it is proxying. HAProxy works at the HTTP application layers, 5 through 7, so it can inspect and manipulate packets in those layers.

Open the /etc/haproxy/haproxy.cfg file:

sudo vi /etc/haproxy/haproxy.cfg
Scroll down to the Site Frontends section. Add the following rewrite to this section for frontend site1 and frontend site2:

Note: When copying and pasting code into Vim from the lab guide, first enter :set paste (and then i to enter insert mode) to avoid adding unnecessary spaces and hashes. To save and quit the file, press Escape followed by :wq. To exit the file without saving, press Escape followed by :q!.

```
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
```
The Site Frontends section of the file should now look like this:

# Site Frontends
frontend site1
    bind *:8000
    mode http
    default_backend site1
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt

frontend site2
    bind *:8100
    mode http
    default_backend site2
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
This rewrite changes the URL path for text files (*.txt) to the /textfiles/ directory on the web server if it is not already set to /textfiles/.

Press Escape, and type :wq to write and quit the file.

Restart HAProxy to pick up the configuration change:

sudo systemctl restart haproxy
Check the original location (/test.txt), using curl for site1 and site2:

curl -s http://127.0.0.1:8000/test.txt
curl -s http://127.0.0.1:8100/test.txt
You should see that the test.txt file is now available in the original location! HAProxy is rewriting the request to pull the file from /textfiles/test.txt.

Check the new location (/textfiles/test.txt), using curl:

curl -s http://127.0.0.1:8000/textfiles/test.txt
curl -s http://127.0.0.1:8100/textfiles/test.txt
The test.txt file is also available in the new location, as the rewrite ignores the request since URL is already set to /textfiles/.

Consolidate the HTTP Frontends Using ACLs
Now you'll replace the 2 HTTP frontends with a single frontend, using ACLs, so that requests for www.site1.com are directed to the site1 backend, and requests for www.site2.com are directed to the site2 backend.

Handle Traffic by Hostname
Reopen the /etc/haproxy/haproxy.cfg file:

sudo vi /etc/haproxy/haproxy.cfg
You will now be making a single frontend for all requests coming in on port 80. First delete everything under frontend site2.

Edit the text currently under # Site Frontends so that the final file looks like this:

# Single frontend for http on port 80
frontend http-in
    bind *:80
    mode http
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
    acl site-1 hdr(host) -i www.site1.com
    acl site-2 hdr(host) -i www.site2.com
    use_backend site1 if site-1
    use_backend site2 if site-2
This will change things so that you're listening for all HTTP traffic on port 80. You'll check the domain the request is coming for, and send it to the correct backend. Remember, you haven't touched the backend configuration — you just changed the frontend.

Press Escape, and type :wq to write and quit the file.

Restart HAProxy to pick up the configuration change:

sudo systemctl restart haproxy
Test the New HTTP Configuration
First, ensure that traffic is directed to the correct backend site.

Check using curl:

curl -s http://www.site1.com/test.txt
curl -s http://www.site2.com/test.txt
The ACLs are directing traffic to the correct backend site, based on request hostnames for both site1 and site2!

Conclusion
Congratulations — you've completed this hands-on lab!files path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt

frontend site2
    bind *:8100
    mode http
    default_backend site2
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
This rewrite changes the URL path for text files (*.txt) to the /textfiles/ directory on the web server if it is not already set to /textfiles/.

Press Escape, and type :wq to write and quit the file.

Restart HAProxy to pick up the configuration change:

sudo systemctl restart haproxy
Check the original location (/test.txt), using curl for site1 and site2:

curl -s http://127.0.0.1:8000/test.txt
curl -s http://127.0.0.1:8100/test.txt
You should see that the test.txt file is now available in the original location! HAProxy is rewriting the request to pull the file from /textfiles/test.txt.

Check the new location (/textfiles/test.txt), using curl:

curl -s http://127.0.0.1:8000/textfiles/test.txt
curl -s http://127.0.0.1:8100/textfiles/test.txt
The test.txt file is also available in the new location, as the rewrite ignores the request since URL is already set to /textfiles/.

Consolidate the HTTP Frontends Using ACLs
Now you'll replace the 2 HTTP frontends with a single frontend, using ACLs, so that requests for www.site1.com are directed to the site1 backend, and requests for www.site2.com are directed to the site2 backend.

Handle Traffic by Hostname
Reopen the /etc/haproxy/haproxy.cfg file:

sudo vi /etc/haproxy/haproxy.cfg
You will now be making a single frontend for all requests coming in on port 80. First delete everything under frontend site2.

Edit the text currently under # Site Frontends so that the final file looks like this:

# Single frontend for http on port 80
frontend http-in
    bind *:80
    mode http
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
    acl site-1 hdr(host) -i www.site1.com
    acl site-2 hdr(host) -i www.site2.com
    use_backend site1 if site-1
    use_backend site2 if site-2
This will change things so that you're listening for all HTTP traffic on port 80. You'll check the domain the request is coming for, and send it to the correct backend. Remember, you haven't touched the backend configuration — you just changed the frontend.

Press Escape, and type :wq to write and quit the file.

Restart HAProxy to pick up the configuration change:

sudo systemctl restart haproxy
Test the New HTTP Configuration
First, ensure that traffic is directed to the correct backend site.

Check using curl:

curl -s http://www.site1.com/test.txt
curl -s http://www.site2.com/test.txt
The ACLs are directing traffic to the correct backend site, based on request hostnames for both site1 and site2!

Conclusion
Congratulations — you've completed this hands-on lab!:

curl -s http://127.0.0.1:8100/textfiles/test.txt
The test.txt file is available in the new location. How can you make this available via the original URL? You can use HAProxy to accomplish that!

Create an HTTP Rewrite
Fortunately, you can handle changes in your HTTP backend using HAProxy's ability to rewrite HTTP traffic that it is proxying. HAProxy works at the HTTP application layers, 5 through 7, so it can inspect and manipulate packets in those layers.

Open the /etc/haproxy/haproxy.cfg file:

sudo vi /etc/haproxy/haproxy.cfg
Scroll down to the Site Frontends section. Add the following rewrite to this section for frontend site1 and frontend site2:

Note: When copying and pasting code into Vim from the lab guide, first enter :set paste (and then i to enter insert mode) to avoid adding unnecessary spaces and hashes. To save and quit the file, press Escape followed by :wq. To exit the file without saving, press Escape followed by :q!.

```
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
```
The Site Frontends section of the file should now look like this:

# Site Frontends
frontend site1
    bind *:8000
    mode http
    default_backend site1
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt

frontend site2
    bind *:8100
    mode http
    default_backend site2
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
This rewrite changes the URL path for text files (*.txt) to the /textfiles/ directory on the web server if it is not already set to /textfiles/.

Press Escape, and type :wq to write and quit the file.

Restart HAProxy to pick up the configuration change:

sudo systemctl restart haproxy
Check the original location (/test.txt), using curl for site1 and site2:

curl -s http://127.0.0.1:8000/test.txt
curl -s http://127.0.0.1:8100/test.txt
You should see that the test.txt file is now available in the original location! HAProxy is rewriting the request to pull the file from /textfiles/test.txt.

Check the new location (/textfiles/test.txt), using curl:

curl -s http://127.0.0.1:8000/textfiles/test.txt
curl -s http://127.0.0.1:8100/textfiles/test.txt
The test.txt file is also available in the new location, as the rewrite ignores the request since URL is already set to /textfiles/.

Consolidate the HTTP Frontends Using ACLs
Now you'll replace the 2 HTTP frontends with a single frontend, using ACLs, so that requests for www.site1.com are directed to the site1 backend, and requests for www.site2.com are directed to the site2 backend.

Handle Traffic by Hostname
Reopen the /etc/haproxy/haproxy.cfg file:

sudo vi /etc/haproxy/haproxy.cfg
You will now be making a single frontend for all requests coming in on port 80. First delete everything under frontend site2.

Edit the text currently under # Site Frontends so that the final file looks like this:

# Single frontend for http on port 80
frontend http-in
    bind *:80
    mode http
    acl p_ext_txt path_end -i .txt
    acl p_folder_textfiles path_beg -i /textfiles/
    http-request set-path /textfiles/%[path] if !p_folder_textfiles p_ext_txt
    acl site-1 hdr(host) -i www.site1.com
    acl site-2 hdr(host) -i www.site2.com
    use_backend site1 if site-1
    use_backend site2 if site-2
This will change things so that you're listening for all HTTP traffic on port 80. You'll check the domain the request is coming for, and send it to the correct backend. Remember, you haven't touched the backend configuration — you just changed the frontend.

Press Escape, and type :wq to write and quit the file.

Restart HAProxy to pick up the configuration change:

sudo systemctl restart haproxy
Test the New HTTP Configuration
First, ensure that traffic is directed to the correct backend site.

Check using curl:

curl -s http://www.site1.com/test.txt
curl -s http://www.site2.com/test.txt
The ACLs are directing traffic to the correct backend site, based on request hostnames for both site1 and site2!

Conclusion
Congratulations — you've completed this hands-on lab!