# coding:utf-8

from lxml import etree

# https://cuiqingcai.com/5545.html
if __name__ == "__main__":
    # 测试打印html
    htmlObject = etree.parse('./1.html', etree.HTMLParser())
    result = etree.tostring(htmlObject)
    # print(result.decode('utf-8'))

    # 选取所有节点
    elements = htmlObject.xpath("//*")
    print("//*选取所有节点数量：", len(elements))

    # 选取所有li节点
    elements = htmlObject.xpath("//li")
    print("//li选取所有li节点数：", len(elements))

    # 选取子节点li>a
    elements = htmlObject.xpath("//li/a")
    print("//li/a选取子节点数：", len(elements))

    # 获取子孙节点 ul a
    elements = htmlObject.xpath("//ul//a")
    print("//ul//a获取子孙节点数：", len(elements))

    # 父亲节点
    elements = htmlObject.xpath('//a[@href="link4.html"]/../@class')
    print("//a[@href=\"link4.html\"]/../@class获取父亲节点数：", len(elements))

    # 属性匹配
    elements = htmlObject.xpath('//li[@class="item-0"]')
    print("//li[@class=\"item-0\"]属性匹配节点数：", len(elements))

    # 获取文本
    elements = htmlObject.xpath('//li[@class="item-0"]/text()')
    print("//li[@class=\"item-0\"]/text()获取文本：", elements)

    elements = htmlObject.xpath('//li[@class="item-0"]/a/text()')
    print("//li[@class=\"item-0\"]/a/text()获取文本：", elements)

    elements = htmlObject.xpath('//li[@class="item-0"]//text()')
    print("//li[@class=\"item-0\"]//text()获取文本：", elements)

    # 获取属性
    elements = htmlObject.xpath('//li/a/@href')
    print("//li/a/@href获取属性：", elements)

    # 使用contains+text()查询文本包含
    # https://tool.4xseo.com/article/36308.html
    elements = htmlObject.xpath("//a[contains(text(), 'second')]")
    print("//a[contains(text(), 'second')]查询文本包含：", len(elements))

    # 查询节点有指定的属性
    # https://stackoverflow.com/questions/3737906/xpath-how-to-check-if-an-attribute-exists
    elements = htmlObject.xpath("//a[@href]")
    print("//a[@href]节点数：", len(elements))

    # 属性值以xxx开始
    # https://stackoverflow.com/questions/26366391/xpath-selecting-attributes-using-starts-with
    elements = htmlObject.xpath("//a[starts-with(@href, 'link2')]")
    print("//a[starts-with(@href, 'link2')]：", len(elements))

    pass
