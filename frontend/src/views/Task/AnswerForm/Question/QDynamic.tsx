import React, { Fragment, forwardRef, useState } from "react";

import { max } from "lodash";

import { Form, Icon, Button } from "antd";
import { FormItemProps } from "antd/lib/form";

import { QProps, Question } from ".";
import { useFormData } from "@/utils";

type P = QProps;

const noLabel: FormItemProps = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 }
  }
};

export const QDynamic = forwardRef<any, P>(({ schema: { id, list } }, ref) => {
  if (!list)
    throw new Error(`template list ${id} is invalid - no list`);

  const [keys, setKeys] = useFormData<number[]>(`#keys.$${id}`, []);
  const [next, setNext] = useState((max(keys) ?? -1) + 1);

  const remove = (key: number) => {
    const newKeys = keys.filter(k => k !== key);
    setKeys(newKeys);
  };

  const add = () => {
    setKeys([...keys, next]);
    setNext(next + 1);
  };

  return <>
    {
      keys.map(key => <Fragment key={key}>
        <Icon className="q-dynamic-delete" type="minus-circle-o" onClick={() => remove(key)}/>
        {list.map(q => <Question key={`${q.id}-${key}`} schema={q} fieldPrefix={`$${id}.@${key}`}/>)}
      </Fragment>)
    }
    <Form.Item {...noLabel}>
      <Button type="dashed" onClick={add} style={{ width: "60%" }}>
        <Icon type="plus"/>&nbsp;添加
      </Button>
    </Form.Item>
  </>;
});
