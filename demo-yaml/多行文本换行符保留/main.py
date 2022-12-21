import yaml

# 删除每行尾部的换行符\n，则看似多行文本，则在程序中会将其视为一行。
# 在">"符号应用的多行文本值中，所有换行符都会被视为空格，有两种方法都可以强制保留换行符：
def test_delete_all_change_line():
    with open('./remove-every-line-crlf.yaml','r',encoding='utf8') as file:#utf8可识别中文
        fff=yaml.safe_load(file)
        print(fff)
    pass


# 保留每行尾部的换行符\n。
def test_reserve_every_line_crlf():
    with open('./reserve-every-line-crlf.yaml','r',encoding='utf8') as file:#utf8可识别中文
        fff=yaml.safe_load(file)
        print(fff)
    pass

# 保留每行尾部的换行符\n的同时，保留内容结尾处的换行符\n。
def test_reserve_every_line_and_last_line_crlf():
    with open('./reserve-every-line-and-last-line-crlf.yaml','r',encoding='utf8') as file:#utf8可识别中文
        fff=yaml.safe_load(file)
        print(fff)
    pass

# 保留每行尾部的换行符\n的同时，删除内容结尾处的换行符\n。
def test_reserve_every_line_and_remove_last_line_crlf():
    with open('./reserve-every-line-and-remove-last-line-crlf.yaml','r',encoding='utf8') as file:#utf8可识别中文
        fff=yaml.safe_load(file)
        print(fff)
    pass

if __name__ == "__main__":
    # test_delete_all_change_line()
    # test_reserve_every_line_crlf()
    # test_reserve_every_line_and_last_line_crlf()
    test_reserve_every_line_and_remove_last_line_crlf()
