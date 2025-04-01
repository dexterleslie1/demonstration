import React, { Component } from 'react'
import PropTypes from 'prop-types'

import Item from '../Item'

import "./index.css"

export default class List extends Component {
  static propTypes = {
    todoList: PropTypes.array.isRequired,
    updateTodoItemFinished: PropTypes.func.isRequired,
    deleteTodoItem: PropTypes.func.isRequired,
  }

  render() {
    return (
      <ul className='todo-list-ul-container'>
        {
          this.props.todoList.map((todoItem) => {
            return <Item key={todoItem.id} {...todoItem}
              updateTodoItemFinished={this.props.updateTodoItemFinished}
              deleteTodoItem={this.props.deleteTodoItem} />
          })
        }
      </ul>
    )
  }
}
