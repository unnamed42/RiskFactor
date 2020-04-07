import React, { FC } from "react";
import { isArray } from "lodash";

import { Form, Checkbox, Radio, Input, Row } from "antd";
import type { FormInstance } from "rc-field-form/es";

import type { RenderProps as P } from ".";
import { text } from "@/config";
import { tuple, shouldUpdate } from "@/utils";

/**
 * 将多项单选（Radio）和多项多选（Checkbox）合并在一起，因为大量代码是相同的
 * 唯一区别在于被选内容`chosen`，Radio只允许选一个（因此chosen是长度为1的数组），Checkbox可以是多个长
 */
export const QChoices: FC<P> = ({ rule: { choices, id, type, customizable }, namePath }) => {
  if (!choices)
    throw new Error(`下拉菜单|多选框 ${id} 没有正确配置选项choices`);
  const multi = type?.includes("multi") ?? false;

  // 决定渲染控件类型
  const [Choice, ChoiceGroup] = (() => {
    const ChoiceType = multi ? Checkbox : Radio;
    // 必须转成React.ElementType，否则报 no constructor 错误
    return tuple(ChoiceType as React.ElementType, ChoiceType.Group);
  })();

  // 选中了“其他”，启用自定义输入选项
  const otherSelected = (form: FormInstance) => {
    const selected = form.getFieldValue(namePath);
    if (!selected) return false;
    // 为Array则是多选，否则是单选
    return (isArray(selected) && selected.includes(choices.length)) ||
      selected === choices.length;
  };

  return <>
    <Form.Item name={namePath} noStyle>
      <ChoiceGroup>
        {
          choices.map((choice, idx) => {
            const item = <Choice value={idx} key={`i-${idx}`}>{choice}</Choice>;
            return multi ? <Row key={idx}>{item}</Row> : item;
          })
        }
        {
          customizable && <Choice key={choices.length} value={choices.length}>
            {text.other}
          </Choice>
        }
      </ChoiceGroup>
    </Form.Item>
    <Form.Item noStyle shouldUpdate={shouldUpdate(namePath)}>
      {
        form => otherSelected(form) ?
          <Form.Item name={[...namePath, "other"]} noStyle required>
            <Input type="text" />
          </Form.Item> :
          null
      }
    </Form.Item>
  </>;
};
