import React from "react";

import { DatePicker } from "antd";

import { QuestionGenerator } from ".";
import { decorated } from "./utils";

export const qdate: QuestionGenerator = (schema, form) => {
    const text = schema.option && schema.option.placeholder;
    return decorated(schema, form)(
        <DatePicker placeholder={text}/>
    );
};
