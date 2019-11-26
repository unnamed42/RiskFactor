import React, { forwardRef, useState } from "react";

import { Form, Checkbox, Radio, Input, Row } from "antd";
import CheckboxGroup, { CheckboxValueType } from "antd/lib/checkbox/Group";
import { RadioChangeEvent } from "antd/lib/radio";
import RadioGroup from "antd/lib/radio/group";

import { QProps } from ".";
import { text } from "@/config";
import { enablerFId, tuple, useForm } from "@/utils";

const { other, required } = text;

const sep = "、";

type Ref = CheckboxGroup | RadioGroup;

/**
 * 将多项单选（Radio）和多项多选（Checkbox）合并在一起，因为大量代码是相同的
 * 唯一区别在于被选内容`chosen`，Radio只允许选一个（因此chosen是长度为1的数组），Checkbox可以是多个长
 */
export const QChoices = forwardRef<Ref, QProps>(({ schema, onChange, value }, ref) => {

  const { choices, id, customizable, type } = schema;
  if (!choices)
    throw new Error(`${type} ${id} is incorrectly configured - no choices`);
  const multi = type?.includes("multi");

  // 决定渲染控件类型
  const [Choice, ChoiceGroup] = (() => {
    const ChoiceType = multi ? Checkbox : Radio;
    // 必须转成React.ElementType，否则报 no constructor 错误
    return tuple(ChoiceType as React.ElementType, ChoiceType.Group);
  })();

  const [initChosen, initInput] = (() => {
    if(value === undefined)
      return tuple(undefined, undefined);
    // 如果用户输入值中含有分隔符，在这里split，之后join在一起，内容不变
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
    return tuple(indexes, inputValue.length ? inputValue.join(sep) : undefined);
  })();

  const [chosen, setChosen] = useState(initChosen);
  const [input, setInput] = useState(initInput);
  const form = useForm();

  const valueChanged = (chosenIdx: number[] | undefined, inputValue: string | undefined) => {
    const values = [...choices, inputValue];
    if(chosenIdx) {
      if(chosenIdx?.length)
        onChange?.(chosenIdx.map(i => values[i]).join(sep));
      else
        onChange?.(undefined);
    }
  };

  const inputChanged = (inputValue: string) => {
    setInput(inputValue);
    valueChanged(chosen, inputValue);
  };

  const changed = (arg: CheckboxValueType[] | RadioChangeEvent) => {
    let chosenIdx: number[] = [];
    if("target" in arg)
      chosenIdx.push(arg.target.value);
    else
      chosenIdx = arg as number[];
    setChosen(chosenIdx);
    valueChanged(chosenIdx, input);
  };

  const selectedValue = () => {
    if(multi) return chosen;
    if(chosen && chosen.length !== 0) return chosen[0];
    return undefined;
  };

  return <ChoiceGroup ref={ref} value={selectedValue()} onChange={changed}>
    {
      choices.map((choice, idx) => {
        const item = <Choice value={idx} key={`i-${idx}`}>{choice}</Choice>;
        return multi ? <Row key={idx}>{item}</Row> : item;
      })
    }
    {
      customizable && <Choice key={choices.length} value={choices.length}>
        {other}
        {
          chosen?.includes(choices.length) && <Form.Item className="form-item-generated">
            {
              form?.getFieldDecorator(`${enablerFId(id)}.#other`, {
                rules: [{ required: true, message: required }],
                initialValue: input
              })(<Input type="text" onChange={e => inputChanged(e.target.value)} />)
            }
          </Form.Item>
        }
      </Choice>
    }
  </ChoiceGroup>;

});
