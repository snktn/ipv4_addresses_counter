### **IPv4 addresses counter**

The console app counts unique IPv4 addresses in huge files. Delimiters don't matter.
This is solution to the task  https://github.com/Ecwid/new-job/blob/master/IP-Addr-Counter.md

**Usage**

Specify the filename as a program argument.

Docker:

`$docker build -t counter https://github.com/snktn/ipv4_addresses_counter.git`

`$docker run -it --mount type=bind,source=$(pwd)/addr_file,target=/usr/addr_counter/file counter`


**Measurements**

For a file from a test task (106,6 GiB)
on AWS EC2 c5.xlarge instance (4 vCPUs, 3.4 GHz, 8 GiB memory, gp3(3000 iops, 500 MB/s ), -Xmn3g,
OpenJDK Runtime Environment (build 15+36-1562):

295 sec

```
avg-cpu:  %user   %nic   %system   %iowait   %steal   %idle
          39.73   0.00   4.33      10.20      0.00     45.07
rrqm/s  wrqm/s r/s     w/s  rMB/s  wMB/s avgrq-sz avgqu-sz await r_await w_await %util
0.00    0.02   1489.36 0.70 369.98 0.00  508.52   0.88     1.44  1.44    0.48    87.40
```








