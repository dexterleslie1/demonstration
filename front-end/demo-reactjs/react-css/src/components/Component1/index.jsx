import React from 'react'
import component1css from "./index.module.css"

export default class Component1 extends React.Component {
    render() {
        return (
            <div className={component1css.mystyle}>
                {/* 用于协助测试嵌套样式引用 */}
                <div className={component1css['nested-1']}>Component1</div>
            </div>
        )
    }
}