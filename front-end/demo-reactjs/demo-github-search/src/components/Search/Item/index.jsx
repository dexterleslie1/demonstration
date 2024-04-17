import React, { Component } from 'react'

import "./index.css"

export default class Item extends Component {
  render() {
    return (
      <div className='search-item-container'>
        {/* <img src="https://gw.alicdn.com/tfs/TB1jwakrbH1gK0jSZFwXXc7aXXa-20-20.png" alt="" /> */}
        <img src={this.props.avatar_url} alt="" />
        <div>{this.props.login}</div>
      </div>
    )
  }
}
