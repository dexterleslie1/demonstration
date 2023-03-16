# 演示css display用法

## display

### block

> 元素能够声明宽度和高度并且独占一行。
>
> div默认是块元素。
>
> 注意：在不声明元素宽度时，元素宽度会自动调整至其父级容器宽度，以填满父级容器宽度。

### inline

> 元素不能够声明宽度和高度并且不会独占一行。
>
> span默认是行内元素。

### inline-block

> 元素能够声明宽度和高度并且不会独占一行。

### table-cell

> 表格单元方式排列。
>
> 注意：能够声明宽度但是不能够声明高度，高度会自动被拉伸到适合单元格高度。

### none

> 元素被隐藏并且不会占据布局空间。

### flex布局

> flex布局步骤和原理：flex-direction确定主轴方向，justify-content确定主轴方向的子元素布局方式，align-items确定侧轴方向未换行子元素布局方式，align-content确定侧轴方式已换行的子元素布局方式。
>
>
> 为父级盒子设置flex布局后，子元素的float、clear和vertical-align属性将失效。

#### flex-direction

> 设置父级元素主轴方向，子元素会按照设定的主轴方向排列。
>
> flex-direction=row（默认值）时，子元素按照x轴从左到右排列。
>
> flex-direction=row-reverse时，子元素按照x轴从右到左反方向排列。
>
> flex-direction=column时，子元素按照y轴从上往下排列。
>
> flex-direction=column-reverse时，子元素按照y轴从下往上反方向排列。

#### justify-content

> 设置主轴上的子元素排列方式。
>
> justify-content=flex-start（默认值）时，第一个子元素紧贴x轴左边或者y轴顶端排列。
>
> justify-content=flex-end时，最后一个子元素紧贴x轴右边或者y轴底端排列。
>
> justify-content=flex-center时，所有子元素中间排列。
>
> justify-content=space-around时，所有子元素平均分配主轴剩余空间。
>
> justify-content=space-between时，子元素先左右或者上下贴边再平均分配剩余空间。

#### flex-wrap

> 在父级元素声明这个属性，用于控制当主轴方向子元素排列空间不足时，子元素排列是否自动换行。
>
> flex-wrap=nowrap（默认值）时，子元素不自动换行，并且子元素会自动压缩宽度以达到所有子元素的宽度之和不超过父级元素的宽度。
>
> flex-wrap=wrap时，子元素自动换行并且子元素的宽度不会变化。

#### align-items

> 在父级元素声明这个属性，用于控制侧轴方向子元素的排列方式。
>
> 注意：这个属性只能够作用于侧轴方向没有换行的子元素。如果侧轴方向子元素换行，则使用align-content进行控制。
>
> align-items=flex-start时，类似justify-content。
>
> align-items=flex-end时，类似justify-content。
>
> align-items=center时，类似justify-content。
>
> align-items=stretch时，子元素沿着侧轴方向自动拉伸以填满父级元素的高度或者宽度。

#### align-content

> 在父级元素声明这个属性，用于控制侧轴方向换行后的子元素的排列方式。
>
> 注意：要让这个属性生效并作用于子元素必须声明父级容器为display:flex;flex-wrap:wrap;并且子元素自动换行了。
>
> align-content=flex-start时，类似justify-content。
>
> align-content=flex-end时，类似justify-content。
>
> align-content=center时，类似justify-content。
>
> align-content=stretch时，类似justify-content。
>
> align-content=space-around时，类似justify-content。
>
> align-content=spance-between时，类似justify-content。

#### flex-flow

> 是flex-direction和flex-wrap的复合属性，例如：flex-flow: row nowrap;

#### flex属性

> 子元素声明flex属性，用于控制子元素分配父级元素剩余的空间，用来表示占多数份。

#### align-self

> 子元素声明align-self属性，用于控制单个子元素和其他子元素侧轴方向上不同的排列方式，用于覆盖父级元素声明的align-items对单个子元素产生的影响。默认值为auto，表示继承父级元素align-items属性。

#### order

> 子元素声明order属性，用于控制子元素的排列顺序，数值越小，排列越靠前，默认值为0。

