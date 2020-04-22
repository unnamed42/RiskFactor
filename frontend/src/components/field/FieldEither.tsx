import React, { FC, useMemo } from "react";

import { Radio, Form } from "antd";

import { RuleEither } from "@/api";
import { useFieldUpdated } from "@/hooks";
import { FieldProps, Field, extendPath } from ".";

type P = FieldProps<RuleEither>;

export const FieldEither: FC<P> = ({ rule: { id, list, yesno = "是/否" }, namePath }) => {
  const splitted = yesno.split("/");
  if (splitted.length < 2)
    throw new Error(`双选问题 ${id} 的选项名配置不正确`);
  const [yes, no] = splitted;

  const valueNamePath = useMemo(() => extendPath(namePath, "value"), [namePath]);
  const choiceChanged = useFieldUpdated(valueNamePath);

  return <>
    <Form.Item name={valueNamePath} noStyle>
      <Radio.Group>
        <Radio value={yes}>{yes}</Radio>
        <Radio value={no}>{no}</Radio>
      </Radio.Group>
    </Form.Item>
    <Form.Item noStyle shouldUpdate={choiceChanged}>
      {
        form => {
          if (form.getFieldValue(valueNamePath) !== yes)
            return null;
          return list?.map(rule => <Field rule={rule} key={rule.id} namePath={namePath} />);
        }
      }
    </Form.Item>
  </>;
};
