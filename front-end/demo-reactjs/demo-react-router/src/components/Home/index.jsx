import React, { Component } from 'react'

import { withRouter } from 'react-router-dom';


class Home extends Component {
  render() {
    const { location } = this.props;
    const currentPath = location.pathname;

    return (
      <div>
        Home当前路由信息：{currentPath}
      </div>
    )
  }
}

export default withRouter(Home)
