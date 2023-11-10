#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import time
import random

if __name__ == "__main__":
	
    ### 光标移动

    # 左移动
    print("---------------- 左移动 ----------------")
    sys.stdout.write("Hello world!")
    sys.stdout.flush()
    for i in range(5):
        time.sleep(.2)
        sys.stdout.write("\u001b[1D")
        sys.stdout.flush()
    print()
    print("------------------------------------------")
    print()

    # 右移动
    print("---------------- 右移动 ----------------")
    sys.stdout.write("Hello world!")
    sys.stdout.flush()
    for i in range(5):
        time.sleep(.2)
        sys.stdout.write("\u001b[1C")
        sys.stdout.flush()
    print()
    print("------------------------------------------")
    print()

    # 上下移动
    print("---------------- 上下移动 ----------------")
    sys.stdout.write("Hello world!")
    sys.stdout.flush()
    for i in range(5):
        time.sleep(.2)
        # 向上移动
        sys.stdout.write("\u001b[1A")
        sys.stdout.flush()

    for i in range(5):
        time.sleep(.2)
        # 向下移动
        sys.stdout.write("\u001b[1B")
        sys.stdout.flush()
    print()
    print("------------------------------------------")
    print()

    # 进度显示
    print("---------------- 进度显示 ----------------")
    print("Loading...")
    for i in range(20):
        sys.stdout.write("\u001b[1000D" + str(i) + "%")
        sys.stdout.flush()
        time.sleep(.1)
    print()
    print("------------------------------------------")
    print()

    # 慢放进度显示
    print("---------------- 慢放进度显示 ----------------")
    print("Loading...")
    for i in range(3):
        sys.stdout.write(str(i + 1) + "%")
        sys.stdout.flush()
        time.sleep(1)
        sys.stdout.write(u"\u001b[1000D")
        sys.stdout.flush()
        time.sleep(1)
    print()
    print("------------------------------------------")
    print()

    # ASCII进度显示
    print("---------------- ASCII进度显示 ----------------")
    print("Loading...")
    for i in range(0, 100):
        time.sleep(0.01)
        width = int(i / 4)
        bar = "[" + "#" * width + " " * (25 - width) + "] " + str(i) + "%"
        sys.stdout.write(u"\u001b[1000D" +  bar)
        sys.stdout.flush()
    print()
    print("------------------------------------------")
    print()


    # 多条进度显示
    print("---------------- 多条进度显示 ----------------")
    count = 5
    all_progress = [0]*count
    sys.stdout.write("\n"*count)
    while any(x < 100 for x in all_progress):
        time.sleep(.01)
        # 找出未完成进度条
        unfinised_progress = [(i, v) for i, v in enumerate(all_progress) if v < 100]
        # 随机选择一个进度条
        i, _ = random.choice(unfinised_progress)
        all_progress[i] += 1

        sys.stdout.write("\u001b[1000D")
        sys.stdout.write("\u001b[" + str(count) + "A")
        for progress in all_progress:
            width = int(progress / 4)
            print("[" + "#" * width + " " * (25 - width) + "]")
    print("------------------------------------------")
    print()

    # 显示和隐藏光标
    print("---------------- 显示和隐藏光标 ----------------")
    print("隐藏光标3秒")
    sys.stdout.write("\u001b[?25l")
    sys.stdout.flush()
    time.sleep(3)
    print("显示光标3秒")
    sys.stdout.write("\u001b[?25h")
    sys.stdout.flush()
    time.sleep(3)
    print("------------------------------------------")
    print()
