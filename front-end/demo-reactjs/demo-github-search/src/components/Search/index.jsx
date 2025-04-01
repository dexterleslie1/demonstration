import React, { Component } from 'react'

import Header from './Header'
import List from './List'

import "./index.css"

export default class Search extends Component {
  state = {
    searchResultList: []
  }

  setSearchResultList = (s) => {
    console.log(s)
    this.setState({ searchResultList: s })
  }

  render() {
    return (
      <div className='search-container'>
        <Header setSearchResultList={this.setSearchResultList} />
        <List searchResultList={this.state.searchResultList} />
      </div>
    )
  }
}
