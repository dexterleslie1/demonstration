import React, { Component } from 'react'
import qs from "query-string"

export default class Detail extends Component {
  render() {
    const {id, content} = this.props.match.params
    return (
      <div>
        <div>ID: {id}</div>
        <div>Content: {content}</div>
      </div>
    )
  }
}
