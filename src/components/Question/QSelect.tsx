import assert from "assert";
import React, { forwardRef, ReactElement } from "react";

import { Select, Form } from "antd";
import { SelectProps, OptionProps } from "antd/lib/select";

import { decorated, generateChildren } from "./utils";

const inputFilter = (input: string, option: ReactElement<OptionProps>) => {
  const text = option.props.value!.toString();
  return text.startsWith(input);
};

type P = QuestionProps & SelectProps;

export default forwardRef<any, P>(({ schema, form, ...remain }, ref) => {

  assert(schema.option !== undefined);

  return (
    <Form.Item label={schema.description}>
      {
        decorated(schema, form)(
          <Select ref={ref} showSearch={schema.option!.filterKey !== undefined}
                  placeholder={schema.option!.placeholderText}
                  filterOption={schema.option!.filterKey ? inputFilter : undefined}
                  mode={schema.type === "MULTI_SELECT" ? "multiple" : undefined}
                  {...remain}>
            {generateChildren(schema, Select.Option)}
          </Select>
        )
      }
    </Form.Item>
  );
});
