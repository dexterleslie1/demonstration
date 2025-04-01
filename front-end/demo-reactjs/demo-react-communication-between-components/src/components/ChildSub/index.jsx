import React, { Component } from 'react'
import PubSub from "pubsub-js"

export default class ChildSub extends Component {

    state = {
        message: ""
    }

    componentDidMount() {
        this.subscriberId = PubSub.subscribe("my-channel", (msg, data) => {
            this.setState({ message: data })
        })
    }

    componentWillUnmount() {
        if (this.subscriberId) {
            PubSub.unsubscribe(this.subscriberId)
        }
    }

    render() {
        let date = new Date().toString()
        return (
            <div>
                订阅到的消息：{date} - {this.state.message}
            </div>
        )
    }
}
