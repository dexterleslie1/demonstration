ALL: test

./obj/add.o: ./src/add.c
	gcc -c ./src/add.c -o ./obj/add.o

./obj/sub.o: ./src/sub.c
	gcc -c ./src/sub.c -o ./obj/sub.o

./obj/main.o: ./src/main.c
	gcc -c ./src/main.c -o ./obj/main.o -I ./include

test: ./obj/add.o ./obj/sub.o ./obj/main.o
	gcc -o test ./obj/add.o ./obj/sub.o ./obj/main.o

clean:
	-rm -rf ./obj/*.o test

# 伪目标，用于解决如果当前目录下存在名为clean或者ALL的文件
# 此时执行make会提示目录已经是最新，无需执行编译程序
# 这样情况下使用伪目标指定clean和ALL强制执行
.PHONY: clean ALL
