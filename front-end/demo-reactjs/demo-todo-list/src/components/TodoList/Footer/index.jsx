import React, { Component } from 'react'

import PropTypes from 'prop-types'

import "./index.css"

export default class Footer extends Component {

  static propTypes = {
    todoList: PropTypes.array.isRequired,
    selectAllTodoItems: PropTypes.func.isRequired,
    deleteFinishedTodoItems: PropTypes.func.isRequired,
  }

  render() {
    let finishedCount = this.props.todoList.reduce((prev, current) => {
      if (current.finished) {
        return prev + 1
      }
      return prev
    }, 0)
    let total = this.props.todoList.length
    return (
      <div className='todo-list-footer-container' >
        <div>
          <input type="checkbox"
            checked={finishedCount === total && total !== 0 ? true : false}
            onChange={(event) => { this.props.selectAllTodoItems(event.target.checked) }} />
          已完成{finishedCount}/全部{total}
        </div>
        <input type="button" value="删除已完成任务"
          onClick={this.props.deleteFinishedTodoItems} />
      </div >
    )
  }
}
