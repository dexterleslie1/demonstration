import React, { Component } from 'react'

import Item from '../Item'

import "./index.css"

export default class List extends Component {
  render() {
    let content
    let hasSearchResult
    if (!this.props.searchResultList || this.props.searchResultList.length <= 0) {
      content = "没有查询结果"
      hasSearchResult = false
    } else {
      content = []
      this.props.searchResultList.map((el) => {
        content.push(<Item key={el.login} {...el}/>)
        hasSearchResult = true
      })
    }

    return (
      <div className='search-list-container' style={{ display: hasSearchResult ? "grid" : "block" }}>
        {content}
      </div>
    )
  }
}
