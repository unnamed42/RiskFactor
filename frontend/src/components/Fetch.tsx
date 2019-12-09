import React, { Component, ReactNode} from "react";
import { Redirect } from "react-router-dom";
import { AxiosError } from "axios";

import { message } from "antd";

import { Loading } from "@/components";
import { text } from "@/config";
import { store } from "@/redux";
import { logout } from "@/redux/auth";

interface P<T> {
  // 该fetch是否需要验证用户token
  guarded?: boolean;
  // 异步获取动作
  fetch: () => Promise<T>;
  // 在获取完成时执行的动作
  onLoadEnd?: (data: T) => void;
  // 获取完成后的渲染函数，做成children的形式
  children: (data: T) => ReactNode;
}

interface S<T> {
  // 加载动作已结束，无论是成功还是失败
  loadEnd: boolean;
  // 401（Unauthorized），此时应当跳转到登录页面
  error401: boolean;
  // 错误原因
  error?: string;
  // 加载好的数据
  data?: T;
}

export class Fetch<T> extends Component<P<T>, S<T>> {

  constructor(props: P<T>) {
    super(props);
    this.state = { loadEnd: false, error401: false };
  }

  async componentDidMount() {
    try {
      const data = await this.props.fetch();
      this.props.onLoadEnd?.(data);
      this.setState({ data });
    } catch(e) {
      const err = e as AxiosError;
      console.log(err.response);
      if(err.response?.status === 401) {
        this.setState({ error401: true });
        message.error(text.reLogin);
      } else
        message.error(err.message);
    } finally {
      this.setState({ loadEnd: true });
    }
  }

  render() {
    const { loadEnd, error, error401, data } = this.state;
    if(error401) {
      store.dispatch(logout());
      return <Redirect to="/login"/>;
    }
    if(!loadEnd)
      return <Loading />;
    if(error || !data)
      return null;
    return this.props.children(data);
  }
}
