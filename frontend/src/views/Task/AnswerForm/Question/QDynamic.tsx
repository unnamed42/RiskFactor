import React, { Component } from "react";

import { Form, Icon, Button } from "antd";
import { FormItemProps } from "antd/lib/form";

import { Question, QProps } from ".";

interface S {
  indexes: number[];
  id: number;
}

type P = QProps<string>;

const noLabel: FormItemProps = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 }
  }
};

export class QDynamic extends Component<P, S> {

  constructor(props: P) {
    super(props);
    const indexes = props.value?.split(",").map(s => Number(s)) ?? [];
    const id = indexes.length ? Math.max(...indexes) + 1 : 0;
    this.state = { indexes, id };
  }

  remove(idx: number) {
    const { indexes } = this.state;
    const newIdx = indexes.filter(i => i !== idx);
    this.setState({ indexes: newIdx });
    this.props.onChange?.(newIdx.join(","));
  }

  add() {
    const { indexes, id } = this.state;
    const newIdx = [...indexes, id + 1];
    this.setState({
      indexes: newIdx,
      id: id + 1
    });
    this.props.onChange?.(newIdx.join(","));
  }

  render() {
    const { indexes } = this.state;
    const { schema: { list } } = this.props;
    return <div>
      {
        indexes.map(idx =>
          <div key={`div-${idx}`}>
            <Icon key={`icon-${idx}`}
              className="q-dynamic-delete"
              type="minus-circle-o"
              onClick={() => this.remove(idx)}
            />
            {
              list!.map((q, i) => {
                const field = `${q.id}@${idx}`;
                return <Question key={`${idx}-${i}`}
                  schema={{ ...q, id: field }}
                  formItemProps={{ required: false, ...(idx === 0 ? undefined : noLabel) }}
                />;
              })
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
}
