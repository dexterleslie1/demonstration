import React, { Component } from 'react'

export default class MyComponent extends Component {
  render() {
    return (
      <div>
        {/* 使用 this.props.children 访问自定义组件传递的标签体内容 */}
        {this.props.children}
      </div>
    )
  }
}
