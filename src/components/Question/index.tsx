import React, { forwardRef } from "react";

import QSelect from "./QSelect";
import QDate from "./QDate";
import QCheckbox from "./QCheckbox";

export default forwardRef<any, QuestionProps>((props, ref) => {
  switch (props.schema.type) {
    case "DATE":
      return (<QDate {...props} ref={ref} />);
    case "SINGLE_SELECT": case "MULTI_SELECT":
      return (<QSelect {...props} ref={ref} />);
    case "SINGLE_CHOICE":
    case "MULTI_CHOICE":
      return (<QCheckbox {...props} ref={ref} />);
    default: return (<div />);
  }
});
