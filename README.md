### **IPv4 addresses counter**

The console app counts unique IPv4 addresses in huge files. Delimiters don't matter.
This is solution to the task  https://github.com/Ecwid/new-job/blob/master/IP-Addr-Counter.md

**Usage**

Specify the filename as a program argument.

Docker:
$docker build -t counter https://github.com/snktn/ipv4_addresses_counter.git
$docker run -m 1280m -it --mount type=bind,source=$(pwd)/addr_file,target=/usr/addr_counter/file counter


**Measurements**

For a file from a test task (106,6 GiB)
on AWS EC2 c5.xlarge instance (4 vCPUs, 3.4 GHz, 8 GiB memory, gp3(3000 iops, 500 MB/s ), -Xmn2g -Xmx3g,
OpenJDK Runtime Environment (build 15+36-1562):

294 sec

```
avg-cpu:  %user   %nic   %system   %iowait   %steal   %idle
          39.66   0.00   4.61      12.36      0.00     43.36
rrqm/s  wrqm/s r/s     w/s  rMB/s  wMB/s avgrq-sz avgqu-sz await r_await w_await %util
0.00    0.02   1556.99 0.77 364.75 0.00  479.55   0.84     1.07  1.07    0.30    83.65
```








