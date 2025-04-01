import React from 'react'
import "./index.scss"

export default class ComponentScss extends React.Component {
    render() {
        return (
            <div className="my-component-scss">
                <div className='nested1'>
                    <div className='nested2'>
                        ComponentScss!
                    </div>
                </div>
            </div>
        )
    }
}