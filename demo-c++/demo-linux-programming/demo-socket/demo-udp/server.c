#include <arpa/inet.h>
#include <errno.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

#define PORT 8888
#define BUF_SIZE 1024

int main(void) {
    int fd;
    /*
     * sockaddr_in：IPv4 套接字地址（netinet/in.h），与 AF_INET 配合使用。
     * addr：本机绑定地址，传给 bind()，指定本机监听哪个 IP/端口。
     * cli：对端地址缓冲区；recvfrom() 写入后得到本次报文来源，再原样传给 sendto() 回包。
     */
    struct sockaddr_in addr, cli;
    socklen_t cli_len; /* recvfrom 传入 cli 缓冲区大小，返回时写入实际地址结构长度 */
    char buf[BUF_SIZE];
    ssize_t n;

    /*
     * socket：创建套接字，返回文件描述符 fd。
     * AF_INET：IPv4 地址族，与 sockaddr_in、bind/recvfrom 等一致。
     * SOCK_DGRAM：无连接、面向报文（UDP），用 sendto/recvfrom，不保证顺序与可靠送达。
     * 第三个参数 protocol：0 表示在 AF_INET + SOCK_DGRAM 下选用默认协议（通常为 UDP）。
     */
    fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (fd < 0) {
        perror("socket");
        return 1;
    }

    /*
     * memset(s, c, n)：从 s 起连续写入 n 个字节，每个字节值为 (unsigned char)c。
     * &addr：目标起始地址；0：填充字节；sizeof(addr)：要覆盖的字节数（整个 sockaddr_in）。
     * 将 addr 整块置 0：避免栈上未初始化字节；sockaddr_in 含填充字段 sin_zero，
     * 若不清零，bind() 时内核比较的地址结构可能含垃圾数据导致行为未定义或绑定失败。
     */
    memset(&addr, 0, sizeof(addr));
    /*
     * sin_family：地址族，须与 socket() 的 AF_INET 一致，bind 时据此解析 sockaddr_in。
     * sin_addr.s_addr：IPv4 地址（网络字节序）。INADDR_ANY 表示绑定本机全部 IPv4 接口；
     * 惯例上对 s_addr 使用 htonl（INADDR_ANY 为 0，转换后仍为 0）。
     * sin_port：端口号，须用 htons 转为网络字节序，与协议首部一致。
     */
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(PORT);

    /*
     * bind(sockfd, addr, addrlen)：将套接字与本机地址（IP+端口）关联，之后在该端口上接收数据。
     * fd：已创建的 UDP 套接字。
     * (struct sockaddr *)&addr：API 使用通用 sockaddr*，故对 sockaddr_in 做指针转换（布局兼容）。
     * sizeof(addr)：本端地址结构长度，内核据此解析。
     * 失败时 perror 打印原因；close(fd) 释放描述符；返回非 0 表示程序异常退出。
     */
    if (bind(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        perror("bind");
        close(fd);
        return 1;
    }

    printf("UDP server listening on 0.0.0.0:%d\n", PORT);

    for (;;) {
        /*
         * recvfrom 要求：调用前 *cli_len 为 src_addr 缓冲区可容纳的最大长度（此处为 sockaddr_in）。
         * 返回后内核将其改为实际写入的地址结构长度。每次循环须重新赋值为 sizeof(cli)，
         * 否则若沿用上次返回的较小值，下次可能截断或行为未定义。
         */
        cli_len = sizeof(cli);
        /*
         * recvfrom(sockfd, buf, len, flags, src_addr, addrlen)：从 sockfd 接收一帧 UDP 数据报。
         * fd：已 bind 的 UDP 套接字。
         * buf / sizeof(buf)-1：存放载荷的缓冲区及最大接收长度；留 1 字节便于之后 buf[n]='\0' 安全打印。
         * flags：0 表示无额外行为（如 MSG_PEEK 等）。
         * (struct sockaddr *)&cli / &cli_len：写入发送方地址；addrlen 见上文循环内说明。
         * 返回值 n：成功为报文长度（字节），失败为 -1。
         */
        n = recvfrom(fd, buf, sizeof(buf) - 1, 0, (struct sockaddr *)&cli, &cli_len);
        if (n < 0) {
            perror("recvfrom");
            continue;
        }
        buf[n] = '\0';
        /*
         * "[%s:%u] %s"：依次打印对端点分十进制 IP、主机序端口号、以 '\0' 结尾的报文正文。
         * inet_ntoa(cli.sin_addr)：将网络序 IPv4 转为可打印字符串（返回静态缓冲区，勿长期保存指针）。
         * ntohs(cli.sin_port)：端口由网络序转主机序再按无符号打印。
         */
        printf("[%s:%u] %s\n", inet_ntoa(cli.sin_addr), ntohs(cli.sin_port), buf);

        /*
         * sendto(sockfd, buf, len, flags, dest_addr, addrlen)：向指定对端发送一帧 UDP 数据报（回显）。
         * fd：同一 UDP 套接字。
         * buf / (size_t)n：发送缓冲区与字节数，与本次 recvfrom 收到的载荷长度一致（不含为打印而写的 '\0'）。
         * flags：0 表示无额外行为。
         * (struct sockaddr *)&cli / cli_len：目的地址与长度，即 recvfrom 填入的发送方，回包原路返回。
         */
        if (sendto(fd, buf, (size_t)n, 0, (struct sockaddr *)&cli, cli_len) < 0)
            perror("sendto");
    }

    return 0;
}
