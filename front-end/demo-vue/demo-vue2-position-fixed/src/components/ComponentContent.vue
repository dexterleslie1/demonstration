<template>
  <div>
    这是ComponentContent
    <div id="myDiv1" class="myDiv1">
      <div>导航1</div>
      <div>导航2</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ComponentContent",
  mounted() {
    new Dragger("myDiv1")

    /**
     * 拖拽类
     */
    function Dragger(elementId) {
      var elementObject = document.getElementById(elementId)

      this.divMover = new DivMover(elementObject)
      this.pointerTracker = new PointerTracker()

      document.onmouseup = () => {
          this.stop()
      }
      document.onmousemove = (event) => {
        this.drag(event)
      }

      elementObject.onmousedown = (event) => {
        this.start(event)
      }

      this.start = function (event) {
          this.pointerTracker.markOriginalPosition(event)
          this.divMover.markOriginalPosition()
      }

      this.stop = function () {
          this.pointerTracker.unmarkOriginalPosition()
          this.divMover.unmarkOriginalPosition()
      }

      this.drag = function (event) {
          if (this.pointerTracker.tracking) {
              this.pointerTracker.updatePosition(event)
              var offset = this.pointerTracker.getOffset()
              this.divMover.move(offset.x, offset.y)
          }
      }

      this.move = function(x, y) {
          this.divMover.move(x, y)
      }

      /**
       * 鼠标跟踪器
       */
      function PointerTracker() {
          // 是否处于跟踪状态
          this.tracking = false

          // 鼠标mousedown时的坐标
          this.originalX = 0
          this.originalY = 0

          // 鼠标当前坐标
          this.currentX = 0
          this.currentY = 0

          /**
           * 标记鼠标mousedown时的坐标
           */
          this.markOriginalPosition = function (event) {
              this.tracking = true
              this.originalX = event.clientX
              this.originalY = event.clientY

              console.log(`PointerTracker markOriginalPosition,
              this.originalX=${this.originalX},this.originalY=${this.originalY}`)
          }

          /**
           * mouseup时取消标记鼠标mousedown时的原始坐标
           */
          this.unmarkOriginalPosition = function () {
              this.tracking = false

              this.originalX = this.currentX
              this.originalY = this.currentY

              console.log(`PointerTracker unmarkOriginalPosition,
              this.originalX=${this.originalX},this.originalY=${this.originalY}`)
          }

          /**
           * 在mousemove过程中更新鼠标当前坐标
           */
          this.updatePosition = function (event) {
              if (!this.tracking) {
                  console.log('PointerTracker isn\'t in tracking mode')
                  return
              }

              this.currentX = event.clientX
              this.currentY = event.clientY

              console.log(`PointerTracker update this.currentX and this.currentY,
              this.currentX=${this.currentX},this.currentY=${this.currentY}`)
          }

          /**
           * 获取鼠标已经移动的偏移量
           * @return {
           *  offsetX: x
           *  offsetY: x
           * }
           */
          this.getOffset = function () {
              var x = this.currentX - this.originalX
              var y = this.currentY - this.originalY

              console.log(`PointerTracker offsetX=${x},offsetY=${y}`)

              return {
                  x: x,
                  y: y
              }
          }
      }

      /**
       * 用于移动div类
       */
      function DivMover(elementObject) {
          this.originalX = 0
          this.originalY = 0
          this.element = elementObject

          /**
           * 标记div原始position
           */
          this.markOriginalPosition = function () {
            var rect = elementObject.getBoundingClientRect();
            this.originalX = rect.left
            this.originalY = rect.top
          }

          /**
           * 清除div原始position标记
           */
          this.unmarkOriginalPosition = function () {
              // this.element = null
          }

          /**
           * 移动div offsetX和offsetY偏移量
           */
          this.move = function (offsetX, offsetY) {
              if (this.element) {
                  var left = this.originalX + offsetX
                  var top = this.originalY + offsetY
                  var viewportWidth = window.innerWidth
                  var viewportHeight = window.innerHeight
                  var offsetWidth = this.element.offsetWidth
                  var offsetHeight = this.element.offsetHeight

                  // 不能超出viewport边界
                  if(top<0) {
                      top = 0
                  }
                  if(left<0) {
                      left = 0
                  }
                  if(top+offsetHeight>viewportHeight) {
                      top = viewportHeight - offsetHeight
                  }
                  if(left+offsetWidth>viewportWidth) {
                      left = viewportWidth - offsetWidth
                  }
                  
                  // this.element.style.left = left + 'px'
                  this.element.style.top = top + 'px'
              }
          }
      }
    }
  }
}
</script>

<style scoped>
.myDiv1 {
  position: fixed;
  /* 相对于整个屏幕高度的20% */
  top: 20%;
  right: 0px;
  /* bottom: 0px; */
  width: 50px;
  height: 150px;
  background-color: orange;
}
</style>