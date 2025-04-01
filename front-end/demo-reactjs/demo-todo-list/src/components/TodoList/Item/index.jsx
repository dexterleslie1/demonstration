import React, { Component } from 'react'
import PropTypes from 'prop-types'
import "./index.css"

export default class Item extends Component {

  static propTypes = {
    updateTodoItemFinished: PropTypes.func.isRequired,
    deleteTodoItem: PropTypes.func.isRequired,
  }

  onChange = (id) => {
    return (event) => {
      this.props.updateTodoItemFinished(id, event.target.checked)
    }
  }

  render() {
    return (
      <li>
        <div>
          <input type="checkbox" checked={this.props.finished}
            onChange={this.onChange(this.props.id)} />{this.props.name}
        </div>

        <input type="button" value="删除" onClick={() => { this.props.deleteTodoItem(this.props.id) }} />
      </li>
    )
  }
}
