srcv = $(wildcard ./src/*.c)
src = $(patsubst ./src/%.c, %.o, $(srcv))
obj = $(patsubst ./src/%.c, %.o, $(srcv))
objp = $(patsubst ./src/%.c, $(obj_dir)/%.o, $(srcv))

src_dir = ./src
obj_dir = ./obj

ALL: test


$(objp): $(obj_dir)/%.o: $(src_dir)/%.c
	gcc -c $< -o $@ -I ./include

test: $(objp)
	gcc -o test $^

# rm前面的-表示rm命令执行失败也不退出继续执行并且不提示错误
clean:
	-rm -rf $(objp) test

.PHONY: clean ALL
