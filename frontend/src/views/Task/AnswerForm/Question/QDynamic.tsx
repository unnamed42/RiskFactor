import React, { Fragment, forwardRef } from "react";

import { max } from "lodash";

import { Form, Icon, Button } from "antd";
import { FormItemProps } from "antd/lib/form";

import { QProps, Question } from ".";
import { useForm } from "@/utils";

type P = QProps;

const noLabel: FormItemProps = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 }
  }
};

const keyId = (questionId: number | string) => `#keys.$${questionId}`;

export const QDynamic = forwardRef<any, P>(({ schema: { id, list } }, ref) => {
  if (!list)
    throw new Error(`template list ${id} is invalid - no list`);

  const form = useForm();

  const keys = (): number[] | undefined => form.getFieldValue(keyId(id));

  const remove = (key: number) => {
    const newKeys = keys()?.filter(k => k !== key);
    form.setFieldsValue({ [keyId(id)]: newKeys });
  };

  const add = () => {
    const currKeys = keys();
    const next = currKeys ? (max(currKeys) ?? -1) + 1 : 0;
    const newKeys = [...(currKeys ?? []), next];
    form.setFieldsValue({ [keyId(id)]: newKeys });
  };

  return <>
    {form.getFieldDecorator(keyId(id))(<Fragment />)}
    {
      keys()?.map(key => <Fragment key={key}>
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
