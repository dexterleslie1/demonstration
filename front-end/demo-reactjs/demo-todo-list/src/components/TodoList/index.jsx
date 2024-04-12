import React, { Component } from 'react'
import { v4 as uuidv4 } from 'uuid'

import Header from './Header';
import List from './List';
import Footer from './Footer';

import "./index.css"

export default class TodoList extends Component {
  state = {
    todoList: [
      { id: "001", name: "吃饭", finished: true },
      { id: "002", name: "工作", finished: true },
      { id: "003", name: "睡觉", finished: false },
    ]
  }

  addTodoItem = (name) => {
    if (!name || !name.trim()) {
      return
    }

    let todoItem = { id: uuidv4(), name: name, finished: false }
    this.state.todoList.unshift(todoItem)
    this.setState({ todoList: this.state.todoList })
  }

  updateTodoItemFinished = (id, finished) => {
    let newTodoList = this.state.todoList.map((todoItem) => {
      if (todoItem.id === id) {
        todoItem.finished = finished
      }
      return todoItem
    })
    this.setState({ todoList: newTodoList })
  }

  deleteTodoItem = (id) => {
    if (window.confirm("确定删除吗？")) {
      let newTodoList = this.state.todoList.filter((todoItem) => {
        return todoItem.id !== id
      })
      this.setState({ todoList: newTodoList })
    }
  }

  selectAllTodoItems = (finished) => {
    let newTodoList = this.state.todoList.map((todoItem) => {
      todoItem.finished = finished
      return todoItem
    })
    this.setState({ todoList: newTodoList })
  }

  deleteFinishedTodoItems = () => {
    if (window.confirm("确认删除吗？")) {
      let newTodoList = this.state.todoList.filter((todoItem) => {
        return !todoItem.finished
      })
      this.setState({ todoList: newTodoList })
    }
  }

  render() {
    return (
      <div className='todo-list-container'>
        <Header addTodoItem={this.addTodoItem} />
        <List todoList={this.state.todoList}
          updateTodoItemFinished={this.updateTodoItemFinished}
          deleteTodoItem={this.deleteTodoItem} />
        <Footer todoList={this.state.todoList}
          selectAllTodoItems={this.selectAllTodoItems}
          deleteFinishedTodoItems={this.deleteFinishedTodoItems} />
      </div>
    )
  }
}
