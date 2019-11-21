import React, { forwardRef, useContext, useState } from "react";

import { Form, Radio, Input } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { QProps, FormContext } from ".";
import { text } from "@/config";

const { other } = text;

export const QChoice = forwardRef<any, QProps>(({ onChange, schema, value }, ref) => {

  const { customizable, choices, id } = schema;
  if (!choices)
    throw new Error(`Question ${id} is invalid - no choices set`);

  const [initSelected, initInput] = ((): [number | undefined, string | undefined] => {
    if(value === undefined)
      return [undefined, undefined];
    const idx = choices.indexOf(value);
    if(idx !== -1)
      return [idx, undefined];
    return customizable ? [choices.length, value] : [undefined, undefined];
  })();

  const [input, setInput] = useState(initInput);
  // 如果传入value是预设选项，设置为其index；否则设置为“其他”对应的index
  const [selected, setSelected] = useState(initSelected);
  const form = useContext(FormContext);

  const inputChanged = (inputValue: string) => {
    setInput(inputValue);
    if(inputValue && selected !== undefined)
      onChange?.(selected === choices.length ? inputValue : choices[selected]);
  };

  const changed = ({ target }: RadioChangeEvent) => {
    const idx = target.value;
    setSelected(idx);
    if(input)
      onChange?.(idx === choices.length ? input : choices[idx]);
  };

  return <Radio.Group ref={ref} value={selected} onChange={changed}>
    {choices.map((choice, idx) => <Radio key={idx} value={idx}>{choice}</Radio>)}
    {
      customizable && <Radio key="$others" value={choices.length}>
        {other}
        {
          selected === choices.length && <Form.Item>
            {
              form?.getFieldDecorator(`$${id}-other`, {
                rules: [{ required: true, message: "此项必填" }],
                initialValue: input
              })(<Input type="text" onChange={e => inputChanged(e.target.value)}/>)
            }
          </Form.Item>
        }
      </Radio>
    }
  </Radio.Group>;
});
