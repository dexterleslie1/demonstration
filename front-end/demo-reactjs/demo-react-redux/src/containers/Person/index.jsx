import React, { Component } from 'react'
import { createAddPersonAction } from '../../redux/actions/person'

import { connect } from 'react-redux'
import { nanoid } from 'nanoid'

class Person extends Component {

    handleAddPerson = () => {
        const name = this.elName.value
        const age = this.elAge.value
        const id = nanoid()
        this.props.myAddPerson({ id: id, name: name, age: age })
    }

    render() {
        return (
            <div>
                <h1>Person组件，上面组件求和：{this.props.myCountO}</h1>
                <div>
                    <input type="text" placeholder='名称' ref={c => this.elName = c} />&nbsp;
                    <input type="text" placeholder='年龄' ref={c => this.elAge = c} />&nbsp;
                    <button onClick={this.handleAddPerson}>添加</button>
                </div>
                <div>
                    <ul>
                        {
                            this.props.personList.map((el) => {
                                return (
                                    <li key={el.id}>
                                        {el.name} - {el.age}
                                    </li>
                                )
                            })
                        }
                    </ul>
                </div>
            </div>
        )
    }
}


function mapStateToProps(state) {
    return { personList: state.myPerson, myCountO: state.myCount }
}

function mapDispatchToProps(dispatch) {
    return {
        myAddPerson: (personObj) => dispatch(createAddPersonAction(personObj)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Person)

