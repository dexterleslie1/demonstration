import React, { Component } from 'react'
import { NavLink, Switch, Route, Redirect } from 'react-router-dom'
import News from './News'
import Message from './Message'

export default class Home extends Component {
  render() {
    return (
      <div>
        <div>
          Home页面
        </div>
        <div>
          <NavLink activeClassName='my-active' to="/home/news">News</NavLink>&nbsp;
          <NavLink activeClassName='my-active' to="/home/message">Message</NavLink>
        </div>
        <div>
          <Switch>
            <Route path='/home/news' component={News} />
            <Route path='/home/message' component={Message} />
            <Redirect from="*" to="/home/news" />
          </Switch>
        </div>
      </div>
    )
  }
}
