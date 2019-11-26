import React, { FC, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";

import { Icon, Layout, Menu, message, PageHeader } from "antd";
import { assign, isEmpty } from "lodash";

import { PageLoading } from "@/components";
import { QForm } from "./QForm";

import { answer, postAnswer, updateAnswer } from "@/api/task";
import { appendArray, firstKey, usePromise, cachedLayout, cachedStructure } from "@/utils";
import { Dict, Question } from "@/types";

// 收集所有Question的id。如果有list，递归进行
const collectQId = (qs: Question[]) => {
  const result = new Set<string>();
  for(let arr = qs; arr.length !== 0; ) {
    let next: Question[] = [];
    arr.forEach(({ id, list }) => {
      result.add(id.toString());
      if(list) next = appendArray(next, list);
    });
    arr = next;
  }
  return result;
};

// 在控件中的answer state是按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 的格式组织的，而
// 后端api中返回的回答是 { "问题id": "问题回复" } 格式，即所有Section下的回答合并到一起。
// 该函数将后端api的格式转化为控件需要的按Section分隔的格式
const putAnswers = (layout: Dict<Question[]>, values: Dict<string>): Dict<Dict<string>> =>
  Object.entries(layout).reduce((obj: Dict<Dict<string>>, [title, list]) => {
    const sectionAnswers: Dict<string> = {};
    if(list === undefined)
      return obj;
    collectQId(list).forEach(id => {
      if(id in values)
        sectionAnswers[id] = values[id];
    });
    return assign(obj, { [title]: sectionAnswers });
  }, {});

interface P extends RouteComponentProps {
  taskId: number | string;
  answerId?: number | string;
  sectionId?: number | string;
}

export const AnswerForm = withRouter<P, FC<P>>(({ taskId, history, ...props }) => {

  // 创建新answer时此项为undefined，其余情况应该均有值
  const [answerId, setAnswerId] = useState(props.answerId);
  // 修改内容，更新回答时使用。如后端api一般，扁平而非按Section分层的结构
  const [patches, setPatches] = useState<Dict<string>>({});

  const [state, updateState] = usePromise(async () => {
    // [问卷样式, 问卷大纲结构]
    const [layout, struct] = await Promise.all([
      cachedLayout(taskId), cachedStructure(taskId)
    ]);
    // 当前选中的 [一级标题(h1)]/[二级标题(h2)]/...
    const header = firstKey(layout) ?? "";
    // 该项目的所有回答，按照 { "[一级标题]/[二级标题]": { "问题id": "问题回复" } } 形式组织
    let answers: Dict = { "#vars": { answerId } };
    if(answerId !== undefined) {
      const values = await answer(answerId);
      answers = assign(answers, putAnswers(layout, values));
    }
    return { layout, struct, header, answers };
  }, e => message.error(e.message));

  if (state.loaded === null)
    return null;
  if (!state.loaded)
    return <PageLoading/>;

  const { layout, struct, answers, header } = state;

  const valuesChanged = (changes: any, allValues: any) => {
    const newAnswers = { ...answers, ...allValues };
    const newPatches = { ...patches, ...changes };
    updateState({ answers: newAnswers });
    console.log(newAnswers);
    setPatches(newPatches);
  };

  const post = async () => {
    if(isEmpty(patches)) {
      message.info("没有更新内容，无需提交");
      return;
    }
    try {
      if (answerId === undefined) {
        const { id } = await postAnswer(taskId, (patches));
        setAnswerId(id); setPatches({});
        message.success("提交成功");
        history.replace(`${history.location.pathname}/${id}`);
      } else {
        await updateAnswer(answerId, (patches));
        setPatches({});
        message.success("更新成功");
      }
    } catch (e) { message.error(e.message); }
  };

  const renderedMenu = struct.map(({ name, list }) => {
    if(!list || list.length === 0)
      return <Menu.Item key={name} onClick={() => updateState({ header: name })}>{name}</Menu.Item>;
    return <Menu.SubMenu key={name} title={<span><Icon type="bars"/>{name}</span>}>
      {
        list.map(s2 =>
          <Menu.Item key={`${name}/${s2.name}`} onClick={() => updateState({ header: `${name}/${s2.name}` })}>
            {s2.name}
          </Menu.Item>)
      }
    </Menu.SubMenu>;
  });

  const parts = header.split("/");

  return <Layout>
    <Layout.Sider style={{ width: 200, background: "#fff" }}>
      <Menu mode="inline" selectedKeys={[header]} defaultOpenKeys={parts.length === 1 ? undefined : [parts[0]]}
            style={{ height: "100%", borderRight: 0 }}>
        {renderedMenu}
      </Menu>
    </Layout.Sider>
    <Layout>
      <Layout.Content style={{ padding: "20px 24px", minHeight: 280 }}>
        <PageHeader title="返回数据页" onBack={() => window.location.hash = `/task/${taskId}/answers`}/>
        <QForm layout={layout[header]} answer={answers}
               onChange={valuesChanged} onSubmit={post} />
      </Layout.Content>
    </Layout>
  </Layout>;

});

export default AnswerForm;
