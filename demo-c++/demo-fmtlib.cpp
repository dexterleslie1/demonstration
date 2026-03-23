#include <fmt/core.h>

int main() {
    int a = 42;
    double b = 3.14159;

    fmt::print("a = {}\n", a);
    fmt::print("b = {:.2f}\n", b);
    fmt::print("Hello, {}!\n", "world");

    // Hex print for a char buffer
    // static_cast<char>避免在 C++ 里触发 narrowing（从 int 到 char 的收窄转换报错）
    char buf[] = {static_cast<char>(0xDE), static_cast<char>(0xAD),
                   static_cast<char>(0xBE), static_cast<char>(0xEF)};
    fmt::print("buf (hex) = ");
    for (unsigned i = 0; i < sizeof(buf); ++i) {
        auto byte = static_cast<unsigned>(static_cast<unsigned char>(buf[i]));
        fmt::print("{:02x}{}", byte, (i + 1 == sizeof(buf)) ? "" : " "); // {:02x}：两位小写hex，不足补0；最后一个字节不输出空格
    }
    fmt::print("\n");

    return 0;
}