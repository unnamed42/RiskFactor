import React, { Component } from "react";

export default class App extends Component {
  async logout(e: React.MouseEvent) {
    ;
  }

  render() {
    return (
      <div>
        <h1>Hello, world!</h1>
        <p>Our API base url is <a href={process.env.API_BASE}>{process.env.API_BASE}</a></p>
        <button onClick={this.logout}>Logout</button>
      </div>
    );
  }
}
