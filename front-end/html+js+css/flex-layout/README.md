# 演示flex布局用法

## 总结

> **justify-content** 用于设置或检索弹性盒子元素在主轴（横轴）方向上的对齐方式。
> NOTE：当flex-direction: row时，横轴为x轴，当flex-direction: column时，横轴为y轴
>
> 
>
> **align-items** 属性定义flex子项在flex容器的当前行的侧轴（纵轴）方向上的对齐方式。
> NOTE：当flex-direction: row时，纵轴为y轴，当flex-direction: column时，纵轴为x轴
>
> **align-content**用于设置flex子项作为一个整体起作用的侧轴方向上的对齐方式
> align-items和align-content的区别:
>
> - flex子项作为一个整体起作用
> - flex子项的父级容器已经设置flex-wrap:wrap
>
> 具体align-content和align-items区别可以通过修改align-content.html和align-items.html demo对比它们的具体区别。
>
> 
>
> **align-self**用于flex子项设置使用，设置指定的flex子项 align-items 属性。
>
> **flex**用于设置flex子项分配所有flex子项空间的比例。
>
> 
>
> **flex-direction**用于设置主轴的方向。
>
> 
>
> **flex-wrap**用于设置子项超出父级容器范围后是否自动换行。