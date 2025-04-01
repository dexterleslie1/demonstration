#!/bin/bash

nasm function_and_call.asm -o .disk.bin

# 使用dd命令制作软盘
# https://untitledfinale.wordpress.com/2007/10/09/create-mount-and-copy-floppy-disks-images-under-linux/
dd bs=512 count=2880 if=/dev/zero of=disk.img
# conv-notrunc表示保留disk.img原来文件大小，不截断文件到512byte
dd bs=512 count=1 if=.disk.bin of=disk.img conv=notrunc