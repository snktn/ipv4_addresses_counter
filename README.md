### **IPv4 addresses counter**

The console app counts unique IPv4 addresses in huge files. Delimiters don't matter.
This is solution to the task  https://github.com/Ecwid/new-job/blob/master/IP-Addr-Counter.md

**Usage**

Specify the filename as a program argument.

**Measurements**

For a file from a test task (106,6 GiB)
on AWS EC2 c5.xlarge instance (4 vCPUs, 3.4 GHz, 8 GiB memory, gp3(3000 iops, 500 MB/s ), -Xmx7g,
OpenJDK Runtime Environment (build 15+36-1562):

323 sec

```
avg-cpu:  %user   %nic   %system   %iowait   %steal   %idle
          63.41   0.00   5.19      10.03      0.00     21.36
rrqm/s  wrqm/s r/s     w/s  rMB/s  wMB/s avgrq-sz avgqu-sz await r_await w_await %util
0.00    0.04   2185.67 0.49 336.72 0.00  315.44   1.25     0.93  0.93    0.20    79.13
```








