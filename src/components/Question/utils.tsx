import React, { ComponentType } from "react";

import { WrappedFormUtils, GetFieldDecoratorOptions } from "antd/lib/form/Form";

type Identity = <T>(t: T) => T;

export const decorated = <T extends any = any>(schema: Question,
                                               form: WrappedFormUtils<T>,
                                               more?: GetFieldDecoratorOptions) => {
  const options = more || {};
  if (schema.option && schema.option.required) {
    const rule = { required: true, message: schema.option.message };
    options.rules = [rule, ...(options.rules || [])];
  }
  return form.getFieldDecorator(schema.field, options) as Identity;
};

export const generateChildren = (schema: Question, Child: ComponentType<any>) => {
  return (schema.list || []).map(({ label, option, field }) => {
    const value = (option && option.filterKey) ? option.filterKey : label;
    return (<Child value={value} key={field}>
      {label}
    </Child>);
  });
};
