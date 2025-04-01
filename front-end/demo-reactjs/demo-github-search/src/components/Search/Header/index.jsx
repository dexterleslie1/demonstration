import React, { Component } from 'react'
import axios from 'axios'

import "./index.css"

export default class Header extends Component {
    keywordEl = React.createRef()

    handleOnClick = () => {
        let value = this.keywordEl.current.value
        if (!value || !value.trim()) {
            alert("请输入关键词")
            return
        }

        axios.get(`/api/v1/search/users?q=${value}`)
            .then(
                response => {
                    this.props.setSearchResultList(response.data.items)
                },
                error => {
                    console.error(error)
                    alert(error)
                }
            )
    }

    render() {
        return (
            <div className='search-header-container'>
                <input type="text" placeholder='输入用户名称关键词' ref={this.keywordEl} />
                <input type="button" value="搜索" onClick={this.handleOnClick} />
            </div>
        )
    }
}
