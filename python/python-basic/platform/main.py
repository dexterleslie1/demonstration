#!/usr/bin/python
# -*- coding: UTF-8 -*-

import platform

var_platform = platform.platform()
var_version = platform.version()
var_architecture = platform.architecture()
var_machine = platform.machine()
var_node = platform.node()
var_processor = platform.processor()
var_system = platform.system()
var_uname = platform.uname()

"""
https://www.cnblogs.com/QQmini/p/11352192.html

apple显示如下：
获取操作系统名称及版本号 : [Darwin-19.5.0-x86_64-i386-64bit]
获取操作系统版本号 : [Darwin Kernel Version 19.5.0: Tue May 26 20:41:44 PDT 2020; root:xnu-6153.121.2~2/RELEASE_X86_64]
获取操作系统位数 : [('64bit', '')]
计算机类型 : [x86_64]
计算机的网络名称 : [MacOSdeMacBook-Pro.local]
计算机处理器信息 : [i386]
获取操作系统类型 : [Darwin]
汇总信息 : [('Darwin', 'MacOSdeMacBook-Pro.local', '19.5.0', 'Darwin Kernel Version 19.5.0: Tue May 26 20:41:44 PDT 2020; root:xnu-6153.121.2~2/RELEASE_X86_64', 'x86_64', 'i386')]

centOS7显示如下：
获取操作系统名称及版本号 : [Linux-3.10.0-957.el7.x86_64-x86_64-with-centos-7.6.1810-Core]
获取操作系统版本号 : [#1 SMP Thu Nov 8 23:39:32 UTC 2018]
获取操作系统位数 : [('64bit', 'ELF')]
计算机类型 : [x86_64]
计算机的网络名称 : [localhost.localdomain]
计算机处理器信息 : [x86_64]
获取操作系统类型 : [Linux]
汇总信息 : [('Linux', 'localhost.localdomain', '3.10.0-957.el7.x86_64', '#1 SMP Thu Nov 8 23:39:32 UTC 2018', 'x86_64', 'x86_64')]
"""
print('获取操作系统名称及版本号 : [{}]'.format(var_platform))
print('获取操作系统版本号 : [{}]'.format(var_version))
print('获取操作系统位数 : [{}]'.format(var_architecture))
print('计算机类型 : [{}]'.format(var_machine))
print('计算机的网络名称 : [{}]'.format(var_node))
print('计算机处理器信息 : [{}]'.format(var_processor))
print('获取操作系统类型 : [{}]'.format(var_system))
print('汇总信息 : [{}]'.format(var_uname))