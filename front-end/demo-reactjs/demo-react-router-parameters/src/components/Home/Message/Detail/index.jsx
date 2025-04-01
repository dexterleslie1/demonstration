import React, { Component } from 'react'
import qs from "query-string"

export default class Detail extends Component {
  render() {
    // URL路由参数
    // const {id, content} = this.props.match.params

    // query路由参数
    // const {id, content} = qs.parse(this.props.location.search)

    // state路由参数
    const {id, content} = this.props.location.state || {}
    return (
      <div>
        <div>ID: {id}</div>
        <div>Content: {content}</div>
      </div>
    )
  }
}
