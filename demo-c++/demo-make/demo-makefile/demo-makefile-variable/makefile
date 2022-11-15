src = $(wildcard ./src/*.c)
obj = $(patsubst ./src/%.c, ./obj/%.o, $(src))

ALL: test

./obj/add.o: ./src/add.c
	gcc -c $< -o $@

./obj/sub.o: ./src/sub.c
	gcc -c $< -o $@

./obj/main.o: ./src/main.c
	gcc -c $< -o $@ -I ./include

test: $(obj)
	gcc -o test $^

# rm前面的-表示rm命令执行失败也不退出继续执行并且不提示错误
clean:
	-rm -rf $(obj) test

.PHONY: clean ALL
