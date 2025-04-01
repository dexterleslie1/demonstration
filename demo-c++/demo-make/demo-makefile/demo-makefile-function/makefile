src = $(wildcard ./src/*.c)
obj = $(patsubst ./src/%.c, ./obj/%.o, $(src))

ALL: test

./obj/add.o: ./src/add.c
	gcc -c ./src/add.c -o ./obj/add.o

./obj/sub.o: ./src/sub.c
	gcc -c ./src/sub.c -o ./obj/sub.o

./obj/main.o: ./src/main.c
	gcc -c ./src/main.c -o ./obj/main.o -I ./include

test: $(obj)
	gcc -o test $(obj)

# rm前面的-表示rm命令执行失败也不退出继续执行并且不提示错误
clean:
	-rm -rf $(obj) test

.PHONY: clean ALL
