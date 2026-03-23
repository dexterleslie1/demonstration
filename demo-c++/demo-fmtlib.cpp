#include <fmt/core.h>

int main() {
    int a = 42;
    double b = 3.14159;

    fmt::print("a = {}\n", a);
    fmt::print("b = {:.2f}\n", b);
    fmt::print("Hello, {}!\n", "world");

    return 0;
}