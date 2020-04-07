import React, { FC, Fragment } from "react";

import { Form } from "antd";
import { PlusCircleOutlined, MinusCircleOutlined } from "@ant-design/icons";

import { RenderProps as P, Renderer } from ".";

export const QDynamic: FC<P> = ({ rule: { id, list }, namePath }) => {
  if (!list)
    throw new Error(`问题组模板 ${id} 配置不正确 - 无内容`);

  return <Form.List name={namePath}>
    {
      (fields, { add, remove }) => <>
        {
          fields.map((field, idx) => <Fragment key={field.key}>
            <MinusCircleOutlined onClick={() => remove(field.name)}/>
            {
              // Form.List下属的Form.Item，其name不需要公用前缀，直接从它的下标开始
              list.map(rule => <Renderer rule={rule} key={rule.id} namePath={[idx]}/>)
            }
          </Fragment>)
        }
        <Form.Item>
          <PlusCircleOutlined onClick={() => add()}/>
        </Form.Item>
      </>
    }
  </Form.List>;
};
