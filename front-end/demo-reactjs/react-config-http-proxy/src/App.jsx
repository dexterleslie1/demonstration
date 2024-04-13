import logo from './logo.svg';
import React from 'react'
import axios from 'axios'
import './App.css';

export default class App extends React.Component {

  handleOnClick1 = () => {
    axios.post("/api/v1/post").then(
      response => {
        console.log(response)
      },
      error => {
        console.error(error)
      }
    )
  }
  handleOnClick2 = () => {
    axios.get("/api/v2/get")
      .then(
        response => {
          console.log(response)
        },
        error => {
          console.error(error)
        }
      )
  }

  render() {
    return (
      <div className="App">
        <input type="button" value="调用/api/v1接口" onClick={this.handleOnClick1} />
        <input type="button" value="调用/api/v2接口" onClick={this.handleOnClick2} />
      </div>
    );
  }
}
