import React, { FC } from "react";

import { Form, Button } from "antd";

import { QProps as P, Question } from ".";

export const QDynamic: FC<P> = ({ rule: { id, list },namePath }) => {
  if (!list)
    throw new Error(`问题组模板 ${id} 配置不正确 - 无内容`);
  return <Form.List name={namePath}>
    {
      (fields, {add, remove}) => <div>
        {
          fields.map(field => { })
        }
      </div>
    }
  </Form.List>;
};

// export class QDynamic extends Component<P> {
//   static contextType = FormContext;
//   declare context: React.ContextType<typeof FormContext>;

//   render() {
//     const { schema: { id, list } } = this.props;
//     if (!list)
//       throw new Error(`template list ${id} is invalid - no list`);

//     const form = this.context!;

//     return <>
//       {form.getFieldDecorator(keyId(id))(<></>)}
//       {
//         this.keys()?.map(key => <Fragment key={key}>
//           {/* tslint:disable-next-line:jsx-no-lambda */}
//           <Icon className="q-dynamic-delete" type="minus-circle-o" onClick={() => this.remove(key)}/>
//           {list.map(q => <Question key={`${q.id}-${key}`} schema={q} fieldPrefix={`$${id}.@${key}`}/>)}
//         </Fragment>)
//       }
//       <Form.Item {...noLabel}>
//         <Button type="dashed" onClick={this.add} style={{ width: "60%" }}>
//           <Icon type="plus"/>&nbsp;添加
//         </Button>
//       </Form.Item>
//     </>;
//   }

//   private keys = (): number[] | undefined => this.context!.getFieldValue(keyId(this.props.schema.id));

//   private remove = (key: number) => {
//     const { schema: { id } } = this.props;
//     const newKeys = this.keys()?.filter(k => k !== key);
//     this.context!.setFieldsValue({ [keyId(id)]: newKeys });
//   };

//   private add = () => {
//     const { schema: { id } } = this.props;
//     const currKeys = this.keys();
//     const next = currKeys ? (max(currKeys) ?? -1) + 1 : 0;
//     const newKeys = [...(currKeys ?? []), next];
//     this.context!.setFieldsValue({ [keyId(id)]: newKeys });
//   };
// }
