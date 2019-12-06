import React, { Component, ReactNode} from "react";

import { message } from "antd";

import { Loading } from "@/components";

interface P<T> {
  // 该fetch是否需要验证用户token
  guarded?: boolean;
  // 异步获取动作
  fetch: () => Promise<T>;
  // 获取完成后的渲染函数，做成children的形式
  children: (data: T) => ReactNode;
}

interface S<T> {
  // 加载动作已结束，无论是成功还是失败
  loadEnd: boolean;
  // 403（Unauthorized），此时应当跳转到登录页面
  error403?: boolean;
  // 错误原因
  error?: string;
  // 加载好的数据
  data?: T;
}

export class Fetch<T> extends Component<P<T>, S<T>> {

  constructor(props: P<T>) {
    super(props);
    this.state = { loadEnd: false };
  }

  componentDidMount() {
    this.props.fetch().then(data => this.setState({ loadEnd: true, data }))
      .catch(err => { this.setState({ loadEnd: true }); message.error(err.message); });
  }

  render() {
    const { loadEnd, error, data } = this.state;
    if(!loadEnd)
      return <Loading />;
    if(error || !data)
      return null;
    return this.props.children(data);
  }
}
