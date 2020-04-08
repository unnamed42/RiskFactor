import React, { FC, CSSProperties, useState, useRef } from "react";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import { omit, set } from "lodash";
import { merge } from "lodash/fp";

import { Layout, PageHeader } from "antd";
import type { Store } from "rc-field-form/es/interface";

import { useApiCached, useApi } from "@/utils";
import {
  getSchemaDetail, schemaModifiedTime, IdType, RuleInfo,
  createAnswer, updateAnswer
} from "@/api";
import { HeadersMenu } from "./HeadersMenu";
import { InternalForm } from "./InternalForm";

interface RouteParams {
  schemaId: string;
  answerId?: string;
}

const layoutStyle: CSSProperties = { width: 200, background: "#fff" };
const containerStyle: CSSProperties = { padding: "20px 24px", minHeight: 280 };

/**
 * get问卷的规则结构，并将多层的header flatten合并处理成一层
 */
const fetch = async (schemaId: IdType) => {
  const schemaSource = await getSchemaDetail(schemaId);
  // 问题header -> header下属的list
  const schema: Record<string, RuleInfo[]> = {};
  const headers: any = {};

  const dfs = (list: RuleInfo[], namePath: string) => {
    for (const rule of list) {
      if (rule.type !== "header")
        continue;
      if ((rule.label === undefined || !rule.list)) {
        console.debug(`问题分块header $${rule.id} 缺少 label 或 list`);
        continue;
      }
      const nextNamePath = namePath ? `${namePath}/${rule.label}` : rule.label;
      // 如果下级问题第一个是header，那么该问题是高它一级的header
      if (rule.list[0].type === "header")
        dfs(rule.list, nextNamePath);
      else { // 否则，为最底层header，结束遍历
        schema[nextNamePath] = rule.list;
        set(headers, nextNamePath.split("/"), null);
      }
    }
  };

  dfs(schemaSource.rules ?? [], "");

  return { ...omit(schemaSource, "rules"), schema, headers };
};

export const AnswerForm: FC = () => {
  const history = useHistory();
  const params = useParams<RouteParams>();
  // 当前选中的 [一级标题(h1)]/[二级标题(h2)]/...
  const [header, setHeader] = useState("");
  const answerRef = useRef<Store>({});

  const schemaId = Number(params.schemaId);
  const answerId = params.answerId ? Number(params.answerId) : undefined;

  const [, submit] = useApi(async (value: Store) => {
    if (answerId === undefined)
      await createAnswer(schemaId, value);
    else
      await updateAnswer(answerId, value);
  }, [answerId, schemaId], { immediate: false, success: "提交成功" });

  const [resp] = useApiCached(() => fetch(schemaId), [schemaId], {
    cacheKey: `schema-${schemaId}`, mtimeGetter: schemaModifiedTime
  });

  if (resp.alt !== undefined)
    return resp.alt;

  const { schema, headers } = resp.data;

  return <Layout>
    <Layout.Sider style={layoutStyle}>
      <HeadersMenu headers={headers} onChange={setHeader} />
    </Layout.Sider>
    <Layout>
      <Layout.Content style={containerStyle}>
        <PageHeader title="返回数据页" onBack={() => history.replace(`/task/${schemaId}/answers`)} />
        <InternalForm schema={schema} header={header} answerId={answerId}
          onValuesChange={changes => answerRef.current = merge(answerRef.current, changes)}
          onFinish={() => { const value = (() => answerRef.current)(); submit(value); }}
        />
      </Layout.Content>
    </Layout>
  </Layout>;
};

// export const AnswerForm: FC<P> = ({ taskId, sectionId, ...props }) => {
//   const history = useHistory();

//   // 创建新answer时此项为undefined，其余情况应该均有值
//   const [answerId, setAnswerId] = useState(props.answerId);
//   // 修改内容，更新回答时使用。如后端api一般，扁平而非按Section分层的结构
//   const [patches, setPatches] = useState<Dict<string>>({});
//   // 该项目的所有回答，按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 形式组织
//   const [answers, setAnswers] = useState<Dict>({ "#vars": { answerId } });
//   // 当前选中的 [一级标题(h1)]/[二级标题(h2)]/...
//   const [header, setHeader] = useState("");

//   const valuesChanged = (changes: any, allValues: any) => {
//     const newAnswers = { ...answers, ...allValues };
//     const newPatches = { ...patches, ...changes };
//     setAnswers(newAnswers);
//     console.log(newAnswers);
//     setPatches(newPatches);
//   };

//   const post = async () => {
//     if (isEmpty(patches)) {
//       message.info("没有更新内容，无需提交");
//       return;
//     }
//     try {
//       if (answerId === undefined) {
//         const { id } = await postAnswer(taskId, answers);
//         setAnswerId(id);
//         setPatches({});
//         setAnswers({ ...answers, "#vars": { answerId: id } });
//         history.replace(`${history.location.pathname}/${id}`);
//         message.success("提交成功");
//       } else {
//         await updateAnswer(taskId, answerId, answers);
//         setPatches({});
//         message.success("更新成功");
//       }
//     } catch (e) {
//       message.error(e.message);
//     }
//   };

//   const parts = header.split("/");

//   const fetch = async () => {
//     // [问卷样式, 问卷大纲结构]
//     const [layout, struct] = await Promise.all([
//       cachedLayout(taskId), cachedStructure(taskId)
//     ]);
//     if (answerId !== undefined)
//       setAnswers({ ...answers, ...(await answer(answerId)) });
//     setHeader(firstKey(layout) ?? "");
//     return tuple(layout, struct);
//   };

//   return <Fetch fetch={fetch}>
//     {([layout, struct]) => <Layout>
//       <Layout.Sider style={{ width: 200, background: "#fff" }}>
//         <Menu mode="inline" selectedKeys={[header]} defaultOpenKeys={parts.length === 1 ? undefined : [parts[0]]}
//               style={{ height: "100%", borderRight: 0 }}>
//           {struct.map(({ name, list }) => (!list || list.length === 0) ?
//             <Menu.Item key={name} onClick={() => setHeader(name)}>{name}</Menu.Item> :
//             <Menu.SubMenu key={name} title={<span><BarsOutlined/>{name}</span>}>
//               {list.map(s2 =>
//                 <Menu.Item key={`${name}/${s2.name}`} onClick={() => setHeader(`${name}/${s2.name}`)}>
//                   {s2.name}
//                 </Menu.Item>
//               )}
//             </Menu.SubMenu>
//           )}
//         </Menu>
//       </Layout.Sider>
//       <Layout>
//         <Layout.Content style={{ padding: "20px 24px", minHeight: 280 }}>
//           <PageHeader title="返回数据页" onBack={() => history.replace(`/task/${taskId}/answers`)}/>
//           <QForm rules={layout[header]!} answer={answers}
//                  onChange={valuesChanged} onSubmit={post}/>
//         </Layout.Content>
//       </Layout>
//     </Layout>
//     }
//   </Fetch>;
// };

export default AnswerForm;
