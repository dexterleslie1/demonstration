## 过滤条件

排除arp、mdns、udp协议并且排除源地址或者目标地址为192.168.1.182

```
not arp and not (ip.src == 192.168.1.182 or ip.dst == 192.168.1.182) and not mdns and not udp
```
