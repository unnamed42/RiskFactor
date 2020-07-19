/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import React, { Component } from "react";

interface State {
  hasError: boolean;
}

// 用于报错，没什么大用
export class ErrorBoundary extends Component<Empty, State> {

  constructor(props: Empty) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(_error: Error) {
    return { hasError: true };
  }

  componentDidCatch(error: any, info: any) {
    console.log(error, info);
  }

  render() {
    const { hasError } = this.state;
    const { children } = this.props;
    if (hasError)
      return <h1>出现异常</h1>;
    return children;
  }

}
