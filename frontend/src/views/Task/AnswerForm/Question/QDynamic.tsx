import React, { Component } from "react";

import { Form, Icon, Button } from "antd";
import { FormItemProps } from "antd/lib/form";

import { QProps } from ".";
import { QList } from "./QList";

interface S {
  next: number;
  keys: string[];
  values: {
    [idx: string]: string;
  };
}

type P = QProps;

const noLabel: FormItemProps = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 }
  }
};

const collectAnswers = (values: S["values"]) => JSON.stringify(Object.values(values));

export class QDynamic extends Component<P, S> {

  constructor(props: P) {
    super(props);
    const { value } = props;
    const values: S["values"] = value !== undefined ? JSON.parse(value) : {};
    const keys = Object.keys(values);
    const next = keys.length;
    this.state = { next, keys, values };
  }

  render() {
    const { values, keys } = this.state;
    const { schema } = this.props;

    return <div>
      {
        // curr 只是用来查看是否是第一条子问题
        keys.map((k, curr) =>
          <div key={`div-${k}`}>
            <Icon key={`icon-${k}`} className="q-dynamic-delete"
                  type="minus-circle-o" onClick={() => this.remove(k)}/>
            {
              <Form.Item>
                <QList schema={schema} key={`${schema.id}@${k}`} value={values[k]}
                       onChange={v => this.itemChanged(k, v)} />
              </Form.Item>
            }
          </div>
        )
      }
      <Form.Item {...noLabel}>
        <Button type="dashed" onClick={() => this.add()} style={{ width: "60%" }}>
          <Icon type="plus" />添加
        </Button>
      </Form.Item>
    </div>;
  }

  private remove(key: string) {
    const { values, keys } = this.state;
    const newKeys = keys.filter(k => k !== key);
    const newValues = Object.assign({}, ...keys.map(k => ({ [k]: values[k] })));
    this.setState({ keys: newKeys, values: newValues });
    this.props.onChange?.(collectAnswers(newValues));
  }

  private itemChanged(key: string, value: string) {
    const values = { ...this.state.values, [key]: value };
    this.setState({ values });
    this.props.onChange?.(collectAnswers(values));
  }

  private add() {
    const { keys, next } = this.state;
    const newKeys = [...keys, next.toString()];
    this.setState({ keys: newKeys, next: next + 1 });
    // 该动作并不改变回答的值
  }
}
