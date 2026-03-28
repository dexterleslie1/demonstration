#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <time.h>

/* 与 server.c 中监听端口一致 */
#define PORT 8888

struct peer
{
    struct in_addr addr;
    in_port_t port;
};

// 定义字符集
const char charset[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

/**
 * @brief 生成随机字符串
 * @param output_char_array 用于存放生成字符串的字符数组
 * @param length 字符串长度 (不包括结尾的 '\0')
 */
void generate_random_string(char *output_char_array, size_t length)
{
    // 确保长度合法 (需要至少1个字符用于 '\0')
    if (length == 0)
        return;

    // 从字符集中随机选取字符填充数组
    for (size_t n = 0; n < length - 1; n++)
    {                                             // 循环 length-1 次，为 '\0' 留出空间
        int key = rand() % (sizeof(charset) - 1); // -1 是为了排除 '\0'
        output_char_array[n] = charset[key];
    }
    output_char_array[length - 1] = '\0'; // 添加字符串结尾符
}

int main(int argc, char *argv[])
{
    /*
     * argv[1]：服务器 IPv4 点分十进制地址；缺省 127.0.0.1（本机）。
     * argv[2]：要发送的报文内容（不含自动换行）；缺省 "ping"。
     */
    const char *host = (argc > 1) ? argv[1] : "192.168.1.181";
    const char *msg = (argc > 2) ? argv[2] : "ping";
    int fd;
    /*
     * srv：对端（服务器）地址，供 sendto / recvfrom 使用。
     * srv_len：recvfrom 入参为缓冲区最大长度，返回后为实际地址结构长度（须取址传入）。
     */
    struct sockaddr_in srv;
    socklen_t srv_len = sizeof(srv);
    ssize_t n;

    /*
     * 创建 UDP 套接字：AF_INET + SOCK_DGRAM，第三个参数 0 表示默认 UDP 协议。
     */
    fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (fd < 0)
    {
        perror("socket");
        return 1;
    }

    /*
     * 将 srv 清零后填写服务器地址：族、端口（网络序）、IPv4 地址。
     * inet_pton(AF_INET, host, &sin_addr)：把文本 IP 转为二进制网络序；返回 1 表示成功。
     */
    memset(&srv, 0, sizeof(srv));
    srv.sin_family = AF_INET;
    srv.sin_port = htons(PORT);
    if (inet_pton(AF_INET, host, &srv.sin_addr) != 1)
    {
        fprintf(stderr, "invalid address: %s\n", host);
        close(fd);
        return 1;
    }

    struct peer peer_server;
    peer_server.addr = srv.sin_addr;
    peer_server.port = srv.sin_port;

    /*
     * sendto：向 srv 发送一帧 UDP；strlen(msg) 为载荷字节数（不含 '\0'）。
     * 客户端未 bind 时，内核会为此套接字隐式绑定临时本地地址。
     */
    if (sendto(fd, msg, strlen(msg), 0, (struct sockaddr *)&srv, sizeof(srv)) < 0)
    {
        perror("sendto");
        close(fd);
        return 1;
    }

    for (;;)
    {
        /*
         * recvfrom：阻塞等待应答；buf 最多收 sizeof(buf)-1 字节，留一字节写 '\0'。
         * srv / srv_len 可得到应答来源（应与服务器一致）；若仅收载荷也可传 NULL。
         */
        char buf[1024];
        n = recvfrom(fd, buf, sizeof(buf), 0, (struct sockaddr *)&srv, &srv_len);
        if (n < 0)
        {
            perror("recvfrom");
            close(fd);
            return 1;
        }

        struct peer other_peer;
        if (peer_server.addr.s_addr == srv.sin_addr.s_addr && peer_server.port == srv.sin_port)
        {
            memcpy(&other_peer, buf, n);
            printf("Server tell peer [%s:%u]\n", inet_ntoa(other_peer.addr), ntohs(other_peer.port));

            // 发送消息给peer
            srv.sin_addr = other_peer.addr;
            srv.sin_port = other_peer.port;
            if (sendto(fd, "Hello", 5, 0, (struct sockaddr *)&srv, sizeof(srv)) < 0)
            {
                perror("sendto peer");
                close(fd);
                return 1;
            }
        }
        else
        {
            // --- 获取并格式化当前时间 ---
            time_t rawtime;
            struct tm *timeinfo;
            char timestamp[20]; // 足够容纳 "yyyy-MM-dd HH:mm:ss\0" (19 chars + null terminator)

            time(&rawtime);                                                        // 获取当前时间 (秒级时间戳)
            timeinfo = localtime(&rawtime);                                        // 转换为本地时间结构
            strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", timeinfo); // 格式化时间
            printf("%s - Received peer [%s:%u] message:%s\n", timestamp, inet_ntoa(srv.sin_addr), ntohs(srv.sin_port), buf);
            sleep(5);

            srand(time(NULL)); // 使用当前时间作为种子初始化随机数生成器
            size_t desired_length = 12;
            char random_str[desired_length + 1]; // +1 为 '\0' 预留空间
            generate_random_string(random_str, sizeof(random_str));
            if (sendto(fd, random_str, sizeof(random_str), 0, (struct sockaddr *)&srv, sizeof(srv)) < 0)
            {
                perror("sendto peer");
                close(fd);
                return 1;
            }
        }
    }

    close(fd);
    return 0;
}
