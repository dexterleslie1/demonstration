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
  render() {
    return (
      <div>
        <div>消息列表如下：</div>
        <hr />
        <div style={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          {
            this.state.messageList.map((el) => {
              return (
                // URL路由参数
                // <NavLink to={`/home/message/detail/${el.id}/${el.content}`}>{el.content}</NavLink>

                // query路由参数
                // <NavLink to={`/home/message/detail?id=${el.id}&content=${el.content}`}>{el.content}</NavLink>

                // state路由参数
                <NavLink to={{ pathname: '/home/message/detail', state: { id: el.id, content: el.content } }}>{el.content}</NavLink>
              )
            })
          }
        </div>
        <hr />
        <div>
          {/* URL路由参数 */}
          {/* <Route path="/home/message/detail/:id/:content" component={Detail} /> */}

          {/* query路由参数 */}
          {/* <Route path="/home/message/detail" component={Detail} /> */}

          {/* state路由参数 */}
          <Route path="/home/message/detail" component={Detail} />
        </div>
      </div>
    )
  }
}
