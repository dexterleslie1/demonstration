## 字符编码原理

> https://www.bilibili.com/video/BV1xD4y1y7yc/?p=3&spm_id_from=pageDriver&vd_source=872f70ee43293e5dbba94e3aecc154d2

### ASCII编码（美国信息交换标准编码American Standard Code for Information Interchage）

> **ASCII字符编码表**
> https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
>
> **存储方式**
> 使用一个字节（共表示128个字符）存储一个英文字符并且二进制第一位必须0（0xxxxxxx），例如字符 a 存储的二进制数据为 0110 0001
>
> **说明**
> 美国自己定义个字符信息交换编码标准



### GBK（国标）

> **存储方式**
> 使用两个字节存储一个汉字并且第一个字节的二进制第一位必须是1（1xxxxxxx xxxxxxxx），例如 "我a你" 存储的二进制数据为 1xxxxxxx xxxxxxxx 0xxxxxxx 1xxxxxxx xxxxxxxx，在解码时候就能够正确地区分开中文和英文。
>
> **说明**
> 中国自己定义的汉字信息交换编码标准
> GBK兼容了ASCII字符集



### unicode字符集（万国码）

> https://zhuanlan.zhihu.com/p/521482451?utm_id=0
> https://blog.csdn.net/hitpter/article/details/133562847?app_version=6.1.9&csdn_share_tail=%7B%22type%22%3A%22blog%22%2C%22rType%22%3A%22article%22%2C%22rId%22%3A%22133562847%22%2C%22source%22%3A%22dexterchan%22%7D&utm_source=app
> https://codeleading.com/article/69711145688/
>
> unicode只是对全球字符进行统一编码，例如："我" 对应的unicode编码十进制为 25105，对应的十六进制编码为 \x6211，对应的转移字符unicode表示法为 \u6211。utf-32和utf-8编码是对unicode字符的具体编码方式。
> Unicode 是「字符集（为每一个「字符」分配一个唯一的 ID（学名为码位 / 码点 / Code Point））」，UTF-8 是「编码规则（将转换为字节序列的规则）」。
> 可以通过在线unicode工具查询 https://www.branah.com/unicode-converter "我"对应的十进制。
> 可以通过在线emoji查询 https://unicode.org/emoji/charts/full-emoji-list.html emoji对应的cod point。
>
> utf-32是unicode的一种编码规则，使用4个字节表示一个字符。缺点占用存储空间，通信效率底下。
>
> **utf-16的编码方式（具体可以参考demo-java演示的 smile emoji \ud83d\ude00 ）**
> 基本平面的字符占用 2 个字节（U+0000到U+FFFF），辅助平面的字符占用 4 个字节（U+010000到U+10FFFF）。
> 也就是说，UTF-16的编码长度要么是2个字节要么是4个字节。当为2字节时，则实际上与Unicode相同。
> 并且还有个原则，在Unicode基本多语言平面内，从U+D800到U+DFFF之间的码点区间是不对应字符的。而UTF-16需要利用这块码位来对辅助平面的字符进行编码。
> 它的具体规则：
>
> - 码点小于U+FFFF，基本字符，不需处理，直接使用，占两个字节。
> - 否则，拆分成两个码元，四个字节，cp表示码点：低位——((cp - 65536) / 1024) + 0xD800，值范围是 0xD800~0xDBFF；高位——((cp - 65536) % 1024) + 0xDC00，值范围是 0xDC00~0xDFFF。
>
> 
>
> **utf-8编码方式**
> 是unicode的一种编码规则，采用可变长编码方案，共分为4个长度区：1个字节、2个字节、3个字节、4个字节。英文和数字使用1个字节存储，兼容ASCII编码。汉字使用3个字节存储。
> 1个字节（ASCII编码）编码方式 0xxxxxxx。2个字节编码方式 110xxxx 10xxxxxx。3个字节编码方式 1110xxxx 10xxxxxx 10xxxxxx。4个字节编码方式 11110xx 10xxxxxx 10xxxxxx  10xxxxxxs。
> 例如："我" 对应的utf-8编码为3个字节，十六进制为 \xe6\x88\x91
> 例如："a我m" 对应的utf-8编码为5个字节，十六进制为 \x61\xe6\x88\x91\x6d，\x61 对应 "a"，\xe6\x88\x91 对应 "我"，x6d 对应 "m"。
>
> **说明**
> utf-8兼容ASCII字符集。
> java源码的字符串使用utf-16编码，所以 smile emoji 在java源码中的声明方式是 str = "\ud83d\ude00";，具体可以参考 demo-java 演示。







