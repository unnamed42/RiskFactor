import React, { forwardRef, useContext, useState, Fragment } from "react";

import { Form, Checkbox, Input } from "antd";
import { CheckboxValueType } from "antd/lib/checkbox/Group";

import { QProps, FormContext } from ".";
import { text } from "@/config";

const { other, required } = text;

const sep = "、";

export const QCheckbox = forwardRef<any, QProps>(({ schema, onChange, value }, ref) => {

  const { selected, choices, id, customizable } = schema;
  if (!choices)
    throw new Error(`choice ${id} is incorrectly configured - no choices`);

  const [initChosen, initInput] = ((): [number[] | undefined, string | undefined] => {
    if(value === undefined)
      return [undefined, selected];
    // 如果用户输入值中含有分隔符，在这里处理了
    const inputValue: string[] = [];
    const indexes = value.split(sep).map(s => {
      const idx = choices.indexOf(s);
      if(idx !== -1)
        return idx;
      if(customizable) {
        inputValue.push(s);
        return choices.length;
      } else
        throw new Error(`unexpected choice ${s} in question ${id}`);
    });
    return [indexes, inputValue.length ? inputValue.join(sep) : undefined];
  })();

  const [chosen, setChosen] = useState(initChosen);
  const [input, setInput] = useState(initInput);
  const form = useContext(FormContext);

  const inputChanged = (inputValue: string) => {
    setInput(inputValue);
    if(inputValue && chosen) {
      const result = chosen.map(i => i === choices.length ? inputValue : choices[i]);
      onChange?.(result.join(sep));
    }
  };

  const changed = (values: CheckboxValueType[]) => {
    const intValues = values as number[];
    setChosen(intValues);
    if(input) {
      const result = intValues.map(i => i === choices.length ? input : choices[i]);
      onChange?.(result.join(sep));
    }
  };

  return <Checkbox.Group ref={ref} value={chosen} onChange={changed}>
    {
      choices.map((choice, idx) => <Fragment key={idx}>
        <Checkbox value={idx}>{choice}</Checkbox><br/>
      </Fragment>)
    }
    {
      customizable && <Checkbox key={choices.length} value={choices.length}>
        {other}
        {
          chosen?.includes(choices.length) && <Form.Item className="form-item-generated">
            {
              form?.getFieldDecorator(`$${id}-other`, {
                rules: [{ required: true, message: required }],
                initialValue: input
              })(<Input type="text" onChange={e => inputChanged(e.target.value)}/>)
            }
          </Form.Item>
        }
      </Checkbox>
    }
  </Checkbox.Group>;

});
