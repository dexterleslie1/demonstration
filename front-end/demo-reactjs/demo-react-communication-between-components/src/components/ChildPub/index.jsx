import React, { Component } from 'react'
import PubSub from 'pubsub-js'

export default class ChildPub extends Component {
    onClick = () => {
        PubSub.publish("my-channel", "Dexterleslie!!!")
    }

    render() {
        return (
            <div>
                <input type='button' value="发布消息" onClick={this.onClick} />
            </div>
        )
    }
}
