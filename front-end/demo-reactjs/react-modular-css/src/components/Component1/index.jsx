import React from 'react'
import component1css from "./index.module.css"

export default class Component1 extends React.Component {
    render() {
        return (
            <div className={component1css.mystyle}>
                Component1
            </div>
        )
    }
}