import React, { Component } from 'react'
import { NavLink, Route } from 'react-router-dom'
import Detail from './Detail'

export default class Message extends Component {
  state = {
    messageList: [{
      id: 1,
      content: "你好"
    }, {
      id: 2,
      content: "大家好"
    }]
  }

  handlePush = (id, content) => {
    this.props.history.push(`/home/message/detail/${id}/${content}`)
  }
  handleReplace = (id, content) => {
    this.props.history.replace(`/home/message/detail/${id}/${content}`)
  }

  render() {
    return (
      <div>
        <div>消息列表如下：</div>
        <hr />
        <ul>
          {
            this.state.messageList.map((el) => {
              return (
                <li>
                  <NavLink to={`/home/message/detail/${el.id}/${el.content}`}>{el.content}</NavLink>
                  &nbsp;&nbsp;<button onClick={() => this.handlePush(el.id, el.content)}>push查看</button>
                  &nbsp;&nbsp;<button onClick={() => this.handleReplace(el.id, el.content)}>replace查看</button>
                </li>
              )
            })
          }
        </ul>
        <hr />
        <div>
          <Route path="/home/message/detail/:id/:content" component={Detail} />
        </div>
      </div>
    )
  }
}
