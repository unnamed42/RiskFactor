import React, { Component } from "react";

interface State {
  hasError: boolean;
}

export class ErrorBoundary extends Component<{}, State> {

  constructor(props: {}) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error) {
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
