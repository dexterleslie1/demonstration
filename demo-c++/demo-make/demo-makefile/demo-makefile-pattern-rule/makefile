src = $(wildcard ./src/*.c)
obj = $(patsubst ./src/%.c, ./obj/%.o, $(src))

ALL: test


$(obj): ./obj/%.o: ./src/%.c
	gcc -c $< -o $@ -I ./include

test: $(obj)
	gcc -o test $^

# rm前面的-表示rm命令执行失败也不退出继续执行并且不提示错误
clean:
	-rm -rf $(obj) test

.PHONY: clean ALL
