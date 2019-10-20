import React, { ComponentType } from "react";

import { WrappedFormUtils, GetFieldDecoratorOptions } from "antd/lib/form/Form";

export function decorated<T = any>(schema: Question, form: WrappedFormUtils<T>, more?: GetFieldDecoratorOptions) {
  const { getFieldDecorator } = form;
  let options: GetFieldDecoratorOptions = {};
  if (schema.option && schema.option.required) {
    options.rules = [{
      required: true,
      message: schema.option.errorMessage
    }];
    if (more)
      options = Object.assign(options, more);
  }
  return getFieldDecorator(schema.fieldName, options);
}

export function generateChildren(schema: Question, Child: ComponentType<any>) {
  return (schema.list || []).map(({ type, description, option, fieldName }) => {
    if (type !== "CHOICE")
      throw new Error(`${fieldName} is not of type CHOICE but ${type}`);

    const value = (option && option.filterKey) ? option.filterKey : description;
    return (<Child value={value} key={fieldName}>
      {description}
    </Child>);
  });
}
