import React, { Component } from 'react'
import PropTypes from 'prop-types'
import "./index.css"

export default class Header extends Component {
  static propTypes = {
    addTodoItem: PropTypes.func.isRequired
  }
  onKeyDown = (event) => {
    if (event.key === "Enter") {
      this.props.addTodoItem(event.target.value)
      event.target.value = ""
    }
  }
  render() {
    return (
      <div className='todo-list-header-container'>
        <input type="text"
          placeholder='请输入的你的任务名称，按回车键确认'
          style={{ width: "20rem" }}
          onKeyDown={this.onKeyDown} />
      </div>
    )
  }
}
