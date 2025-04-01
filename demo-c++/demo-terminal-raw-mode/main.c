#include <termios.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>

// https://viewsourcecode.org/snaptoken/kilo/02.enteringRawMode.html

// 编译程序
// gcc main.c

// 运行程序
// ./a.out

struct termios orig_termios;

void disableRawMode();

void enableRawMode();

int main() {
    enableRawMode();

    char c;
    while (read(STDIN_FILENO, &c, 1) == 1 && c != 'q') {
        if (iscntrl(c)) {
            printf("%d\r\n", c);
        } else {
            printf("%d ('%c')\r\n", c, c);
        }
    }

    return 0;
}

// 禁用raw模式
// 程序退出时退出terminal raw模式
void disableRawMode() {
    tcsetattr(STDIN_FILENO, TCSAFLUSH, &orig_termios);
}

// 启用raw模式
void enableRawMode() {
    tcgetattr(STDIN_FILENO, &orig_termios);

    int result = atexit(disableRawMode);
    if (result != 0) {
        perror("atexit error");
        exit(1);
    }

    struct termios raw = orig_termios;

    // There is an ICANON flag that allows us to turn off canonical mode.
    // This means we will finally be reading input byte-by-byte, instead of line-by-line.
//    raw.c_lflag &= ~(ECHO | ICANON);
    raw.c_iflag &= ~(ICRNL | IXON);
    raw.c_oflag &= ~(OPOST);
    raw.c_lflag &= ~(ECHO | ICANON | IEXTEN | ISIG);


    tcsetattr(
            STDIN_FILENO,

            // it waits for all pending output to be written to the terminal,
            // and also discards any input that hasn’t been read
            TCSAFLUSH,
            &raw);
}