import React, { ComponentType, ReactNode } from "react";

import { WrappedFormUtils, GetFieldDecoratorOptions } from "antd/lib/form/Form";

export function decorated<T = any>(schema: Question, form: WrappedFormUtils<T>, more?: GetFieldDecoratorOptions) {
  const { getFieldDecorator } = form;
  let options: GetFieldDecoratorOptions = {};
  if (schema.option && schema.option.required) {
    options.rules = [{
      required: true,
      message: schema.option.message
    }];
    if (more)
      options = Object.assign(options, more);
  }
  if (Object.keys(options).length !== 0)
    return getFieldDecorator(schema.field, options);
  else
    return (node: ReactNode) => node;
}

export function generateChildren(schema: Question, Child: ComponentType<any>) {
  return (schema.list || []).map(({ type, label, option, field }) => {
    if (type !== "CHOICE")
      throw new Error(`${field} is not of type CHOICE but ${type}`);

    const value = (option && option.filterKey) ? option.filterKey : label;
    return (<Child value={value} key={field}>
      {label}
    </Child>);
  });
}
