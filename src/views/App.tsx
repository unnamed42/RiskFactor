import React, { Component } from "react";

import { LoginForm } from "@/components";

export default class App extends Component {
  async logout(e: React.MouseEvent) {
    await setTimeout(() => {

    }, 100);
  }

  render() {
    return (
      <div>
        <h1>Hello, world!</h1>
        <p>Our API base url is <a href={process.env.API_BASE}>{process.env.API_BASE}</a></p>
        {/* <Button onClick={this.logout}>Logout</Button> */}
        <LoginForm/>
      </div>
    );
  }
}
