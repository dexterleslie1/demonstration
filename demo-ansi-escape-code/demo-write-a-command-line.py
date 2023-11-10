import tty
import sys

def main(args):
    # 保存旧的设置用于之后恢复tty
    # https://www.programcreek.com/python/example/89182/tty.setraw
    mode = tty.tcgetattr(sys.stdin)

    try:
        tty.setraw(sys.stdin)

        # while True:
        #     char = sys.stdin.read(1)
        #     if ord(char) == 3: # CTRL-C
        #         break;
        #     print(ord(char))
        #     # 定位光标到最左边
        #     sys.stdout.write(u"\u001b[1000D\u001b[0m")
        #     sys.stdout.flush()

        while True:
            prompt = ">> "
            # 当前已经输入内容
            input = ""
            # 光标当前位置
            index = len(prompt + input)

            while True:
                sys.stdout.write("\u001b[1000D\u001b[0m")
                # 清除一行
                sys.stdout.write("\u001b[0K")
                sys.stdout.write(prompt + input)
                sys.stdout.write("\u001b[1000D\u001b[0m")
                if index > 0:
                    sys.stdout.write("\u001b[" + str(index) + "C\u001b[0m")
                sys.stdout.flush()

                char = ord(sys.stdin.read(1))

                # ctrl+c退出程序
                if char == 3:
                    return
                elif 32 <= char <= 126:
                    # 在光标当前位置插入字符
                    input = input[:index-len(prompt)] + chr(char) + input[index-len(prompt):]
                    index += 1
                elif char in {10, 13}:
                    sys.stdout.write("\u001b[1000D")
                    print("\n你的输入：" + input)

                    input = ""
                    index = len(prompt + input)
                elif char == 27:
                    # 键盘上下左右按键被按下时会发出 3 个 sys.stdin.read(1) 信号
                    next1, next2 = ord(sys.stdin.read(1)), ord(sys.stdin.read(1))
                    if next1 == 91:
                        # 向左按键
                        if next2 == 68:
                            index = max(len(prompt), index-1)
                        # 向右按键
                        elif next2 == 67:
                            index = min(len(prompt + input), index+1)
                # 删除字符
                elif char == 127:
                    if len(prompt) < index:
                            input = input[:index-len(prompt)-1] + input[index-len(prompt):]
                            index -= 1 

    finally:
        # 程序退出时光标移动到最左边
        sys.stdout.write("\n\u001b[1000D")
        sys.stdout.flush()

        # 恢复tty到raw模式之前
        tty.tcsetattr(sys.stdin, tty.TCSAFLUSH, mode)

if __name__ == "__main__":
    main(sys.argv)