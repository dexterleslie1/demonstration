document.addEventListener('DOMContentLoaded', () => {
    // var elementSplitter = document.getElementsByClassName('splitter')[0]

    new MyLrSplitPane('my-lr-split-pane')
})

/**
 * 我的左右分割面板
 */
function MyLrSplitPane(myLrSplitPaneContainerId) {
    // 获取我的左右分割面板容器元素
    this.myLrSplitPaneContainer =
        document.getElementById(myLrSplitPaneContainerId)
    this.mouseTracker = new MouseTracker(this.myLrSplitPaneContainer)

    // 绑定鼠标相关事件
    var elementSplitter = this.myLrSplitPaneContainer.getElementsByClassName('splitter')[0]
    elementSplitter.onmousedown = (event) => {
        this.mouseTracker.start(event)
    }
    document.onmouseup = () => {
        this.mouseTracker.stop()
    }
    document.onmousemove = (event) => {
        this.mouseTracker.drag(event)
    }

    var btnLPaneCollapseExpandToggle = this.myLrSplitPaneContainer.getElementsByClassName('btn-l-pane-collapse-expand-toggle')[0]
    btnLPaneCollapseExpandToggle.onclick = () => {
        var elementLeftPane = this.myLrSplitPaneContainer.getElementsByClassName('left-pane')[0]
        var currentDisplay = elementLeftPane.style.display
        if (currentDisplay == '' || currentDisplay !== 'none') {
            elementLeftPane.style.display = 'none'
            btnLPaneCollapseExpandToggle.innerHTML = '&gt;'
        } else {
            elementLeftPane.style.display = ''
            btnLPaneCollapseExpandToggle.innerHTML = '&lt;'
        }

    }

    function LeftPaneResizer(myLrSplitPaneContainer) {
        this.myLrSplitPaneContainer = myLrSplitPaneContainer
        this.elementTarget = null
        this.originalWidth = 0

        this.start = function () {
            if (!this.elementTarget) {
                this.elementTarget = this.myLrSplitPaneContainer.getElementsByClassName('left-pane')[0]
                this.originalWidth = this.elementTarget.offsetWidth
            }
        }

        this.stop = function () {
            this.elementTarget = null
        }

        this.resize = function (widthToAdjust) {
            // console.log(`widhtToAdjust=${widthToAdjust}`)
            if (this.elementTarget) {
                this.elementTarget.style.width = (this.originalWidth + widthToAdjust) + 'px'
            }
        }
    }

    function MouseTracker(myLrSplitPaneContainer) {
        this.myLrSplitPaneContainer = myLrSplitPaneContainer
        this.leftPaneResizer = new LeftPaneResizer(this.myLrSplitPaneContainer)
        this.tracking = false
        this.originalX = 0
        this.currentX = 0

        this.start = function (event) {
            this.tracking = true
            this.originalX = event.clientX

            this.leftPaneResizer.start()
        }

        this.stop = function () {
            this.tracking = false
            this.originalX = this.currentX

            this.leftPaneResizer.stop()
        }

        this.drag = function (event) {
            if (!this.tracking)
                return

            this.currentX = event.clientX

            this.leftPaneResizer.resize(this.getOffsetX())
        }

        this.getOffsetX = function () {
            return this.currentX - this.originalX
        }
    }

}
