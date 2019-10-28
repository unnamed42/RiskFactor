import React, { forwardRef, useState } from "react";

import { Icon, Button } from "antd";
import Form, { FormItemProps } from "antd/lib/form";

import { Question } from ".";

interface ItemProps extends Omit<QProps, "onChange"> {
  idx: number;
  onChange: (value: any, idx: number) => void;
  onRemove: (idx: number) => void;
}

const noLabel: FormItemProps = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 }
  }
};

const QDynamicItem = forwardRef<any, ItemProps>((props, ref) => {

  const [value, setValue] = useState(props.value || {});
  const { schema: { list }, idx } = props;

  const onChange = (e: QChangeEvent) => {
    const newValue = { ...value, [e.field]: e.value };
    setValue(newValue);
    props.onChange(newValue, idx);
  };

  return <div ref={ref}>
    <Icon className="q-dynamic-delete"
      type="minus-circle-o"
      onClick={() => props.onRemove(idx)}
    />
    {
      list!.map(q =>
        <Question key={`${q.field}[${idx}]`}
          schema={q}
          value={value[q.field]}
          onChange={onChange} required={false}
          {...(idx === 0 ? undefined : noLabel)}
        />
      )
    }
  </div>;
});

export const QDynamic = forwardRef<any, QProps<any[]>>((props, ref) => {

  const [values, setValues] = useState(props.value || []);

  const { schema } = props;

  const onChange = (values: any[]) => {
    setValues(values);
    if (props.onChange)
      props.onChange({ field: schema.field, value: values });
  };

  const remove = (idx: number) => {
    onChange(values.filter((_, i) => i !== idx));
  };

  const childChanged = (e: QChangeEvent, idx: number) => {
    values[idx] = e.value;
    onChange(values.slice(0));
  };

  const add = () => {
    onChange([...values, {}]);
  };

  return <div ref={ref}>
    {
      values.map((value, idx) =>
        <QDynamicItem schema={schema} idx={idx} value={value} key={idx}
          onChange={childChanged} onRemove={remove} />
      )
    }
    <Form.Item {...noLabel}>
      <Button type="dashed" onClick={add} style={{width: "60%"}}>
        <Icon type="plus">添加</Icon>
      </Button>
    </Form.Item>
  </div>;
});
