import React, { forwardRef } from "react";

import { Form } from "antd";

import QSelect from "./QSelect";
import QDate from "./QDate";
import QCheckbox from "./QCheckbox";
import QYesNoChoice from "./QYesNoChoice";
import QInput from "./QInput";

const render = (props: QuestionProps, ref: React.Ref<any>) => {
  switch (props.schema.type) {
    case "DATE":
      return (<QDate {...props} ref={ref} />);
    case "NUMBER": case "TEXT":
      return (<QInput {...props} ref={ref} />);
    case "SINGLE_SELECT": case "MULTI_SELECT":
      return (<QSelect {...props} ref={ref} />);
    case "SINGLE_CHOICE":
    case "MULTI_CHOICE":
      return (<QCheckbox {...props} ref={ref} />);
    case "YESNO_CHOICE":
      return (<QYesNoChoice {...props} ref={ref} />);
    default: return (<div />);
  }
};

export default forwardRef<any, QuestionProps>((props, ref) => {
  const rendered = render(props, ref);
  if (props.isChild)
    return <Form.Item label={props.schema.description}>{rendered}</Form.Item>;
  else
    return rendered;
});
